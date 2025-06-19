import { Router } from "express";
import { createTransaction, getUserTransactions, getTransactionById } from "../controllers/transaction.controller";
import { authenticate } from "../middleware/auth.middleware";

const router = Router();

router.post("/", authenticate, createTransaction);
router.get("/", authenticate, getUserTransactions);
router.get("/:id", authenticate, getTransactionById);

export default router; 