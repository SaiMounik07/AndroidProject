package com.example.androidlearning.ui.search

import android.os.Bundle
import android.os.PersistableBundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidlearning.R
import com.example.androidlearning.model.Product
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import android.view.inputmethod.InputMethodManager
import com.google.android.material.floatingactionbutton.FloatingActionButton


class SearchActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val json = assets.open("product.json")
            .bufferedReader()
            .use { it.readText() }

        val gson = Gson()
        val type = object : TypeToken<List<Product>>() {}.type
        val products: List<Product> = gson.fromJson(json, type)
        var productAdapter=ProductAdapter(products)

        recyclerView.adapter = productAdapter
        var searchText = findViewById<EditText>(R.id.et_search)
        var searchButton = findViewById<ImageView>(R.id.iv_search)
        var clearButton = findViewById<ImageView>(R.id.iv_clear_text)


        searchButton.setOnClickListener {
            productAdapter.updateData(products.filter { i->i.name.lowercase().contains(searchText.text.toString().lowercase()) }.toList())

        }
        val btnScrollTop = findViewById<FloatingActionButton>(R.id.btn_scroll_top)
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                if (!rv.canScrollVertically(-1)) {
                    btnScrollTop.hide()
                } else {
                    btnScrollTop.show()
                }
            }
        })
        btnScrollTop.setOnClickListener {
            recyclerView.smoothScrollToPosition(0)
        }
        searchText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {  }

            override fun afterTextChanged(s: Editable?) {
                val query = s?.toString()?.trim().orEmpty()
                clearButton.visibility = if (query.isEmpty()) View.GONE else View.VISIBLE

                val filtered = if (query.isEmpty()) {
                    products
                } else {
                    products.filter { it.name.contains(query, ignoreCase = true) }
                }

                productAdapter.updateData(filtered)
            }
        })
        clearButton.setOnClickListener {
            searchText.setText("")
        }

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