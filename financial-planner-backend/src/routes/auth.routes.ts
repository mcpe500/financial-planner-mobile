import { RequestHandler, Router } from "express";
import passport from "passport";
import { authenticate } from "../middleware/auth.middleware";
import { getCurrentUser, googleAuth, googleCallback, logout } from "../controllers/auth.controller";

const router = Router();

// Google OAuth routes
router.get("/google", googleAuth);
router.get(
	"/google/callback", 
	passport.authenticate("google", { session: false, failureRedirect: "/login" }),
	googleCallback
);

// Get current user
router.get("/me", authenticate, getCurrentUser);

// Logout endpoint
router.post("/logout", authenticate, logout);

export default router;