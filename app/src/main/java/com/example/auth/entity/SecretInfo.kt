package com.example.auth.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "secret_info")
@Parcelize
data class SecretInfo(
    @PrimaryKey val id: Int?,
    var secret: String,
    var nickname:String,
): Parcelable
