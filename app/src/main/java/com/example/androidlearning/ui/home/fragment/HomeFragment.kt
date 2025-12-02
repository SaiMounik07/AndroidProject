package com.example.androidlearning.ui.home.fragment


import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.androidlearning.R
import com.example.androidlearning.base.constants.Constants.GUEST
import com.example.androidlearning.base.constants.Constants.USERNAME
import com.example.androidlearning.databinding.QuickActionBinding
import com.example.androidlearning.ui.addproduct.fragment.AddProductFragment
import com.example.androidlearning.ui.home.HomeActivity
import com.example.androidlearning.ui.home.HomeViewModel
import com.example.androidlearning.ui.search.fragment.ProductFragment

class HomeFragment : Fragment(R.layout.quick_action) {
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
                    (activity as HomeActivity).replaceFragment(
                        ProductFragment(),
                        selectBottomId = R.id.nav_search
                    )
//                    requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
//                        .selectedItemId=R.id.nav_search
                }

                homeContent.cardSearch.setOnClickListener {
                    (activity as HomeActivity).replaceFragment(
                        ProductFragment(),
                        selectBottomId = R.id.nav_search
                    )

                }
                homeContent.cardAddProduct.setOnClickListener {
                    (activity as HomeActivity).replaceFragment(
                        AddProductFragment(),
                        selectBottomId = R.id.nav_add_product
                    )

                }

            }
        }
    }

}