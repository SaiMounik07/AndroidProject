package com.example.androidlearning.ui.home.fragment


import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidlearning.R
import com.example.androidlearning.base.constants.Constants.GUEST
import com.example.androidlearning.base.constants.Constants.KEY_USERNAME
import com.example.androidlearning.data.model.Product
import com.example.androidlearning.databinding.QuickActionBinding
import com.example.androidlearning.ui.addproduct.fragment.AddProductFragment
import com.example.androidlearning.ui.home.HomeActivity
import com.example.androidlearning.ui.home.HomeViewModel
import com.example.androidlearning.ui.search.ProductAdapter
import com.example.androidlearning.ui.search.fragment.ProductFragment
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment: Fragment(R.layout.quick_action) {
    val homeViewModel: HomeViewModel by viewModels()
    var binding: QuickActionBinding? = null
    private var isLoading = false
    private var displayedProducts: MutableList<Product> = mutableListOf()
    private lateinit var productAdapter: ProductAdapter
    private lateinit var layoutManager: LinearLayoutManager
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = QuickActionBinding.bind(view)
        binding?.let {
            with(it) {
                etSearchHome.requestFocus()
                val username = homeViewModel.getValueByKey(KEY_USERNAME, GUEST)
                tvLoginUser.text = "Hello ${username?.substringBefore("@")}"
                etSearchHome.setOnClickListener {
                    (activity as HomeActivity).replaceFragment(ProductFragment(),selectBottomId=R.id.nav_search)
                }

                homeContent.cardSearch.setOnClickListener {
                    (activity as HomeActivity).replaceFragment(ProductFragment(),selectBottomId=R.id.nav_search)

                }
                homeContent.cardAddProduct.setOnClickListener {
                    (activity as HomeActivity).replaceFragment(AddProductFragment(),selectBottomId=R.id.nav_add_product)
                }
            }
        }
        setupRecyclerView()
        observeViewModel()
        handleClickHandlers()
        homeViewModel.loadInitialProducts()
    }

    private fun handleClickHandlers() {
        binding?.cartCard?.ivDeleteAll?.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Delete All Products")
                .setMessage("Are you sure you want to delete all products?")
                .setPositiveButton("Delete") { _, _ ->
                    homeViewModel.clearCart()
                    displayedProducts.clear()
                    productAdapter.notifyDataSetChanged()
                    updateEmptyState()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun setupRecyclerView() {
        layoutManager = LinearLayoutManager(context)
        with(binding?.cartCard?.cartRecycler) {
            this?.layoutManager = this@HomeFragment.layoutManager
            this?.visibility = View.VISIBLE
            productAdapter = ProductAdapter(
                products = displayedProducts,
                onDeleteClick = { product, position ->
                    handleDeleteProduct(product, position)
                },
                screenName = "HOME"
            )
            this?.adapter = productAdapter
            this?.addOnScrollListener(createScrollListener())
        }

    }
    private fun handleDeleteProduct(product: Product, position: Int) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Product")
            .setMessage("Are you sure you want to delete ${product.name}?")
            .setPositiveButton("Delete") { _, _ ->
                val removedProduct = displayedProducts[position]
                displayedProducts.removeAt(position)
                productAdapter.notifyItemRemoved(position)
                var snackbar= binding?.root?.let {
                    Snackbar.make(
                        it,
                        "Product deleted",
                        Snackbar.LENGTH_SHORT
                    )
                }?.setAction("UNDO") {
                    displayedProducts.add(position, removedProduct)
                    productAdapter.notifyItemInserted(position)
                }
                snackbar?.addCallback(object : Snackbar.Callback() {
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        if (event == DISMISS_EVENT_ACTION) return

                        if (!displayedProducts.contains(removedProduct)) {
                            homeViewModel.deleteProduct(removedProduct)
                        }
                    }
                })
                    ?.show()
            }

            .setNegativeButton("Cancel", null)
            .show()
    }
    private fun createScrollListener() = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
            handlePagination()
        }
    }
    private fun handlePagination() {
        val isAtBottom = layoutManager.findLastCompletelyVisibleItemPosition() >= displayedProducts.size - 2
        val isPaginating = homeViewModel.isPaginating.value == true
        val hasMore = homeViewModel.hasMoreProducts()

        if (!isLoading && !isPaginating && isAtBottom && hasMore) {
            homeViewModel.loadNextPage()
        }
    }
    private fun observeViewModel() {
        homeViewModel.products.observe(viewLifecycleOwner) { products ->
            if (products.isNotEmpty()) {
                val startPosition = displayedProducts.size
                displayedProducts.addAll(products)
                productAdapter.notifyItemRangeInserted(startPosition, products.size)
                updateEmptyState()
            }

        }
        
        homeViewModel.isPaginating.observe(viewLifecycleOwner) { isPaginating ->
            isLoading = isPaginating
            binding?.cartCard?.loadingLayout?.visibility = if (isPaginating) View.VISIBLE else View.GONE
            binding?.noProductsCart?.root?.visibility = View.GONE

        }
        

    }
    
    private fun updateEmptyState() {
        if (displayedProducts.isEmpty() && !isLoading) {
            binding?.cartCard?.ivDeleteAll?.visibility = View.GONE
            binding?.noProductsCart?.root?.visibility = View.VISIBLE
            binding?.noProductsCart?.btnSearchAgain?.visibility = View.GONE
        } else {
            binding?.cartCard?.ivDeleteAll?.visibility = View.VISIBLE
            binding?.noProductsCart?.root?.visibility = View.GONE
        }
    }


}