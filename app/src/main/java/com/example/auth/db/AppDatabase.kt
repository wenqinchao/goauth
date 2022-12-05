package com.example.auth.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.auth.dao.SecretInfoDao
import com.example.auth.entity.SecretInfo

@Database(entities = [SecretInfo::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun secretInfoDao(): SecretInfoDao
}