package com.example.androidlearning.data.db.dao

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.androidlearning.data.model.Product

@Entity(tableName = "product")
data class ProductDB(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var products:List<Product>,
    var user:String

)