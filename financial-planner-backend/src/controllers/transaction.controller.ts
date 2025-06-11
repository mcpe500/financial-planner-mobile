import { Request, Response } from "express";
import { AuthRequest } from "../types/request.types";
import { genAIService } from "../services/genAI.service";
import database from "../services/database.service";

export const processReceiptOCR = async (req: AuthRequest, res: Response): Promise<void> => {
    try {
        const { image_base64, user_id } = req.body;

        // Validate request
        if (!image_base64) {
            res.status(400).json({
                success: false,
                message: "Image data is required"
            });
            return
        }

        if (!user_id) {
            res.status(400).json({
                success: false,
                message: "User ID is required"
            });
            return
        }

        // Verify user matches authenticated user
        if (req.user?.id !== user_id) {
            res.status(403).json({
                success: false,
                message: "Unauthorized access"
            });
            return
        }

        console.log(`Processing OCR for user: ${user_id}`);
        console.log(`Image data length: ${image_base64.length} characters`);

        // Process image with GenAI service
        const ocrResult = await genAIService.processReceiptOCR(image_base64);

        console.log(`OCR processing completed for user: ${user_id}`);
        console.log(`Extracted data:`, {
            amount: ocrResult.total_amount,
            merchant: ocrResult.merchant_name,
            items: ocrResult.items.length
        });

        // Return successful response
        res.status(200).json({
            success: true,
            message: "Receipt processed successfully",
            data: ocrResult
        });

    } catch (error: any) {
        console.error("Error processing receipt OCR:", error);

        res.status(500).json({
            success: false,
            message: "Internal server error while processing receipt",
            error: process.env.NODE_ENV === 'development' ? error.message : undefined
        });
        return
    }
};

/**
 * Store transaction from OCR data
 */
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
        }

        console.log(`Storing transaction from OCR for user: ${user_id}`);
        console.log(`Transaction data:`, {
            amount: total_amount,
            merchant: merchant_name,
            items: items?.length || 0
        });

        // Create transaction record in database
        const transactionData = {
            user_id: user_id,
            amount: total_amount,
            description: `${merchant_name} - Receipt transaction`,
            merchant_name: merchant_name,
            date: date || new Date().toISOString(),
            location: location || null,
            receipt_id: receipt_id || `receipt_${Date.now()}`,
            category: category || 'General',
            notes: notes || null,
            items: items ? JSON.stringify(items) : null,
            transaction_type: total_amount >= 0 ? 'income' : 'expense',
            created_at: new Date().toISOString(),
            updated_at: new Date().toISOString()
        };

        // Store in Supabase
        const storedTransaction = await database.createTransaction(transactionData);

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