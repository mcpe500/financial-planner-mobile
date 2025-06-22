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
		// Validate walletId if provided
		if (payload.walletId) {
			const isValidWallet = await this.validateWalletId(payload.walletId, userId);
			if (!isValidWallet) {
				throw new Error('Invalid walletId provided');
			}
		}

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

	async getTransactionByUserId(id: string): Promise<TransactionType | null> {
		const { data, error } = await this.supabase
			.from('transactions')
			.select('*')
			.eq('user_id', id)
			.single();
		if (error && error.code !== 'PGRST116') throw error;
		return data;
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

	// CATEGORY METHODS
	async createCategory(payload: any): Promise<any> {
		const { data, error } = await this.supabase
			.from('categories')
			.insert([payload])
			.select()
			.single();
		if (error) throw error;
		return data;
	}

	async updateCategory(id: string, payload: any): Promise<any> {
		const { data, error } = await this.supabase
			.from('categories')
			.update(payload)
			.eq('id', id)
			.select()
			.single();
		if (error) throw error;
		return data;
	}

	async deleteCategory(id: string): Promise<void> {
		const { error } = await this.supabase
			.from('categories')
			.delete()
			.eq('id', id);
		if (error) throw error;
	}

	async getCategoryTree(userId: string): Promise<any[]> {
		const { data, error } = await this.supabase
			.from('categories')
			.select('*')
			.eq('user_id', userId)
			.order('name', { ascending: true });
		if (error) throw error;
		return data || [];
	}

	// Wallet methods
	async getWallets(userId: string): Promise<any[]> {
		const { data, error } = await this.supabase
			.from('wallets')
			.select('*')
			.eq('user_id', userId);
		if (error) throw error;
		// Map database response to use walletId as the main ID
		return (data || []).map(wallet => ({
			...wallet,
			id: wallet.walletId || wallet.id.toString() // Use walletId as primary ID
		}));
	}

	async syncWallets(userId: string, wallets: any[]): Promise<any[]> {
		const walletsToSync = wallets.map(wallet => ({
			name: wallet.name,
			type: wallet.type,
			balance: wallet.balance,
			color_hex: wallet.color_hex,
			icon_name: wallet.icon_name,
			walletId: wallet.id, // Frontend ID becomes walletId
			user_id: userId,
			updated_at: new Date().toISOString()
		}));

		const { data, error } = await this.supabase
			.from('wallets')
			.upsert(walletsToSync, { onConflict: 'walletId' })
			.select();
		if (error) throw error;
		// Map response to use walletId as main ID
		return (data || []).map(wallet => ({
			...wallet,
			id: wallet.walletId
		}));
	}

	async createWallet(userId: string, wallet: any): Promise<any> {
		const walletData = {
			name: wallet.name,
			type: wallet.type,
			balance: wallet.balance,
			color_hex: wallet.color_hex,
			icon_name: wallet.icon_name,
			walletId: wallet.id, // Frontend ID becomes walletId
			user_id: userId,
			created_at: new Date().toISOString(),
			updated_at: new Date().toISOString()
		};

		const { data, error } = await this.supabase
			.from('wallets')
			.insert(walletData)
			.select()
			.single();
		if (error) throw error;
		// Return with walletId as main ID
		return {
			...data,
			id: data.walletId
		};
	}

	async updateWallet(userId: string, walletId: string, wallet: any): Promise<any> {
		const updateData = {
			name: wallet.name,
			type: wallet.type,
			balance: wallet.balance,
			color_hex: wallet.color_hex,
			icon_name: wallet.icon_name,
			updated_at: new Date().toISOString()
		};

		const { data, error } = await this.supabase
			.from('wallets')
			.update(updateData)
			.eq('walletId', walletId) // Use walletId for lookup
			.eq('user_id', userId)
			.select()
			.single();
		if (error) throw error;
		// Return with walletId as main ID
		return {
			...data,
			id: data.walletId
		};
	}

	async getWalletByWalletId(walletId: string): Promise<any> {
		const { data, error } = await this.supabase
			.from('wallets')
			.select('*')
			.eq('walletId', walletId)
			.single();
		if (error && error.code !== 'PGRST116') throw error;
		if (!data) return null;
		// Return with walletId as main ID
		return {
			...data,
			id: data.walletId
		};
	}

	// Helper method to validate walletId exists for transactions
	async validateWalletId(walletId: string, userId: string): Promise<boolean> {
		try {
			const { data, error } = await this.supabase
				.from('wallets')
				.select('id')
				.eq('walletId', walletId)
				.eq('user_id', userId)
				.single();
			return !error && !!data;
		} catch (error) {
			return false;
		}
	}

	async findUserById(id: string) {
		const { data, error } = await this.supabase
			.from('users')
			.select('*')
			.eq('id', id)
			.single();
		if (error) {
			console.error('Error finding user by ID:', error);
			return null;
		}
		return data;
	}

	async updateUser(id: string, updates: Record<string, any>) {
		const { data, error } = await this.supabase
			.from('users')
			.update(updates)
			.eq('id', id);
		if (error) {
			console.error('Error updating user:', error);
		}
		return { data, error };
	}

	async deleteUserById(id: string) {
		const { error } = await this.supabase
			.from('users')
			.delete()
			.eq('id', id);
		if (error) {
			console.error('Error deleting user by ID:', error);
		}
		return { error };
	}
}

const database = Database.getInstance();
export default database;