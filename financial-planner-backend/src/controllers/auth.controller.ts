import { Request, Response } from "express";
import passport from "passport";
import jwt, { Secret } from "jsonwebtoken"; // Import Secret type
import { config } from "../config/config";
import { User } from "../types/user.types";

// Generate JWT token
const generateToken = (id: string): string => {
    const secret = config.jwt.secret as Secret; // Cast to Secret type
    if (!secret) {
        console.error("JWT Secret is not defined in config!");
        throw new Error("JWT signing failed: Secret not configured.");
    }
    const payload = { id };
    const token = jwt.sign(payload, secret, { expiresIn: config.jwt.expiresIn, });
    return token;
};

// Google authentication
export const googleAuth = passport.authenticate("google", {
    scope: ["profile", "email"],
    session: false,
});

// Google callback
export const googleCallback = (req: Request, res: Response) => {
    try {
        const user = req.user as User;

        if (!user) {
            return res.status(401).json({ message: "Authentication failed" });
        }

        // Generate token
        const token = generateToken(user.id);

        // For mobile apps, you can redirect to a custom URL scheme
        // or show a page with the token that the app can retrieve

        // Option 1: Redirect to a custom URL scheme (your Android app needs to handle this)
        // res.redirect(`yourapp://auth?token=${token}`);

        // Option 2: Show a page with the token
        res.send(`
			<html>
				<head>
					<title>Authentication Successful</title>
					<meta name="viewport" content="width=device-width, initial-scale=1.0">
					<style>
						body { font-family: Arial, sans-serif; text-align: center; padding: 20px; }
						.token-container { margin: 20px 0; padding: 10px; background: #f5f5f5; border-radius: 5px; word-break: break-all; }
					</style>
				</head>
				<body>
					<h2>Authentication Successful</h2>
					<p>Please copy this token and paste it in your app:</p>
					<div class="token-container">${token}</div>
					<p>You can now close this window and return to the app.</p>
				</body>
			</html>
		`);
    } catch (error: any) {
        res.status(500).json({ message: error.message });
    }
};

// Get current user
export const getCurrentUser = (req: Request, res: Response) => {
    try {
        res.status(200).json({ user: req.user });
    } catch (error: any) {
        res.status(500).json({ message: error.message });
    }
};
