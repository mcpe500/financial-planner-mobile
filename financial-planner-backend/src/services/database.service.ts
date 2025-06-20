import { createClient, SupabaseClient } from "@supabase/supabase-js";
import { config } from "../config/config";
import { UserType, UserProfileUpdatePayload } from "../types/user.types";
import { TransactionPayload, TransactionType } from "../types/transaction.types";
import { 
    Category,
    CategoryTree,
    CreateCategoryPayload,
    UpdateCategoryPayload
} from "../types/category.types";
import { DatabaseAdapter } from "./database-adapter.interface";
import { LocalStorageAdapter } from "./local-storage-adapter";

class Database {
	private static instance: Database;
	private adapter: DatabaseAdapter;

	private constructor() {
		if (config.useLocalStorage) {
			this.adapter = new LocalStorageAdapter();
		} else {
			const supabase = createClient(config.supabase.url, config.supabase.anon_key);
			this.adapter = {
				// Implement DatabaseAdapter methods using supabase
				createCategory: async (category) => {
				    const { data, error } = await supabase
				        .from('categories')
				        .insert([category])
				        .select()
				        .single();
				    if (error) throw error;
				    return data;
				},
				getCategoryTree: async (userId) => {
				    const { data, error } = await supabase
				        .from('categories')
				        .select('*')
				        .eq('user_id', userId);
				    if (error) throw error;
				    return data || [];
				},
				updateCategory: async (id, payload) => {
				    const { data, error } = await supabase
				        .from('categories')
				        .update(payload)
				        .eq('id', id)
				        .select()
				        .single();
				    if (error) throw error;
				    return data;
				},
				deleteCategory: async (id) => {
				    const { error } = await supabase
				        .from('categories')
				        .delete()
				        .eq('id', id);
				    if (error) throw error;
				},
				createTag: async (tag) => {
				    const { data, error } = await supabase
				        .from('tags')
				        .insert([tag])
				        .select()
				        .single();
				    if (error) throw error;
				    return data;
				},
				getTags: async (userId) => {
				    const { data, error } = await supabase
				        .from('tags')
				        .select('*')
				        .eq('user_id', userId);
				    if (error) throw error;
				    return data || [];
				},
				assignTagsToTransaction: async (assignments) => {
				    const { error } = await supabase
				        .from('transaction_tags')
				        .insert(assignments);
				    if (error) throw error;
				},
				
				// Transaction methods
				createTransaction: async (transaction) => {
					const { data, error } = await supabase
						.from('transactions')
						.insert([transaction])
						.select()
						.single();
					if (error) throw error;
					return data;
				},
				getTransactionById: async (id) => {
					const { data, error } = await supabase
						.from('transactions')
						.select('*')
						.eq('id', id)
						.single();
					if (error && error.code !== 'PGRST116') throw error;
					return data;
				},
				getTransactionsByUserId: async (userId) => {
					const { data, error } = await supabase
						.from('transactions')
						.select('*')
						.eq('user_id', userId);
					if (error) throw error;
					return data || [];
				},
				updateTransaction: async (id, payload) => {
					const { data, error } = await supabase
						.from('transactions')
						.update(payload)
						.eq('id', id)
						.select()
						.single();
					if (error) throw error;
					return data;
				},
				deleteTransaction: async (id) => {
					const { error } = await supabase
						.from('transactions')
						.delete()
						.eq('id', id);
					if (error) throw error;
				},
				
				// User methods
				getUsers: async () => {
					const { data, error } = await supabase
						.from('users')
						.select('*');
					if (error) throw error;
					return data || [];
				},
				createUser: async (user) => {
					const { data, error } = await supabase
						.from('users')
						.insert([user])
						.select()
						.single();
					if (error) throw error;
					return data;
				},
				updateUser: async (id, payload) => {
					const { data, error } = await supabase
						.from('users')
						.update(payload)
						.eq('id', id)
						.select()
						.single();
					if (error) throw error;
					return data;
				},
				deleteUser: async (id) => {
					const { error } = await supabase
						.from('users')
						.delete()
						.eq('id', id);
					if (error) throw error;
				}
			};
		}
	}

	public static getInstance(): Database {
		if (!Database.instance) {
			Database.instance = new Database();
		}
		return Database.instance;
	}

	public getClient(): DatabaseAdapter {
		return this.adapter;
	}

	//UserType methods
	async findUserByEmail(email: string): Promise<UserType | null> {
		const users = await this.adapter.getUsers();
		return users.find(u => u.email === email) || null;
	}

	async findUserByGoogleId(googleId: string): Promise<UserType | null> {
		const users = await this.adapter.getUsers();
		return users.find(u => u.google_id === googleId) || null;
	}

	async createUser(userData: Partial<UserType>): Promise<UserType> {
		const newUser = await this.adapter.createUser(userData);
		return newUser;
	}

	async getUserById(id: string): Promise<UserType | null> {
		const users = await this.adapter.getUsers();
		return users.find(u => u.id === id) || null;
	}
	
	async deleteUser(email: string): Promise<void> {
		const user = await this.findUserByEmail(email);
		if (!user) {
			throw new Error("User not found");
		}
		await this.adapter.deleteUser(user.id);
	}

	async updateUserProfile(userId: string, profileData: UserProfileUpdatePayload): Promise<UserType | null> {
		try {
			console.log(`Updating user profile for ID: ${userId}`);
			
			const updateData: Partial<UserType> = {
				...profileData,
				updated_at: new Date().toISOString()
			};

			const updatedUser = await this.adapter.updateUser(userId, updateData);
			console.log('User profile updated successfully:', updatedUser);
			return updatedUser;
		} catch (error) {
			console.error('Error in updateUserProfile:', error);
			throw error;
		}
	}

	// Category methods
	async createCategory(category: CreateCategoryPayload): Promise<Category> {
	    return await this.adapter.createCategory(category);
	}

	async getCategoryTree(userId: string): Promise<CategoryTree[]> {
	    return await this.adapter.getCategoryTree(userId);
	}

	async updateCategory(id: string, payload: UpdateCategoryPayload): Promise<Category> {
	    return await this.adapter.updateCategory(id, payload);
	}

	async deleteCategory(id: string): Promise<void> {
	    return await this.adapter.deleteCategory(id);
	}

	// Transaction methods
	async createTransaction(userIdOrData: string | any, payload?: TransactionPayload): Promise<TransactionType | any> {
	    try {
	        let transactionData: any;
	        
	        if (typeof userIdOrData === 'string' && payload) {
	            transactionData = {
	                ...payload,
	                user_id: userIdOrData,
	                created_at: new Date().toISOString(),
	                updated_at: new Date().toISOString(),
	                sync_status: 'pending'
	            };
	        } else {
	            transactionData = {
	                ...userIdOrData,
	                created_at: new Date().toISOString(),
	                updated_at: new Date().toISOString(),
	                sync_status: 'pending'
	            };
	        }

	        console.log('Creating transaction in database:', transactionData);
	        
	        const createdTransaction = await this.adapter.createTransaction(transactionData);
	        console.log('Transaction created successfully:', createdTransaction);
	        return createdTransaction;
	    } catch (error) {
	        console.error('Error in createTransaction:', error);
	        throw error;
	    }
	}

	async getUserTransactions(userId: string): Promise<TransactionType[]> {
		try {
			const transactions = await this.adapter.getTransactionsByUserId(userId);
			return transactions.sort((a, b) =>
				new Date(b.date).getTime() - new Date(a.date).getTime()
			) as TransactionType[];
		} catch (error) {
			console.error('Error in getUserTransactions:', error);
			throw error;
		}
	}

	async getTransactionById(id: string): Promise<TransactionType | null> {
		return await this.adapter.getTransactionById(id) as TransactionType | null;
	}

	async getTransactionsByUserId(userId: string): Promise<any[]> {
		try {
			const transactions = await this.adapter.getTransactionsByUserId(userId);
			// Sort by created_at descending if the data has that field
			return transactions.sort((a, b) => {
				const dateA = new Date(a.created_at || a.date).getTime();
				const dateB = new Date(b.created_at || b.date).getTime();
				return dateB - dateA;
			});
		} catch (error) {
			console.error('Error in getTransactionsByUserId:', error);
			throw error;
		}
	}

	async updateTransaction(transactionId: string, updateData: any): Promise<any | null> {
		try {
			const updatedTransaction = await this.adapter.updateTransaction(transactionId, updateData);
			return updatedTransaction;
		} catch (error) {
			console.error('Error in updateTransaction:', error);
			throw error;
		}
	}

	async deleteTransaction(transactionId: string): Promise<void> {
		try {
			await this.adapter.deleteTransaction(transactionId);
		} catch (error) {
			console.error('Error in deleteTransaction:', error);
			throw error;
		}
	}
}

export default Database.getInstance();

