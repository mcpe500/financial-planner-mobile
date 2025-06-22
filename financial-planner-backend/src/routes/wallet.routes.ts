import { Router } from "express";
import { 
    getWallets, 
    createWallet, 
    updateWallet, 
    syncWallets 
} from "../controllers/wallet.controller";
import { authMiddleware } from "../middleware/auth.middleware";

const router = Router();
router.use(authMiddleware);

// Get all wallets for authenticated user
router.get("/", getWallets);

// Create new wallet
router.post("/", createWallet);

// Update existing wallet
router.put("/:id", updateWallet);

// Sync wallets
router.post("/sync", syncWallets);

export default router;