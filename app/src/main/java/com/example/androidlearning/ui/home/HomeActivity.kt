package com.example.androidlearning.ui.home

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.androidlearning.R
import com.example.androidlearning.ui.learnings.LearningActivity
import com.example.androidlearning.ui.search.SearchActivity
import com.google.android.material.button.MaterialButton

class HomeActivity:AppCompatActivity() {
    lateinit var homeViewModel: HomeViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        val text=findViewById<TextView>(R.id.tv_login_user)
        val logout=findViewById<MaterialButton>(R.id.mb_logout)
        val searchPage=findViewById<MaterialButton>(R.id.search_page)
        homeViewModel= ViewModelProvider(this).get(HomeViewModel::class.java)
        val username=homeViewModel.getValueByKey("username","Guest")
        text.setText("Hello $username")
        logout.setOnClickListener {
            homeViewModel.clearData()
            Toast.makeText(this,"logged out $username",Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LearningActivity::class.java))
        }
        searchPage.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }
        Toast.makeText(this,"HI $username",Toast.LENGTH_SHORT).show()
    }
}