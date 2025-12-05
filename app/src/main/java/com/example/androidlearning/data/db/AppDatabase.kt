package com.example.androidlearning.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.androidlearning.data.db.dao.ProductDB
import com.example.androidlearning.data.db.dao.ProductDao
import com.example.androidlearning.data.model.Product

@Database(entities = [ProductDB::class], version = 1)
@TypeConverters(com.example.androidlearning.base.utils.TypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
}
