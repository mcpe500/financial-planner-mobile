export interface TransactionType {
  id: string;
  user_id: string;
  amount: number;
  type: 'expense' | 'income';
  category: string;
  description?: string;
  date: string; // ISO date string
  created_at: string;
  updated_at: string;
  merchant_name?: string;
  location?: string;
  receipt_id?: string;
  items?: any[];
  notes?: string;
}

export interface TransactionPayload {
  amount: number;
  type: 'expense' | 'income';
  category: string;
  description?: string;
  date: string; // ISO date string
  merchant_name?: string;
  location?: string;
  receipt_id?: string;
  items?: any[];
  notes?: string;
}