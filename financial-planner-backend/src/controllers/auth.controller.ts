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
            console.error("No user found in request after authentication");
            res.status(401).json({ message: "Authentication failed" });
            return;
        }

        // Generate token
        const token = generateToken(user.id);
        
        console.log(`User ${user.email} authenticated successfully, generating token`);

        // Create the deep link URL
        const mobileUrl = `finplanner://auth?token=${token}&userId=${user.id}&email=${encodeURIComponent(user.email || '')}&name=${encodeURIComponent(user.name || '')}`;
        
        console.log(`Direct redirect to: ${mobileUrl}`);
        
        // Direct redirect - NO HTML
        res.redirect(302, mobileUrl);
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

// Logout endpoint (for token blacklisting if needed)
export const logout = async (req: AuthRequest, res: Response) => {
    try {
        // Since we're using JWT tokens, we just need to tell the client to discard the token
        // In a more secure implementation, you might want to blacklist the token
        res.json({ message: "Logged out successfully" });
    } catch (error) {
        console.error("Logout error:", error);
        res.status(500).json({ message: "Logout failed" });
    }
};