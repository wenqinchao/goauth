package com.example.auth.dao

import androidx.room.*
import com.example.auth.entity.SecretInfo

@Dao
interface SecretInfoDao {
    @Query("SELECT * from secret_info")
    fun getAll(): MutableList<SecretInfo>

    @Insert
    fun insertAll(vararg users: SecretInfo)

    @Delete
    fun delete(user: SecretInfo)

    @Update
    fun update(vararg users: SecretInfo)

    @Query("DELETE FROM secret_info WHERE id = :id")
    fun deleteById(id: Int): Int

    @Query("UPDATE secret_info SET nickname = :nickname WHERE id = :id")
    fun updateById(id: Int, nickname: String):Int

    @Query("SELECT * from secret_info WHERE nickname LIKE '%' || :nickname || '%'")
    fun searchByNickname(nickname: String):MutableList<SecretInfo>
}