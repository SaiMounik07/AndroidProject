package com.example.androidlearning.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProductDB(entry: ProductDB)

    @Query("SELECT * FROM product WHERE user = :userId")
    suspend fun getByUser(userId: String): ProductDB?

    @Query("DELETE FROM product WHERE user = :userId")
    suspend fun deleteByUser(userId: String)

}