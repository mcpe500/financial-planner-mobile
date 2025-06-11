import { Router } from "express";
import { authenticate } from "../middleware/auth.middleware";
import { processReceiptOCR, storeTransactionFromOCR } from "../controllers/transaction.controller";

const router = Router();

// POST /api/transactions/receipt-ocr - Process receipt image for OCR
router.post("/receipt-ocr", authenticate, processReceiptOCR);

// POST /api/receipts/process - Process receipts endpoint (for Android app)
router.post("/process", authenticate, processReceiptOCR);

// POST /api/transactions/store - Store transaction from OCR data
router.post("/store", authenticate, storeTransactionFromOCR);

export default router;
