import dotenv from "dotenv";

// Load environment variables
dotenv.config();

export const config = {
	server: {
		port: process.env.PORT || 3000,
		nodeEnv: process.env.NODE_ENV || "development",
	},
	supabase: {
		url: process.env.SUPABASE_URL || "",
		anon_key: process.env.SUPABASE_ANON_KEY || "",
	},
	google: {
		clientId: process.env.GOOGLE_CLIENT_ID || "",
		clientSecret: process.env.GOOGLE_CLIENT_SECRET || "",
		callbackUrl:
			process.env.GOOGLE_CALLBACK_URL ||
			"http://localhost:3000/api/auth/google/callback",
	},
	jwt: {
		secret: process.env.JWT_SECRET || "your-secret-key",
		expiresIn: process.env.JWT_EXPIRES_IN || "7d",
	},
};
