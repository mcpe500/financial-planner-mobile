import { RequestHandler, Router } from "express";
import passport from "passport";
// import { googleAuth, googleCallback, getCurrentUser } from "../controllers/auth.controller";
import { authenticate } from "../middleware/auth.middleware";
import { getCurrentUser, googleAuth, googleCallback } from "../controllers/auth.controller";

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

export default router;