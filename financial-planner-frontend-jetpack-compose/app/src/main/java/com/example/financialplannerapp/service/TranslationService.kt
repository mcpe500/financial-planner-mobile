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

// In-memory cache for translations (replace with actual loading mechanism if needed)
// This is a simplified example. In a real app, you'd load these from resources or a server.
private val englishTranslations = mapOf(
    Translations.AppName.name to "Financial Planner",
    Translations.Settings.name to "Settings",
    Translations.SettingsTitle.name to "Application Settings",
    Translations.AppSettings.name to "App Settings",
    Translations.LanguageSetting.name to "Language",
    Translations.LanguageSettingDesc.name to "Change the application language",
    Translations.ThemeSetting.name to "Theme",
    Translations.ThemeSettingDesc.name to "Change the application theme",
    Translations.ThemeLight.name to "Light",
    Translations.ThemeDark.name to "Dark",
    Translations.ThemeSystem.name to "System Default",
    Translations.LoginButton.name to "Login",
    Translations.UsernameLabel.name to "Username",
    Translations.PasswordLabel.name to "Password",
    Translations.AddRecurringBillTitle.name to "Add Recurring Bill",
    Translations.BillNameLabel.name to "Bill Name",
    Translations.EstimatedAmountLabel.name to "Estimated Amount",
    Translations.DueDateLabel.name to "Due Date",
    Translations.RepeatCycleLabel.name to "Repeat Cycle",
    Translations.CategoryLabel.name to "Category",
    Translations.NotesLabel.name to "Notes",
    Translations.Save.name to "Save",
    Translations.Cancel.name to "Cancel",
    Translations.Back.name to "Back",
    Translations.BillCalendarTitle.name to "Bill Calendar",
    Translations.MissingTranslation.name to "Translation not found"
    // Add all other English translations here
)

private val spanishTranslations = mapOf(
    Translations.AppName.name to "Planificador Financiero",
    Translations.Settings.name to "Configuración",
    Translations.SettingsTitle.name to "Configuración de la Aplicación",
    Translations.AppSettings.name to "Configuración de App",
    Translations.LanguageSetting.name to "Idioma",
    Translations.LanguageSettingDesc.name to "Cambiar el idioma de la aplicación",
    Translations.ThemeSetting.name to "Tema",
    Translations.ThemeSettingDesc.name to "Cambiar el tema de la aplicación",
    Translations.ThemeLight.name to "Claro",
    Translations.ThemeDark.name to "Oscuro",
    Translations.ThemeSystem.name to "Predeterminado del Sistema",
    Translations.LoginButton.name to "Iniciar Sesión",
    Translations.UsernameLabel.name to "Usuario",
    Translations.PasswordLabel.name to "Contraseña",
    Translations.AddRecurringBillTitle.name to "Añadir Factura Recurrente",
    Translations.BillNameLabel.name to "Nombre de la Factura",
    Translations.EstimatedAmountLabel.name to "Monto Estimado",
    Translations.DueDateLabel.name to "Fecha de Vencimiento",
    Translations.RepeatCycleLabel.name to "Ciclo de Repetición",
    Translations.CategoryLabel.name to "Categoría",
    Translations.NotesLabel.name to "Notas",
    Translations.Save.name to "Guardar",
    Translations.Cancel.name to "Cancelar",
    Translations.Back.name to "Atrás",
    Translations.BillCalendarTitle.name to "Calendario de Facturas",
    Translations.MissingTranslation.name to "Traducción no encontrada"
    // Add all other Spanish translations here
)

private val allTranslations: Map<String, Map<String, String>> = mapOf(
    "en" to englishTranslations,
    "es" to spanishTranslations
)

class TranslationServiceImpl(
    // Context can be used for loading resources if needed in the future
    private val context: Context,
    initialLanguageCode: String = Locale.getDefault().language.takeIf { allTranslations.containsKey(it) } ?: "en"
) : TranslationProvider {

    private val _translationStateFlow = MutableStateFlow<TranslationState?>(null)
    override val translationStateFlow: StateFlow<TranslationState?> = _translationStateFlow.asStateFlow()

    init {
        val initialTranslations = allTranslations[initialLanguageCode] ?: englishTranslations
        val availableLanguages = allTranslations.keys.map { langCode ->
            LanguageOption(langCode, Locale(langCode).displayLanguage.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() })
        }
        _translationStateFlow.value = TranslationState(
            currentLanguage = initialLanguageCode,
            translationsMap = initialTranslations,
            availableLanguages = availableLanguages
        )
    }

    override fun translate(key: Translations, vararg args: Any): String {
        val template = _translationStateFlow.value?.translationsMap?.get(key.name) ?: allTranslations["en"]?.get(Translations.MissingTranslation.name) ?: key.name
        return if (args.isNotEmpty()) String.format(template, *args) else template
    }

    override fun translate(key: String, vararg args: Any): String {
        val template = _translationStateFlow.value?.translationsMap?.get(key) ?: allTranslations["en"]?.get(Translations.MissingTranslation.name) ?: key
        return if (args.isNotEmpty()) String.format(template, *args) else template
    }

    override fun changeLanguage(languageCode: String) {
        val newTranslations = allTranslations[languageCode]
        if (newTranslations != null) {
            val currentState = _translationStateFlow.value
            _translationStateFlow.value = currentState?.copy(
                currentLanguage = languageCode,
                translationsMap = newTranslations
            )
        } else {
            // Handle unsupported language, e.g., log an error or default to English
            if (languageCode != "en") { // Avoid infinite loop if "en" is somehow missing
                changeLanguage("en") // Default to English
            }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: TranslationServiceImpl? = null

        fun getInstance(context: Context): TranslationServiceImpl {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: TranslationServiceImpl(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}

val LocalTranslationProvider = compositionLocalOf<TranslationProvider> {
    error("No TranslationProvider provided")
}

@Composable
fun ProvideTranslations(
    translationProvider: TranslationProvider,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalTranslationProvider provides translationProvider,
        content = content
    )
}

// Composable helper to get a translated string
@Composable
fun translate(key: Translations, vararg args: Any): String {
    val translator = LocalTranslationProvider.current
    return translator.translate(key, *args)
}

// Overload for string keys if needed, though enum is preferred for type safety
@Composable
fun translate(key: String, vararg args: Any): String {
    val translator = LocalTranslationProvider.current
    return translator.translate(key, *args)
}