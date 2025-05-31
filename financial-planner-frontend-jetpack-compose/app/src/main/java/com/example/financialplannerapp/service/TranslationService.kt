package com.example.financialplannerapp.service

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Translation Service
 * 
 * Provides internationalization support for the Financial Planner app.
 * Supports Indonesian (id), English (en), and Chinese (zh) languages.
 * 
 * Features:
 * - Runtime language switching
 * - Reactive language updates via Compose
 * - Comprehensive translation coverage
 * - Fallback to English for missing translations
 * 
 * Usage:
 * ```kotlin
 * val translator = LocalTranslator.current
 * Text(text = translator.get("app_name"))
 * ```
 */
class TranslationService private constructor() {
    
    companion object {
        @Volatile
        private var INSTANCE: TranslationService? = null
        
        fun getInstance(): TranslationService {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: TranslationService().also { INSTANCE = it }
            }
        }
    }
    
    private val _currentLanguage = MutableStateFlow("id")
    val currentLanguage: Flow<String> = _currentLanguage.asStateFlow()
    
    private var currentLang: String = "id"
    
    /**
     * Set current language
     * 
     * @param languageCode Language code ("id", "en", "zh")
     */
    fun setLanguage(languageCode: String) {
        if (languageCode in supportedLanguages.keys) {
            currentLang = languageCode
            _currentLanguage.value = languageCode
        }
    }
    
    /**
     * Get current language code
     * 
     * @return Current language code
     */
    fun getCurrentLanguage(): String = currentLang
    
    /**
     * Get translated string by key
     * 
     * @param key Translation key
     * @param args Optional arguments for string formatting
     * @return Translated string or fallback
     */
    fun get(key: String, vararg args: Any): String {
        val translation = translations[currentLang]?.get(key) 
            ?: translations["en"]?.get(key) 
            ?: key
        
        return if (args.isNotEmpty()) {
            String.format(translation, *args)
        } else {
            translation
        }
    }
    
    /**
     * Get language display name
     * 
     * @param languageCode Language code
     * @return Display name in current language
     */
    fun getLanguageName(languageCode: String): String {
        return supportedLanguages[languageCode] ?: languageCode
    }
    
    /**
     * Get all supported languages
     * 
     * @return Map of language codes to display names
     */
    fun getSupportedLanguages(): Map<String, String> = supportedLanguages
    
    // Supported languages with their display names
    private val supportedLanguages = mapOf(
        "id" to "Bahasa Indonesia",
        "en" to "English",
        "zh" to "中文"
    )
    
    // Translation data
    private val translations = mapOf(
        // Indonesian translations
        "id" to mapOf(
            // App General
            "app_name" to "Financial Planner",
            "loading" to "Memuat...",
            "error" to "Terjadi kesalahan",
            "success" to "Berhasil",
            "cancel" to "Batal",
            "confirm" to "Konfirmasi",
            "save" to "Simpan",
            "delete" to "Hapus",
            "edit" to "Edit",
            "back" to "Kembali",
            "next" to "Selanjutnya",
            "previous" to "Sebelumnya",
            "done" to "Selesai",
            "yes" to "Ya",
            "no" to "Tidak",
            
            // App Settings
            "app_settings" to "Pengaturan Aplikasi",
            "app_settings_desc" to "Kustomisasi pengalaman aplikasi Anda",
            "theme_setting" to "Tema Aplikasi",
            "theme_setting_desc" to "Pilih tema yang diinginkan",
            "theme_light" to "Terang",
            "theme_dark" to "Gelap", 
            "theme_system" to "Sistem",
            "language_setting" to "Bahasa",
            "language_setting_desc" to "Pilih bahasa aplikasi",
            "currency_setting" to "Mata Uang Default",
            "currency_setting_desc" to "Pilih mata uang default untuk aplikasi",
            "notifications_setting" to "Notifikasi Lokal",
            "notifications_setting_desc" to "Aktifkan notifikasi untuk pengingat dan update",
            "enable_notifications" to "Aktifkan Notifikasi",
            
            // Theme change messages
            "theme_changed_to" to "Tema diubah ke %s",
            "language_changed_to" to "Bahasa diubah ke %s",
            "currency_changed_to" to "Mata uang diubah ke %s",
            "notifications_enabled" to "Notifikasi diaktifkan",
            "notifications_disabled" to "Notifikasi dinonaktifkan",
            
            // Currency options
            "currency_idr" to "IDR - Rupiah",
            "currency_usd" to "USD - Dollar",
            "currency_eur" to "EUR - Euro",
            "currency_jpy" to "JPY - Yen",
            
            // Security Settings
            "security_settings" to "Keamanan",
            "pin_app" to "PIN Aplikasi",
            "pin_app_desc" to "Lindungi aplikasi dengan PIN 4-6 digit",
            "pin_active" to "PIN Aktif",
            "pin_inactive" to "PIN Tidak Aktif",
            "pin_protected" to "🛡️ Aplikasi terlindungi dengan PIN",
            "pin_not_protected" to "⚠️ Aplikasi tidak memiliki proteksi PIN",
            
            // User Profile
            "user_profile" to "Profil Pengguna",
            "profile_updated" to "Profil berhasil diperbarui",
            "name" to "Nama",
            "email" to "Email",
            "phone" to "Telepon",
            "date_of_birth" to "Tanggal Lahir",
            "occupation" to "Pekerjaan",
            "monthly_income" to "Pendapatan Bulanan",
            "financial_goals" to "Tujuan Keuangan",
        ),
        
        // English translations
        "en" to mapOf(
            // App General
            "app_name" to "Financial Planner",
            "loading" to "Loading...",
            "error" to "An error occurred",
            "success" to "Success",
            "cancel" to "Cancel",
            "confirm" to "Confirm",
            "save" to "Save",
            "delete" to "Delete",
            "edit" to "Edit",
            "back" to "Back",
            "next" to "Next",
            "previous" to "Previous",
            "done" to "Done",
            "yes" to "Yes",
            "no" to "No",
            
            // App Settings
            "app_settings" to "App Settings",
            "app_settings_desc" to "Customize your app experience",
            "theme_setting" to "App Theme",
            "theme_setting_desc" to "Choose your preferred theme",
            "theme_light" to "Light",
            "theme_dark" to "Dark",
            "theme_system" to "System",
            "language_setting" to "Language",
            "language_setting_desc" to "Choose app language",
            "currency_setting" to "Default Currency",
            "currency_setting_desc" to "Choose default currency for the app",
            "notifications_setting" to "Local Notifications",
            "notifications_setting_desc" to "Enable notifications for reminders and updates",
            "enable_notifications" to "Enable Notifications",
            
            // Theme change messages
            "theme_changed_to" to "Theme changed to %s",
            "language_changed_to" to "Language changed to %s",
            "currency_changed_to" to "Currency changed to %s",
            "notifications_enabled" to "Notifications enabled",
            "notifications_disabled" to "Notifications disabled",
            
            // Currency options
            "currency_idr" to "IDR - Rupiah",
            "currency_usd" to "USD - Dollar",
            "currency_eur" to "EUR - Euro",
            "currency_jpy" to "JPY - Yen",
            
            // Security Settings
            "security_settings" to "Security",
            "pin_app" to "App PIN",
            "pin_app_desc" to "Protect app with 4-6 digit PIN",
            "pin_active" to "PIN Active",
            "pin_inactive" to "PIN Inactive",
            "pin_protected" to "🛡️ App protected with PIN",
            "pin_not_protected" to "⚠️ App has no PIN protection",
            
            // User Profile
            "user_profile" to "User Profile",
            "profile_updated" to "Profile updated successfully",
            "name" to "Name",
            "email" to "Email",
            "phone" to "Phone",
            "date_of_birth" to "Date of Birth",
            "occupation" to "Occupation",
            "monthly_income" to "Monthly Income",
            "financial_goals" to "Financial Goals",
        ),
        
        // Chinese translations
        "zh" to mapOf(
            // App General
            "app_name" to "财务规划师",
            "loading" to "加载中...",
            "error" to "发生错误",
            "success" to "成功",
            "cancel" to "取消",
            "confirm" to "确认",
            "save" to "保存",
            "delete" to "删除",
            "edit" to "编辑",
            "back" to "返回",
            "next" to "下一步",
            "previous" to "上一步",
            "done" to "完成",
            "yes" to "是",
            "no" to "否",
            
            // App Settings
            "app_settings" to "应用设置",
            "app_settings_desc" to "自定义您的应用体验",
            "theme_setting" to "应用主题",
            "theme_setting_desc" to "选择您喜欢的主题",
            "theme_light" to "浅色",
            "theme_dark" to "深色",
            "theme_system" to "系统",
            "language_setting" to "语言",
            "language_setting_desc" to "选择应用语言",
            "currency_setting" to "默认货币",
            "currency_setting_desc" to "选择应用的默认货币",
            "notifications_setting" to "本地通知",
            "notifications_setting_desc" to "启用提醒和更新通知",
            "enable_notifications" to "启用通知",
            
            // Theme change messages
            "theme_changed_to" to "主题已更改为 %s",
            "language_changed_to" to "语言已更改为 %s",
            "currency_changed_to" to "货币已更改为 %s",
            "notifications_enabled" to "通知已启用",
            "notifications_disabled" to "通知已禁用",
            
            // Currency options
            "currency_idr" to "IDR - 印尼盾",
            "currency_usd" to "USD - 美元",
            "currency_eur" to "EUR - 欧元",
            "currency_jpy" to "JPY - 日元",
            
            // Security Settings
            "security_settings" to "安全设置",
            "pin_app" to "应用PIN码",
            "pin_app_desc" to "使用4-6位PIN码保护应用",
            "pin_active" to "PIN码已激活",
            "pin_inactive" to "PIN码未激活",
            "pin_protected" to "🛡️ 应用受PIN码保护",
            "pin_not_protected" to "⚠️ 应用没有PIN码保护",
            
            // User Profile
            "user_profile" to "用户资料",
            "profile_updated" to "资料更新成功",
            "name" to "姓名",
            "email" to "邮箱",
            "phone" to "电话",
            "date_of_birth" to "出生日期",
            "occupation" to "职业",
            "monthly_income" to "月收入",
            "financial_goals" to "财务目标",
        )
    )
}

/**
 * Compose Local for Translation Service
 */
val LocalTranslator = compositionLocalOf<TranslationService> {
    error("No TranslationService provided")
}

/**
 * Translation Provider Composable
 * 
 * Provides translation service to the entire app hierarchy.
 * 
 * @param language Current language code
 * @param content App content
 */
@Composable
fun TranslationProvider(
    language: String = "id",
    content: @Composable () -> Unit
) {
    val translator = remember { TranslationService.getInstance() }
    
    // Update language when it changes
    translator.setLanguage(language)
    
    CompositionLocalProvider(
        LocalTranslator provides translator,
        content = content
    )
}

/**
 * Extension function for easy translation access
 * 
 * @param key Translation key
 * @param args Optional formatting arguments
 * @return Translated string
 */
@Composable
fun String.translate(vararg args: Any): String {
    val translator = LocalTranslator.current
    return translator.get(this, *args)
}