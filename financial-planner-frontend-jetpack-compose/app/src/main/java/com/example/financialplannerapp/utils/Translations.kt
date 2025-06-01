package com.example.financialplannerapp.utils

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext

/**
 * Translation System for Financial Planner App
 * 
 * Provides complete translations for Indonesian, English, and Chinese languages.
 * Includes reactive translation updates when language changes.
 */
object Translations {
    
    /**
     * Translation Keys - Type-safe access to translation strings
     */
    sealed class Key(val value: String) {
        // App Settings
        object AppSettings : Key("app_settings")
        object Back : Key("back")
        object ThemeSetting : Key("theme_setting")
        object ThemeSettingDesc : Key("theme_setting_desc")
        object ThemeLight : Key("theme_light")
        object ThemeDark : Key("theme_dark")
        object ThemeSystem : Key("theme_system")
        object ThemeChangedTo : Key("theme_changed_to")
        
        // Language Settings
        object LanguageSetting : Key("language_setting")
        object LanguageSettingDesc : Key("language_setting_desc")
        object SelectLanguage : Key("select_language")
        object LanguageChangedTo : Key("language_changed_to")
        
        // Currency Settings
        object CurrencySetting : Key("currency_setting")
        object CurrencySettingDesc : Key("currency_setting_desc")
        object SelectCurrency : Key("select_currency")
        object CurrencyIdr : Key("currency_idr")
        object CurrencyUsd : Key("currency_usd")
        object CurrencyEur : Key("currency_eur")
        object CurrencyJpy : Key("currency_jpy")
        object CurrencyChangedTo : Key("currency_changed_to")
        
        // Notification Settings
        object NotificationsSetting : Key("notifications_setting")
        object NotificationsSettingDesc : Key("notifications_setting_desc")
        object NotificationsEnabled : Key("notifications_enabled")
        object NotificationsDisabled : Key("notifications_disabled")
        
        // Common UI Elements
        object Save : Key("save")
        object Cancel : Key("cancel")
        object Settings : Key("settings")
        object Profile : Key("profile")
        object Dashboard : Key("dashboard")
        object Loading : Key("loading")
        object Error : Key("error")
        object Success : Key("success")
        
        // Menu Items
        object PersonalProfile : Key("personal_profile")
        object Security : Key("security")
        object AppInfo : Key("app_info")
        object Logout : Key("logout")
    }
    
    /**
     * Complete Translation Dictionary
     * Supports Indonesian (id), English (en), and Chinese (zh)
     */
    private val translations = mapOf(
        "id" to mapOf( // Indonesian
            "app_settings" to "Pengaturan Aplikasi",
            "back" to "Kembali",
            "theme_setting" to "Tema",
            "theme_setting_desc" to "Pilih tema tampilan aplikasi",
            "theme_light" to "Terang",
            "theme_dark" to "Gelap",
            "theme_system" to "Sistem",
            "theme_changed_to" to "Tema diubah ke",
            
            "language_setting" to "Bahasa",
            "language_setting_desc" to "Pilih bahasa aplikasi",
            "select_language" to "Pilih Bahasa",
            "language_changed_to" to "Bahasa diubah ke",
            
            "currency_setting" to "Mata Uang",
            "currency_setting_desc" to "Pilih mata uang default",
            "select_currency" to "Pilih Mata Uang",
            "currency_idr" to "Rupiah Indonesia",
            "currency_usd" to "Dolar AS",
            "currency_eur" to "Euro",
            "currency_jpy" to "Yen Jepang",
            "currency_changed_to" to "Mata uang diubah ke",
            
            "notifications_setting" to "Notifikasi",
            "notifications_setting_desc" to "Aktifkan atau nonaktifkan notifikasi",
            "notifications_enabled" to "Notifikasi diaktifkan",
            "notifications_disabled" to "Notifikasi dinonaktifkan",
            
            "save" to "Simpan",
            "cancel" to "Batal",
            "settings" to "Pengaturan",
            "profile" to "Profil",
            "dashboard" to "Beranda",
            "loading" to "Memuat...",
            "error" to "Kesalahan",
            "success" to "Berhasil",
            
            "personal_profile" to "Profil Pribadi",
            "security" to "Keamanan",
            "app_info" to "Informasi Aplikasi",
            "logout" to "Keluar"
        ),
        
        "en" to mapOf( // English
            "app_settings" to "App Settings",
            "back" to "Back",
            "theme_setting" to "Theme",
            "theme_setting_desc" to "Choose app display theme",
            "theme_light" to "Light",
            "theme_dark" to "Dark",
            "theme_system" to "System",
            "theme_changed_to" to "Theme changed to",
            
            "language_setting" to "Language",
            "language_setting_desc" to "Choose app language",
            "select_language" to "Select Language",
            "language_changed_to" to "Language changed to",
            
            "currency_setting" to "Currency",
            "currency_setting_desc" to "Choose default currency",
            "select_currency" to "Select Currency",
            "currency_idr" to "Indonesian Rupiah",
            "currency_usd" to "US Dollar",
            "currency_eur" to "Euro",
            "currency_jpy" to "Japanese Yen",
            "currency_changed_to" to "Currency changed to",
            
            "notifications_setting" to "Notifications",
            "notifications_setting_desc" to "Enable or disable notifications",
            "notifications_enabled" to "Notifications enabled",
            "notifications_disabled" to "Notifications disabled",
            
            "save" to "Save",
            "cancel" to "Cancel",
            "settings" to "Settings",
            "profile" to "Profile",
            "dashboard" to "Dashboard",
            "loading" to "Loading...",
            "error" to "Error",
            "success" to "Success",
            
            "personal_profile" to "Personal Profile",
            "security" to "Security",
            "app_info" to "App Information",
            "logout" to "Logout"
        ),
        
        "zh" to mapOf( // Chinese
            "app_settings" to "应用设置",
            "back" to "返回",
            "theme_setting" to "主题",
            "theme_setting_desc" to "选择应用显示主题",
            "theme_light" to "浅色",
            "theme_dark" to "深色",
            "theme_system" to "系统",
            "theme_changed_to" to "主题已更改为",
            
            "language_setting" to "语言",
            "language_setting_desc" to "选择应用语言",
            "select_language" to "选择语言",
            "language_changed_to" to "语言已更改为",
            
            "currency_setting" to "货币",
            "currency_setting_desc" to "选择默认货币",
            "select_currency" to "选择货币",
            "currency_idr" to "印尼盾",
            "currency_usd" to "美元",
            "currency_eur" to "欧元",
            "currency_jpy" to "日元",
            "currency_changed_to" to "货币已更改为",
            
            "notifications_setting" to "通知",
            "notifications_setting_desc" to "启用或禁用通知",
            "notifications_enabled" to "通知已启用",
            "notifications_disabled" to "通知已禁用",
            
            "save" to "保存",
            "cancel" to "取消",
            "settings" to "设置",
            "profile" to "个人资料",
            "dashboard" to "仪表板",
            "loading" to "加载中...",
            "error" to "错误",
            "success" to "成功",
            
            "personal_profile" to "个人资料",
            "security" to "安全",
            "app_info" to "应用信息",
            "logout" to "退出"
        )
    )
    
    /**
     * Get translation for a specific key and language
     */
    fun get(key: String, language: String): String {
        return translations[language]?.get(key) 
            ?: translations["en"]?.get(key) // Fallback to English
            ?: key // Fallback to key itself
    }
    
    /**
     * Get translation using Key enum
     */
    fun get(key: Key, language: String): String {
        return get(key.value, language)
    }
    
    /**
     * Get all available languages
     */
    fun getAvailableLanguages(): List<Pair<String, String>> {
        return listOf(
            "id" to "Bahasa Indonesia",
            "en" to "English",
            "zh" to "中文"
        )
    }
}

/**
 * Current Language Provider
 * Provides reactive language state throughout the app
 */
class TranslationState {
    private val _currentLanguage = mutableStateOf("id")
    val currentLanguage: State<String> = _currentLanguage
    
    fun setLanguage(language: String) {
        _currentLanguage.value = language
    }
    
    companion object {
        @Volatile
        private var INSTANCE: TranslationState? = null
        
        fun getInstance(): TranslationState {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: TranslationState().also { INSTANCE = it }
            }
        }
    }
}

/**
 * Translation Provider Composable
 * Provides language context to the entire app
 */
val LocalLanguage = compositionLocalOf { "id" }

@Composable
fun TranslationProvider(
    language: String,
    content: @Composable () -> Unit
) {
    val translationState = remember { TranslationState.getInstance() }
    
    // Update language when it changes
    LaunchedEffect(language) {
        translationState.setLanguage(language)
    }
    
    CompositionLocalProvider(LocalLanguage provides language) {
        content()
    }
}

/**
 * Translation Hook - Use this to get translations in Composables
 */
@Composable
fun translate(key: Translations.Key): String {
    val currentLanguage = LocalLanguage.current
    return Translations.get(key, currentLanguage)
}

/**
 * Translation Hook - Use this for string keys
 */
@Composable
fun translate(key: String): String {
    val currentLanguage = LocalLanguage.current
    return Translations.get(key, currentLanguage)
}

/**
 * Non-Composable translation function for use outside of Compose
 */
fun translateStatic(key: Translations.Key, language: String = "id"): String {
    return Translations.get(key, language)
}

/**
 * Non-Composable translation function for string keys
 */
fun translateStatic(key: String, language: String = "id"): String {
    return Translations.get(key, language)
}