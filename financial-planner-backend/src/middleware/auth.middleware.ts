import { Response, NextFunction } from "express";
import jwt from "jsonwebtoken";
import { config } from "../config/config";
import database from "../services/database.service";
import { DecodedToken } from "../types/user.types";
import { AuthRequest } from "../types/request.types";

export const authenticate = async (
    req: AuthRequest,
    res: Response,
    next: NextFunction,
): Promise<void> => {
    try {
        // Get token from header
        const authHeader = req.headers.authorization;

        if (!authHeader || !authHeader.startsWith("Bearer ")) {
            res.status(401).json({ message: "Authentication required. Please log in." });
            return;
        }

        const token = authHeader.split(" ")[1];

        try {
            // Verify token
            const decoded = jwt.verify(token, config.jwt.secret) as DecodedToken;

            // Find user by id
            const user = await database.getUserById(decoded.id);

            if (!user) {
                res.status(401).json({ message: "User not found or token is invalid" });
                return;
            }

            // Attach user to request object
            req.user = user;
            next();
        } catch (jwtError) {
            res.status(401).json({ message: "Authentication failed. Invalid token." });
        }
    } catch (error) {
        res.status(500).json({ message: "Server error during authentication." });
    }
};

export const authorize = (...roles: string[]) => {
    return (req: AuthRequest, res: Response, next: NextFunction) => {
        if (!req.user) {
            return res.status(401).json({ message: "Authentication required" });
        }

        if (!roles.includes(req.user.role)) {
            return res
                .status(403)
                .json({ message: "You do not have permission to perform this action" });
        }

        next();
    };
};

// Export authenticate as authMiddleware for compatibility
export const authMiddleware = authenticate;
