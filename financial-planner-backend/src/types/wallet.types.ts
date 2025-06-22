export interface WalletPayload {
  id?: string; // Frontend UUID
  name: string;
  type: string;
  balance: number;
  colorHex: string;
  iconName: string;
}

export interface WalletType {
  id: number; // Database int8 ID
  walletId: string; // Frontend UUID
  name: string;
  type: string;
  balance: number;
  colorHex: string;
  iconName: string;
  user_id: string;
  created_at: string;
  updated_at: string;
}