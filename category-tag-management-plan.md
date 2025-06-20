# Category and Tag Management Implementation Plan

## Overview
This document outlines the implementation plan for adding dynamic category and tag management to the financial planner application. The system will support both SQLite and Supabase databases, with unlimited category hierarchy and flexible tagging.

## Database Schema

### Categories Table
```sql
CREATE TABLE categories (
    id UUID PRIMARY KEY,
    name TEXT NOT NULL,
    parent_id UUID REFERENCES categories(id)
);
```

### Tags Table
```sql
CREATE TABLE tags (
    id UUID PRIMARY KEY,
    name TEXT NOT NULL
);
```

### Transaction Tags Join Table
```sql
CREATE TABLE transaction_tags (
    transaction_id UUID REFERENCES transactions(id),
    tag_id UUID REFERENCES tags(id),
    PRIMARY KEY (transaction_id, tag_id)
);
```

## API Endpoints

### Category Endpoints
- `POST /categories` - Create new category
- `GET /categories` - Get category hierarchy
- `PUT /categories/:id` - Update category
- `DELETE /categories/:id` - Delete category

### Tag Endpoints
- `POST /tags` - Create new tag
- `GET /tags` - List all tags
- `POST /transactions/:id/tags` - Assign tags to transaction

## Service Layer

### CategoryService
- `createCategory(name: string, parentId?: string)`
- `getCategoryTree()`
- `updateCategory(id: string, name: string)`
- `deleteCategory(id: string)`

### TagService
- `createTag(name: string)`
- `getTags()`
- `assignTagsToTransaction(transactionId: string, tagIds: string[])`

## Dual Database Support
The system will support both SQLite and Supabase through a database adapter pattern:

```typescript
interface DatabaseAdapter {
    createCategory(category: Category): Promise<Category>;
    getCategoryTree(): Promise<CategoryTree>;
    // ... other methods
}
```

Implementation steps:
1. Create abstract base adapter
2. Implement SQLiteAdapter
3. Implement SupabaseAdapter
4. Use environment variable to select adapter

## Integration with Transactions
- Update Transaction model to support multiple tags
- Add tag assignment functionality to transaction creation/update
- Include tags in transaction retrieval

## Next Steps
1. Review and approve this plan
2. Switch to Code mode for implementation