package com.example.financialplannerapp.utils

import androidx.annotation.StringRes

/**
 * Translation Manager
 * 
 * Centralized translation system supporting multiple languages with type-safe access.
 * Uses sealed classes for language types and translation keys.
 */
object Translations {
    // Supported languages
    enum class Language {
        ENGLISH, CHINESE, INDONESIAN
    }

    // Translation keys
    sealed class Key {
        // Navigation
        object Back : Key()
        
        // App Settings
        object AppSettings : Key()
        
        // Theme Settings
        object ThemeSetting : Key()
        object ThemeSettingDesc : Key()
        object ThemeLight : Key()
        object ThemeDark : Key()
        object ThemeSystem : Key()
        object ThemeChangedTo : Key()
        
        // Language Settings
        object LanguageSetting : Key()
        object LanguageSettingDesc : Key()
        object LanguageChangedTo : Key()
        
        // Currency Settings
        object CurrencySetting : Key()
        object CurrencySettingDesc : Key()
        object CurrencyIdr : Key()
        object CurrencyUsd : Key()
        object CurrencyEur : Key()
        object CurrencyJpy : Key()
        object CurrencyChangedTo : Key()
        
        // Notification Settings
        object NotificationsSetting : Key()
        object NotificationsSettingDesc : Key()
        object EnableNotifications : Key()
        object NotificationsEnabled : Key()
        object NotificationsDisabled : Key()
        
        // General
        object Save : Key()
        object Cancel : Key()
        object Confirm : Key()
        object Settings : Key()
        object Loading : Key()
        object Error : Key()
        object Success : Key()
    }

    // Translation maps for each language
    private val englishTranslations = mapOf(
        Key.Back to "Back",
        Key.AppSettings to "App Settings",
        Key.ThemeSetting to "Theme Settings",
        Key.ThemeSettingDesc to "Choose app theme",
        Key.ThemeLight to "Light",
        Key.ThemeDark to "Dark",
        Key.ThemeSystem to "System",
        Key.ThemeChangedTo to "Theme changed to %s",
        Key.LanguageSetting to "Language Settings",
        Key.LanguageSettingDesc to "Choose app language",
        Key.CurrencySetting to "Currency Settings",
        Key.CurrencySettingDesc to "Choose default currency",
        Key.CurrencyIdr to "Indonesian Rupiah (IDR)",
        Key.CurrencyUsd to "US Dollar (USD)",
        Key.CurrencyEur to "Euro (EUR)",
        Key.CurrencyJpy to "Japanese Yen (JPY)",
        Key.CurrencyChangedTo to "Currency changed to %s",
        Key.NotificationsSetting to "Notification Settings",
        Key.NotificationsSettingDesc to "Manage app notifications",
        Key.EnableNotifications to "Enable Notifications",
        Key.NotificationsEnabled to "Notifications enabled",
        Key.NotificationsDisabled to "Notifications disabled",
        Key.Save to "Save",
        Key.Cancel to "Cancel",
        Key.Confirm to "Confirm",
        Key.Settings to "Settings",
        Key.Loading to "Loading...",
        Key.Error to "An error occurred",
        Key.Success to "Success"
    )

    private val chineseTranslations = mapOf(
        Key.Back to "返回",
        Key.AppSettings to "应用设置",
        Key.ThemeSetting to "主题设置",
        Key.ThemeSettingDesc to "选择应用主题",
        Key.ThemeLight to "浅色",
        Key.ThemeDark to "深色",
        Key.ThemeSystem to "系统",
        Key.ThemeChangedTo to "主题已更改为 %s",
        Key.LanguageSetting to "语言设置",
        Key.LanguageSettingDesc to "选择应用语言",
        Key.CurrencySetting to "货币设置",
        Key.CurrencySettingDesc to "选择默认货币",
        Key.CurrencyIdr to "印尼盾 (IDR)",
        Key.CurrencyUsd to "美元 (USD)",
        Key.CurrencyEur to "欧元 (EUR)",
        Key.CurrencyJpy to "日元 (JPY)",
        Key.CurrencyChangedTo to "货币已更改为 %s",
        Key.NotificationsSetting to "通知设置",
        Key.NotificationsSettingDesc to "管理应用通知",
        Key.EnableNotifications to "启用通知",
        Key.NotificationsEnabled to "通知已启用",
        Key.NotificationsDisabled to "通知已禁用",
        Key.Save to "保存",
        Key.Cancel to "取消",
        Key.Confirm to "确认",
        Key.Settings to "设置",
        Key.Loading to "加载中...",
        Key.Error to "发生错误",
        Key.Success to "成功"
    )

    private val indonesianTranslations = mapOf(
        Key.Back to "Kembali",
        Key.AppSettings to "Pengaturan Aplikasi",
        Key.ThemeSetting to "Pengaturan Tema",
        Key.ThemeSettingDesc to "Pilih tema untuk aplikasi",
        Key.ThemeLight to "Terang",
        Key.ThemeDark to "Gelap",
        Key.ThemeSystem to "Sistem",
        Key.ThemeChangedTo to "Tema diubah ke %s",
        Key.LanguageSetting to "Pengaturan Bahasa",
        Key.LanguageSettingDesc to "Pilih bahasa untuk aplikasi",
        Key.CurrencySetting to "Pengaturan Mata Uang",
        Key.CurrencySettingDesc to "Pilih mata uang default",
        Key.CurrencyIdr to "Rupiah Indonesia (IDR)",
        Key.CurrencyUsd to "Dollar Amerika (USD)",
        Key.CurrencyEur to "Euro (EUR)",
        Key.CurrencyJpy to "Yen Jepang (JPY)",
        Key.CurrencyChangedTo to "Mata uang diubah ke %s",
        Key.NotificationsSetting to "Pengaturan Notifikasi",
        Key.NotificationsSettingDesc to "Kelola notifikasi aplikasi",
        Key.EnableNotifications to "Aktifkan Notifikasi",
        Key.NotificationsEnabled to "Notifikasi diaktifkan",
        Key.NotificationsDisabled to "Notifikasi dinonaktifkan",
        Key.Save to "Simpan",
        Key.Cancel to "Batal",
        Key.Confirm to "Konfirmasi",
        Key.Settings to "Pengaturan",
        Key.Loading to "Memuat...",
        Key.Error to "Terjadi kesalahan",
        Key.Success to "Berhasil"
    )

    // Current language setting (default to English)
    var currentLanguage: Language = Language.ENGLISH

    /**
     * Get translation for a specific key
     * @param key Translation key
     * @param formatArgs Optional format arguments for string formatting
     */
    fun get(key: Key, vararg formatArgs: Any): String {
        val translationMap = when (currentLanguage) {
            Language.ENGLISH -> englishTranslations
            Language.CHINESE -> chineseTranslations
            Language.INDONESIAN -> indonesianTranslations
        }
        
        return translationMap[key]?.let { 
            if (formatArgs.isNotEmpty()) it.format(*formatArgs) 
            else it 
        } ?: throw IllegalArgumentException("Translation not found for key: $key")
    }
}