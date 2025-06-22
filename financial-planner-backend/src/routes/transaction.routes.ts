import { Router } from "express";
import { authenticate } from "../middleware/auth.middleware";
import {
  createTransaction,
  getUserTransactions,
  getTransactionById,
  updateTransaction,
  deleteTransaction,
  processReceiptOCR,
  storeTransactionFromOCR,
  processVoiceInput,
  processQRCode,
  assignTagsToTransaction
} from "../controllers/transaction.controller";

const router = Router();

// Transaction CRUD operations
// POST /api/transactions - Create a new transaction
router.post("/", authenticate, createTransaction);

// GET /api/transactions - Get all transactions for user
router.get("/", authenticate, getUserTransactions);

// GET /api/transactions/:id - Get transaction by ID
router.get("/:id", authenticate, getTransactionById);

// PUT /api/transactions/:id - Update transaction
router.put("/:id", authenticate, updateTransaction);

// DELETE /api/transactions/:id - Delete transaction
router.delete("/:id", authenticate, deleteTransaction);

// Tag assignment endpoint
// POST /api/transactions/:id/tags - Assign tags to transaction
router.post("/:id/tags", authenticate, assignTagsToTransaction);

// Input method endpoints
// POST /api/transactions/ocr - Process receipt via OCR
router.post("/ocr", authenticate, processReceiptOCR);

// POST /api/transactions/speech - Process voice input
router.post("/speech", authenticate, processVoiceInput);

// POST /api/transactions/qr - Process QR code
router.post("/qr", authenticate, processQRCode);

// Legacy endpoints (for compatibility)
router.post("/receipt-ocr", authenticate, processReceiptOCR);
router.post("/process", authenticate, processReceiptOCR);
router.post("/store", authenticate, storeTransactionFromOCR);

export default router;
