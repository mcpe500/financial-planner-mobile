export interface TransactionType {
    id: string;
    user_id: string;
    amount: number;
    type: 'INCOME' | 'EXPENSE';
    date: string; // ISO date string
    pocket: string; // Cash, Bank, E-Wallet, Credit Card
    category: string;
    note?: string;
    tags?: string[];
    // Receipt-related fields
    is_from_receipt: boolean;
    receipt_id?: string;
    merchant_name?: string;
    location?: string;
    receipt_image_path?: string;
    receipt_confidence?: number;
    receipt_items?: any[]; // JSON array of receipt items
    // Sync fields
    is_synced: boolean;
    backend_transaction_id?: string;
    created_at: string;
    updated_at: string;
}

export interface TransactionPayload {
    amount: number;
    type: 'INCOME' | 'EXPENSE';
    date: string; // ISO date string
    pocket: string;
    category: string;
    note?: string;
    tags?: string[];
    // Receipt-related fields (optional)
    is_from_receipt?: boolean;
    receipt_id?: string;
    merchant_name?: string;
    location?: string;
    receipt_image_path?: string;
    receipt_confidence?: number;
    receipt_items?: any[];
}

export interface ReceiptOCRResult {
    total_amount: number;
    merchant_name: string;
    date?: string;
    location?: string;
    items: ReceiptItem[];
    confidence: number;
}

export interface ReceiptItem {
    name: string;
    quantity?: number;
    price: number;
    category?: string;
}

export interface TransactionFilter {
    start_date?: string;
    end_date?: string;
    type?: 'INCOME' | 'EXPENSE';
    category?: string;
    pocket?: string;
    tags?: string[];
    is_from_receipt?: boolean;
    limit?: number;
    offset?: number;
}