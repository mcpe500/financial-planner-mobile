import { Router } from "express";
import { authenticate } from "../middleware/auth.middleware";
import {
  createTransaction,
  getUserTransactions,
  getTransactionById,
  processReceiptOCR,
  storeTransactionFromOCR
} from "../controllers/transaction.controller";

const router = Router();

// Transaction CRUD operations
// POST /api/transactions - Create a new transaction
router.post("/", authenticate, createTransaction);

// GET /api/transactions - Get all transactions for user
router.get("/", authenticate, getUserTransactions);

// GET /api/transactions/:id - Get transaction by ID
router.get("/:id", authenticate, getTransactionById);

// Receipt processing endpoints
// POST /api/transactions/receipt-ocr - Process receipt image for OCR
router.post("/receipt-ocr", authenticate, processReceiptOCR);

// POST /api/transactions/process - Process receipts (Android)
router.post("/process", authenticate, processReceiptOCR);

// POST /api/transactions/store - Store transaction from OCR data
router.post("/store", authenticate, storeTransactionFromOCR);

export default router;
