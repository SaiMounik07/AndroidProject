package com.example.androidlearning.ui.home.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.androidlearning.R
import com.example.androidlearning.data.model.Banner
import com.example.androidlearning.databinding.FragmentBannerBinding
import com.example.androidlearning.ui.home.BannerAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BannerFragment : Fragment(R.layout.fragment_banner) {
    
    private lateinit var binding: FragmentBannerBinding
    private lateinit var bannerAdapter: BannerAdapter
    private val handler = Handler(Looper.getMainLooper())
    private var currentPage = 0
    private val autoScrollDelay = 3000L
    
    private val banners = listOf(
        Banner(1, "https://www.static-src.com/siva/asset/12_2025/Master_Pan-Olivia_Tommy_3Dec-900x300-Homepage_Carousel_Mobile.jpg?w=839"),
        Banner(2, "https://www.static-src.com/siva/asset/12_2025/ovodes25900x300.jpg?w=839"),
        Banner(3, "https://www.static-src.com/siva/asset/11_2025/CRTV-9196_Homepage_Mobile_Lokcer_11_25-compress.png?w=839"),
        Banner(4, "https://www.static-src.com/siva/asset/12_2025/900x300-donasi-bencana-rev.png?w=839"),
        Banner(5, "https://www.static-src.com/siva/asset/12_2025/CRTV-9175_CAROUSEL_900x300_AnainBranDay2_11_25.png?w=839")
    )
    
    private val autoScrollRunnable = object : Runnable {
        override fun run() {
            if (::binding.isInitialized) {
                currentPage++
                binding.viewPager.setCurrentItem(currentPage-1, true)
                handler.postDelayed(this, autoScrollDelay)
            }
        }
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentBannerBinding.bind(view)
        
        setupViewPager()
        setupIndicators()
        startAutoScroll()
    }

    private fun setupViewPager() {
        bannerAdapter = BannerAdapter(banners) { banner ->
            android.widget.Toast.makeText(
                requireContext(),
                "Banner ${banner.id} clicked",
                android.widget.Toast.LENGTH_SHORT
            ).show()
        }

        binding.viewPager.adapter = bannerAdapter
        val startPosition = Int.MAX_VALUE / 2
        val offset = if (banners.isNotEmpty()) startPosition % banners.size else 0
        currentPage = startPosition - offset
        binding.viewPager.setCurrentItem(currentPage, false)
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentPage = position
                updateIndicators(position % banners.size)
            }
        })
    }
    
    private fun setupIndicators() {
        binding.indicatorContainer.removeAllViews()
        
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(4, 0, 4, 0)
        }
        
        for (i in banners.indices) {
            val indicator = ImageView(requireContext())
            indicator.setImageDrawable(
                ContextCompat.getDrawable(requireContext(), R.drawable.indicator_inactive)
            )
            binding.indicatorContainer.addView(indicator, layoutParams)
        }

        updateIndicators(0)
    }
    
    private fun updateIndicators(position: Int) {
        val actualPosition = position % banners.size
        
        for (i in 0 until binding.indicatorContainer.childCount) {
            val indicator = binding.indicatorContainer.getChildAt(i) as? ImageView
            indicator?.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    if (i == actualPosition) R.drawable.indicator_active else R.drawable.indicator_inactive
                )
            )
        }
    }
    
    private fun startAutoScroll() {
        handler.postDelayed(autoScrollRunnable, autoScrollDelay)
    }
    
    private fun stopAutoScroll() {
        handler.removeCallbacks(autoScrollRunnable)
    }
    
    override fun onResume() {
        super.onResume()
        startAutoScroll()
    }
    
    override fun onPause() {
        super.onPause()
        stopAutoScroll()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        stopAutoScroll()
    }
}
