import { Response } from "express";
import database from "../services/database.service";
import { AuthRequest } from "../types/request.types";
import { TransactionPayload } from "../types/transaction.types";

export const createTransaction = async (req: AuthRequest, res: Response): Promise<void>  => {
  try {
    if (!req.user) {
      res.status(401).json({ message: "Authentication required" });
      return;
    }
    const payload: TransactionPayload = req.body;
    const transaction = await database.createTransaction(req.user.id, payload);
    res.status(201).json({ success: true, data: transaction });
  } catch (error) {
    res.status(500).json({ success: false, message: "Failed to create transaction", error });
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