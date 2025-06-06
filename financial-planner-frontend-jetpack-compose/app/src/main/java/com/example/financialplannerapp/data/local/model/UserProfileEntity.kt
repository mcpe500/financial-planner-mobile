package com.example.financialplannerapp.data.local.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val firebaseUid: String? = null, // For Firebase sync
    var name: String,
    var email: String,
    var photoUrl: String? = null,
    val registrationDate: Long = System.currentTimeMillis(),
    var lastLoginDate: Long = System.currentTimeMillis(),
    
    // To track if the local data has been synced with a backend
    @ColumnInfo(name = "is_synced_with_server", defaultValue = "1") // Default to true if not using server sync initially or synced upon creation
    var isSyncedWithServer: Boolean = true,

    // To track local modifications not yet pushed to a backend
    @ColumnInfo(name = "is_data_modified", defaultValue = "0")
    var isDataModified: Boolean = false,

    // Example additional fields (add as per your application's needs)
    var currencyPreference: String? = "USD",
    var notificationEnabled: Boolean = true
) : Parcelable