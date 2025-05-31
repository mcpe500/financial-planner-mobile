import { Router, Request, Response } from "express";
import authRoutes from "./auth.routes";
import accountRoutes from "./account.routes";
import profileRoutes from "./profile.routes";
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
apiRouter.use("/api/auth", authRoutes);
apiRouter.use("/api/profile", profileRoutes);
// apiRouter.use("/api/account", accountApiRoutes);

// Web routes
apiRouter.use("/account", accountRoutes);

export default apiRouter;
