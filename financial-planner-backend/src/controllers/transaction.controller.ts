import { Request, Response } from "express";
import { AuthRequest } from "../types/request.types";
import { genAIService } from "../services/genAI.service";
import database from "../services/database.service";
import { TransactionPayload } from "../types/transaction.types";

// Receipt processing function
export const processReceiptOCR = async (req: AuthRequest, res: Response): Promise<void> => {
  try {
    const { image_base64 } = req.body;

    if (!image_base64) {
      res.status(400).json({
        success: false,
        message: "Image data is required"
      });
      return;
    }

    if (!req.user) {
      res.status(401).json({ message: "Authentication required" });
      return;
    }

    const ocrResult = await genAIService.processReceiptOCR(image_base64);
    console.log('OCR Result:', ocrResult);

    // Automatically store the transaction
    const transactionData: TransactionPayload = {
      amount: ocrResult.total_amount || 0,
      type: 'EXPENSE',
      date: ocrResult.date || new Date().toISOString(),
      pocket: 'Cash',
      category: 'General',
      note: `${ocrResult.merchant_name || 'Receipt'} - Receipt transaction`,
      tags: [],
      is_from_receipt: true,
      receipt_id: ocrResult.receipt_id || `receipt_${Date.now()}`,
      merchant_name: ocrResult.merchant_name || '',
      location: ocrResult.location || undefined,
      receipt_items: ocrResult.items || []
    };

    console.log('Transaction data to store:', transactionData);
    console.log('User ID:', req.user.id);
    
    const storedTransaction = await database.createTransaction(req.user.id, transactionData);
    console.log('Stored transaction:', storedTransaction);

    res.status(200).json({
      success: true,
      message: "Receipt processed and transaction stored successfully",
      data: ocrResult,
      transaction: {
        id: storedTransaction.id,
        amount: storedTransaction.amount,
        merchant: storedTransaction.merchant_name,
        date: storedTransaction.date
      }
    });

  } catch (error: any) {
    console.error("Error processing receipt OCR:", error);
    console.error("Error details:", { error: JSON.stringify(error), stack: error.stack });

    res.status(500).json({
      success: false,
      message: "Internal server error while processing receipt",
      error: process.env.NODE_ENV === 'development' ? error.message : undefined
    });
  }
};

// Store transaction from OCR data
export const storeTransactionFromOCR = async (req: AuthRequest, res: Response): Promise<void> => {
  try {
    const {
      total_amount,
      merchant_name,
      date,
      items,
      location,
      receipt_id,
      category,
      notes
    } = req.body;

    const user_id = req.user?.id;

    // Validate request
    if (!total_amount || !merchant_name || !user_id) {
      res.status(400).json({
        success: false,
        message: "Required fields missing: total_amount, merchant_name"
      });
      return;
    };

    console.log(`Storing transaction from OCR for user: ${user_id}`);
    console.log(`Transaction data:`, {
      amount: total_amount,
      merchant: merchant_name,
      items: items?.length || 0
    });

    // Create transaction record in database
    const transactionData: TransactionPayload = {
      amount: total_amount,
      type: total_amount >= 0 ? 'INCOME' : 'EXPENSE',
      date: date || new Date().toISOString(),
      pocket: 'Cash',
      category: category || 'General',
      note: notes || `${merchant_name} - Receipt transaction`,
      tags: [],
      is_from_receipt: true,
      receipt_id: receipt_id || `receipt_${Date.now()}`,
      merchant_name: merchant_name,
      location: location || null,
      receipt_items: items || []
    };

    // Store in Supabase
    const storedTransaction = await database.createTransaction(user_id, transactionData);

    console.log(`Transaction stored successfully with ID: ${storedTransaction.id}`);

    // Return successful response
    res.status(201).json({
      success: true,
      message: "Transaction stored successfully",
      data: {
        transaction_id: storedTransaction.id,
        amount: storedTransaction.amount,
        merchant: storedTransaction.merchant_name,
        date: storedTransaction.date,
        receipt_id: storedTransaction.receipt_id
      }
    });

  } catch (error: any) {
    console.error("Error storing transaction from OCR:", error);

    res.status(500).json({
      success: false,
      message: "Internal server error while storing transaction",
      error: process.env.NODE_ENV === 'development' ? error.message : undefined
    });
    return;
  }
};

// Transaction CRUD functions
export const createTransaction = async (req: AuthRequest, res: Response): Promise<void> => {
  try {
    if (!req.user) {
      res.status(401).json({ message: "Authentication required" });
      return;
    }
    const payload: TransactionPayload = req.body;
    const transaction = await database.createTransaction(req.user.id, payload);
    res.status(201).json({ success: true, data: transaction });
  } catch (error: any) {
    console.error("Error creating transaction:", error);
    res.status(500).json({
      success: false,
      message: "Failed to create transaction",
      error: process.env.NODE_ENV === 'development' ? error.message : undefined
    });
  }
};

export const getUserTransactions = async (req: AuthRequest, res: Response): Promise<void> => {
  try {
    if (!req.user) {
      res.status(401).json({ message: "Authentication required" });
      return;
    }
    const transactions = await database.getUserTransactions(req.user.id);
    res.status(200).json({ success: true, data: transactions });
  } catch (error) {
    res.status(500).json({ success: false, message: "Failed to get transactions", error });
  }
};

export const getTransactionById = async (req: AuthRequest, res: Response): Promise<void> => {
  try {
    if (!req.user) {
      res.status(401).json({ message: "Authentication required" });
      return;
    }
    const { id } = req.params;
    const transaction = await database.getTransactionById(id);
    if (!transaction || transaction.user_id !== req.user.id) {
      res.status(404).json({ message: "Transaction not found" });
      return;
    }
    res.status(200).json({ success: true, data: transaction });
  } catch (error) {
    res.status(500).json({ success: false, message: "Failed to get transaction", error });
  }
};

export const updateTransaction = async (req: AuthRequest, res: Response): Promise<void> => {
  try {
    if (!req.user) {
      res.status(401).json({ message: "Authentication required" });
      return;
    }
    const { id } = req.params;
    const payload = req.body;

    const transaction = await database.getTransactionById(id);
    if (!transaction || transaction.user_id !== req.user.id) {
      res.status(404).json({ message: "Transaction not found" });
      return;
    }

    const updatedTransaction = await database.updateTransaction(id, {
      ...payload,
      updated_at: new Date().toISOString()
    });

    res.status(200).json({ success: true, data: updatedTransaction });
  } catch (error) {
    res.status(500).json({
      success: false,
      message: "Failed to update transaction",
      error: process.env.NODE_ENV === 'development' ? (error as Error).message : undefined
    });
  }
};

export const deleteTransaction = async (req: AuthRequest, res: Response): Promise<void> => {
  try {
    if (!req.user) {
      res.status(401).json({ message: "Authentication required" });
      return;
    }
    const { id } = req.params;

    const transaction = await database.getTransactionById(id);
    if (!transaction || transaction.user_id !== req.user.id) {
      res.status(404).json({ message: "Transaction not found" });
      return;
    }

    await database.deleteTransaction(id);
    res.status(204).send();
  } catch (error) {
    res.status(500).json({
      success: false,
      message: "Failed to delete transaction",
      error: process.env.NODE_ENV === 'development' ? (error as Error).message : undefined
    });
  }
};

export const processVoiceInput = async (req: AuthRequest, res: Response): Promise<void> => {
  try {
    if (!req.user) {
      res.status(401).json({ message: "Authentication required" });
      return;
    }
    const { audio_base64 } = req.body;

    // TODO: Implement voice processing logic
    // TODO: Implement actual voice processing logic
    const processedData = {
      amount: 0,
      description: "Voice input transaction",
      date: new Date().toISOString()
    };

    res.status(200).json({
      success: true,
      message: "Voice input processed successfully",
      data: processedData
    });
  } catch (error) {
    res.status(500).json({
      success: false,
      message: "Failed to process voice input",
      error: process.env.NODE_ENV === 'development' ? (error as Error).message : undefined
    });
  }
};

export const processQRCode = async (req: AuthRequest, res: Response): Promise<void> => {
  try {
    if (!req.user) {
      res.status(401).json({ message: "Authentication required" });
      return;
    }
    const { qr_data } = req.body;

    // TODO: Implement QR code processing logic
    // TODO: Implement actual QR code processing logic
    const processedData = {
      amount: 0,
      description: "QR code transaction",
      date: new Date().toISOString()
    };

    res.status(200).json({
      success: true,
      message: "QR code processed successfully",
      data: processedData
    });
  } catch (error) {
    res.status(500).json({
      success: false,
      message: "Failed to process QR code",
      error: process.env.NODE_ENV === 'development' ? (error as Error).message : undefined
    });
  }
};

export const assignTagsToTransaction = async (req: AuthRequest, res: Response): Promise<void> => {
  try {
    if (!req.user) {
      res.status(401).json({ message: "Authentication required" });
      return;
    }
    const { id } = req.params;
    const { tag_ids }: { tag_ids: string[] } = req.body;

    if (!Array.isArray(tag_ids)) {
      res.status(400).json({ success: false, message: "tag_ids must be an array" });
      return;
    }

    // First, remove existing tags for this transaction
    await database.removeAllTagsFromTransaction(id);

    // Then, assign new tags
    if (tag_ids.length > 0) {
      await database.assignTagsToTransaction(id, tag_ids);
    }

    res.status(200).json({ success: true, message: "Tags assigned successfully" });
  } catch (error: any) {
    console.error("Error assigning tags:", error);
    res.status(500).json({
      success: false,
      message: "Failed to assign tags",
      error: process.env.NODE_ENV === 'development' ? error.message : undefined
    });
  }
};
