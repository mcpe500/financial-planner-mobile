import { GoogleGenerativeAI } from "@google/generative-ai";

export interface ReceiptOCRData {
    total_amount: number;
    merchant_name: string;
    date: string;
    items: Array<{
        name: string;
        price: number;
        quantity: number;
        category: string;
    }>;
    location: string;
    confidence: number;
    receipt_id: string;
}

export class GenAIService {
    private genAI: GoogleGenerativeAI;
    private model: any;

    constructor() {
        const apiKey = process.env.GEMINI_API_KEY;
        if (!apiKey) {
            throw new Error("GEMINI_API_KEY is not configured in environment variables");
        }        this.genAI = new GoogleGenerativeAI(apiKey);
        this.model = this.genAI.getGenerativeModel({ 
            model: "gemini-2.5-flash-preview-05-20"
        });
    }

    /**
     * Process receipt image using Gemini AI for OCR extraction
     */
    async processReceiptOCR(imageBase64: string): Promise<ReceiptOCRData> {        try {
            console.log(`Starting Gemini AI OCR processing...`);
            console.log(`Image data length: ${imageBase64.length} characters`);            // Enhanced base64 cleaning and validation
            let cleanBase64 = imageBase64;
            let mimeType = "image/jpeg"; // default

            // Remove data URL prefix if present and extract MIME type
            const dataUrlMatch = imageBase64.match(/^data:image\/([a-zA-Z]+);base64,(.+)$/);
            if (dataUrlMatch) {
                const detectedType = dataUrlMatch[1].toLowerCase();
                cleanBase64 = dataUrlMatch[2];
                
                // Map common image types to supported MIME types
                switch (detectedType) {
                    case 'jpg':
                    case 'jpeg':
                        mimeType = "image/jpeg";
                        break;
                    case 'png':
                        mimeType = "image/png";
                        break;
                    case 'webp':
                        mimeType = "image/webp";
                        break;
                    default:
                        mimeType = "image/jpeg"; // fallback
                }
                console.log(`Detected image type: ${detectedType}, using MIME type: ${mimeType}`);
            } else {
                // Try to remove any remaining prefixes
                cleanBase64 = imageBase64.replace(/^data:image\/[^;]+;base64,/, '');
            }

            // Remove only specific whitespace characters that shouldn't be in base64
            cleanBase64 = cleanBase64.replace(/[\r\n\t ]/g, '');

            console.log(`Cleaned base64 length: ${cleanBase64.length} characters`);

            // Validate base64 string
            if (!cleanBase64 || cleanBase64.length === 0) {
                console.error("Empty or invalid base64 string");
                return this.createFallbackResponse();
            }
            
            // Check if base64 is too short (likely corrupted)
            if (cleanBase64.length < 100) {
                console.error("Base64 data too short, likely corrupted");
                return this.createFallbackResponse();
            }
            
            // Check if base64 is valid format
            if (!this.validateBase64(cleanBase64)) {
                console.error("Invalid base64 format - failed validation");
                console.log(`First 100 chars: ${cleanBase64.substring(0, 100)}`);
                console.log(`Last 100 chars: ${cleanBase64.substring(cleanBase64.length - 100)}`);
                return this.createFallbackResponse();
            }
            
            console.log(`Using MIME type: ${mimeType}`);

            const prompt = `
            Analyze this receipt image and extract the following information in JSON format:

            Please provide the response in this exact JSON structure:
            {
                "total_amount": <number>,
                "merchant_name": "<string>",
                "date": "<YYYY-MM-DD format>",
                "items": [
                    {
                        "name": "<item name>",
                        "price": <number>,
                        "quantity": <number>,
                        "category": "<category>"
                    }
                ],
                "location": "<store location if available>",
                "confidence": <number between 0 and 1>,
                "receipt_id": "<generate unique id>"
            }

            Instructions:
            1. Extract the total amount as a number (not string)
            2. Extract merchant/store name
            3. Extract or infer the date in YYYY-MM-DD format (use today's date if not visible)
            4. Extract individual items with their prices, quantities, and categorize them (Food & Drink, Groceries, Retail, etc.)
            5. Extract store location if visible
            6. Provide confidence score based on image quality and text clarity
            7. Generate a unique receipt_id using timestamp

            If any information is unclear or missing, make reasonable assumptions or use default values.
            `;            const imageParts = [
                {
                    inlineData: {
                        data: cleanBase64,
                        mimeType: mimeType
                    }
                }
            ];            const result = await this.model.generateContent([prompt, ...imageParts]);
            const response = await result.response;
            const text = response.text();

            console.log(`Gemini AI response received, length: ${text.length} characters`);

            // Parse the JSON response
            let ocrData: ReceiptOCRData;
            try {
                // Clean the response text - remove any markdown formatting
                const cleanedText = text.replace(/```json\n?/g, '').replace(/```\n?/g, '').trim();
                ocrData = JSON.parse(cleanedText);
            } catch (parseError) {
                console.error("Error parsing Gemini response:", parseError);
                console.log("Raw response:", text);
                
                // Fallback to a structured response if parsing fails
                ocrData = this.createFallbackResponse();
            }

            // Validate and sanitize the response
            ocrData = this.validateAndSanitizeResponse(ocrData);

            console.log(`OCR processing completed successfully:`, {
                merchant: ocrData.merchant_name,
                total: ocrData.total_amount,
                items: ocrData.items.length,
                confidence: ocrData.confidence
            });

            return ocrData;        } catch (error: any) {
            console.error("Error in Gemini AI OCR processing:", error);
            
            // Log specific error details for debugging
            if (error.status) {
                console.error(`API Error Status: ${error.status}`);
                console.error(`API Error Message: ${error.message}`);
            }
            
            if (error.errorDetails) {
                console.error("Error Details:", JSON.stringify(error.errorDetails, null, 2));
            }
            
            // Return fallback response on error
            console.log("Returning fallback response due to error");
            return this.createFallbackResponse();
        }
    }

    /**
     * Create a fallback response when Gemini AI fails
     */
    private createFallbackResponse(): ReceiptOCRData {
        return {
            total_amount: 0.00,
            merchant_name: "Unknown Merchant",
            date: new Date().toISOString().split('T')[0],
            items: [
                {
                    name: "Unable to extract items",
                    price: 0.00,
                    quantity: 1,
                    category: "Unknown"
                }
            ],
            location: "Unknown Location",
            confidence: 0.1,
            receipt_id: `receipt_fallback_${Date.now()}`
        };
    }

    /**
     * Validate and sanitize the OCR response from Gemini
     */
    private validateAndSanitizeResponse(data: any): ReceiptOCRData {
        const sanitized: ReceiptOCRData = {
            total_amount: typeof data.total_amount === 'number' ? data.total_amount : 0.00,
            merchant_name: typeof data.merchant_name === 'string' ? data.merchant_name : "Unknown Merchant",
            date: this.validateDate(data.date),
            items: Array.isArray(data.items) ? this.validateItems(data.items) : [],
            location: typeof data.location === 'string' ? data.location : "Unknown Location",
            confidence: typeof data.confidence === 'number' ? Math.max(0, Math.min(1, data.confidence)) : 0.5,
            receipt_id: typeof data.receipt_id === 'string' ? data.receipt_id : `receipt_${Date.now()}`
        };

        // Ensure we have at least one item
        if (sanitized.items.length === 0) {
            sanitized.items.push({
                name: "Receipt Total",
                price: sanitized.total_amount,
                quantity: 1,
                category: "Unknown"
            });
        }

        return sanitized;
    }

    /**
     * Validate and format date
     */
    private validateDate(dateString: any): string {
        if (typeof dateString === 'string') {
            // Try to parse the date
            const date = new Date(dateString);
            if (!isNaN(date.getTime())) {
                return date.toISOString().split('T')[0];
            }
        }
        
        // Return today's date as fallback
        return new Date().toISOString().split('T')[0];
    }

    /**
     * Validate and sanitize items array
     */
    private validateItems(items: any[]): Array<{name: string; price: number; quantity: number; category: string}> {
        return items
            .filter(item => item && typeof item === 'object')
            .map(item => ({
                name: typeof item.name === 'string' ? item.name : "Unknown Item",
                price: typeof item.price === 'number' ? item.price : 0.00,
                quantity: typeof item.quantity === 'number' ? Math.max(1, item.quantity) : 1,
                category: typeof item.category === 'string' ? item.category : "Unknown"
            }));
    }    /**
     * Validate base64 string
     */
    private validateBase64(base64String: string): boolean {
        try {
            // Check if string is empty
            if (!base64String || base64String.length === 0) {
                return false;
            }

            // Check basic format - base64 should only contain these characters
            const base64Pattern = /^[A-Za-z0-9+/]*={0,2}$/;
            if (!base64Pattern.test(base64String)) {
                console.error("Base64 string contains invalid characters");
                return false;
            }
            
            // Check if length is valid (must be multiple of 4 after padding)
            if (base64String.length % 4 !== 0) {
                console.error(`Base64 string length (${base64String.length}) is not a multiple of 4`);
                return false;
            }
            
            // Try to decode it to verify it's valid
            try {
                if (typeof Buffer !== 'undefined') {
                    const decoded = Buffer.from(base64String, 'base64');
                    // Check if the decoded data makes sense for an image (should be reasonably large)
                    if (decoded.length < 50) {
                        console.error(`Decoded base64 data too small: ${decoded.length} bytes`);
                        return false;
                    }
                    return true;
                } else {
                    // Fallback for environments without Buffer
                    return true;
                }
            } catch (decodeError) {
                console.error("Failed to decode base64:", decodeError);
                return false;
            }
            
        } catch (error) {
            console.error("Error validating base64:", error);
            return false;
        }
    }

    /**
     * Check if the service is properly configured
     */
    static isConfigured(): boolean {
        return !!process.env.GEMINI_API_KEY;
    }
}

// Export a singleton instance
export const genAIService = new GenAIService();