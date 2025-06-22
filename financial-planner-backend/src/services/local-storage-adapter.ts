import { DatabaseAdapter } from "./database-adapter.interface";
import {
    Category,
    CreateCategoryPayload,
    UpdateCategoryPayload,
    CategoryTree
} from "../types/category.types";
import {
    Tag,
    CreateTagPayload,
    TransactionTagAssignment
} from "../types/tag.types";
import { UserType } from "../types/user.types";

export class LocalStorageAdapter implements DatabaseAdapter {
    private readonly CATEGORY_KEY = 'categories';
    private readonly TAG_KEY = 'tags';
    private readonly TAG_ASSIGNMENT_KEY = 'tag_assignments';
    private readonly USER_KEY = 'users';

    // Category methods
    async createCategory(category: CreateCategoryPayload): Promise<Category> {
        const categories = this.getCategories();
        const newCategory: Category = {
            id: crypto.randomUUID(),
            ...category,
            created_at: new Date().toISOString(),
            updated_at: new Date().toISOString()
        };
        categories.push(newCategory);
        this.saveCategories(categories);
        return newCategory;
    }

    async getCategoryTree(): Promise<CategoryTree[]> {
        const categories = this.getCategories();
        const categoryMap = new Map<string, CategoryTree>();
        const roots: CategoryTree[] = [];

        // Create map of all categories
        categories.forEach(cat => {
            categoryMap.set(cat.id, { ...cat });
        });

        // Build tree structure
        categoryMap.forEach(cat => {
            if (cat.parent_id) {
                const parent = categoryMap.get(cat.parent_id);
                if (parent) {
                    if (!parent.children) {
                        parent.children = [];
                    }
                    parent.children.push(cat);
                }
            } else {
                roots.push(cat);
            }
        });

        return roots;
    }

    async updateCategory(id: string, payload: UpdateCategoryPayload): Promise<Category> {
        const categories = this.getCategories();
        const index = categories.findIndex(c => c.id === id);
        if (index === -1) {
            throw new Error('Category not found');
        }
        categories[index] = { ...categories[index], ...payload };
        this.saveCategories(categories);
        return categories[index];
    }

    async deleteCategory(id: string): Promise<void> {
        let categories = this.getCategories();
        categories = categories.filter(c => c.id !== id);
        this.saveCategories(categories);
    }

    // Tag methods
    async createTag(tag: CreateTagPayload): Promise<Tag> {
        const tags = await this.getTags();
        const newTag: Tag = {
            id: crypto.randomUUID(),
            ...tag,
            created_at: new Date().toISOString(),
            updated_at: new Date().toISOString()
        };
        tags.push(newTag);
        this.saveTags(tags);
        return newTag;
    }

    async batchAssignTagsToTransactions(assignments: TransactionTagAssignment[]): Promise<void> {
        const existingAssignments = this.getTagAssignments();
        assignments.forEach(assignment => {
            if (!existingAssignments.some(a =>
                a.transaction_id === assignment.transaction_id &&
                a.tag_id === assignment.tag_id
            )) {
                existingAssignments.push(assignment);
            }
        });
        this.saveTagAssignments(existingAssignments);
    }

    async assignTagsToTransaction(transactionId: string, tagIds: string[]): Promise<void> {
        const assignments = tagIds.map(tagId => ({
            transaction_id: transactionId,
            tag_id: tagId
        }));
        await this.batchAssignTagsToTransactions(assignments);
    }

    async removeAllTagsFromTransaction(transactionId: string): Promise<void> {
        let assignments = this.getTagAssignments();
        assignments = assignments.filter(a => a.transaction_id !== transactionId);
        this.saveTagAssignments(assignments);
    }
   
    // User methods
    async getUsers(): Promise<UserType[]> {
    	return this.getUsersFromStorage();
    }
   
    async createUser(user: Partial<UserType>): Promise<UserType> {
    	const users = await this.getUsersFromStorage();
    	const newUser: UserType = {
    		id: crypto.randomUUID(),
    		email: user.email || '',
    		name: user.name || '',
    		role: 'user',
    		...user,
    		created_at: new Date().toISOString(),
    		updated_at: new Date().toISOString()
    	};
    	users.push(newUser);
    	this.saveUsers(users);
    	return newUser;
    }
   
    async updateUser(id: string, payload: Partial<UserType>): Promise<UserType> {
    	const users = await this.getUsersFromStorage();
    	const index = users.findIndex(u => u.id === id);
    	if (index === -1) {
    		throw new Error('User not found');
    	}
    	users[index] = {
    		...users[index],
    		...payload,
    		updated_at: new Date().toISOString()
    	};
    	this.saveUsers(users);
    	return users[index];
    }
   
    async deleteUser(id: string): Promise<void> {
    	let users = await this.getUsersFromStorage();
    	users = users.filter(u => u.id !== id);
    	this.saveUsers(users);
    }
   
    // User helper methods
    private getUsersFromStorage(): UserType[] {
    	const data = localStorage.getItem(this.USER_KEY);
    	return data ? JSON.parse(data) : [];
    }
   
    private saveUsers(users: UserType[]): void {
    	localStorage.setItem(this.USER_KEY, JSON.stringify(users));
    }
   
    // Transaction methods
    private readonly TRANSACTION_KEY = 'transactions';
   
    async createTransaction(transaction: any): Promise<any> {
    	const transactions = this.getTransactions();
    	const newTransaction = {
    		...transaction,
    		id: crypto.randomUUID(),
    		created_at: new Date().toISOString()
    	};
    	transactions.push(newTransaction);
    	this.saveTransactions(transactions);
    	return newTransaction;
    }
   
    async getTransactionById(id: string): Promise<any | null> {
    	const transactions = this.getTransactions();
    	return transactions.find(t => t.id === id) || null;
    }
   
    async getTransactionsByUserId(userId: string): Promise<any[]> {
    	const transactions = this.getTransactions();
    	return transactions.filter(t => t.user_id === userId);
    }
   
    async updateTransaction(id: string, payload: any): Promise<any> {
    	const transactions = this.getTransactions();
    	const index = transactions.findIndex(t => t.id === id);
    	if (index === -1) {
    		throw new Error('Transaction not found');
    	}
    	transactions[index] = {
    		...transactions[index],
    		...payload,
    		updated_at: new Date().toISOString()
    	};
    	this.saveTransactions(transactions);
    	return transactions[index];
    }
   
    async deleteTransaction(id: string): Promise<void> {
    	let transactions = this.getTransactions();
    	transactions = transactions.filter(t => t.id !== id);
    	this.saveTransactions(transactions);
    }
   
    // Transaction helper methods
    private getTransactions(): any[] {
    	const data = localStorage.getItem(this.TRANSACTION_KEY);
    	return data ? JSON.parse(data) : [];
    }
   
    private saveTransactions(transactions: any[]): void {
    	localStorage.setItem(this.TRANSACTION_KEY, JSON.stringify(transactions));
    }

    // Helper methods
    private getCategories(): Category[] {
        const data = localStorage.getItem(this.CATEGORY_KEY);
        return data ? JSON.parse(data) : [];
    }

    private saveCategories(categories: Category[]): void {
        localStorage.setItem(this.CATEGORY_KEY, JSON.stringify(categories));
    }

    async getTags(): Promise<Tag[]> {
        const data = localStorage.getItem(this.TAG_KEY);
        return data ? JSON.parse(data) : [];
    }

    private saveTags(tags: Tag[]): void {
        localStorage.setItem(this.TAG_KEY, JSON.stringify(tags));
    }

    private getTagAssignments(): TransactionTagAssignment[] {
        const data = localStorage.getItem(this.TAG_ASSIGNMENT_KEY);
        return data ? JSON.parse(data) : [];
    }

    private saveTagAssignments(assignments: TransactionTagAssignment[]): void {
        localStorage.setItem(this.TAG_ASSIGNMENT_KEY, JSON.stringify(assignments));
    }
}