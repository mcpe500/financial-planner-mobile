import { Request, Response } from "express";
import passport from "passport";
import jwt, { Secret, SignOptions } from "jsonwebtoken"; // Import Secret and SignOptions types
import { config } from "../config/config";
import { UserType } from "../types/user.types";
import { AuthRequest } from "../types/request.types";

// Generate JWT token
const generateToken = (id: string): string => {
    const secret = config.jwt.secret as Secret;
    if (!secret) {
        console.error("JWT Secret is not defined in config!");
        throw new Error("JWT signing failed: Secret not configured.");
    }
    const expiresIn = config.jwt.expiresIn as number | string;
    const payload = { id };
    const options: SignOptions = { expiresIn: expiresIn as any };
    return jwt.sign(payload, secret, options);
};

// Google authentication
export const googleAuth = passport.authenticate("google", {
    scope: ["profile", "email"],
    session: false,
});

// Google callback
export const googleCallback = (req: Request, res: Response): void => {
    try {
        const user = req.user as UserType;

        if (!user) {
            res.status(401).json({ message: "Authentication failed" });
            return;
        }

        // Generate token
        const token = generateToken(user.id);

        // For mobile apps, redirect to a custom URL scheme that your Android app can handle
        res.redirect(`finplanner://auth?token=${token}`);

        // Alternatively, use a JSON response if your app prefers that
        // res.json({ token });
    } catch (error) {
        console.error("Authentication callback error:", error);
        res.status(500).json({ message: "Authentication failed" });
    }
};

// Get current user info endpoint
export const getCurrentUser = async (req: AuthRequest, res: Response) => {
    try {
        // User is attached by authenticate middleware
        res.json({ user: req.user });
    } catch (error) {
        console.error("Get current user error:", error);
        res.status(500).json({ message: "Failed to get user information" });
    }
};