package com.example.androidlearning.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidlearning.R
import com.example.androidlearning.base.constants.Constants.LOAD_DELAY_MS
import com.example.androidlearning.base.constants.Constants.MIN_SEARCH_LENGTH
import com.example.androidlearning.base.constants.Constants.SEARCH_DEBOUNCE_MS
import com.example.androidlearning.databinding.CardProductBinding
import com.example.androidlearning.databinding.SearchFragmentBinding
import com.example.androidlearning.data.model.Product
import com.example.androidlearning.ui.search.ProductAdapter
import com.example.androidlearning.ui.search.SearchViewModel
import com.example.androidlearning.ui.search.productData
import com.google.android.material.bottomsheet.BottomSheetDialog

class ProductFragment : Fragment(R.layout.search_fragment) {
    private var displayedProducts: MutableList<Product> = mutableListOf()
    private lateinit var productAdapter: ProductAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private var isLoading = false
    private lateinit var binding: SearchFragmentBinding
    private lateinit var searchViewModel: SearchViewModel
    
    private var searchRunnable: Runnable? = null
    private val searchHandler = Handler(Looper.getMainLooper())


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = SearchFragmentBinding.bind(view)
        searchViewModel = ViewModelProvider(requireActivity())[SearchViewModel::class.java]

        setupRecyclerView()
        setupSearchListeners()
        setupClickListeners()
        loadInitialData()
    }

    private fun setupRecyclerView() {
        layoutManager = LinearLayoutManager(requireContext())
        with(binding.recyclerView) {
            this.layoutManager = this@ProductFragment.layoutManager
            visibility = View.VISIBLE
            productAdapter = ProductAdapter(displayedProducts) { product ->
                showProductDetailsBottomSheet(product)
            }
            adapter = productAdapter
            addOnScrollListener(createScrollListener())
        }
    }

    private fun setupSearchListeners() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.noProductsInclude.root.visibility = View.GONE
            }

            override fun afterTextChanged(s: Editable?) {
                handleSearchTextChange(s?.toString() ?: "")
            }
        })
    }

    private fun setupClickListeners() {
        with(binding) {
            btnScrollTop.setOnClickListener {
                recyclerView.smoothScrollToPosition(0)
            }
            ivBack.setOnClickListener {
                findNavController().navigateUp()
            }
            ivClearText.setOnClickListener {
                resetSearch()
            }
            noProductsInclude.btnSearchAgain.setOnClickListener {
                resetSearch()
            }
        }
    }

    private fun loadInitialData() {
        searchViewModel.loadProductsFromJson(requireContext())
        loadMoreProducts()
    }

    private fun createScrollListener() = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
            handleScrollTopButtonVisibility(rv)
            handlePagination()
        }
    }

    private fun handleScrollTopButtonVisibility(rv: RecyclerView) {
        if (!rv.canScrollVertically(-1)) {
            binding.btnScrollTop.hide()
        } else {
            binding.btnScrollTop.show()
        }
    }

    private fun handlePagination() {
        val isAtBottom = layoutManager.findLastCompletelyVisibleItemPosition() == displayedProducts.size - 1
        if (!isLoading && isAtBottom && searchViewModel.hasMoreItems()) {
            loadMoreProducts()
        }
    }

    private fun handleSearchTextChange(text: String) {
        val query = text.trim()
        binding.ivClearText.visibility = if (query.isEmpty()) View.GONE else View.VISIBLE

        searchRunnable?.let { searchHandler.removeCallbacks(it) }
        searchRunnable = Runnable {
            if (query.length < MIN_SEARCH_LENGTH && query.isNotEmpty()) {
                handleSearchReset()
            } else {
                performSearch(query)
            }
        }
        searchRunnable?.let { 
            searchHandler.postDelayed(it, SEARCH_DEBOUNCE_MS)
        }
    }

    private fun handleSearchReset() {
        binding.noProductsInclude.root.visibility = View.GONE
    }

    private fun performSearch(query: String) {
        val filteredProducts = searchViewModel.filterProducts(query)
        val shouldShowNoResults = searchViewModel.shouldShowNoResults(filteredProducts, query)
        
        binding.noProductsInclude.root.visibility = 
            if (shouldShowNoResults) View.VISIBLE else View.GONE
        
        searchViewModel.updateSourceList(filteredProducts)
        displayedProducts.clear()
        productAdapter.notifyDataSetChanged()
        loadMoreProducts()
    }

    private fun resetSearch() {
        binding.etSearch.setText("")
        binding.noProductsInclude.root.visibility = View.GONE
        searchViewModel.resetToAllProducts()
        displayedProducts.clear()
        productAdapter.notifyDataSetChanged()
        loadMoreProducts()
    }

    private fun showProductDetailsBottomSheet(product: Product) {
        val dialog = BottomSheetDialog(requireContext())
        val bottomSheetBinding = CardProductBinding.inflate(LayoutInflater.from(requireContext()))
        with(bottomSheetBinding) {
            productData(product)
            dialog.setContentView(root)
            showProduct.visibility = View.GONE
        }
        dialog.show()
    }

    private fun loadMoreProducts() {
        if (isLoading || !searchViewModel.hasMoreItems()) return
        
        isLoading = true
        showLoadingIndicator(true)

        Handler(Looper.getMainLooper()).postDelayed({
            val currentSize = displayedProducts.size
            val newItems = searchViewModel.getNextPageItems()

            if (newItems.isNotEmpty()) {
                displayedProducts.addAll(newItems)
                productAdapter.notifyItemRangeInserted(currentSize, newItems.size)
            }

            showLoadingIndicator(false)
            isLoading = false
        }, LOAD_DELAY_MS)
    }

    private fun showLoadingIndicator(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        searchRunnable?.let { searchHandler.removeCallbacks(it) }
    }
}
