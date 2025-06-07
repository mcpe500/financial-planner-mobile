package com.example.financialplannerapp.core.util

import androidx.compose.runtime.Composable
import com.example.financialplannerapp.service.LocalTranslationProvider
import com.example.financialplannerapp.data.model.Translations

@Composable
fun translate(key: Translations): String {
    val translationProvider = LocalTranslationProvider.current
    return translationProvider.translate(key)
}