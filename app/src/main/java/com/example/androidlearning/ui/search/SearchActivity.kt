package com.example.androidlearning.ui.search

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidlearning.R
import com.example.androidlearning.model.Product
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton

class SearchActivity : AppCompatActivity() {

    private var allProducts: List<Product> = emptyList()
    private var displayedProducts: MutableList<Product> = mutableListOf()
    private lateinit var productAdapter: ProductAdapter
    private var isLoading = false
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.recycler_view_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        progressBar = findViewById(R.id.progressBar)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        val json = assets.open("product.json")
            .bufferedReader()
            .use { it.readText() }

        val gson = Gson()
        val type = object : TypeToken<List<Product>>() {}.type
        allProducts = gson.fromJson(json, type)

        productAdapter = ProductAdapter(displayedProducts)
        recyclerView.adapter = productAdapter

        loadMoreProducts()

        var searchText = findViewById<EditText>(R.id.et_search)
        var searchButton = findViewById<ImageView>(R.id.iv_search)
        var clearButton = findViewById<ImageView>(R.id.iv_clear_text)
        val noProductsFound = findViewById<ConstraintLayout>(R.id.noProductsInclude)
        val searchAgain = noProductsFound.findViewById<MaterialButton>(R.id.btnSearchAgain)

        searchButton.setOnClickListener {
            val query = searchText.text.toString().lowercase()
            val filtered = allProducts.filter { i -> i.name.lowercase().contains(query) }
            productAdapter.updateData(filtered)
        }

        val btnScrollTop = findViewById<FloatingActionButton>(R.id.btn_scroll_top)
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

        searchText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val query = s?.toString()?.trim().orEmpty()
                clearButton.visibility = if (query.isEmpty()) View.GONE else View.VISIBLE
                if (query.length < 3) {
                    noProductsFound.visibility = View.GONE
                    displayedProducts.clear()
                    loadMoreProducts()
                    productAdapter.updateData(displayedProducts)
                    return
                }

                val filtered = allProducts.filter {
                    it.name.contains(query, ignoreCase = true)
                }
                noProductsFound.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
                productAdapter.updateData(filtered)
            }
        })

        clearButton.setOnClickListener {
            searchText.setText("")
        }
        searchAgain.setOnClickListener {
            searchText.setText("")
        }
    }

    private fun loadMoreProducts() {
        if (isLoading) return
        isLoading = true
        progressBar.visibility = View.VISIBLE

        Handler(Looper.getMainLooper()).postDelayed({
            val nextIndex = displayedProducts.size
            val endIndex = (nextIndex + 10).coerceAtMost(allProducts.size)
            if (nextIndex < endIndex) {
                displayedProducts.addAll(allProducts.subList(nextIndex, endIndex))
                productAdapter.notifyDataSetChanged()
            }
            progressBar.visibility = View.GONE
            isLoading = false
        }, 1000)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
            currentFocus!!.clearFocus()
        }
        return super.dispatchTouchEvent(ev)
    }
}
