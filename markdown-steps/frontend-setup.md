## Frontend Setup Steps

### 1. Room Database
**TransactionEntity.kt**:
```kotlin
@Entity(tableName = "transactions")
data class TransactionEntity(
  // ... updated properties
)
```

**TransactionDao.kt**:
```kotlin
@Dao
interface TransactionDao {
  // ... CRUD operations
}
```

### 2. Repository & ViewModel
**TransactionRepository.kt**:
```kotlin
class TransactionRepository(...) {
  // ... local and remote operations
  suspend fun syncPendingTransactions() {...}
}
```

**TransactionViewModel.kt**:
```kotlin
class TransactionViewModel(...) : ViewModel() {
  // ... observable state
  fun addTransaction(transaction: TransactionEntity) {...}
}
```

### 3. UI Components
**TransactionHistoryScreen.kt**:
```kotlin
@Composable
fun TransactionHistoryScreen(viewModel: TransactionViewModel) {
  // LazyColumn with transaction items
}
```

**AddTransactionScreen.kt**:
```kotlin
@Composable
fun AddTransactionScreen(navController: NavController, ...) {
  // Form for new transactions
}
```

### 4. Navigation
**AppNavigation.kt**:
```kotlin
composable("transactions") { ... }
composable("add_transaction") { ... }
composable("scan_receipt") { ... }