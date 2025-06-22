import { Router, Request, Response } from "express";
import authRoutes from "./auth.routes";
import accountRoutes from "./account.routes";
import profileRoutes from "./profile.routes";
import transactionRoutes from "./transaction.routes";
import walletRoutes from "./wallet.routes";
import { healthCheck } from '../controllers/health.controller';
// import accountApiRoutes from "./account-api.routes";

const apiRouter = Router();

// Health check endpoint
apiRouter.get("/health", (_req: Request, res: Response) => {
	res.status(200).json({ status: "ok", message: "Server is running" });
});

// Base routes
apiRouter.get("/", (_req: Request, res: Response) => {
	res.json({ message: "Welcome to Financial Planner API" });
});

// API routes
apiRouter.use("/api/v1/auth", authRoutes);
apiRouter.use("/api/v1/profile", profileRoutes);
apiRouter.use("/api/v1/transactions", transactionRoutes);
apiRouter.use("/api/v1/receipts", transactionRoutes);
apiRouter.use("/api/v1/wallets", walletRoutes);
apiRouter.use("/api/v1/account", accountRoutes);

// Legacy API routes (keep for backward compatibility)
apiRouter.use("/api/auth", authRoutes);
apiRouter.use("/api/profile", profileRoutes);
apiRouter.use("/api/transactions", transactionRoutes);
apiRouter.use("/api/receipts", transactionRoutes);
apiRouter.use("/api/account", accountRoutes);

// Web routes
apiRouter.use("/account", accountRoutes);

// Health check endpoint
apiRouter.get('/api/health', healthCheck);

export default apiRouter;
