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
        res.json(wallets);
        return;
    } catch (error) {
        console.error('Error fetching wallets:', error);
        res.status(500).json({ error: "Failed to fetch wallets" });
        return;
    }
};

export const syncWallets = async (req: AuthRequest, res: Response) => {
    try {
        const userId = req.user?.id;
        if (!userId) {
            res.status(401).json({ error: "Unauthorized" });
            return;
        }

        const wallets = req.body;
        if (!Array.isArray(wallets)) {
            res.status(400).json({ error: "Invalid wallet data" });
            return;
        }

        const updatedWallets = await database.syncWallets(userId, wallets);
        res.json(updatedWallets);
        return;
    } catch (error) {
        console.error('Error syncing wallets:', error);
        res.status(500).json({ error: "Failed to sync wallets" });
        return;
    }
};

export const createWallet = async (req: AuthRequest, res: Response) => {
    try {
        const userId = req.user?.id;
        if (!userId) {
            res.status(401).json({ error: "Unauthorized" });
            return;
        }

        const wallet = req.body;
        if (!wallet) {
            res.status(400).json({ error: "Invalid wallet data" });
            return;
        }

        const newWallet = await database.createWallet(userId, wallet);
        res.status(201).json(newWallet);
        return;
    } catch (error) {
        console.error('Error creating wallet:', error);
        res.status(500).json({ error: "Failed to create wallet" });
        return;
    }
};

export const updateWallet = async (req: AuthRequest, res: Response) => {
    try {
        const userId = req.user?.id;
        if (!userId) {
            res.status(401).json({ error: "Unauthorized" });
            return;
        }

        const walletId = req.params.id;
        const wallet = req.body;
        if (!wallet) {
            res.status(400).json({ error: "Invalid wallet data" });
            return;
        }

        const updatedWallet = await database.updateWallet(userId, walletId, wallet);
        res.json(updatedWallet);
        return;
    } catch (error) {
        console.error('Error updating wallet:', error);
        res.status(500).json({ error: "Failed to update wallet" });
        return;
    }
};
