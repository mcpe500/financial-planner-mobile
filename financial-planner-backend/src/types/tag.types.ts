export interface Tag {
    id: string;
    name: string;
    user_id: string;
    created_at: string;
    updated_at: string;
}

export interface CreateTagPayload {
    name: string;
    user_id: string;
}

export interface UpdateTagPayload {
    name?: string;
}

export interface TransactionTagAssignment {
    transaction_id: string;
    tag_id: string;
}