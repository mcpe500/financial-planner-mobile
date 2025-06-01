package com.example.financialplannerapp.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Type Converters for Room Database
 * 
 * Handles conversion of complex data types to and from primitive types
 * that can be stored in SQLite database.
 */
class Converters {
    
    private val gson = Gson()
    
    /**
     * Convert List<String> to JSON string
     */
    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return if (value == null) null else gson.toJson(value)
    }
    
    /**
     * Convert JSON string to List<String>
     */
    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        return if (value == null) null else {
            val listType = object : TypeToken<List<String>>() {}.type
            gson.fromJson(value, listType)
        }
    }
    
    /**
     * Convert Map<String, Any> to JSON string
     */
    @TypeConverter
    fun fromStringMap(value: Map<String, Any>?): String? {
        return if (value == null) null else gson.toJson(value)
    }
    
    /**
     * Convert JSON string to Map<String, Any>
     */
    @TypeConverter
    fun toStringMap(value: String?): Map<String, Any>? {
        return if (value == null) null else {
            val mapType = object : TypeToken<Map<String, Any>>() {}.type
            gson.fromJson(value, mapType)
        }
    }
}