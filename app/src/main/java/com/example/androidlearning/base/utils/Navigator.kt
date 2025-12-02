package com.example.androidlearning.base.utils

import androidx.fragment.app.Fragment
import com.example.androidlearning.R

interface Navigator {
    fun replaceFragment(fragment: Fragment, containerId: Int = R.id.home_page, addToBackStack: Boolean = true, selectBottomId: Int? = null)
}
