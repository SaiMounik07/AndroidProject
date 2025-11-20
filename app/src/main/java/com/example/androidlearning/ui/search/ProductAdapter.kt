package com.example.androidlearning.ui.search

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidlearning.R
import com.example.androidlearning.model.Product

class ProductAdapter(private var products: List<Product>): RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductAdapter.ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_product, parent, false)
        return ProductViewHolder(view)
    }
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.tvProductName.text = product.name
        holder.tvProductPrice.text = product.price.priceDisplay
        holder.tvActualPrice.text = product.price.strikeThroughPriceDisplay
        holder.tvActualPrice.paintFlags =
        holder.tvActualPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        if (holder.tvActualPrice.text.isEmpty()) {
            holder.tvActualPrice.visibility = View.GONE
            holder.tvDiscountLabel.visibility = View.GONE
            holder.tvDiscountPercent.visibility = View.GONE
        }

        holder.tvDiscountPercent.text = "${product.price.discount}%"
        holder.tvFreeShipping.visibility =
            if (product.uspLabelsTags.contains("FREE_SHIPPING")) View.VISIBLE else View.GONE

        holder.tvDiscountLabel.text = "Diskon ${product.price.discount}%"
        holder.tvRating.text = product.review.rating.toString()
        holder.tvSoldNumbers.text = product.soldCountTotal.toString()
        holder.tvSoldText.text = "Terjual"
        holder.tvStoreName.text = product.location
        if (!product.brand.equals("no brand")){
            holder.ivOfficialIcon.visibility= View.VISIBLE
        }
        holder.ivDiamondIcon.visibility =
            if (product.badge.merchantBadge == "Diamond") View.VISIBLE else View.GONE
        Glide.with(holder.itemView)
            .load(product.images.firstOrNull())
            .error(R.drawable.img_1)
            .into(holder.ivProductImage)

    }

    override fun getItemCount(): Int {
       return products.size
    }
    class ProductViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val ivProductImage = itemView.findViewById<ImageView>(R.id.iv_product_image)
        val tvProductName = itemView.findViewById<TextView>(R.id.product_name)
        val tvProductPrice = itemView.findViewById<TextView>(R.id.product_price)
        val ivTicketIcon = itemView.findViewById<ImageView>(R.id.iv_tiket_icon)
        val tvActualPrice = itemView.findViewById<TextView>(R.id.tv_actual_price)
        val tvDiscountPercent = itemView.findViewById<TextView>(R.id.tv_dicount_percent)
        val tvFreeShipping = itemView.findViewById<TextView>(R.id.tv_free_shipping_text)
        val tvDiscountLabel = itemView.findViewById<TextView>(R.id.tv_dicount_label)
        val ivRatingStar = itemView.findViewById<ImageView>(R.id.iv_rating_star)
        val tvRating = itemView.findViewById<TextView>(R.id.tv_rating)
        val tvDot = itemView.findViewById<TextView>(R.id.dot)
        val tvSoldText = itemView.findViewById<TextView>(R.id.tv_sold_text)
        val tvSoldNumbers = itemView.findViewById<TextView>(R.id.tv_sold_numbers)
        val ivOfficialIcon = itemView.findViewById<ImageView>(R.id.iv_official_icon)
        val ivDiamondIcon = itemView.findViewById<ImageView>(R.id.iv_diamond_icon)
        val tvStoreName = itemView.findViewById<TextView>(R.id.tv_store_name)
    }
    fun updateData(newProducts: List<Product>) {
        products = newProducts
        notifyDataSetChanged()
    }

}

