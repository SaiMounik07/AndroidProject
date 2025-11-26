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
import com.example.androidlearning.ui.learnings.LearningActivity
import com.example.androidlearning.ui.search.SearchActivity
import com.google.android.material.button.MaterialButton

class HomeActivity:AppCompatActivity() {
    lateinit var homeViewModel: HomeViewModel
    lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.home) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
//        val text=findViewById<TextView>(R.id.tv_login_user)
//        val logout=findViewById<MaterialButton>(R.id.mb_logout)
//        val searchPage=findViewById<MaterialButton>(R.id.search_page)
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        binding.etSearchHome.requestFocus()
            val username = homeViewModel.getValueByKey(USERNAME, GUEST)
            binding.tvLoginUser.text = "Hello $username"
            binding.mbLogout.setOnClickListener {
                homeViewModel.clearData()
                Toast.makeText(this, "logged out $username", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LearningActivity::class.java))
            }
            binding.etSearchHome.setOnClickListener {
                startActivity(Intent(this, SearchActivity::class.java))
            }
            binding.searchPage.setOnClickListener {
                startActivity(Intent(this, SearchActivity::class.java))
            }
            Toast.makeText(this, "HI $username", Toast.LENGTH_SHORT).show()
        }

}