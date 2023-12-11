package com.example.spgunlp.ui

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.spgunlp.MainActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

abstract class BaseFragment : Fragment() {

    protected open var bottomNavigationViewVisibility = View.VISIBLE
    protected open lateinit var fabButton:FloatingActionButton

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // get the reference of the parent activity and call the setBottomNavigationVisibility method.
        if (activity is MainActivity) {
            var  mainActivity = activity as MainActivity
            mainActivity.setBottomNavigationVisibility(bottomNavigationViewVisibility)
            fabButton=mainActivity.getFab()
        }
    }
    override fun onResume() {
        super.onResume()
        if (activity is MainActivity) {
            var  mainActivity = activity as MainActivity
            mainActivity.setBottomNavigationVisibility(bottomNavigationViewVisibility)
            mainActivity.updateColorFab()
        }
    }
}