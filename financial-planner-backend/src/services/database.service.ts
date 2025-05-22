import { createClient, SupabaseClient } from "@supabase/supabase-js";
import { config } from "../config/config";
import { UserType } from "../types/user.types";

class Database {
	private static instance: Database;
	private supabase: SupabaseClient;

	private constructor() {
		this.supabase = createClient(config.supabase.url, config.supabase.anon_key);
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

	//UserType methods
	async findUserByEmail(email: string): Promise<UserType | null> {
		const { data, error } = await this.supabase
			.from("users")
			.select("*")
			.eq("email", email)
			.single();

		if (error && error.code !== "PGRST116") {
			throw error;
		}

		return data as UserType | null;
	}

	async findUserByGoogleId(googleId: string): Promise<UserType | null> {
		const { data, error } = await this.supabase
			.from("users")
			.select("*")
			.eq("google_id", googleId)
			.single();

		if (error && error.code !== "PGRST116") {
			throw error;
		}

		return data as UserType | null;
	}

	async createUser(userData: Partial<UserType>): Promise<UserType> {
		const { data, error } = await this.supabase
			.from("users")
			.insert([userData])
			.select()
			.single();

		if (error) {
			throw error;
		}

		return data as UserType;
	}

	async getUserById(id: string): Promise<UserType | null> {
		const { data, error } = await this.supabase
			.from("users")
			.select("*")
			.eq("id", id)
			.single();

		if (error) {
			throw error;
		}

		return data as UserType | null;
	}
	
	async deleteUser(email: string): Promise<void> {
		// First, check if the user exists
		const user = await this.findUserByEmail(email);
		
		if (!user) {
			throw new Error("User not found");
		}
		
		// Delete the user
		const { error } = await this.supabase
			.from("users")
			.delete()
			.eq("email", email);
		
		if (error) {
			throw error;
		}
	}
}

export default Database.getInstance();
