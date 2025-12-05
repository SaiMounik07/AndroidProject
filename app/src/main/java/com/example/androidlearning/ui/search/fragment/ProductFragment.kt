package com.example.androidlearning.ui.search.fragment

import android.app.AlertDialog
import android.content.res.Resources
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidlearning.R
import com.example.androidlearning.base.constants.Constants
import com.example.androidlearning.base.constants.Constants.ITEMS_PER_PAGE
import com.example.androidlearning.data.model.Product
import com.example.androidlearning.data.repository.ProductRepository
import com.example.androidlearning.databinding.CardProductBinding
import com.example.androidlearning.databinding.SearchFragmentBinding
import com.example.androidlearning.ui.addproduct.fragment.AddProductFragment
import com.example.androidlearning.ui.search.ProductAdapter
import com.example.androidlearning.ui.search.SearchViewModel
import com.example.androidlearning.ui.search.productData
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProductFragment : Fragment(R.layout.search_fragment) {
    private var displayedProducts: MutableList<Product> = mutableListOf()
    private lateinit var productAdapter: ProductAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private var isLoading = false
    private lateinit var binding: SearchFragmentBinding
    private val searchViewModel: SearchViewModel by viewModels()
    private var searchRunnable: Runnable? = null
    private val searchHandler = Handler(Looper.getMainLooper())



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = SearchFragmentBinding.bind(view)

        setupRecyclerView()
        setupSearchListeners()
        setupClickListeners()
        observeViewModel()
        
        binding.noProductsInclude.root.visibility = View.VISIBLE
        binding.noProductsInclude.btnSearchAgain.text = "Start Searching"

        searchViewModel.checkSession()
    }

    private fun setupRecyclerView() {
        layoutManager = LinearLayoutManager(requireContext())
        with(binding.recyclerView) {
            this.layoutManager = this@ProductFragment.layoutManager
            visibility = View.VISIBLE
            productAdapter = ProductAdapter(
                products = displayedProducts,
                onProductClick = { product ->
                    showProductDetailsBottomSheet(product)
                },
                isGridView = false,
                onEditClick = { product, position ->
                    handleEditProduct(product, position)
                },
                onDeleteClick = { product, position ->
                    handleDeleteProduct(product, position)
                },
                screenName = "SEARCH"
            )
            adapter = productAdapter
            addOnScrollListener(createScrollListener())
        }
    }

    private fun handleEditProduct(product: Product, position: Int) {
        val fragment = AddProductFragment()
        val bundle = Bundle().apply {
            putBoolean("EDIT_MODE", true)
            putString("PRODUCT_NAME", product.name)
            putString("PRODUCT_BRAND", product.brand)
            putDouble("PRODUCT_SALE_PRICE", product.price.salePrice)
            putDouble("PRODUCT_LIST_PRICE", product.price.listPrice)
            putString("PRODUCT_LOCATION", product.location)
            putString("PRODUCT_IMAGE", product.images.firstOrNull() ?: "")
            putBoolean("PRODUCT_FREE_SHIPPING", product.tags.contains("FREE_SHIPPING"))
            putBoolean("PRODUCT_FREE_GIFT", product.tags.contains("FREE_GIFT"))
            putBoolean("PRODUCT_FLASH_SALE", product.tags.contains("FLASH_SALE_CAMPAIGN"))
            putBoolean("PRODUCT_OFFICIAL_STORE", product.brand != "no brand")
            putBoolean("PRODUCT_DIAMOND_STORE", product.badge.merchantBadge == "Diamond")
        }
        fragment.arguments = bundle
        
        parentFragmentManager.beginTransaction()
            .replace(R.id.home_page, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun handleDeleteProduct(product: Product, position: Int) {
       AlertDialog.Builder(requireContext())
            .setTitle("Delete Product")
            .setMessage("Are you sure you want to delete ${product.name}?")
            .setPositiveButton("Delete") { _, _ ->
                val removedProduct = displayedProducts[position]
                displayedProducts.removeAt(position)
                productAdapter.notifyItemRemoved(position)
                var snackbar=Snackbar.make(
                    binding.root,
                    "Product deleted",
                    Snackbar.LENGTH_SHORT
                ).setAction("UNDO") {
                    displayedProducts.add(position, removedProduct)
                    productAdapter.notifyItemInserted(position)
                }
                    snackbar.addCallback(object : Snackbar.Callback() {
                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            if (event == DISMISS_EVENT_ACTION) return

                            if (!displayedProducts.contains(removedProduct)) {
                                searchViewModel.deleteProduct(removedProduct)
                            }
                        }
                    })
                    .show()
            }

            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun observeViewModel() {
        searchViewModel.searchResults.observe(viewLifecycleOwner) { products ->
            Log.d("ProductFragment", "Received ${products.size} total products")
            
            displayedProducts.clear()
            displayedProducts.addAll(products)
            productAdapter.notifyDataSetChanged()
            
            if (products.isEmpty()) {
                binding.noProductsInclude.root.visibility = View.VISIBLE
                binding.noProductsInclude.btnSearchAgain.text = "Clear Search"
            } else {
                binding.noProductsInclude.root.visibility = View.GONE
            }
        }

        searchViewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            isLoading = loading
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
            binding.recyclerView.visibility = if (loading) View.GONE else View.VISIBLE
        }
        
        searchViewModel.isPaginating.observe(viewLifecycleOwner) { paginating ->
            productAdapter.showLoadingIndicator(paginating)
            if (displayedProducts.isNotEmpty()) {
                productAdapter.notifyItemChanged(displayedProducts.size - 1)
            }
        }
        
        searchViewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                displayedProducts.clear()
                binding.noProductsInclude.root.visibility= View.VISIBLE
                AlertDialog.Builder(requireContext())
                    .setTitle("Error")
                    .setMessage("Unable to fetch Products")
                    .setPositiveButton("OK", null)
                    .show()

                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
            }
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
                parentFragmentManager.popBackStack()
            }
            ivClearText.setOnClickListener {
                resetSearch()
            }
            noProductsInclude.btnSearchAgain.setOnClickListener {
                resetSearch()
            }
            ivToggleView.setOnClickListener {
                toggleViewType()
            }
            fabAddProduct.setOnClickListener {
                navigateToAddProduct()
            }

        }
    }

    private fun navigateToAddProduct() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.home_page, AddProductFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun toggleViewType() {
        val isCurrentlyGrid = layoutManager is GridLayoutManager

        if (isCurrentlyGrid) {
            layoutManager = LinearLayoutManager(requireContext())
            binding.ivToggleView.setImageResource(R.drawable.linear)
        } else {
            layoutManager = GridLayoutManager(requireContext(), 2)
            binding.ivToggleView.setImageResource(R.drawable.gridview)
        }

        binding.recyclerView.layoutManager = layoutManager
        productAdapter.toggleViewType(!isCurrentlyGrid)
        binding.recyclerView.addOnScrollListener(createScrollListener())
    }

    private fun createScrollListener() = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
            handleScrollTopButtonVisibility(rv)
            handlePagination()
        }
    }

    private fun handleScrollTopButtonVisibility(rv: RecyclerView) {
        if (!rv.canScrollVertically(-1) || isLoading) {
            binding.btnScrollTop.hide()
        } else {
            binding.btnScrollTop.show()
        }
    }

    private fun handlePagination() {
        val isAtBottom = layoutManager.findLastCompletelyVisibleItemPosition() >= displayedProducts.size - 3
        val isPaginating = searchViewModel.isPaginating.value == true
        
        if (!isLoading && !isPaginating && isAtBottom && searchViewModel.hasMorePages()) {
            searchViewModel.loadNextPage()
        }
    }

    private fun handleSearchTextChange(text: String) {
        val query = text.trim()
        binding.ivClearText.visibility = if (query.isEmpty()) View.GONE else View.VISIBLE

        searchRunnable?.let { searchHandler.removeCallbacks(it) }
        searchRunnable = Runnable {
            if (query.length < Constants.MIN_SEARCH_LENGTH && query.isNotEmpty()) {
                handleSearchReset()
            } else {
                performSearch(query)
            }
        }
        searchRunnable?.let {
            searchHandler.postDelayed(it, Constants.SEARCH_DEBOUNCE_MS)
        }
    }

    private fun handleSearchReset() {
        binding.noProductsInclude.root.visibility = View.GONE
    }

    private fun performSearch(query: String) {
        if (query.length >= Constants.MIN_SEARCH_LENGTH) {
            searchViewModel.searchProducts(query)
        } else if (query.isEmpty()) {

            displayedProducts.clear()
            productAdapter.notifyDataSetChanged()
            binding.noProductsInclude.root.visibility = View.VISIBLE
//            binding.noProductsInclude.tvNoProducts.text = "Search for products"
            binding.noProductsInclude.btnSearchAgain.text = "Start Searching"
        }
    }

    private fun resetSearch() {
        binding.etSearch.setText("")
        searchViewModel.clearSearch()
//        showEmptyState()
    }

    private fun showProductDetailsBottomSheet(product: Product) {
        val dialog = BottomSheetDialog(requireContext())
        val bottomSheetBinding = CardProductBinding.inflate(LayoutInflater.from(requireContext()))
        with(bottomSheetBinding) {
            productData(product, screenName = "SEARCH")
            dialog.setContentView(root)
            showProduct.visibility = View.GONE
            ivMenu.visibility= View.GONE
            addProduct.visibility= View.VISIBLE

            addProduct.setOnClickListener {
                searchViewModel.addTheProductToCartByUserId(product, onSuccess = {
                    Snackbar.make(binding.root, "Product added to cart", Snackbar.LENGTH_SHORT).show()
                }, onFailure = {
                    Snackbar.make(binding.root, "Failed to add product to cart", Snackbar.LENGTH_SHORT).show()
                })
                dialog.dismiss()
            }

        }
        val bottomSheet = dialog.behavior
        dialog.setOnShowListener {
            bottomSheet.peekHeight = Resources.getSystem().displayMetrics.heightPixels
            bottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
        }
        dialog.show()
    }
    override fun onResume() {
        super.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerView.adapter=null
        searchRunnable?.let { searchHandler.removeCallbacks(it) }
    }

}
