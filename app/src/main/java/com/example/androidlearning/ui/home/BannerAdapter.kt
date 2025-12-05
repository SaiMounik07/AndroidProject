package com.example.androidlearning.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidlearning.R
import com.example.androidlearning.data.model.Banner
import com.example.androidlearning.databinding.ItemBannerBinding

class BannerAdapter(
    private val banners: List<Banner>,
    private val onBannerClick: (Banner) -> Unit = {}
) : RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val binding = ItemBannerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BannerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        val actualPosition = position % banners.size
        holder.bind(banners[actualPosition])
    }

    override fun getItemCount(): Int {
        return if (banners.isEmpty()) 0 else Int.MAX_VALUE
    }

    inner class BannerViewHolder(private val binding: ItemBannerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(banner: Banner) {
            Glide.with(binding.root.context)
                .load(banner.imageUrl)
                .placeholder(R.drawable.img_1)
                .error(R.drawable.img_1)
                .fitCenter()
                .into(binding.ivBanner)

            binding.root.setOnClickListener {
                onBannerClick(banner)
            }
        }
    }
}
