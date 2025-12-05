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
import com.example.androidlearning.databinding.CardProductGridBinding
import com.example.androidlearning.data.model.Product

class ProductAdapter(
    private var products: List<Product>, 
    private val onProductClick: (Product) -> Unit={},
    private var isGridView: Boolean = false,
    private val screenName: String? ,
    private val onEditClick: ((Product, Int) -> Unit)? = null,
    private val onDeleteClick: ((Product, Int) -> Unit)? = null

): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
     var flag = false
    var lastProduct=false
    
    companion object {
        const val VIEW_TYPE_LIST = 0
        const val VIEW_TYPE_GRID = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (isGridView) VIEW_TYPE_GRID else VIEW_TYPE_LIST
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_GRID) {
            val binding = CardProductGridBinding.inflate(
                LayoutInflater.from(parent.context), 
                parent, 
                false
            )
            GridViewHolder(binding)
        } else {
            val binding = CardProductBinding.inflate(
                LayoutInflater.from(parent.context), 
                parent, 
                false
            )
            ListViewHolder(binding)
        }
    }
    fun showLoadingIndicator(flag:Boolean){
       this.flag=flag
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val product = products[position]
        when (holder) {
            is ListViewHolder -> holder.binding.productData(
                product,
                onProductClick,
                onEditClick,
                onDeleteClick,
                holder,
                lastElement = (flag && position==(products.size-1)&&!lastProduct),
                screenName=screenName
            )
            is GridViewHolder -> holder.binding.productDataGrid(
                product,
                onProductClick,
                onEditClick,
                onDeleteClick,
                holder,
                lastElement = (flag && position==(products.size-1)&&!lastProduct)

            )

        }
    }

    override fun getItemCount(): Int {
        return products.size
    }

    class ListViewHolder(val binding: CardProductBinding) : RecyclerView.ViewHolder(binding.root)
    class GridViewHolder(val binding: CardProductGridBinding) : RecyclerView.ViewHolder(binding.root)


    fun toggleViewType(isGrid: Boolean) {
        isGridView = isGrid
        notifyDataSetChanged()
    }
}

fun CardProductBinding.productData(
    product: Product,
    onProductClick: (Product) -> Unit = {},
    onEditClick: ((Product, Int) -> Unit)? = null,
    onDeleteClick: ((Product, Int) -> Unit)? = null,
    holder: RecyclerView.ViewHolder?=null,
    lastElement: Boolean=false,
    screenName: String?
) {

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

        addProduct.visibility=View.GONE

        ivMenu.setOnClickListener { view ->
            val popup = android.widget.PopupMenu(view.context, view)
            popup.menuInflater.inflate(R.menu.product_menu, popup.menu)
            popup.setOnMenuItemClickListener { menuItem ->
                val currentPosition = holder?.bindingAdapterPosition
                if (currentPosition != RecyclerView.NO_POSITION) {
                    when (menuItem.itemId) {
                        R.id.action_edit -> {
                            onEditClick?.invoke(product, currentPosition?.toInt() ?: 0)
                            true
                        }
                        R.id.action_delete -> {
                            onDeleteClick?.invoke(product, currentPosition?.toInt() ?: 0)
                            true
                        }
                        else -> false
                    }
                } else {
                    false
                }
            }
            popup.show()
        }
        if(lastElement)
            progressBar.visibility= View.VISIBLE
        else{
            progressBar.visibility= View.GONE
        }
        if (screenName.equals("HOME")){
            ivMenu.visibility=View.GONE
            addProduct.visibility=View.GONE
            showProduct.visibility=View.GONE
            ivDeleteIcon.visibility=View.VISIBLE
            ivDeleteIcon.setOnClickListener {
                onDeleteClick?.invoke(product, holder?.bindingAdapterPosition ?: 0)
            }
        }
        else {
            ivMenu.visibility = View.VISIBLE
            addProduct.visibility=View.GONE
            showProduct.visibility=View.VISIBLE
            ivDeleteIcon.visibility=View.GONE
        }

    }
}


fun CardProductGridBinding.productDataGrid(
    product: Product, 
    onProductClick: (Product) -> Unit = {},
    onEditClick: ((Product, Int) -> Unit)? = null,
    onDeleteClick: ((Product, Int) -> Unit)? = null,
    holder: RecyclerView.ViewHolder,
    lastElement: Boolean=false
) {
        progressBar.visibility = View.GONE
        
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
            addProduct.visibility=View.GONE

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

            ivMenu.setOnClickListener { view ->
                val popup = android.widget.PopupMenu(view.context, view)
                popup.menuInflater.inflate(R.menu.product_menu, popup.menu)
                popup.setOnMenuItemClickListener { menuItem ->
                    val currentPosition = holder.bindingAdapterPosition
                    if (currentPosition != RecyclerView.NO_POSITION) {
                        when (menuItem.itemId) {
                            R.id.action_edit -> {
                                onEditClick?.invoke(product, currentPosition)
                                true
                            }
                            R.id.action_delete -> {
                                onDeleteClick?.invoke(product, currentPosition)
                                true
                            }
                            else -> false
                        }
                    } else {
                        false
                    }
                }
                if(lastElement)
                    progressBar.visibility= View.VISIBLE
                else{

                    progressBar.visibility= View.GONE
                }
                popup.show()
            }

        }

}
