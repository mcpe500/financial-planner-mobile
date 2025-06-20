export interface Category {
    id: string;
    name: string;
    parent_id?: string; // For sub-categories
    user_id: string;
    created_at: string;
    updated_at: string;
}

export interface CategoryTree extends Category {
    children?: CategoryTree[];
}

export interface CreateCategoryPayload {
    name: string;
    parent_id?: string;
    user_id: string;
}

export interface UpdateCategoryPayload {
    name?: string;
    parent_id?: string;
}