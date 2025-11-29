package com.example.androidlearning.ui.addproduct

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.androidlearning.base.constants.Constants.GUEST
import com.example.androidlearning.base.constants.Constants.USERNAME
import com.example.androidlearning.data.model.Product
import com.example.androidlearning.data.repository.MainRepository

class AddProductViewModel : ViewModel() {

    val mainRepository: MainRepository= MainRepository()

    private val _discountPercent= MutableLiveData<Double>()
    val discountPercent: LiveData<Double> = _discountPercent

    fun getDiscountPrice(listPrice:Double,salePrice:Double){
        _discountPercent.value=if (listPrice > salePrice) {
            ((listPrice - salePrice) / listPrice * 100)
        } else {
            0.0
        }
    }
    fun saveProduct(product: Product){
        val username=mainRepository.getValueByKey(USERNAME, GUEST)
        mainRepository.saveProduct(username.toString(),product)
    }
    fun deleteProduct(product:Product){
        val username=mainRepository.getValueByKey(USERNAME, GUEST)
        mainRepository.deleteProduct(username.toString(),product)
    }
    fun getProducts(): List<Product>{
        val username=mainRepository.getValueByKey(USERNAME, GUEST)
        return mainRepository.getProducts(username.toString())
    }
}
