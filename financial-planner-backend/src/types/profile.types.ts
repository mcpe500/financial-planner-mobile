export interface UserProfileUpdatePayload {
  name: string;
  phone?: string;
  dateOfBirth?: string; // Expected format: YYYY-MM-DD or DD/MM/YYYY
  occupation?: string;
  monthlyIncome?: string; // Will be stored as string for now, can be converted to number if needed
  financialGoals?: string;
}

export interface UserProfileResponse {
  id: string;
  name: string;
  email: string;
  phone?: string;
  dateOfBirth?: string;
  occupation?: string;
  monthlyIncome?: string;
  financialGoals?: string;
  updatedAt: string;
}