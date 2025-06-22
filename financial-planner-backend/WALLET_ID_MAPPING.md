# Wallet ID Mapping Implementation

## Database Schema Changes Required

The `wallets` table needs to be updated with the following structure:

```sql
-- Add walletId column to existing wallets table
ALTER TABLE wallets ADD COLUMN walletId TEXT UNIQUE;

-- Create index for performance
CREATE INDEX idx_wallets_walletId ON wallets(walletId);

-- Update existing records (if any) with generated UUIDs
UPDATE wallets SET walletId = gen_random_uuid()::text WHERE walletId IS NULL;

-- Make walletId NOT NULL after updating existing records
ALTER TABLE wallets ALTER COLUMN walletId SET NOT NULL;
```

## ID Mapping Strategy

### Frontend to Backend Mapping:
- **Frontend**: Uses UUID strings as wallet IDs (e.g., "550e8400-e29b-41d4-a716-446655440000")
- **Backend Database**: 
  - `id` column: int8 (auto-increment primary key)
  - `walletId` column: text (stores frontend UUID, unique constraint)

### API Behavior:
1. **Create Wallet**: Frontend sends UUID as `id`, backend stores it as `walletId`
2. **Get Wallets**: Backend returns `walletId` as `id` field to frontend
3. **Update Wallet**: Frontend uses UUID, backend looks up by `walletId`
4. **Sync Wallets**: Uses `walletId` for upsert operations

## Updated Database Methods

### Key Changes:
- All wallet operations now use `walletId` for lookups instead of database `id`
- Response mapping ensures frontend always receives `walletId` as the `id` field
- Added validation for wallet existence in transaction creation
- Upsert operations use `walletId` as conflict resolution key

### New Methods:
- `getWalletByWalletId(walletId: string)`: Get wallet by frontend UUID
- `validateWalletId(walletId: string, userId: string)`: Validate wallet exists for user

## Transaction Integration

### Changes Made:
- Added `walletId` field to `TransactionPayload` and `TransactionType` interfaces
- Transaction creation validates `walletId` exists and belongs to user
- All transaction operations can now reference wallets by frontend UUID

### Usage:
```typescript
// Creating transaction with wallet reference
const transaction = await database.createTransaction(userId, {
  amount: 100,
  type: 'EXPENSE',
  walletId: 'frontend-uuid-here', // Frontend wallet UUID
  // ... other fields
});
```

## API Response Format

### Standardized Response Format:
```json
{
  "success": true,
  "data": [
    {
      "id": "frontend-uuid", // walletId from database
      "name": "My Wallet",
      "type": "CASH",
      "balance": 1000,
      "color_hex": "#FF5722",
      "icon_name": "wallet",
      "user_id": "user-uuid",
      "created_at": "2024-01-01T00:00:00Z",
      "updated_at": "2024-01-01T00:00:00Z"
    }
  ]
}
```

## Migration Notes

1. **Database Migration**: Run the SQL commands above to add `walletId` column
2. **Existing Data**: Update existing wallets with generated UUIDs
3. **Frontend Compatibility**: Frontend continues using UUID strings as wallet IDs
4. **Backward Compatibility**: Old API endpoints maintain same interface

## Benefits

1. **Consistent IDs**: Frontend always works with UUIDs
2. **Database Efficiency**: Internal operations use int8 primary keys
3. **Conflict Resolution**: Upsert operations work reliably with UUIDs
4. **User Isolation**: Wallet validation ensures proper user ownership
5. **Transaction Integrity**: Transactions can reference wallets safely