package com.example.androidlearning.ui.home

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.androidlearning.R
import com.example.androidlearning.base.constants.Constants.GUEST
import com.example.androidlearning.base.constants.Constants.USERNAME
import com.example.androidlearning.databinding.ActivityHomeBinding
import com.example.androidlearning.ui.addproduct.AddProductActivity
import com.example.androidlearning.ui.login.ConstraintLoginActivity
import com.example.androidlearning.ui.search.SearchActivity
import com.google.android.material.snackbar.Snackbar

class HomeActivity:AppCompatActivity() {
    lateinit var homeViewModel: HomeViewModel
    var binding: ActivityHomeBinding?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        binding?.let {
           with(it) {
               setContentView(root)
               ViewCompat.setOnApplyWindowInsetsListener(home) { v, insets ->
                   val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                   v.setPadding(
                       systemBars.left,
                       systemBars.top,
                       systemBars.right,
                       systemBars.bottom
                   )
                   insets
               }
               homeViewModel = ViewModelProvider(this@HomeActivity)[HomeViewModel::class.java]
               etSearchHome.requestFocus()
               val username = homeViewModel.getValueByKey(USERNAME, GUEST)
               tvLoginUser.text = "Hello $username"
               bottomNavigation.setOnItemSelectedListener { item ->
                   when (item.itemId) {
                       R.id.nav_home -> {
                           true
                       }

                       R.id.nav_search -> {
                           startActivity(Intent(this@HomeActivity, SearchActivity::class.java))
                           true
                       }

                       R.id.nav_add_product -> {
                           startActivity(
                               Intent(
                                   this@HomeActivity,
                                   AddProductActivity::class.java
                               )
                           )
                           true
                       }

                       R.id.nav_logout -> {
                           startActivity(
                               Intent(
                                   this@HomeActivity,
                                   ConstraintLoginActivity::class.java
                               )
                           )
                           Snackbar.make(root, "Logged out", Snackbar.LENGTH_LONG).show()
                           finish()
                           true
                       }

                       else -> false
                   }
               }
               etSearchHome.setOnClickListener {
                   startActivity(Intent(this@HomeActivity, SearchActivity::class.java))
               }

               homeContent.cardSearch.setOnClickListener {
                   startActivity(Intent(this@HomeActivity, SearchActivity::class.java))
               }

               homeContent.cardAddProduct.setOnClickListener {
                   startActivity(Intent(this@HomeActivity, AddProductActivity::class.java))
               }
               Snackbar.make(root, "Hi $username", Snackbar.LENGTH_LONG).show()
           }
       }
    }

    override fun onResume() {
        super.onResume()
        binding?.bottomNavigation?.menu?.findItem(R.id.nav_home)?.isChecked = true
    }
}