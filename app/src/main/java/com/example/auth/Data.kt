package com.example.auth

import com.example.auth.dao.SecretInfoDao
import com.example.auth.db.AppDatabase
import com.example.auth.entity.SecretInfo

object Data {
    @JvmStatic
    var secrets:MutableList<SecretInfo> = mutableListOf()

    @JvmStatic
    var dao: SecretInfoDao? = null
}