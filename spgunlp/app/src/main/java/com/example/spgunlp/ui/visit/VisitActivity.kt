package com.example.spgunlp.ui.visit

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.spgunlp.R.id.active_visit
import com.example.spgunlp.databinding.ActivityVisitBinding

class VisitActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVisitBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVisitBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction()
            .add(active_visit, VisitFragment())
            .commit()
    }

}