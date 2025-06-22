import { Request, Response, RequestHandler } from "express";
import { AuthRequest } from "../types/request.types";
import database from "../services/database.service";

export const getWallets = async (req: AuthRequest, res: Response) => {
    try {
        const userId = req.user?.id;
        if (!userId) {
            res.status(401).json({ error: "Unauthorized" });
            return;
        }

        const wallets = await database.getWallets(userId);
        res.json({ success: true, data: wallets });
        return;
    } catch (error) {
        console.error('Error fetching wallets:', error);
        res.status(500).json({ success: false, error: "Failed to fetch wallets" });
        return;
    }
};

export const syncWallets = async (req: AuthRequest, res: Response) => {
    try {
        const userId = req.user?.id;
        if (!userId) {
            res.status(401).json({ success: false, error: "Unauthorized" });
            return;
        }

        const wallets = req.body;
        if (!Array.isArray(wallets)) {
            res.status(400).json({ success: false, error: "Invalid wallet data - must be array" });
            return;
        }

        // Validate each wallet has required fields
        for (const wallet of wallets) {
            if (!wallet.id || !wallet.name) {
                res.status(400).json({ success: false, error: "Each wallet must have id and name" });
                return;
            }
        }

        const updatedWallets = await database.syncWallets(userId, wallets);
        res.json({ success: true, data: updatedWallets });
        return;
    } catch (error) {
        console.error('Error syncing wallets:', error);
        res.status(500).json({ success: false, error: "Failed to sync wallets" });
        return;
    }
};

export const createWallet = async (req: AuthRequest, res: Response) => {
    try {
        const userId = req.user?.id;
        if (!userId) {
            res.status(401).json({ success: false, error: "Unauthorized" });
            return;
        }

        const wallet = req.body;
        if (!wallet || !wallet.id || !wallet.name) {
            res.status(400).json({ success: false, error: "Wallet must have id and name" });
            return;
        }

        // Check if wallet with this walletId already exists
        const existingWallet = await database.getWalletByWalletId(wallet.id);
        if (existingWallet) {
            res.status(409).json({ success: false, error: "Wallet with this ID already exists" });
            return;
        }

        const newWallet = await database.createWallet(userId, wallet);
        res.status(201).json({ success: true, data: newWallet });
        return;
    } catch (error) {
        console.error('Error creating wallet:', error);
        res.status(500).json({ success: false, error: "Failed to create wallet" });
        return;
    }
};

export const updateWallet = async (req: AuthRequest, res: Response) => {
    try {
        const userId = req.user?.id;
        if (!userId) {
            res.status(401).json({ success: false, error: "Unauthorized" });
            return;
        }

        const walletId = req.params.id; // This is the walletId (UUID from frontend)
        const wallet = req.body;
        if (!wallet || !wallet.name) {
            res.status(400).json({ success: false, error: "Wallet must have name" });
            return;
        }

        // Check if wallet exists
        const existingWallet = await database.getWalletByWalletId(walletId);
        if (!existingWallet) {
            res.status(404).json({ success: false, error: "Wallet not found" });
            return;
        }

        const updatedWallet = await database.updateWallet(userId, walletId, wallet);
        res.json({ success: true, data: updatedWallet });
        return;
    } catch (error) {
        console.error('Error updating wallet:', error);
        res.status(500).json({ success: false, error: "Failed to update wallet" });
        return;
    }
};
