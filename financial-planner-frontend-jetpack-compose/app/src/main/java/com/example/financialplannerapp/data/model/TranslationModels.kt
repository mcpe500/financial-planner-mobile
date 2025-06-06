package com.example.financialplannerapp.data.model

import kotlinx.coroutines.flow.StateFlow
import java.util.Locale

// Enum for all translation keys used in the application
enum class Translations {
    // General UI
    AppName, Back, Save, Cancel, Delete, Edit, Add, Yes, No, Error, Success, Loading, Close, Search, NoResults,

    // Settings Screen Titles & General Settings Terms
    Settings, // General "Settings" title
    SettingsTitle, // Title for the main settings screen
    AppSettings, // For "App Settings" specifically

    // Profile Settings
    SettingsProfile, SettingsProfileDesc,

    // Account Settings
    SettingsAccount, SettingsAccountDesc,

    // Appearance/Theme Settings
    SettingsAppearance, SettingsAppearanceDesc,
    ThemeSetting, ThemeSettingDesc,
    ThemeLight, ThemeDark, ThemeSystem,

    // Language Settings
    LanguageSetting, LanguageSettingDesc,
    // Specific languages might be listed here or handled differently, e.g., English, Spanish

    // Currency Settings
    CurrencySetting, CurrencySettingDesc,
    // Specific currencies might be listed here or handled differently, e.g., USD, EUR

    // Notification Settings
    SettingsNotifications, SettingsNotificationsDesc,
    NotificationsSetting, NotificationsSettingDesc,

    // Security Settings
    SettingsSecurity, SettingsSecurityDesc,

    // Privacy Settings
    SettingsPrivacy, SettingsPrivacyDesc,

    // About Section
    SettingsAbout, SettingsAboutDesc,

    // Logout
    SettingsLogout, SettingsLogoutDesc,

    // Login Screen
    LoginButton, LoginFailed, UsernameLabel, PasswordLabel,

    // Token Entry Screen
    EnterTokenPrompt, SubmitTokenButton, InvalidToken,

    // Bill Management
    AddRecurringBillTitle, BillNameLabel, EstimatedAmountLabel, DueDateLabel, RepeatCycleLabel,
    CategoryLabel, NotesLabel, AutoPayLabel, NotificationEnabledLabel,
    RepeatCycleDaily, RepeatCycleWeekly, RepeatCycleMonthly, RepeatCycleQuarterly, RepeatCycleYearly,
    BillCalendarTitle,
    BillStatusPaid, BillStatusOverdue, BillStatusDueSoon, BillStatusUpcoming,
    TotalForDate, NoBillsForDate, ViewBillDetails,

    // Common Actions
    Confirm, AreYouSure,

    // Validation Messages
    FieldRequired, InvalidEmailFormat, PasswordTooShort,

    // User Profile
    UserProfileTitle, UserDetails, ChangePassword,

    // Fallback for missing keys
    MissingTranslation
}

// Represents the current state of translations in the app
data class TranslationState(
    val currentLanguage: String, // e.g., "en", "es", "fr"
    val translationsMap: Map<String, String>, // Map of translation key (.name) to its string value
    val availableLanguages: List<LanguageOption> = listOf(
        LanguageOption("en", "English"),
        LanguageOption("es", "Español"),
        // Add other supported languages here
    )
)

data class LanguageOption(val code: String, val displayName: String)

// Interface for providing translation services
interface TranslationProvider {
    fun translate(key: Translations, vararg args: Any): String
    fun translate(key: String, vararg args: Any): String // For dynamic keys or keys not in enum

    val translationStateFlow: StateFlow<TranslationState?> // To observe changes in language or translations
    fun changeLanguage(languageCode: String)
}
