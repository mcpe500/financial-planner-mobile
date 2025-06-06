package com.example.financialplannerapp.core.util

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext

/**
 * Translation System            // User Profile Settings
            "name_field" to "Nama Lengkap",
            "email_field" to "Email",
            "phone_field" to "Nomor Telepon",
            "birth_date_field" to "Tanggal Lahir",
            "occupation_field" to "Pekerjaan",
            "monthly_income_field" to "Pendapatan Bulanan (IDR)",
            "financial_goals_field" to "Tujuan Keuangan",
            "personal_info" to "Informasi Personal",
            "professional_info" to "Informasi Profesional",
            "sync_data" to "Sinkronisasi Data",
            "last_sync" to "Sinkronisasi Terakhir:",
            "sync_to_server" to "Sinkronkan ke Server",
            "syncing" to "Menyinkronkan...",
            "offline_mode" to "Offline - Tidak dapat sinkronisasi",
            "offline_notice" to "💾 Data disimpan secara lokal dan akan disinkronkan saat online",
            "check_connection" to "Cek Koneksi",
            "edit_profile" to "Edit Profil",
            "email_readonly" to "Email tidak dapat diubah",
            "unsaved_changes" to "Ada perubahan yang belum disinkronkan",or Financial Planner App
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
        object Logout : Key("logout");
        object DataSync : Key("data_sync");
        object DataSyncDesc : Key("data_sync_desc");
        object BackupRestore : Key("backup_restore");
        object BackupRestoreDesc : Key("backup_restore_desc");
        object HelpCenter : Key("help_center");
        object HelpCenterDesc : Key("help_center_desc");
        object ContactSupport : Key("contact_support");
        object ContactSupportDesc : Key("contact_support_desc");
        
        // Security Settings
        object PinApplication : Key("pin_application");
        object PinProtectApp : Key("pin_protect_app");
        object PinActive : Key("pin_active");
        object PinInactive : Key("pin_inactive");
        object PinProtected : Key("pin_protected");
        object PinNotProtected : Key("pin_not_protected");
        object BiometricAuth : Key("biometric_auth");
        object UseFingerprintFace : Key("use_fingerprint_face");
        object BiometricActive : Key("biometric_active");
        object BiometricInactive : Key("biometric_inactive");
        object AutoLock : Key("auto_lock");
        object LockWhenInactive : Key("lock_when_inactive");
        object AutoLockActive : Key("auto_lock_active");
        object AutoLockInactive : Key("auto_lock_inactive");
        object SetupPin : Key("setup_pin");
        object DisablePin : Key("disable_pin");
        object DisablePinConfirm : Key("disable_pin_confirm");
        object Yes : Key("yes");
        object SecurityTips : Key("security_tips");
        object PinStrength : Key("pin_strength");
        object TooShort : Key("too_short");
        object Weak : Key("weak");
        object Medium : Key("medium");
        object Strong : Key("strong");
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
            "logout" to "Keluar",
            "data_sync" to "Sinkronisasi Data",
            "data_sync_desc" to "Status dan pengaturan sinkronisasi",
            "backup_restore" to "Backup & Restore",
            "backup_restore_desc" to "Cadangkan dan pulihkan data",
            "help_center" to "Pusat Bantuan",
            "help_center_desc" to "FAQ dan panduan penggunaan",
            "contact_support" to "Hubungi Kami",
            "contact_support_desc" to "Laporkan masalah atau kirim feedback",
            
            // Security Settings
            "pin_application" to "PIN Aplikasi",
            "pin_protect_app" to "Lindungi aplikasi dengan PIN 4-6 digit",
            "pin_active" to "PIN Aktif",
            "pin_inactive" to "PIN Tidak Aktif",
            "pin_protected" to "🛡️ Aplikasi terlindungi dengan PIN",
            "pin_not_protected" to "⚠️ Aplikasi tidak memiliki proteksi PIN",
            "biometric_auth" to "Autentikasi Biometrik",
            "use_fingerprint_face" to "Gunakan sidik jari atau Face ID",
            "biometric_active" to "Biometrik Aktif",
            "biometric_inactive" to "Biometrik Tidak Aktif",
            "auto_lock" to "Kunci Otomatis",
            "lock_when_inactive" to "Kunci aplikasi saat tidak aktif",
            "auto_lock_active" to "Kunci Otomatis Aktif",
            "auto_lock_inactive" to "Kunci Otomatis Tidak Aktif",
            "setup_pin" to "Atur PIN Aplikasi",
            "disable_pin" to "Nonaktifkan PIN",
            "disable_pin_confirm" to "Apakah Anda yakin ingin menonaktifkan PIN? Aplikasi akan menjadi kurang aman.",
            "yes" to "Ya",
            "security_tips" to "Tips Keamanan:",
            "pin_strength" to "Kekuatan PIN:",
            "too_short" to "Terlalu Pendek",
            "weak" to "Lemah",
            "medium" to "Sedang",
            "strong" to "Kuat"
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
            "logout" to "Logout",
            "data_sync" to "Data Sync",
            "data_sync_desc" to "Status and sync settings",
            "backup_restore" to "Backup & Restore",
            "backup_restore_desc" to "Backup and restore data",
            "help_center" to "Help Center",
            "help_center_desc" to "FAQ and user guide",
            "contact_support" to "Contact Us",
            "contact_support_desc" to "Report issues or send feedback",
            
            // User Profile Settings
            "name_field" to "Full Name",
            "email_field" to "Email",
            "phone_field" to "Phone Number",
            "birth_date_field" to "Date of Birth",
            "occupation_field" to "Occupation",
            "monthly_income_field" to "Monthly Income (IDR)",
            "financial_goals_field" to "Financial Goals",
            "personal_info" to "Personal Information",
            "professional_info" to "Professional Information",
            "sync_data" to "Data Sync",
            "last_sync" to "Last Sync:",
            "sync_to_server" to "Sync to Server",
            "syncing" to "Syncing...",
            "offline_mode" to "Offline - Cannot sync",
            "offline_notice" to "💾 Data saved locally and will sync when online",
            "check_connection" to "Check Connection",
            "edit_profile" to "Edit Profile",
            "email_readonly" to "Email cannot be changed",
            "unsaved_changes" to "There are unsaved changes",
            "pin_protect_app" to "Protect app with 4-6 digit PIN",
            "pin_active" to "PIN Active",
            "pin_inactive" to "PIN Inactive",
            "pin_protected" to "🛡️ App protected with PIN",
            "pin_not_protected" to "⚠️ App has no PIN protection",
            "biometric_auth" to "Biometric Authentication",
            "use_fingerprint_face" to "Use fingerprint or Face ID",
            "biometric_active" to "Biometric Active",
            "biometric_inactive" to "Biometric Inactive",
            "auto_lock" to "Auto Lock",
            "lock_when_inactive" to "Lock app when inactive",
            "auto_lock_active" to "Auto Lock Active",
            "auto_lock_inactive" to "Auto Lock Inactive",
            "setup_pin" to "Setup App PIN",
            "disable_pin" to "Disable PIN",
            "disable_pin_confirm" to "Are you sure you want to disable PIN? The app will become less secure.",
            "yes" to "Yes",
            "security_tips" to "Security Tips:",
            "pin_strength" to "PIN Strength:",
            "too_short" to "Too Short",
            "weak" to "Weak",
            "medium" to "Medium",
            "strong" to "Strong"
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
            "logout" to "退出",
            "data_sync" to "数据同步",
            "data_sync_desc" to "状态和同步设置",
            "backup_restore" to "备份与恢复",
            "backup_restore_desc" to "备份和恢复数据",
            "help_center" to "帮助中心",
            "help_center_desc" to "常见问题和用户指南",
            "contact_support" to "联系我们",
            "contact_support_desc" to "报告问题或发送反馈",
            
            // User Profile Settings
            "name_field" to "全名",
            "email_field" to "邮箱",
            "phone_field" to "电话号码",
            "birth_date_field" to "出生日期",
            "occupation_field" to "职业",
            "monthly_income_field" to "月收入 (IDR)",
            "financial_goals_field" to "财务目标",
            "personal_info" to "个人信息",
            "professional_info" to "职业信息",
            "sync_data" to "数据同步",
            "last_sync" to "最后同步:",
            "sync_to_server" to "同步到服务器",
            "syncing" to "同步中...",
            "offline_mode" to "离线 - 无法同步",
            "offline_notice" to "💾 数据已在本地保存，联网时将同步",
            "check_connection" to "检查连接",
            "edit_profile" to "编辑个人资料",
            "email_readonly" to "邮箱无法更改",
            "unsaved_changes" to "有未保存的更改",
            
            // Security Settings  
            "pin_application" to "应用PIN",
            "pin_protect_app" to "使用4-6位PIN保护应用",
            "pin_active" to "PIN已激活",
            "pin_inactive" to "PIN未激活",
            "pin_protected" to "🛡️ 应用已受PIN保护",
            "pin_not_protected" to "⚠️ 应用没有PIN保护",
            "biometric_auth" to "生物识别认证",
            "use_fingerprint_face" to "使用指纹或Face ID",
            "biometric_active" to "生物识别已激活",
            "biometric_inactive" to "生物识别未激活",
            "auto_lock" to "自动锁定",
            "lock_when_inactive" to "闲置时锁定应用",
            "auto_lock_active" to "自动锁定已激活",
            "auto_lock_inactive" to "自动锁定未激活",
            "setup_pin" to "设置应用PIN",
            "disable_pin" to "禁用PIN",
            "disable_pin_confirm" to "您确定要禁用PIN吗？应用将变得不太安全。",
            "yes" to "是",
            "security_tips" to "安全提示：",
            "pin_strength" to "PIN强度：",
            "too_short" to "太短",
            "weak" to "弱",
            "medium" to "中等",
            "strong" to "强"
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