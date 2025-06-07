package com.example.financialplannerapp.service

import android.content.Context
import androidx.compose.runtime.*
import com.example.financialplannerapp.data.model.LanguageOption
import com.example.financialplannerapp.data.model.TranslationProvider
import com.example.financialplannerapp.data.model.TranslationState
import com.example.financialplannerapp.data.model.Translations
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

interface TranslationProvider {
    fun translate(key: com.example.financialplannerapp.data.model.Translations, vararg args: Any): String
    fun translate(key: String, vararg args: Any): String
    val translationStateFlow: StateFlow<TranslationState?>
    fun changeLanguage(languageCode: String)
    fun getCurrentTranslations(): Map<String, String>
    fun getAvailableLanguages(): List<LanguageOption>
}

// --- Complete Translation Maps (as they should be) ---
private val englishTranslations = mapOf(
    // Enum mappings - exact match with enum names converted to lowercase
    "settingsprofile" to "Profile",
    "settingsprofiledesc" to "View and edit your personal profile",
    "themesetting" to "Theme",
    "themesettingdesc" to "Choose app display theme",
    "security" to "Security",
    "settings" to "Settings",
    "appinfo" to "App Information",
    "datasync" to "Data Sync",
    "datasyncdesc" to "Status and sync settings",
    "backuprestore" to "Backup & Restore",
    "backuprestoredesc" to "Backup and restore data",
    "helpcenter" to "Help Center",
    "helpcenterdesc" to "FAQ and user guide",
    "contactsupport" to "Contact Us",
    "contactsupportdesc" to "Report issues or send feedback",
    "logout" to "Logout",
    "languagesetting" to "Language",
    "languagesettingdesc" to "Choose app language",
    "currencysetting" to "Currency",
    "currencysettingdesc" to "Choose default currency",
    "notificationssetting" to "Notifications",
    "notificationssettingdesc" to "Enable or disable notifications",
    
    // Additional standard mappings
    "app_settings" to "App Settings", "back" to "Back", "theme_setting" to "Theme", "theme_setting_desc" to "Choose app display theme", "theme_light" to "Light", "theme_dark" to "Dark", "theme_system" to "System", "theme_changed_to" to "Theme changed to", "language_setting" to "Language", "language_setting_desc" to "Choose app language", "select_language" to "Select Language", "language_changed_to" to "Language changed to", "currency_setting" to "Currency", "currency_setting_desc" to "Choose default currency", "select_currency" to "Select Currency", "currency_idr" to "Indonesian Rupiah", "currency_usd" to "US Dollar", "currency_eur" to "Euro", "currency_jpy" to "Japanese Yen", "currency_changed_to" to "Currency changed to", "notifications_setting" to "Notifications", "notifications_setting_desc" to "Enable or disable notifications", "notifications_enabled" to "Notifications enabled", "notifications_disabled" to "Notifications disabled", "save" to "Save", "cancel" to "Cancel", "profile" to "Profile", "dashboard" to "Dashboard", "loading" to "Loading...", "error" to "Error", "success" to "Success", "personal_profile" to "Personal Profile", "data_sync" to "Data Sync", "data_sync_desc" to "Status and sync settings", "backup_restore" to "Backup & Restore", "backup_restore_desc" to "Backup and restore data", "help_center" to "Help Center", "help_center_desc" to "FAQ and user guide", "contact_support" to "Contact Us", "contact_support_desc" to "Report issues or send feedback", "pin_application" to "Application PIN", "pin_protect_app" to "Protect app with 4-6 digit PIN", "pin_active" to "PIN Active", "pin_inactive" to "PIN Inactive", "pin_protected" to "🛡️ App protected with PIN", "pin_not_protected" to "⚠️ App has no PIN protection", "biometric_auth" to "Biometric Authentication", "use_fingerprint_face" to "Use fingerprint or Face ID", "biometric_active" to "Biometric Active", "biometric_inactive" to "Biometric Inactive", "auto_lock" to "Auto Lock", "lock_when_inactive" to "Lock app when inactive", "auto_lock_active" to "Auto Lock Active", "auto_lock_inactive" to "Auto Lock Inactive", "setup_pin" to "Setup App PIN", "disable_pin" to "Disable PIN", "disable_pin_confirm" to "Are you sure you want to disable PIN? The app will become less secure.", "yes" to "Yes", "security_tips" to "Security Tips:", "pin_strength" to "PIN Strength:", "too_short" to "Too Short", "weak" to "Weak", "medium" to "Medium", "strong" to "Strong"
)
private val spanishTranslations = mapOf(
    // Enum mappings - exact match with enum names converted to lowercase
    "settingsprofile" to "Perfil",
    "settingsprofiledesc" to "Ver y editar tu perfil personal",
    "themesetting" to "Tema",
    "themesettingdesc" to "Elegir tema de la aplicación",
    "security" to "Seguridad",
    "settings" to "Configuración",
    "appinfo" to "Información de la App",
    "datasync" to "Sincronización de Datos",
    "datasyncdesc" to "Estado y ajustes de sincronización",
    "backuprestore" to "Copia de Seguridad y Restauración",
    "backuprestoredesc" to "Hacer copia de seguridad y restaurar datos",
    "helpcenter" to "Centro de Ayuda",
    "helpcenterdesc" to "Preguntas frecuentes y guía de usuario",
    "contactsupport" to "Contactar Soporte",
    "contactsupportdesc" to "Reportar problemas o enviar comentarios",
    "logout" to "Cerrar Sesión",
    "languagesetting" to "Idioma",
    "languagesettingdesc" to "Cambiar idioma de la aplicación",
    "currencysetting" to "Moneda",
    "currencysettingdesc" to "Elegir moneda predeterminada",
    "notificationssetting" to "Notificaciones",
    "notificationssettingdesc" to "Activar o desactivar notificaciones",
    
    // Additional standard mappings
    "app_settings" to "Configuración de App", "back" to "Atrás", "theme_setting" to "Tema", "theme_setting_desc" to "Elegir tema de la aplicación", "theme_light" to "Claro", "theme_dark" to "Oscuro", "theme_system" to "Sistema", "theme_changed_to" to "Tema cambiado a", "language_setting" to "Idioma", "language_setting_desc" to "Cambiar idioma de la aplicación", "select_language" to "Seleccionar Idioma", "language_changed_to" to "Idioma cambiado a", "currency_setting" to "Moneda", "currency_setting_desc" to "Elegir moneda predeterminada", "select_currency" to "Seleccionar Moneda", "currency_idr" to "Rupia Indonesia", "currency_usd" to "Dólar Estadounidense", "currency_eur" to "Euro", "currency_jpy" to "Yen Japonés", "currency_changed_to" to "Moneda cambiada a", "notifications_setting" to "Notificaciones", "notifications_setting_desc" to "Activar o desactivar notificaciones", "notifications_enabled" to "Notificaciones activadas", "notifications_disabled" to "Notificaciones desactivadas", "save" to "Guardar", "cancel" to "Cancelar", "profile" to "Perfil", "dashboard" to "Panel", "loading" to "Cargando...", "error" to "Error", "success" to "Éxito", "personal_profile" to "Perfil Personal", "data_sync" to "Sincronización de Datos", "data_sync_desc" to "Estado y ajustes de sincronización", "backup_restore" to "Copia de Seguridad y Restauración", "backup_restore_desc" to "Hacer copia de seguridad y restaurar datos", "help_center" to "Centro de Ayuda", "help_center_desc" to "Preguntas frecuentes y guía de usuario", "contact_support" to "Contactar Soporte", "contact_support_desc" to "Reportar problemas o enviar comentarios", "pin_application" to "PIN de la Aplicación", "pin_protect_app" to "Proteger la aplicación con PIN de 4-6 dígitos", "pin_active" to "PIN Activo", "pin_inactive" to "PIN Inactivo", "pin_protected" to "🛡️ Aplicación protegida con PIN", "pin_not_protected" to "⚠️ La aplicación no tiene protección por PIN", "biometric_auth" to "Autenticación Biométrica", "use_fingerprint_face" to "Usar huella digital o Face ID", "biometric_active" to "Biometría Activa", "biometric_inactive" to "Biometría Inactiva", "auto_lock" to "Bloqueo Automático", "lock_when_inactive" to "Bloquear la aplicación cuando esté inactiva", "auto_lock_active" to "Bloqueo Automático Activo", "auto_lock_inactive" to "Bloqueo Automático Inactivo", "setup_pin" to "Configurar PIN de la App", "disable_pin" to "Desactivar PIN", "disable_pin_confirm" to "¿Estás seguro de que quieres desactivar el PIN? La aplicación será menos segura.", "yes" to "Sí", "security_tips" to "Consejos de Seguridad:", "pin_strength" to "Fortaleza del PIN:", "too_short" to "Muy Corto", "weak" to "Débil", "medium" to "Medio", "strong" to "Fuerte"
)
private val indonesianTranslations = mapOf(
    // Enum mappings - exact match with enum names converted to lowercase
    "settingsprofile" to "Profil",
    "settingsprofiledesc" to "Lihat dan edit profil personal Anda",
    "themesetting" to "Tema",
    "themesettingdesc" to "Pilih tema tampilan aplikasi",
    "security" to "Keamanan",
    "settings" to "Pengaturan",
    "appinfo" to "Informasi Aplikasi",
    "datasync" to "Sinkronisasi Data",
    "datasyncdesc" to "Status dan pengaturan sinkronisasi",
    "backuprestore" to "Cadangkan & Pulihkan",
    "backuprestoredesc" to "Cadangkan dan pulihkan data",
    "helpcenter" to "Pusat Bantuan",
    "helpcenterdesc" to "FAQ dan panduan pengguna",
    "contactsupport" to "Hubungi Kami",
    "contactsupportdesc" to "Laporkan masalah atau kirim masukan",
    "logout" to "Keluar",
    "languagesetting" to "Bahasa",
    "languagesettingdesc" to "Pilih bahasa aplikasi",
    "currencysetting" to "Mata Uang",
    "currencysettingdesc" to "Pilih mata uang default",
    "notificationssetting" to "Notifikasi",
    "notificationssettingdesc" to "Aktifkan atau nonaktifkan notifikasi",
    
    // Additional standard mappings
    "app_settings" to "Pengaturan Aplikasi", "back" to "Kembali", "theme_setting" to "Tema", "theme_setting_desc" to "Pilih tema tampilan aplikasi", "theme_light" to "Terang", "theme_dark" to "Gelap", "theme_system" to "Sistem", "theme_changed_to" to "Tema diubah ke", "language_setting" to "Bahasa", "language_setting_desc" to "Pilih bahasa aplikasi", "select_language" to "Pilih Bahasa", "language_changed_to" to "Bahasa diubah ke", "currency_setting" to "Mata Uang", "currency_setting_desc" to "Pilih mata uang default", "select_currency" to "Pilih Mata Uang", "currency_idr" to "Rupiah Indonesia", "currency_usd" to "Dolar Amerika Serikat", "currency_eur" to "Euro", "currency_jpy" to "Yen Jepang", "currency_changed_to" to "Mata uang diubah ke", "notifications_setting" to "Notifikasi", "notifications_setting_desc" to "Aktifkan atau nonaktifkan notifikasi", "notifications_enabled" to "Notifikasi diaktifkan", "notifications_disabled" to "Notifikasi dinonaktifkan", "save" to "Simpan", "cancel" to "Batal", "profile" to "Profil", "dashboard" to "Dasbor", "loading" to "Memuat...", "error" to "Kesalahan", "success" to "Sukses", "personal_profile" to "Profil Pribadi", "data_sync" to "Sinkronisasi Data", "data_sync_desc" to "Status dan pengaturan sinkronisasi", "backup_restore" to "Cadangkan & Pulihkan", "backup_restore_desc" to "Cadangkan dan pulihkan data", "help_center" to "Pusat Bantuan", "help_center_desc" to "FAQ dan panduan pengguna", "contact_support" to "Hubungi Kami", "contact_support_desc" to "Laporkan masalah atau kirim masukan", "pin_application" to "PIN Aplikasi", "pin_protect_app" to "Lindungi aplikasi dengan PIN 4-6 digit", "pin_active" to "PIN Aktif", "pin_inactive" to "PIN Tidak Aktif", "pin_protected" to "🛡️ Aplikasi dilindungi dengan PIN", "pin_not_protected" to "⚠️ Aplikasi tidak memiliki perlindungan PIN", "biometric_auth" to "Autentikasi Biometrik", "use_fingerprint_face" to "Gunakan sidik jari atau Face ID", "biometric_active" to "Biometrik Aktif", "biometric_inactive" to "Biometrik Tidak Aktif", "auto_lock" to "Kunci Otomatis", "lock_when_inactive" to "Kunci aplikasi saat tidak aktif", "auto_lock_active" to "Kunci Otomatis Aktif", "auto_lock_inactive" to "Kunci Otomatis Tidak Aktif", "setup_pin" to "Atur PIN Aplikasi", "disable_pin" to "Nonaktifkan PIN", "disable_pin_confirm" to "Apakah Anda yakin ingin menonaktifkan PIN? Aplikasi akan menjadi kurang aman.", "yes" to "Ya", "security_tips" to "Tips Keamanan:", "pin_strength" to "Kekuatan PIN:", "too_short" to "Terlalu Pendek", "weak" to "Lemah", "medium" to "Sedang", "strong" to "Kuat"
)
private val chineseTranslations = mapOf(
    // Enum mappings - exact match with enum names converted to lowercase
    "settingsprofile" to "个人资料",
    "settingsprofiledesc" to "查看和编辑您的个人资料",
    "themesetting" to "主题",
    "themesettingdesc" to "选择应用显示主题",
    "security" to "安全",
    "settings" to "设置",
    "appinfo" to "应用信息",
    "datasync" to "数据同步",
    "datasyncdesc" to "状态和同步设置",
    "backuprestore" to "备份与恢复",
    "backuprestoredesc" to "备份和恢复数据",
    "helpcenter" to "帮助中心",
    "helpcenterdesc" to "常见问题与用户指南",
    "contactsupport" to "联系我们",
    "contactsupportdesc" to "报告问题或发送反馈",
    "logout" to "登出",
    "languagesetting" to "语言",
    "languagesettingdesc" to "选择应用语言",
    "currencysetting" to "货币",
    "currencysettingdesc" to "选择默认货币",
    "notificationssetting" to "通知",
    "notificationssettingdesc" to "启用或禁用通知",
    
    // Additional standard mappings
    "app_settings" to "应用设置", "back" to "返回", "theme_setting" to "主题", "theme_setting_desc" to "选择应用显示主题", "theme_light" to "浅色", "theme_dark" to "深色", "theme_system" to "系统", "theme_changed_to" to "主题已更改为", "language_setting" to "语言", "language_setting_desc" to "选择应用语言", "select_language" to "选择语言", "language_changed_to" to "语言已更改为", "currency_setting" to "货币", "currency_setting_desc" to "选择默认货币", "select_currency" to "选择货币", "currency_idr" to "印尼盾", "currency_usd" to "美元", "currency_eur" to "欧元", "currency_jpy" to "日元", "currency_changed_to" to "货币已更改为", "notifications_setting" to "通知", "notifications_setting_desc" to "启用或禁用通知", "notifications_enabled" to "通知已启用", "notifications_disabled" to "通知已禁用", "save" to "保存", "cancel" to "取消", "profile" to "个人资料", "dashboard" to "仪表板", "loading" to "加载中...", "error" to "错误", "success" to "成功", "personal_profile" to "个人资料", "data_sync" to "数据同步", "data_sync_desc" to "状态和同步设置", "backup_restore" to "备份与恢复", "backup_restore_desc" to "备份和恢复数据", "help_center" to "帮助中心", "help_center_desc" to "常见问题与用户指南", "contact_support" to "联系我们", "contact_support_desc" to "报告问题或发送反馈", "pin_application" to "应用PIN码", "pin_protect_app" to "使用4-6位PIN码保护应用", "pin_active" to "PIN码已激活", "pin_inactive" to "PIN码未激活", "pin_protected" to "🛡️ 应用受PIN码保护", "pin_not_protected" to "⚠️ 应用没有PIN码保护", "biometric_auth" to "生物认证", "use_fingerprint_face" to "使用指纹或面容ID", "biometric_active" to "生物认证已激活", "biometric_inactive" to "生物认证未激活", "auto_lock" to "自动锁定", "lock_when_inactive" to "应用不活动时锁定", "auto_lock_active" to "自动锁定已激活", "auto_lock_inactive" to "自动锁定未激活", "setup_pin" to "设置应用PIN码", "disable_pin" to "禁用PIN码", "disable_pin_confirm" to "您确定要禁用PIN码吗？应用将变得不那么安全。", "yes" to "是", "security_tips" to "安全提示:", "pin_strength" to "PIN码强度:", "too_short" to "太短", "weak" to "弱", "medium" to "中", "strong" to "强"
)

private val allTranslations: Map<String, Map<String, String>> = mapOf(
    "en" to englishTranslations,
    "es" to spanishTranslations,
    "id" to indonesianTranslations,
    "zh" to chineseTranslations
)

class TranslationServiceImpl(
    private val context: Context,
    initialLanguageCode: String = Locale.getDefault().language.takeIf { allTranslations.containsKey(it) } ?: "en"
) : TranslationProvider {

    private val _translationStateFlow = MutableStateFlow<TranslationState?>(null) // Made nullable
    override val translationStateFlow: StateFlow<TranslationState?> = _translationStateFlow.asStateFlow() // Made nullable

    init {
        val initialTranslations = allTranslations[initialLanguageCode] ?: englishTranslations
        val availableLanguages = allTranslations.keys.map { langCode ->
            val locale = Locale(langCode)
            // Ensure title casing for display language
            LanguageOption(langCode, locale.getDisplayLanguage(locale).replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() })
        }
        _translationStateFlow.value = TranslationState(
            currentLanguage = initialLanguageCode, // Changed from languageCode
            translationsMap = initialTranslations,
            availableLanguages = availableLanguages
        )
    }

    // This method expects a Translations enum key
    override fun translate(key: Translations, vararg args: Any): String {
        // Use the enum's name property to get the string key
        val stringKey = key.name.lowercase() // Convert enum name to lowercase key
        val template = _translationStateFlow.value?.translationsMap?.get(stringKey) 
                       ?: allTranslations["en"]?.get(stringKey) // Fallback to English
                       ?: stringKey // Fallback to the key itself
        return if (args.isNotEmpty()) String.format(template, *args) else template
    }

    // This method expects a String key
    override fun translate(key: String, vararg args: Any): String {
        val template = _translationStateFlow.value?.translationsMap?.get(key) 
                       ?: allTranslations["en"]?.get(key) // Fallback to English
                       ?: key // Fallback to the key itself
        return if (args.isNotEmpty()) String.format(template, *args) else template
    }
    
    override fun changeLanguage(languageCode: String) {
        val newTranslations = allTranslations[languageCode]
        if (newTranslations != null) {
            val currentState = _translationStateFlow.value
            if (currentState != null) {
                _translationStateFlow.value = currentState.copy(
                    currentLanguage = languageCode, // Changed from languageCode
                    translationsMap = newTranslations
                )
            }
        } else {
            // Fallback to English if the selected language is not found and it's not already English
            if (languageCode != "en") {
                changeLanguage("en")
            }
        }
    }

    // Added to fulfill the TranslationProvider interface from previous steps
    override fun getCurrentTranslations(): Map<String, String> {
        return _translationStateFlow.value?.translationsMap ?: allTranslations["en"] ?: emptyMap()
    }

    // Added to fulfill the TranslationProvider interface from previous steps
    override fun getAvailableLanguages(): List<LanguageOption> {
         return _translationStateFlow.value?.availableLanguages ?: allTranslations.keys.map { langCode ->
            val locale = Locale(langCode)
            LanguageOption(langCode, locale.getDisplayLanguage(locale).replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() })
        }
    }
}