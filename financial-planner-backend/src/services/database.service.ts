import { createClient } from "@supabase/supabase-js";
import { config } from "../config/config";
import { UserType, UserProfileUpdatePayload } from "../types/user.types";
import { TransactionPayload, TransactionType } from "../types/transaction.types";

class Database {
	private static instance: Database;
	private supabase;

	private constructor() {
		this.supabase = createClient(config.supabase.url, config.supabase.anon_key);
	}

	public static getInstance(): Database {
		if (!Database.instance) {
			Database.instance = new Database();
		}
		return Database.instance;
	}

	// User methods
	async findUserByEmail(email: string): Promise<UserType | null> {
		const { data, error } = await this.supabase.from('users').select('*').eq('email', email).single();
		if (error && error.code !== 'PGRST116') throw error;
		return data;
	}

	async findUserByGoogleId(googleId: string): Promise<UserType | null> {
		const { data, error } = await this.supabase.from('users').select('*').eq('google_id', googleId).single();
		if (error && error.code !== 'PGRST116') throw error;
		return data;
	}

	async createUser(userData: { email: string; name: string; google_id: string; avatar_url: string; role?: "user" | "admin"; }): Promise<UserType> {
		const { data, error } = await this.supabase.from('users').insert([userData]).select().single();
		if (error) throw error;
		return data;
	}

	async getUserById(id: string): Promise<UserType | null> {
		const { data, error } = await this.supabase.from('users').select('*').eq('id', id).single();
		if (error && error.code !== 'PGRST116') throw error;
		return data;
	}
	
	async deleteUser(email: string): Promise<void> {
		const user = await this.findUserByEmail(email);
		if (!user) {
			throw new Error("User not found");
		}
		const { error } = await this.supabase.from('users').delete().eq('id', user.id);
		if (error) throw error;
	}

	async updateUserProfile(userId: string, profileData: UserProfileUpdatePayload): Promise<UserType | null> {
		try {
			const updateData: Partial<UserType> = {
				...profileData,
				updated_at: new Date().toISOString()
			};

			const { data, error } = await this.supabase.from('users').update(updateData).eq('id', userId).select().single();
			if (error) throw error;
			return data;
		} catch (error) {
			console.error('Error in updateUserProfile:', error);
			throw error;
		}
	}

	// Transaction methods
	async createTransaction(userId: string, payload: TransactionPayload): Promise<TransactionType> {
		const { data, error } = await this.supabase
			.from('transactions')
			.insert([{ ...payload, user_id: userId }])
			.select()
			.single();
		if (error) throw error;
		return data;
	}

	async getUserTransactions(userId: string): Promise<TransactionType[]> {
		const { data, error } = await this.supabase
			.from('transactions')
			.select('*')
			.eq('user_id', userId)
			.order('date', { ascending: false });
		if (error) throw error;
		return data || [];
	}

	async getTransactionById(id: string): Promise<TransactionType | null> {
		const { data, error } = await this.supabase
			.from('transactions')
			.select('*')
			.eq('id', id)
			.single();
		if (error && error.code !== 'PGRST116') throw error;
		return data;
	}
	
	async updateTransaction(transactionId: string, updateData: Partial<TransactionPayload>): Promise<TransactionType | null> {
		const { data, error } = await this.supabase
			.from('transactions')
			.update(updateData)
			.eq('id', transactionId)
			.select()
			.single();
		if (error) throw error;
		return data;
	}
	
	async deleteTransaction(transactionId: string): Promise<void> {
		const { error } = await this.supabase
			.from('transactions')
			.delete()
			.eq('id', transactionId);
		if (error) throw error;
	}

	// Tag methods for transaction controller
	async assignTagsToTransaction(transactionId: string, tagIds: string[]): Promise<void> {
		const assignments = tagIds.map(tagId => ({ transaction_id: transactionId, tag_id: tagId }));
		const { error } = await this.supabase.from('transaction_tags').insert(assignments);
		if (error) throw error;
	}

	async removeAllTagsFromTransaction(transactionId: string): Promise<void> {
		const { error } = await this.supabase.from('transaction_tags').delete().eq('transaction_id', transactionId);
		if (error) throw error;
	}
}

const database = Database.getInstance();
export default database;