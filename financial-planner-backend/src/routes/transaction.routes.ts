import { Router } from "express";
import { authenticate } from "../middleware/auth.middleware";
import { processReceiptOCR } from "../controllers/transaction.controller";

const router = Router();

// POST /api/transactions/receipt-ocr - Process receipt image for OCR
router.post("/receipt-ocr", authenticate, processReceiptOCR);


export default router;
