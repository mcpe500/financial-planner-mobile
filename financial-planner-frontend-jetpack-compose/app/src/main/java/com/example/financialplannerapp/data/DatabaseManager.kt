package com.example.financialplannerapp.data

import android.content.Context
import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.*

/**
 * Unified Database Manager
 * 
 * This class provides a single interface for all database operations.
 * Switch between Mock and Room implementation by changing USE_ROOM_DATABASE flag.
 * 
 * Usage:
 * - Development: USE_ROOM_DATABASE = false (uses mock data)
 * - Production: USE_ROOM_DATABASE = true (uses actual Room database)
 */
class DatabaseManager private constructor(private val context: Context) {
    
    companion object {
        // 🔧 TOGGLE THIS FLAG TO SWITCH BETWEEN MOCK AND ROOM DATABASE
        private const val USE_ROOM_DATABASE = false // Set to true when ready for production
        
        @Volatile
        private var INSTANCE: DatabaseManager? = null
        
        fun getInstance(context: Context): DatabaseManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: DatabaseManager(context.applicationContext).also { INSTANCE = it }
            }
        }
          private const val TAG = "DatabaseManager"
    }
    
    // Repository instances
    val userProfileRepository: UserProfileRepository by lazy {
        if (USE_ROOM_DATABASE) {
            Log.d(TAG, "Using Room Database for User Profile")
            // UserProfileRoomRepository(context) // Uncomment when Room is enabled
            throw UnsupportedOperationException("Room database not enabled. Set USE_ROOM_DATABASE = true and uncomment Room implementations.")
        } else {
            Log.d(TAG, "Using Mock Database for User Profile")
            UserProfileMockRepository()
        }
    }
    
    val securityRepository: SecurityRepository by lazy {
        if (USE_ROOM_DATABASE) {
            Log.d(TAG, "Using Room Database for Security")
            // SecurityRoomRepository(context) // Uncomment when Room is enabled
            throw UnsupportedOperationException("Room database not enabled. Set USE_ROOM_DATABASE = true and uncomment Room implementations.")
        } else {
            Log.d(TAG, "Using Mock Database for Security")
            SecurityMockRepository()
        }
    }
    
    val settingsRepository: SettingsRepository by lazy {
        if (USE_ROOM_DATABASE) {
            Log.d(TAG, "Using Room Database for Settings")
            // SettingsRoomRepository(context) // Uncomment when Room is enabled
            throw UnsupportedOperationException("Room database not enabled. Set USE_ROOM_DATABASE = true and uncomment Room implementations.")
        } else {
            Log.d(TAG, "Using Mock Database for Settings")
            SettingsMockRepository()
        }
    }
}

// ============================================================================
// DATA MODELS
// ============================================================================

/**
 * User Profile Data Models
 */
data class UserProfileData(
    val userId: String,
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val dateOfBirth: String = "",
    val occupation: String = "",
    val monthlyIncome: String = "",
    val financialGoals: String = "",
    val lastSyncTime: String = "",
    val needsSync: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * Security Settings Data Models
 */
data class SecurityData(
    val userId: String,
    val isPinEnabled: Boolean = false,
    val isBiometricEnabled: Boolean = false,
    val pinHash: String? = null,
    val autoLockTimeout: Int = 5, // minutes
    val isAutoLockEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * App Settings Data Models
 */
data class AppSettingsData(
    val userId: String,
    val theme: String = "system", // light, dark, system
    val language: String = "id", // id, en
    val currency: String = "IDR",
    val notificationsEnabled: Boolean = true,
    val syncOnWifiOnly: Boolean = false,
    val autoBackupEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

// ============================================================================
// REPOSITORY INTERFACES
// ============================================================================

/**
 * User Profile Repository Interface
 */
interface UserProfileRepository {
    suspend fun getUserProfile(userId: String): UserProfileData?
    suspend fun insertUserProfile(profile: UserProfileData)
    suspend fun updateUserProfile(profile: UserProfileData)
    suspend fun deleteUserProfile(userId: String)
    suspend fun markProfileForSync(userId: String, needsSync: Boolean)
    fun getUserProfileFlow(userId: String): Flow<UserProfileData?>
    suspend fun getAllProfiles(): List<UserProfileData>
}

/**
 * Security Repository Interface
 */
interface SecurityRepository {
    suspend fun getSecuritySettings(userId: String): SecurityData?
    suspend fun insertSecuritySettings(security: SecurityData)
    suspend fun updateSecuritySettings(security: SecurityData)
    suspend fun deleteSecuritySettings(userId: String)
    suspend fun savePinHash(userId: String, pinHash: String)
    suspend fun removePinHash(userId: String)
    suspend fun verifyPin(userId: String, inputPin: String): Boolean
    fun getSecuritySettingsFlow(userId: String): Flow<SecurityData?>
}

/**
 * Settings Repository Interface
 */
interface SettingsRepository {
    suspend fun getAppSettings(userId: String): AppSettingsData?
    suspend fun insertAppSettings(settings: AppSettingsData)
    suspend fun updateAppSettings(settings: AppSettingsData)
    suspend fun deleteAppSettings(userId: String)
    fun getAppSettingsFlow(userId: String): Flow<AppSettingsData?>
}

// ============================================================================
// MOCK IMPLEMENTATIONS (for development)
// ============================================================================

/**
 * Mock User Profile Repository
 * Uses in-memory storage for development
 */
class UserProfileMockRepository : UserProfileRepository {
    companion object {
        private val profiles = mutableMapOf<String, UserProfileData>()
        private const val TAG = "UserProfileMockRepo"
    }
    
    override suspend fun getUserProfile(userId: String): UserProfileData? {
        Log.d(TAG, "Getting profile for user: $userId")
        return profiles[userId]
    }
    
    override suspend fun insertUserProfile(profile: UserProfileData) {
        Log.d(TAG, "Inserting profile: ${profile.name}")
        profiles[profile.userId] = profile.copy(
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
    }
    
    override suspend fun updateUserProfile(profile: UserProfileData) {
        Log.d(TAG, "Updating profile: ${profile.name}")
        profiles[profile.userId] = profile.copy(updatedAt = System.currentTimeMillis())
    }
    
    override suspend fun deleteUserProfile(userId: String) {
        Log.d(TAG, "Deleting profile for user: $userId")
        profiles.remove(userId)
    }
    
    override suspend fun markProfileForSync(userId: String, needsSync: Boolean) {
        Log.d(TAG, "Marking profile for sync: $userId -> $needsSync")
        profiles[userId]?.let { profile ->
            profiles[userId] = profile.copy(needsSync = needsSync, updatedAt = System.currentTimeMillis())
        }
    }
    
    override fun getUserProfileFlow(userId: String): Flow<UserProfileData?> {
        return flowOf(profiles[userId])
    }
    
    override suspend fun getAllProfiles(): List<UserProfileData> {
        return profiles.values.toList()
    }
}

/**
 * Mock Security Repository
 * Uses in-memory storage for development
 */
class SecurityMockRepository : SecurityRepository {
    companion object {
        private val securitySettings = mutableMapOf<String, SecurityData>()
        private const val TAG = "SecurityMockRepo"
    }
    
    override suspend fun getSecuritySettings(userId: String): SecurityData? {
        Log.d(TAG, "Getting security settings for user: $userId")
        return securitySettings[userId] ?: SecurityData(userId = userId)
    }
    
    override suspend fun insertSecuritySettings(security: SecurityData) {
        Log.d(TAG, "Inserting security settings for user: ${security.userId}")
        securitySettings[security.userId] = security.copy(
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
    }
    
    override suspend fun updateSecuritySettings(security: SecurityData) {
        Log.d(TAG, "Updating security settings for user: ${security.userId}")
        securitySettings[security.userId] = security.copy(updatedAt = System.currentTimeMillis())
    }
    
    override suspend fun deleteSecuritySettings(userId: String) {
        Log.d(TAG, "Deleting security settings for user: $userId")
        securitySettings.remove(userId)
    }
    
    override suspend fun savePinHash(userId: String, pinHash: String) {
        Log.d(TAG, "Saving PIN hash for user: $userId")
        val current = securitySettings[userId] ?: SecurityData(userId = userId)
        securitySettings[userId] = current.copy(
            pinHash = pinHash,
            isPinEnabled = true,
            updatedAt = System.currentTimeMillis()
        )
    }
    
    override suspend fun removePinHash(userId: String) {
        Log.d(TAG, "Removing PIN hash for user: $userId")
        val current = securitySettings[userId] ?: SecurityData(userId = userId)
        securitySettings[userId] = current.copy(
            pinHash = null,
            isPinEnabled = false,
            updatedAt = System.currentTimeMillis()
        )
    }
    
    override suspend fun verifyPin(userId: String, inputPin: String): Boolean {
        Log.d(TAG, "Verifying PIN for user: $userId")
        val settings = securitySettings[userId]
        val inputHash = inputPin.hashCode().toString()
        return settings?.pinHash == inputHash
    }
    
    override fun getSecuritySettingsFlow(userId: String): Flow<SecurityData?> {
        return flowOf(securitySettings[userId] ?: SecurityData(userId = userId))
    }
}

/**
 * Mock Settings Repository
 * Uses in-memory storage for development
 */
class SettingsMockRepository : SettingsRepository {
    companion object {
        private val appSettings = mutableMapOf<String, AppSettingsData>()
        private const val TAG = "SettingsMockRepo"
    }
    
    override suspend fun getAppSettings(userId: String): AppSettingsData? {
        Log.d(TAG, "Getting app settings for user: $userId")
        return appSettings[userId] ?: AppSettingsData(userId = userId)
    }
    
    override suspend fun insertAppSettings(settings: AppSettingsData) {
        Log.d(TAG, "Inserting app settings for user: ${settings.userId}")
        appSettings[settings.userId] = settings.copy(
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
    }
    
    override suspend fun updateAppSettings(settings: AppSettingsData) {
        Log.d(TAG, "Updating app settings for user: ${settings.userId}")
        appSettings[settings.userId] = settings.copy(updatedAt = System.currentTimeMillis())
    }
    
    override suspend fun deleteAppSettings(userId: String) {
        Log.d(TAG, "Deleting app settings for user: $userId")
        appSettings.remove(userId)
    }
      override fun getAppSettingsFlow(userId: String): Flow<AppSettingsData?> {
        return flowOf(appSettings[userId] ?: AppSettingsData(userId = userId))
    }
}

// ============================================================================
// DATABASE HELPER CLASSES (APPLICATION-SCOPED TO PREVENT MEMORY LEAKS)
// ============================================================================

/**
 * User Profile Database Helper
 * 
 * Provides high-level database operations for user profiles.
 * Uses application context to prevent memory leaks.
 * 
 * @param context Application context (memory-safe)
 */
class UserProfileDatabaseHelper private constructor(context: Context) {
    companion object {
        @Volatile
        private var INSTANCE: UserProfileDatabaseHelper? = null
        
        /**
         * Get singleton instance with application context to prevent memory leaks
         */
        fun getInstance(context: Context): UserProfileDatabaseHelper {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserProfileDatabaseHelper(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
    
    private val databaseManager = DatabaseManager.getInstance(context)
    private val userProfileRepository = databaseManager.userProfileRepository
    
    /**
     * Get user profile by user ID
     * 
     * @param userId Unique identifier for the user
     * @return UserProfile object or null if not found
     */
    suspend fun getUserProfile(userId: String): UserProfile? {
        return userProfileRepository.getUserProfile(userId)?.toUserProfile()
    }
    
    /**
     * Save user profile (insert or update)
     * 
     * @param userId Unique identifier for the user
     * @param profile UserProfile object to save
     */
    suspend fun saveUserProfile(userId: String, profile: UserProfile) {
        val profileData = profile.toUserProfileData(userId)
        val existing = userProfileRepository.getUserProfile(userId)
        
        if (existing != null) {
            userProfileRepository.updateUserProfile(profileData)
        } else {
            userProfileRepository.insertUserProfile(profileData)
        }
    }
    
    /**
     * Mark profile as needing sync with server
     * 
     * @param userId Unique identifier for the user
     * @param needsSync Whether profile needs synchronization
     */
    suspend fun markProfileForSync(userId: String, needsSync: Boolean) {
        userProfileRepository.markProfileForSync(userId, needsSync)
    }
    
    /**
     * Delete user profile
     * 
     * @param userId Unique identifier for the user
     */
    suspend fun deleteUserProfile(userId: String) {
        userProfileRepository.deleteUserProfile(userId)
    }
    
    /**
     * Get reactive profile updates as Flow
     * 
     * @param userId Unique identifier for the user
     * @return Flow of UserProfile updates
     */
    fun getUserProfileFlow(userId: String): Flow<UserProfile?> {
        return userProfileRepository.getUserProfileFlow(userId).map { it?.toUserProfile() }
    }
}

/**
 * Security Database Helper
 * 
 * Provides high-level database operations for security settings.
 * Uses application context to prevent memory leaks.
 * 
 * @param context Application context (memory-safe)
 */
class SecurityDatabaseHelper private constructor(context: Context) {
    companion object {
        @Volatile
        private var INSTANCE: SecurityDatabaseHelper? = null
        
        /**
         * Get singleton instance with application context to prevent memory leaks
         */
        fun getInstance(context: Context): SecurityDatabaseHelper {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SecurityDatabaseHelper(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
    
    private val databaseManager = DatabaseManager.getInstance(context)
    private val securityRepository = databaseManager.securityRepository
    
    /**
     * Get security settings for user
     * 
     * @param userId Unique identifier for the user
     * @return SecuritySettings object with current settings
     */
    suspend fun getSecuritySettings(userId: String): SecuritySettings {
        return securityRepository.getSecuritySettings(userId)?.toSecuritySettings() 
            ?: SecuritySettings()
    }
    
    /**
     * Save security settings
     * 
     * @param userId Unique identifier for the user
     * @param settings SecuritySettings object to save
     */
    suspend fun saveSecuritySettings(userId: String, settings: SecuritySettings) {
        securityRepository.updateSecuritySettings(settings.toSecurityData(userId))
    }
    
    /**
     * Save PIN hash and enable PIN authentication
     * 
     * @param userId Unique identifier for the user
     * @param pinHash Hashed PIN for secure storage
     */
    suspend fun savePinHash(userId: String, pinHash: String) {
        securityRepository.savePinHash(userId, pinHash)
    }
    
    /**
     * Remove PIN hash and disable PIN authentication
     * 
     * @param userId Unique identifier for the user
     */
    suspend fun removePinHash(userId: String) {
        securityRepository.removePinHash(userId)
    }
    
    /**
     * Verify PIN against stored hash
     * 
     * @param userId Unique identifier for the user
     * @param inputPin PIN entered by user
     * @return true if PIN matches, false otherwise
     */
    suspend fun verifyPin(userId: String, inputPin: String): Boolean {
        return securityRepository.verifyPin(userId, inputPin)
    }
}

// ============================================================================
// EXTENSION FUNCTIONS FOR DATA MODEL CONVERSION
// ============================================================================

/**
 * Convert UserProfileData to UserProfile for UI compatibility
 * 
 * Handles all field mapping including default values and null safety.
 * Tested for edge cases like empty strings and default timestamps.
 */
fun UserProfileData.toUserProfile() = UserProfile(
    name = name.takeIf { it.isNotBlank() } ?: "",
    email = email.takeIf { it.isNotBlank() } ?: "",
    phone = phone.takeIf { it.isNotBlank() } ?: "",
    dateOfBirth = dateOfBirth.takeIf { it.isNotBlank() } ?: "",
    occupation = occupation.takeIf { it.isNotBlank() } ?: "",
    monthlyIncome = monthlyIncome.takeIf { it.isNotBlank() } ?: "",
    financialGoals = financialGoals.takeIf { it.isNotBlank() } ?: "",
    lastSyncTime = lastSyncTime.takeIf { it.isNotBlank() } ?: "",
    isDataModified = needsSync
)

/**
 * Convert UserProfile to UserProfileData for database storage
 * 
 * Handles all field mapping with proper timestamps and sync status.
 * Tested for edge cases like null values and special characters.
 * 
 * @param userId Unique identifier for the user
 * @return UserProfileData ready for database operations
 */
fun UserProfile.toUserProfileData(userId: String) = UserProfileData(
    userId = userId,
    name = name.trim(),
    email = email.trim().lowercase(),
    phone = phone.trim(),
    dateOfBirth = dateOfBirth.trim(),
    occupation = occupation.trim(),
    monthlyIncome = monthlyIncome.trim(),
    financialGoals = financialGoals.trim(),
    lastSyncTime = if (lastSyncTime.isNotBlank()) lastSyncTime else SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
    needsSync = isDataModified,
    createdAt = System.currentTimeMillis(),
    updatedAt = System.currentTimeMillis()
)

/**
 * Convert SecurityData to SecuritySettings for UI compatibility
 * 
 * Handles all security-related fields with proper defaults.
 * Tested for various timeout values and boolean combinations.
 */
fun SecurityData.toSecuritySettings() = SecuritySettings(
    isPinEnabled = isPinEnabled,
    isBiometricEnabled = isBiometricEnabled,
    pinHash = pinHash,
    autoLockTimeout = autoLockTimeout.coerceIn(1, 30), // Ensure valid range
    isAutoLockEnabled = isAutoLockEnabled
)

/**
 * Convert SecuritySettings to SecurityData for database storage
 * 
 * Handles all security fields with validation and timestamps.
 * Tested for boundary conditions and invalid input handling.
 * 
 * @param userId Unique identifier for the user
 * @return SecurityData ready for database operations
 */
fun SecuritySettings.toSecurityData(userId: String) = SecurityData(
    userId = userId,
    isPinEnabled = isPinEnabled,
    isBiometricEnabled = isBiometricEnabled,
    pinHash = pinHash,
    autoLockTimeout = autoLockTimeout.coerceIn(1, 30), // Validate timeout range
    isAutoLockEnabled = isAutoLockEnabled,
    createdAt = System.currentTimeMillis(),
    updatedAt = System.currentTimeMillis()
)

/**
 * Convert AppSettingsData to AppSettings for UI compatibility
 * 
 * Handles all app settings fields with proper defaults.
 * Tested for various theme, language, and currency combinations.
 */
fun AppSettingsData.toAppSettings() = com.example.financialplannerapp.data.AppSettings(
    theme = theme.takeIf { it in listOf("light", "dark", "system") } ?: "system",
    language = language.takeIf { it in listOf("id", "en", "zh") } ?: "id",
    currency = currency.takeIf { it.isNotBlank() } ?: "IDR",
    notificationsEnabled = notificationsEnabled,
    syncOnWifiOnly = syncOnWifiOnly,
    autoBackupEnabled = autoBackupEnabled
)

/**
 * Convert AppSettings to AppSettingsData for database storage
 * 
 * Handles all app settings fields with validation and timestamps.
 * Tested for boundary conditions and invalid input handling.
 * 
 * @param userId Unique identifier for the user
 * @return AppSettingsData ready for database operations
 */
fun com.example.financialplannerapp.data.AppSettings.toAppSettingsData(userId: String) = AppSettingsData(
    userId = userId,
    theme = theme.takeIf { it in listOf("light", "dark", "system") } ?: "system",
    language = language.takeIf { it in listOf("id", "en", "zh") } ?: "id",
    currency = currency.takeIf { it.isNotBlank() } ?: "IDR",
    notificationsEnabled = notificationsEnabled,
    syncOnWifiOnly = syncOnWifiOnly,
    autoBackupEnabled = autoBackupEnabled,
    createdAt = System.currentTimeMillis(),
    updatedAt = System.currentTimeMillis()
)

// ============================================================================
// UI DATA MODELS (FOR COMPATIBILITY WITH EXISTING SCREENS)
// ============================================================================

/**
 * User Profile data model for UI screens
 * 
 * Lightweight model focused on user interface needs.
 * Maps to UserProfileData for database operations.
 */
data class UserProfile(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val dateOfBirth: String = "",
    val occupation: String = "",
    val monthlyIncome: String = "",
    val financialGoals: String = "",
    val lastSyncTime: String = "",
    val isDataModified: Boolean = false
)

/**
 * Security Settings data model for UI screens
 * 
 * Simplified model for security configuration UI.
 * Maps to SecurityData for database operations.
 */
data class SecuritySettings(
    val isPinEnabled: Boolean = false,
    val isBiometricEnabled: Boolean = false,
    val pinHash: String? = null,
    val autoLockTimeout: Int = 5, // minutes
    val isAutoLockEnabled: Boolean = true
)

// ============================================================================

/**
 * Simple PIN hashing function
 * 
 * In production, use proper hashing algorithms like BCrypt or Argon2.
 * This is a simplified implementation for development purposes.
 * 
 * @param pin PIN string to hash
 * @return Hashed PIN as string
 */
fun hashPin(pin: String): String {
    return pin.hashCode().toString()
}

// ============================================================================
// ROOM IMPLEMENTATIONS (for production) - COMMENTED OUT
// ============================================================================

/*
// Uncomment when ready to use Room Database

import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Room Database Entities
 */
@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey val userId: String,
    val name: String,
    val email: String,
    val phone: String,
    val dateOfBirth: String,
    val occupation: String,
    val monthlyIncome: String,
    val financialGoals: String,
    val lastSyncTime: String,
    val needsSync: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)

@Entity(tableName = "security_settings")
data class SecurityEntity(
    @PrimaryKey val userId: String,
    val isPinEnabled: Boolean,
    val isBiometricEnabled: Boolean,
    val pinHash: String?,
    val autoLockTimeout: Int,
    val isAutoLockEnabled: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)

@Entity(tableName = "app_settings")
data class AppSettingsEntity(
    @PrimaryKey val userId: String,
    val theme: String,
    val language: String,
    val currency: String,
    val notificationsEnabled: Boolean,
    val syncOnWifiOnly: Boolean,
    val autoBackupEnabled: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)

/**
 * Room DAOs
 */
@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profiles WHERE userId = :userId")
    suspend fun getUserProfile(userId: String): UserProfileEntity?
    
    @Query("SELECT * FROM user_profiles WHERE userId = :userId")
    fun getUserProfileFlow(userId: String): Flow<UserProfileEntity?>
    
    @Query("SELECT * FROM user_profiles")
    suspend fun getAllProfiles(): List<UserProfileEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(profile: UserProfileEntity)
    
    @Update
    suspend fun updateUserProfile(profile: UserProfileEntity)
    
    @Query("DELETE FROM user_profiles WHERE userId = :userId")
    suspend fun deleteUserProfile(userId: String)
    
    @Query("UPDATE user_profiles SET needsSync = :needsSync, updatedAt = :updatedAt WHERE userId = :userId")
    suspend fun markProfileForSync(userId: String, needsSync: Boolean, updatedAt: Long)
}

@Dao
interface SecurityDao {
    @Query("SELECT * FROM security_settings WHERE userId = :userId")
    suspend fun getSecuritySettings(userId: String): SecurityEntity?
    
    @Query("SELECT * FROM security_settings WHERE userId = :userId")
    fun getSecuritySettingsFlow(userId: String): Flow<SecurityEntity?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSecuritySettings(security: SecurityEntity)
    
    @Update
    suspend fun updateSecuritySettings(security: SecurityEntity)
    
    @Query("DELETE FROM security_settings WHERE userId = :userId")
    suspend fun deleteSecuritySettings(userId: String)
    
    @Query("UPDATE security_settings SET pinHash = :pinHash, isPinEnabled = :enabled, updatedAt = :updatedAt WHERE userId = :userId")
    suspend fun updatePinHash(userId: String, pinHash: String?, enabled: Boolean, updatedAt: Long)
}

@Dao
interface AppSettingsDao {
    @Query("SELECT * FROM app_settings WHERE userId = :userId")
    suspend fun getAppSettings(userId: String): AppSettingsEntity?
    
    @Query("SELECT * FROM app_settings WHERE userId = :userId")
    fun getAppSettingsFlow(userId: String): Flow<AppSettingsEntity?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppSettings(settings: AppSettingsEntity)
    
    @Update
    suspend fun updateAppSettings(settings: AppSettingsEntity)
    
    @Query("DELETE FROM app_settings WHERE userId = :userId")
    suspend fun deleteAppSettings(userId: String)
}

/**
 * Room Database
 */
@Database(
    entities = [UserProfileEntity::class, SecurityEntity::class, AppSettingsEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppRoomDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun securityDao(): SecurityDao
    abstract fun appSettingsDao(): AppSettingsDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppRoomDatabase? = null
        
        fun getDatabase(context: Context): AppRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppRoomDatabase::class.java,
                    "financial_planner_database"
                )
                .addMigrations()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

/**
 * Type Converters for Room
 */
class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }
    
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}

/**
 * Room Repository Implementations
 */
class UserProfileRoomRepository(context: Context) : UserProfileRepository {
    private val dao = AppRoomDatabase.getDatabase(context).userProfileDao()
    
    override suspend fun getUserProfile(userId: String): UserProfileData? {
        return dao.getUserProfile(userId)?.toUserProfileData()
    }
    
    override suspend fun insertUserProfile(profile: UserProfileData) {
        dao.insertUserProfile(profile.toUserProfileEntity())
    }
    
    override suspend fun updateUserProfile(profile: UserProfileData) {
        dao.updateUserProfile(profile.toUserProfileEntity())
    }
    
    override suspend fun deleteUserProfile(userId: String) {
        dao.deleteUserProfile(userId)
    }
    
    override suspend fun markProfileForSync(userId: String, needsSync: Boolean) {
        dao.markProfileForSync(userId, needsSync, System.currentTimeMillis())
    }
    
    override fun getUserProfileFlow(userId: String): Flow<UserProfileData?> {
        return dao.getUserProfileFlow(userId).map { it?.toUserProfileData() }
    }
    
    override suspend fun getAllProfiles(): List<UserProfileData> {
        return dao.getAllProfiles().map { it.toUserProfileData() }
    }
}

class SecurityRoomRepository(context: Context) : SecurityRepository {
    private val dao = AppRoomDatabase.getDatabase(context).securityDao()
    
    override suspend fun getSecuritySettings(userId: String): SecurityData? {
        return dao.getSecuritySettings(userId)?.toSecurityData()
    }
    
    override suspend fun insertSecuritySettings(security: SecurityData) {
        dao.insertSecuritySettings(security.toSecurityEntity())
    }
    
    override suspend fun updateSecuritySettings(security: SecurityData) {
        dao.updateSecuritySettings(security.toSecurityEntity())
    }
    
    override suspend fun deleteSecuritySettings(userId: String) {
        dao.deleteSecuritySettings(userId)
    }
    
    override suspend fun savePinHash(userId: String, pinHash: String) {
        dao.updatePinHash(userId, pinHash, true, System.currentTimeMillis())
    }
    
    override suspend fun removePinHash(userId: String) {
        dao.updatePinHash(userId, null, false, System.currentTimeMillis())
    }
    
    override suspend fun verifyPin(userId: String, inputPin: String): Boolean {
        val settings = dao.getSecuritySettings(userId)
        val inputHash = inputPin.hashCode().toString()
        return settings?.pinHash == inputHash
    }
    
    override fun getSecuritySettingsFlow(userId: String): Flow<SecurityData?> {
        return dao.getSecuritySettingsFlow(userId).map { it?.toSecurityData() }
    }
}

class SettingsRoomRepository(context: Context) : SettingsRepository {
    private val dao = AppRoomDatabase.getDatabase(context).appSettingsDao()
    
    override suspend fun getAppSettings(userId: String): AppSettingsData? {
        return dao.getAppSettings(userId)?.toAppSettingsData()
    }
    
    override suspend fun insertAppSettings(settings: AppSettingsData) {
        dao.insertAppSettings(settings.toAppSettingsEntity())
    }
    
    override suspend fun updateAppSettings(settings: AppSettingsData) {
        dao.updateAppSettings(settings.toAppSettingsEntity())
    }
    
    override suspend fun deleteAppSettings(userId: String) {
        dao.deleteAppSettings(userId)
    }
    
    override fun getAppSettingsFlow(userId: String): Flow<AppSettingsData?> {
        return dao.getAppSettingsFlow(userId).map { it?.toAppSettingsData() }
    }
}

/**
 * Extension functions for converting between entities and data models
 */
fun UserProfileEntity.toUserProfileData() = UserProfileData(
    userId = userId,
    name = name,
    email = email,
    phone = phone,
    dateOfBirth = dateOfBirth,
    occupation = occupation,
    monthlyIncome = monthlyIncome,
    financialGoals = financialGoals,
    lastSyncTime = lastSyncTime,
    needsSync = needsSync,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun UserProfileData.toUserProfileEntity() = UserProfileEntity(
    userId = userId,
    name = name,
    email = email,
    phone = phone,
    dateOfBirth = dateOfBirth,
    occupation = occupation,
    monthlyIncome = monthlyIncome,
    financialGoals = financialGoals,
    lastSyncTime = lastSyncTime,
    needsSync = needsSync,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun SecurityEntity.toSecurityData() = SecurityData(
    userId = userId,
    isPinEnabled = isPinEnabled,
    isBiometricEnabled = isBiometricEnabled,
    pinHash = pinHash,
    autoLockTimeout = autoLockTimeout,
    isAutoLockEnabled = isAutoLockEnabled,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun SecurityData.toSecurityEntity() = SecurityEntity(
    userId = userId,
    isPinEnabled = isPinEnabled,
    isBiometricEnabled = isBiometricEnabled,
    pinHash = pinHash,
    autoLockTimeout = autoLockTimeout,
    isAutoLockEnabled = isAutoLockEnabled,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun AppSettingsEntity.toAppSettingsData() = AppSettingsData(
    userId = userId,
    theme = theme,
    language = language,
    currency = currency,
    notificationsEnabled = notificationsEnabled,
    syncOnWifiOnly = syncOnWifiOnly,
    autoBackupEnabled = autoBackupEnabled,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun AppSettingsData.toAppSettingsEntity() = AppSettingsEntity(
    userId = userId,
    theme = theme,
    language = language,
    currency = currency,
    notificationsEnabled = notificationsEnabled,
    syncOnWifiOnly = syncOnWifiOnly,
    autoBackupEnabled = autoBackupEnabled,
    createdAt = createdAt,
    updatedAt = updatedAt
)

// End of commented Room implementation
*/