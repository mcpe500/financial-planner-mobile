import { Router } from "express";
import { Request, Response } from "express";
import authRoutes from "./auth.routes";

const router = Router();

// Health check endpoint
router.get("/health", (req: Request, res: Response) => {
	res.status(200).json({ status: "ok", message: "Server is running" });
});

// Base routes
router.get("/", (req: Request, res: Response) => {
	res.json({ message: "Welcome to Financial Planner API" });
});

// API routes
router.use("/api/auth", authRoutes);

export default router;
