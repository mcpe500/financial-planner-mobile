import { Request, Response } from "express";
import { AuthRequest } from "../types/request.types";

// Mock OCR service - replace with actual OCR service integration
class MockOCRService {
    static async processReceipt(imageBase64: string): Promise<any> {
        // Simulate processing time
        await new Promise(resolve => setTimeout(resolve, 2000));

        // Mock OCR response
        return {
            total_amount: Math.round((Math.random() * 100 + 10) * 100) / 100,
            merchant_name: this.getRandomMerchant(),
            date: new Date().toISOString().split('T')[0],
            items: [
                {
                    name: "Coffee",
                    price: 4.50,
                    quantity: 1,
                    category: "Food & Drink"
                },
                {
                    name: "Sandwich",
                    price: 8.99,
                    quantity: 1,
                    category: "Food & Drink"
                }
            ],
            location: "Downtown Store",
            confidence: 0.95,
            receipt_id: `receipt_${Date.now()}`
        };
    }

    private static getRandomMerchant(): string {
        const merchants = [
            "Starbucks",
            "McDonald's",
            "Target",
            "Walmart",
            "CVS Pharmacy",
            "Whole Foods",
            "Best Buy",
            "Home Depot"
        ];
        return merchants[Math.floor(Math.random() * merchants.length)];
    }
}

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

        // Process image with OCR service
        const ocrResult = await MockOCRService.processReceipt(image_base64);

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