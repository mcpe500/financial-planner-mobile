import { Category, CategoryTree, CreateCategoryPayload, UpdateCategoryPayload } from "../types/category.types";
import { Tag, CreateTagPayload, TransactionTagAssignment } from "../types/tag.types";
import { UserType } from "../types/user.types";

export interface DatabaseAdapter {
	// User methods
	getUsers(): Promise<UserType[]>;
	createUser(user: Partial<UserType>): Promise<UserType>;
	updateUser(id: string, payload: Partial<UserType>): Promise<UserType>;
	deleteUser(id: string): Promise<void>;
	
	// Category methods
	createCategory(category: CreateCategoryPayload): Promise<Category>;
	getCategoryTree(): Promise<CategoryTree[]>;
	updateCategory(id: string, payload: UpdateCategoryPayload): Promise<Category>;
	deleteCategory(id: string): Promise<void>;
	
	// Tag methods
	createTag(tag: CreateTagPayload): Promise<Tag>;
	getTags(): Promise<Tag[]>;
	assignTagsToTransaction(assignment: TransactionTagAssignment[]): Promise<void>;

	// Transaction methods
	createTransaction(transaction: any): Promise<any>;
	getTransactionById(id: string): Promise<any | null>;
	getTransactionsByUserId(userId: string): Promise<any[]>;
	updateTransaction(id: string, payload: any): Promise<any>;
	deleteTransaction(id: string): Promise<void>;
}