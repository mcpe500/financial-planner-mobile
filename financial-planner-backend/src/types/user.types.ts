export interface UserType {
	id: string;
	email: string;
	name: string;
	avatar_url?: string;
	created_at: string;
	updated_at: string;
	google_id?: string;
	role: "user" | "admin";
}

export interface DecodedToken {
	id: string;
	iat: number;
	exp: number;
}
