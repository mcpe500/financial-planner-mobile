import { 
    Category,
    CategoryTree,
    CreateCategoryPayload,
    UpdateCategoryPayload
} from "../types/category.types";
import { 
    Tag,
    CreateTagPayload,
    TransactionTagAssignment
} from "../types/tag.types";
import { UserType } from "../types/user.types";
import { TransactionType } from "../types/transaction.types";

export interface DatabaseAdapter {
    // Category methods
    createCategory(category: CreateCategoryPayload): Promise<Category>;
    getCategoryTree(userId: string): Promise<CategoryTree[]>;
    updateCategory(id: string, payload: UpdateCategoryPayload): Promise<Category>;
    deleteCategory(id: string): Promise<void>;

    // Tag methods
    createTag(tag: CreateTagPayload): Promise<Tag>;
    getTags(userId: string): Promise<Tag[]>;
    assignTagsToTransaction(assignments: TransactionTagAssignment[]): Promise<void>;

    // Transaction methods
    createTransaction(transaction: any): Promise<any>;
    getTransactionById(id: string): Promise<any | null>;
    getTransactionsByUserId(userId: string): Promise<any[]>;
    updateTransaction(id: string, payload: any): Promise<any>;
    deleteTransaction(id: string): Promise<void>;

    // User methods
    getUsers(): Promise<UserType[]>;
    createUser(user: Partial<UserType>): Promise<UserType>;
    updateUser(id: string, payload: Partial<UserType>): Promise<UserType>;
    deleteUser(id: string): Promise<void>;
}