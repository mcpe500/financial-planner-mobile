export interface Tag {
    id: string;
    name: string;
}

export interface CreateTagPayload {
    name: string;
}

export interface TransactionTagAssignment {
    transaction_id: string;
    tag_id: string;
}