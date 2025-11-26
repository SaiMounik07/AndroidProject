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
import com.example.androidlearning.databinding.CardProductBinding
import com.example.androidlearning.databinding.SearchFragmentBinding
import com.example.androidlearning.data.model.Product
import com.example.androidlearning.ui.search.ProductAdapter
import com.example.androidlearning.ui.search.SearchViewModel
import com.example.androidlearning.ui.search.productData
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
class ProductFragment : Fragment(R.layout.search_fragment) {
    private var allProducts: List<Product> = emptyList()
    private var displayedProducts: MutableList<Product> = mutableListOf()
    private lateinit var productAdapter: ProductAdapter
    private var isLoading = false
    lateinit var binding: SearchFragmentBinding
    lateinit var searchViewModel: SearchViewModel


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = SearchFragmentBinding.bind(view)
        val layoutManager = LinearLayoutManager(requireContext())
        with(binding) {
            recyclerView.layoutManager = layoutManager
            recyclerView.visibility = View.VISIBLE

            searchViewModel= ViewModelProvider(requireActivity())[SearchViewModel::class.java]

            allProducts =searchViewModel.loadProductsFromJson(requireContext())
            productAdapter = ProductAdapter(displayedProducts) { product ->
                showProductDetailsBottomSheet(product)
            }
            recyclerView.adapter = productAdapter

            loadMoreProducts()

            ivSearch.setOnClickListener {
                val query = etSearch.text.toString().lowercase()
                val filtered = allProducts.filter { i -> i.name.lowercase().contains(query)
                }
                productAdapter.updateData(filtered)
            }
            val btnScrollTop = view.findViewById<FloatingActionButton>(R.id.btn_scroll_top)
            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                    if (!rv.canScrollVertically(-1)) {
                        btnScrollTop.hide()
                    } else {
                        btnScrollTop.show()
                    }

                    if (!isLoading && layoutManager.findLastCompletelyVisibleItemPosition() == displayedProducts.size - 1) {
                        loadMoreProducts()
                    }
                }
            })
            btnScrollTop.setOnClickListener {
                recyclerView.smoothScrollToPosition(0)
            }
            ivBack.setOnClickListener {
                findNavController().navigateUp()
            }

            etSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    noProductsInclude.root.visibility = View.GONE
                }

                override fun afterTextChanged(s: Editable?) {
                    s?.toString()?.length?.let {
                        if (it >= 3 || it == 0) {
                            val query = s.toString().trim()
                            binding.ivClearText.visibility =
                                if (query.isEmpty()) View.GONE else View.VISIBLE
                            if (query.length < 3) {
                                binding.noProductsInclude.root.visibility = View.GONE
                                displayedProducts.clear()
                                loadMoreProducts()
                                productAdapter.updateData(displayedProducts)
                                return
                            }
                            if (!isLoading) {
                                val filtered = allProducts.filter {
                                    it.name.contains(query, ignoreCase = true)
                                }

                                noProductsInclude.root.visibility =
                                    if (filtered.isEmpty()) View.VISIBLE else View.GONE
                                productAdapter.updateData(filtered)
                            }
                        }
                    }
                }
            })
            ivClearText.setOnClickListener {
                resetSearch()
            }
            noProductsInclude.btnSearchAgain.setOnClickListener {
                resetSearch()
            }
        }
    }

    private fun resetSearch() {
        with(binding){
        etSearch.setText("")
        noProductsInclude.root.visibility = View.GONE
        displayedProducts.clear()
        loadMoreProducts()
        }
    }

    private fun showProductDetailsBottomSheet(product: Product) {
        val dialog = BottomSheetDialog(requireContext())
        val binding = CardProductBinding.inflate(LayoutInflater.from(requireContext()))
        with(binding) {
            productData(product)
            dialog.setContentView(root)
            showProduct.visibility = View.GONE
        }
        dialog.show()
    }

    private fun loadMoreProducts() {
        if (isLoading) return
        isLoading = true
        with(binding) {
            progressBar.visibility = View.VISIBLE

            Handler(Looper.getMainLooper()).postDelayed({
                val currentSize = displayedProducts.size
                val nextIndex = currentSize
                val endIndex = (nextIndex + 10).coerceAtMost(allProducts.size)

                if (nextIndex < endIndex) {
                    val newItems = allProducts.subList(nextIndex, endIndex)
                    displayedProducts.addAll(newItems)
                    productAdapter.notifyItemRangeInserted(currentSize, newItems.size)
                }

                progressBar.visibility = View.GONE
                isLoading = false

            }, 1000)
        }

    }


}
