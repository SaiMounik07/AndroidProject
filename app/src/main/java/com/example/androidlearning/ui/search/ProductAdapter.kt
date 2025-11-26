package com.example.androidlearning.ui.search

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidlearning.R
import com.example.androidlearning.databinding.CardProductBinding
import com.example.androidlearning.data.model.Product

class ProductAdapter(private var products: List<Product>, private val onProductClick: (Product) -> Unit): RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductViewHolder {

        val binding = CardProductBinding.inflate(LayoutInflater.from(parent.context))
        return ProductViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        with(holder.binding) {
            productData(product, onProductClick)

        }
    }

    override fun getItemCount(): Int {
        return products.size
    }

    class ProductViewHolder(val binding: CardProductBinding) : RecyclerView.ViewHolder(binding.root)


    fun updateData(newProducts: List<Product>) {
        if (products is MutableList) {
            (products as MutableList<Product>).clear()
            (products as MutableList<Product>).addAll(newProducts)
            notifyDataSetChanged()
        } else {
            products = newProducts
            notifyDataSetChanged()
        }
    }
}

fun CardProductBinding.productData(product: Product,onProductClick: (Product) -> Unit ={}) {
    if (!product.name.isEmpty()) {
        productName.text = product.name
        productPrice.text = product.price.priceDisplay
        tvActualPrice.text = product.price.strikeThroughPriceDisplay
        tvActualPrice.paintFlags = tvActualPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        if (tvActualPrice.text.isEmpty()) {
            tvActualPrice.visibility = View.GONE
            tvDiscountLabel.visibility = View.GONE
            tvDiscountPercent.visibility = View.GONE
        } else {
            tvActualPrice.visibility = View.VISIBLE
            tvDiscountLabel.visibility = View.VISIBLE
            tvDiscountPercent.visibility = View.VISIBLE
        }

        tvDiscountPercent.text = "${product.price.discount}%"
        tvFreeShippingText.visibility =
            if (product.tags.contains("FREE_GIFT")) View.VISIBLE else View.GONE
        freeShippingLogo.visibility =
            if (product.tags.contains("FREE_SHIPPING")) View.VISIBLE else View.GONE
        ivFlashsale.isVisible = product.tags.contains("FLASH_SALE_CAMPAIGN")

        tvDiscountLabel.text = "Diskon ${product.price.discount}%"
        if (product.review.rating == 0 && product.review.sellerRating != 0.0) {
            ivRatingStar.visibility = View.GONE
            ivShopRating.visibility = View.VISIBLE
            tvRating.visibility = View.VISIBLE
            tvRating.text = product.review.sellerRating.toString()
            dot.visibility = View.VISIBLE
            tvSoldText.visibility = View.VISIBLE
            tvSoldNumbers.visibility = View.VISIBLE
        } else if (product.review.rating != 0 && product.review.sellerRating != 0.0) {
            ivRatingStar.visibility = View.VISIBLE
            ivShopRating.visibility = View.GONE
            tvRating.visibility = View.VISIBLE
            tvRating.text = product.review.rating.toString()
            dot.visibility = View.VISIBLE
            tvSoldText.visibility = View.VISIBLE
            tvSoldNumbers.visibility = View.VISIBLE
        } else if ((product.review.rating == 0 && product.review.sellerRating == 0.0) && product.soldCountTotal != 0) {
            ivRatingStar.visibility = View.GONE
            ivShopRating.visibility = View.GONE
            tvRating.visibility = View.GONE
            dot.visibility = View.VISIBLE
            tvSoldText.visibility = View.VISIBLE
            tvSoldNumbers.visibility = View.VISIBLE
        } else {
            ivRatingStar.visibility = View.GONE
            ivShopRating.visibility = View.GONE
            tvRating.visibility = View.GONE
            dot.visibility = View.GONE
            tvSoldText.visibility = View.GONE
            tvSoldNumbers.visibility = View.GONE

        }


        tvSoldNumbers.text = product.soldCountTotal.toString()
        tvSoldText.text = "Terjual"
        tvStoreName.text = product.location
        if (product.brand != "no brand") {
            ivOfficialIcon.visibility = View.VISIBLE
        }
        ivDiamondIcon.visibility =
            if (product.badge.merchantBadge == "Diamond") View.VISIBLE else View.GONE
        Glide.with(root)
            .load(product.images.firstOrNull())
            .error(R.drawable.img_1)
            .into(ivProductImage)

        showProduct.setOnClickListener {
            onProductClick(product)
        }
        showProduct.setOnClickListener {
            onProductClick(product)
        }

    }
}
