package com.example.androidlearning.data.repository

import com.example.androidlearning.data.db.dao.ProductDB
import com.example.androidlearning.data.db.dao.ProductDao
import com.example.androidlearning.data.di.NetworkModule
import com.example.androidlearning.data.model.Product
import com.example.androidlearning.data.model.ProductResponseData
import com.example.androidlearning.data.remote.api.ProductApi
import retrofit2.Response
import javax.inject.Inject

class ProductRepository @Inject constructor() {
    @Inject
    lateinit var productApi: ProductApi
    @Inject
    lateinit var productDao:ProductDao

    suspend fun getProducts(
                     searchTerm: String?,
                     showFacets: Boolean = true,
                     channelId: String,
                     start: Int = 0,
                     page: Int = 0,
                     itemPerPage: Int = 10
    ): ProductResponseData {
        val queryParams = mapOf<String, Any>(
            "searchTerm" to (searchTerm ?: ""),
            "showFacets" to showFacets,
            "channelId" to channelId,
            "start" to start,
            "page" to page,
            "itemPerPage" to itemPerPage

        )
        return productApi.getProducts(queryParams = queryParams)
    }

    suspend fun getProductsByUser(user:String): List<Product>{
        return productDao.getByUser(user)?.products.orEmpty()
    }
    suspend fun saveProductsForUser(user:String,products:Product) : Boolean {
        productDao.getByUser(user)?.let {
            val lists: MutableList<Product> = getProductsByUser(user).toMutableList()
            if (products in lists) {
                return false
            }
            lists.add(products)
            it.products=lists
            productDao.insertProductDB(it)
        } ?: run{
            productDao.insertProductDB(ProductDB(0, mutableListOf(),user))
        }

        if (productDao.getByUser(user)?.products?.contains(products) == true) {
            return true
        }
        return false

    }
    suspend fun deleteProductForUser(user:String,product: Product){
        productDao.getByUser(user)?.let {
            val lists: MutableList<Product> = getProductsByUser(user).toMutableList()
            lists.remove(product)
            it.products=lists
            productDao.insertProductDB(it)
        }
    }
    suspend fun deleteProduct(user:String){
        productDao.deleteByUser(user)
        }
    }

