export interface UserType {
	id: string;
	email: string;
	name: string;
	avatar_url?: string;
	created_at: string;
	updated_at: string;
	google_id?: string;
	role: "user" | "admin";
	date_of_birth?: string; // DATE field from database
	financial_goals?: string; // TEXT field from database
	monthly_income?: number; // FLOAT8 field from database
	occupation?: string; // TEXT field from database
	phone?: string; // VARCHAR field from database
}

export interface UserProfileUpdatePayload {
	name?: string;
	phone?: string;
	date_of_birth?: string;
	occupation?: string;
	monthly_income?: number;
	financial_goals?: string;
}

export interface DecodedToken {
	id: string;
	iat: number;
	exp: number;
}
