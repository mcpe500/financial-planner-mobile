package com.example.financialplannerapp.data.local

import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.util.*
import com.example.financialplannerapp.data.local.model.ReceiptItem
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class Converters {
    private val moshi = Moshi.Builder().build()

    @TypeConverter
    fun fromDate(value: Date?): Long? = value?.time

    @TypeConverter
    fun toDate(value: Long?): Date? = value?.let { Date(it) }

    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        val type = Types.newParameterizedType(List::class.java, String::class.java)
        return moshi.adapter<List<String>>(type).toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        val type = Types.newParameterizedType(List::class.java, String::class.java)
        return moshi.adapter<List<String>>(type).fromJson(value ?: "[]")
    }
}

class ReceiptItemListConverter {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    private val type = Types.newParameterizedType(List::class.java, ReceiptItem::class.java)
    private val adapter = moshi.adapter<List<ReceiptItem>>(type)

    @TypeConverter
    fun fromReceiptItemList(value: List<ReceiptItem>?): String? {
        return adapter.toJson(value)
    }

    @TypeConverter
    fun toReceiptItemList(value: String?): List<ReceiptItem>? {
        return adapter.fromJson(value ?: "[]")
    }
}