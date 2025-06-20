export interface Category {
    id: string;
    name: string;
    parent_id?: string;
}

export interface CategoryTree extends Category {
    children?: CategoryTree[];
}

export interface CreateCategoryPayload {
    name: string;
    parent_id?: string;
}

export interface UpdateCategoryPayload {
    name: string;
}