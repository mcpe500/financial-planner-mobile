import { createClient, SupabaseClient } from "@supabase/supabase-js";
import { config } from "../config/config";
import { User } from "../types/user.types";

class Database {
	private static instance: Database;
	private supabase: SupabaseClient;

	private constructor() {
		this.supabase = createClient(config.supabase.url, config.supabase.key);
	}

	public static getInstance(): Database {
		if (!Database.instance) {
			Database.instance = new Database();
		}
		return Database.instance;
	}

	public getClient(): SupabaseClient {
		return this.supabase;
	}

	// User methods
	async findUserByEmail(email: string): Promise<User | null> {
		const { data, error } = await this.supabase
			.from("users")
			.select("*")
			.eq("email", email)
			.single();

		if (error && error.code !== "PGRST116") {
			throw error;
		}

		return data as User | null;
	}

	async findUserByGoogleId(googleId: string): Promise<User | null> {
		const { data, error } = await this.supabase
			.from("users")
			.select("*")
			.eq("google_id", googleId)
			.single();

		if (error && error.code !== "PGRST116") {
			throw error;
		}

		return data as User | null;
	}

	async createUser(userData: Partial<User>): Promise<User> {
		const { data, error } = await this.supabase
			.from("users")
			.insert([userData])
			.select()
			.single();

		if (error) {
			throw error;
		}

		return data as User;
	}

	async getUserById(id: string): Promise<User | null> {
		const { data, error } = await this.supabase
			.from("users")
			.select("*")
			.eq("id", id)
			.single();

		if (error) {
			throw error;
		}

		return data as User | null;
	}
}

export default Database.getInstance();
