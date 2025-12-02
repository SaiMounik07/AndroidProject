package com.example.androidlearning.ui.home

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.IdRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import com.example.androidlearning.R
import com.example.androidlearning.base.constants.Constants.GUEST
import com.example.androidlearning.base.constants.Constants.USERNAME
import com.example.androidlearning.databinding.ActivityHomeBinding
import com.example.androidlearning.databinding.QuickActionBinding
import com.example.androidlearning.ui.addproduct.AddProductActivity
import com.example.androidlearning.ui.addproduct.fragment.AddProductFragment
import com.example.androidlearning.ui.home.fragment.HomeFragment
import com.example.androidlearning.ui.login.ConstraintLoginActivity
import com.example.androidlearning.ui.search.SearchActivity
import com.example.androidlearning.ui.search.fragment.ProductFragment
import com.google.android.material.snackbar.Snackbar

class HomeActivity : AppCompatActivity() {

    var binding: ActivityHomeBinding? = null
    lateinit var homeViewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
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
                supportFragmentManager.beginTransaction()
                    .replace(R.id.home_page, HomeFragment())
                    .commit()

                bottomNavigation.setOnItemSelectedListener { item ->
                    if (bottomNavigation.selectedItemId == item.itemId) {
                        return@setOnItemSelectedListener true
                    }
                    when (item.itemId) {
                        R.id.nav_home -> {
                            supportFragmentManager.beginTransaction()
                                .replace(R.id.home_page, HomeFragment())
                                .commit()
                            true
                        }

                        R.id.nav_search -> {

                            supportFragmentManager.beginTransaction()
                                .replace(R.id.home_page, ProductFragment())
                                .commit()
                            true
                        }

                        R.id.nav_add_product -> {
                            supportFragmentManager.beginTransaction()
                                .replace(R.id.home_page, AddProductFragment())
                                .commit()
                            true
                        }

                        R.id.nav_logout -> {
                            AlertDialog.Builder(this@HomeActivity)
                                .setTitle("Logout")
                                .setMessage("Are you sure you want to logout?")
                                .setPositiveButton("Logout") { _, _ ->
                                    Snackbar.make(root, "Logged out", Snackbar.LENGTH_LONG).show()
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        startActivity(
                                            Intent(
                                                this@HomeActivity,
                                                ConstraintLoginActivity::class.java
                                            )
                                        )
                                    }, 1500)
                                }
                                .setNegativeButton("Cancel", null)
                                .show()
                        }

                    }
                    homeViewModel.logout()
                    true
                }
                val username = homeViewModel.getValueByKey(USERNAME, GUEST)
                Snackbar.make(root, "Hi $username", Snackbar.LENGTH_LONG).show()

            }

        }


    }

    fun replaceFragment(
        fragment: Fragment,
        addToBackStack: Boolean = true,
        selectBottomId: Int? = null
    ) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.home_page, fragment)
            .commit()
//        selectBottomId?.let {
//            binding?.bottomNavigation?.selectedItemId = it
    }


}