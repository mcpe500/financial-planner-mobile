# Financial Planner Architecture Documentation

## Frontend Architecture (Jetpack Compose)

```mermaid
graph TD
    subgraph Frontend Architecture
        UI[UI Layer] --> VM[ViewModel Layer]
        VM --> Repo[Repository Layer]
        Repo --> Local[Local Data Sources]
        Repo --> Remote[Remote Data Sources]
        
        UI -->|Compose Screens| DeepLink[DeepLinkTestScreen]
        UI -->|Compose Screens| Main[MainActivity]
        UI -->|Compose Screens| Navigation[AppNavigation]
        
        VM -->|ViewModels| AuthVM[AuthViewModel]
        VM -->|ViewModels| TransVM[TransactionViewModel]
        VM -->|ViewModels| ProfileVM[UserProfileViewModel]
        
        Repo -->|Repositories| AuthRepo[AuthRepository]
        Repo -->|Repositories| TransRepo[TransactionRepository]
        Repo -->|Repositories| ProfileRepo[UserProfileRepository]
        
        Local -->|Database| Room[Room Database]
        Local -->|Preferences| Prefs[SharedPreferences]
        
        Remote -->|API| Retrofit[Retrofit API]
        Remote -->|Services| Firebase[Firebase Services]
    end
```

### Key Components:
1. **UI Layer**: Jetpack Compose screens with state hoisting
2. **ViewModel Layer**: Manages UI state and business logic
3. **Repository Layer**: Mediates between data sources
4. **Local Data**: Room DB for transactions, SharedPreferences for tokens
5. **Remote Data**: Retrofit for REST API, Firebase for auth/services

## Backend Architecture (Express.js)

```mermaid
graph LR
    subgraph Backend Architecture
        Routes[API Routes] --> Controllers[Controllers]
        Controllers --> Services[Services]
        Services --> Adapters[Database Adapters]
        
        Routes -->|Routing| AuthR[Auth Routes]
        Routes -->|Routing| TransR[Transaction Routes]
        Routes -->|Routing| ProfileR[Profile Routes]
        
        Controllers -->|Logic| AuthCtrl[AuthController]
        Controllers -->|Logic| TransCtrl[TransactionController]
        Controllers -->|Logic| ProfileCtrl[ProfileController]
        
        Services -->|Business Logic| DBS[DatabaseService]
        Services -->|Business Logic| Email[EmailService]
        Services -->|Business Logic| AI[GenAIService]
        
        Adapters -->|Database| Supabase[Supabase]
        Adapters -->|Storage| Local[LocalStorage]
    end
```

### Key Components:
1. **Routes**: REST API endpoint definitions
2. **Controllers**: Request handling and validation
3. **Services**: Business logic implementation
4. **Adapters**: Database abstraction layer

## Entity Relationship Diagram

```mermaid
erDiagram
    USER {
        string id PK
        string email
        string name
        datetime created_at
    }
    
    TRANSACTION {
        string id PK
        string user_id FK
        decimal amount
        string description
        string merchant_name
        datetime date
        string category_id
        json items
    }
    
    USER_PROFILE {
        string id PK
        string user_id FK
        json preferences
    }
    
    CATEGORY {
        string id PK
        string name
        string type
    }
    
    USER ||--o{ TRANSACTION : has
    USER ||--o| USER_PROFILE : has
    TRANSACTION }o--|| CATEGORY : belongs_to
```

### Relationships:
- One User has many Transactions
- One User has one Profile
- Transactions belong to a Category

## Integration Points
1. **Authentication Flow**
   - Token-based auth using JWT
   - TokenManager handles local storage
   - Deep linking for OAuth callbacks

2. **Data Synchronization**
   - Offline-first approach
   - Sync status tracking
   - Conflict resolution

3. **Error Handling**
   - Consistent error formats
   - Global error middleware
   - Logging and monitoring

## Next Steps
1. Implement transaction synchronization logic
2. Add category management to both frontend and backend
3. Develop reconciliation screen for data conflicts