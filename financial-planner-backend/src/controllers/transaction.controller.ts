import { Request, Response } from "express";
import { AuthRequest } from "../types/request.types";
import { genAIService } from "../services/genAI.service";

export const processReceiptOCR = async (req: AuthRequest, res: Response): Promise<void> => {
    try {
        const { image_base64, user_id } = req.body;

        // Validate request
        if (!image_base64) {
            res.status(400).json({
                success: false,
                message: "Image data is required"
            });
            return
        }

        if (!user_id) {
            res.status(400).json({
                success: false,
                message: "User ID is required"
            });
            return
        }

        // Verify user matches authenticated user
        if (req.user?.id !== user_id) {
            res.status(403).json({
                success: false,
                message: "Unauthorized access"
            });
            return
        }

        console.log(`Processing OCR for user: ${user_id}`);
        console.log(`Image data length: ${image_base64.length} characters`);

        // Process image with GenAI service
        const ocrResult = await genAIService.processReceiptOCR(image_base64);

        console.log(`OCR processing completed for user: ${user_id}`);
        console.log(`Extracted data:`, {
            amount: ocrResult.total_amount,
            merchant: ocrResult.merchant_name,
            items: ocrResult.items.length
        });

        // Return successful response
        res.status(200).json({
            success: true,
            message: "Receipt processed successfully",
            data: ocrResult
        });

    } catch (error: any) {
        console.error("Error processing receipt OCR:", error);

        res.status(500).json({
            success: false,
            message: "Internal server error while processing receipt",
            error: process.env.NODE_ENV === 'development' ? error.message : undefined
        });
        return
    }
};