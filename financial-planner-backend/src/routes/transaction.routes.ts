import { Router } from "express";
import {
	createTransaction,
	deleteTransaction,
	getTransactionById,
	getUserTransactions,
	updateTransaction,
	processReceiptOCR,
	storeTransactionFromOCR,
	assignTagsToTransaction,
	processVoiceInput,
	processQRCode
} from "../controllers/transaction.controller";
import { authMiddleware } from "../middleware/auth.middleware";

const router = Router();

router.use(authMiddleware);

router.post("/receipt-ocr", processReceiptOCR);
router.post("/from-ocr", storeTransactionFromOCR);
router.post("/voice", processVoiceInput);
router.post("/qr-code", processQRCode);

router.route("/")
	.get(getUserTransactions)
	.post(createTransaction);

router.route("/:id")
	.get(getTransactionById)
	.put(updateTransaction)
	.delete(deleteTransaction);

router.post("/:id/tags", assignTagsToTransaction);


export default router;
