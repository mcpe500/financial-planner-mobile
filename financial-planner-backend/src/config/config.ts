import dotenv from "dotenv";

// Initialize dotenv
dotenv.config();

// Define config interface
interface Config {
    server: {
        port: number;
        nodeEnv: string;
    };
    jwt: {
        secret: string;
        expiresIn: string;
    };
    supabase: {
        url: string;
        anon_key: string;
    };
    google: {
        clientId: string;
        clientSecret: string;
        callbackUrl: string;
    };
    email: {
        host: string;
        port: number;
        secure: boolean;
        user: string;
        password: string;
    };
    app: {
        url: string;
    };
}

// Config object
export const config: Config = {
    server: {
        port: parseInt(process.env.PORT || "3000", 10),
        nodeEnv: process.env.NODE_ENV || "development",
    },
    jwt: {
        secret: process.env.JWT_SECRET || "your-secret-key",
        expiresIn: process.env.JWT_EXPIRES_IN || "7d",
    },
    supabase: {
        url: process.env.SUPABASE_URL || "",
        anon_key: process.env.SUPABASE_ANON_KEY || "",
    },
    google: {
        clientId: process.env.GOOGLE_CLIENT_ID || "",
        clientSecret: process.env.GOOGLE_CLIENT_SECRET || "",
        callbackUrl: process.env.GOOGLE_CALLBACK_URL || "",
    },
    email: {
        host: process.env.EMAIL_HOST || "smtp.gmail.com",
        port: parseInt(process.env.EMAIL_PORT || "587", 10),
        secure: process.env.EMAIL_SECURE === "true",
        user: process.env.EMAIL_USER || "",
        password: process.env.EMAIL_PASSWORD || "",
    },
    app: {
        url: process.env.APP_URL || "http://localhost:3000",
    },
};
