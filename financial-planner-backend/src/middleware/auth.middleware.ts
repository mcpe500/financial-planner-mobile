import { Response, NextFunction } from "express";
import jwt from "jsonwebtoken";
import { config } from "../config/config";
import database from "../services/database.service";
import { DecodedToken } from "../types/user.types";
// import { AuthRequest } from "../types/request.types";

export const authenticate = async (
    req: AuthRequest,
    res: Response,
    next: NextFunction,
): Promise<Response<any, Record<string, any>> | undefined> => {
    try {
        // Get token from header
        const authHeader = req.headers.get('authorization');

        if (!authHeader || !authHeader.startsWith("Bearer ")) {
            return res
                .status(401)
                .json({ message: "Authentication required. Please log in." });
        }

        const token = authHeader.split(" ")[1];

        // Verify token
        const decoded = jwt.verify(token, config.jwt.secret) as DecodedToken;

        // Find user by id
        const user = await database.getUserById(decoded.id);

        if (!user) {
            return res
                .status(401)
                .json({ message: "User not found or token is invalid" });
        }

        // Attach user to request object
        req.user = user;
        next();
    } catch (error) {
        return res
            .status(401)
            .json({ message: "Authentication failed. Invalid token." });
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
