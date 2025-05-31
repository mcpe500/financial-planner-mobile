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

	async updateUserProfile(userId: string, profileData: any): Promise<any> {
		try {
			console.log(`Updating user profile for ID: ${userId}`);
			
			// Prepare update data - map frontend fields to database columns
			const updateData: any = {};
			
			if (profileData.name) updateData.name = profileData.name;
			if (profileData.phone) updateData.phone = profileData.phone;
			if (profileData.dateOfBirth) updateData.date_of_birth = profileData.dateOfBirth;
			if (profileData.occupation) updateData.occupation = profileData.occupation;
			if (profileData.monthlyIncome) updateData.monthly_income = profileData.monthlyIncome;
			if (profileData.financialGoals) updateData.financial_goals = profileData.financialGoals;
			
			// Add updated timestamp
			updateData.updated_at = new Date().toISOString();

			const { data, error } = await this.supabase
				.from('users')
				.update(updateData)
				.eq('id', userId)
				.select()
				.single();

			if (error) {
				console.error('Supabase error updating user profile:', error);
				throw error;
			}

			if (!data) {
				console.log('No user found with ID:', userId);
				return null;
			}

			console.log('User profile updated successfully:', data);
			return data;
		} catch (error) {
			console.error('Error in updateUserProfile:', error);
			throw error;
		}
	}
}

export default Database.getInstance();
