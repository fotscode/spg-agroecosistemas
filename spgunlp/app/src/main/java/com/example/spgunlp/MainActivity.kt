package com.example.spgunlp

import android.os.Bundle
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.spgunlp.databinding.ActivityMainBinding
import com.example.spgunlp.util.PreferenceHelper
import com.example.spgunlp.util.PreferenceHelper.get

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //getSupportActionBar()?.hide()

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
            R.id.navigation_active, R.id.navigation_inactive, R.id.navigation_profile, R.id.navigation_stats))
        //setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    fun setBottomNavigationVisibility(visibility: Int) {
        binding.navView.visibility = visibility
    }

    override fun onBackPressed() {
        val preferences= PreferenceHelper.defaultPrefs(this)
        if (!preferences["jwt", ""].contains(".")) {
            return
        }

        val bottomNavigationView = binding.navView
        val selectedItemId = bottomNavigationView.selectedItemId
        if (R.id.navigation_active != selectedItemId) {
            bottomNavigationView.selectedItemId = R.id.navigation_active
            return
        }
        super.onBackPressed()
    }
}