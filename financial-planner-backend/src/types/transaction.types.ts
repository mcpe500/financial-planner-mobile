export interface TransactionType {
    id: string;
    user_id: string;
    amount: number;
    type: 'expense' | 'income';
    category_id: string;
    date: string; // ISO date string
    description?: string;
    merchant_name?: string;
    location?: string;
    receipt_id?: string;
    items?: any[];
    notes?: string;
    tags?: string[];
    created_at: string;
    updated_at: string;
    sync_status: 'synced' | 'pending' | 'error';
}

export interface TransactionPayload {
    amount: number;
    type: 'expense' | 'income';
    category_id: string;
    description?: string;
    date: string; // ISO date string
    merchant_name?: string;
    location?: string;
    receipt_id?: string;
    items?: any[];
    notes?: string;
    tags?: string[];
}