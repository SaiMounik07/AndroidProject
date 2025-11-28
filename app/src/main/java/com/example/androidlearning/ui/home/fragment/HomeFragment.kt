package com.example.androidlearning.ui.home.fragment


import android.R.id.home
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.androidlearning.R
import com.example.androidlearning.base.constants.Constants.GUEST
import com.example.androidlearning.base.constants.Constants.USERNAME
import com.example.androidlearning.databinding.ActivityHomeBinding
import com.example.androidlearning.databinding.QuickActionBinding
import com.example.androidlearning.ui.addproduct.AddProductActivity
import com.example.androidlearning.ui.addproduct.fragment.AddProductFragment
import com.example.androidlearning.ui.home.HomeViewModel
import com.example.androidlearning.ui.login.ConstraintLoginActivity
import com.example.androidlearning.ui.search.SearchActivity
import com.example.androidlearning.ui.search.fragment.ProductFragment
import com.google.android.material.snackbar.Snackbar

class HomeFragment: Fragment(R.layout.quick_action) {
    lateinit var homeViewModel: HomeViewModel
    var binding: QuickActionBinding? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = QuickActionBinding.bind(view)
        binding?.let {
            with(it) {
                homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
                etSearchHome.requestFocus()
                val username = homeViewModel.getValueByKey(USERNAME, GUEST)
                tvLoginUser.text = "Hello $username"
                etSearchHome.setOnClickListener {
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.home_page, ProductFragment())
                        .addToBackStack(null)
                        .commit()
                }

                homeContent.cardSearch.setOnClickListener {
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.home_page, ProductFragment())
                        .addToBackStack(null)
                        .commit()                }

                homeContent.cardAddProduct.setOnClickListener {
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.home_page, AddProductFragment())
                        .addToBackStack(null)
                        .commit()                }
//                Snackbar.make(root, "Hi $username", Snackbar.LENGTH_LONG).show()
            }
        }
    }

}