package com.example.androidlearning.base.utils

import androidx.room.TypeConverter
import com.example.androidlearning.data.model.Product
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TypeConverters {
        @TypeConverter
        fun fromProductList(value: List<Product>): String {
            return Gson().toJson(value)
        }
        @TypeConverter
        fun toProductList(value: String): List<Product> {
            val type = object : TypeToken<List<Product>>() {}.type
            return Gson().fromJson(value, type)
        }
}