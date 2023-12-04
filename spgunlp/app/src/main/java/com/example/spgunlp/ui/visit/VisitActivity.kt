package com.example.spgunlp.ui.visit

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.spgunlp.R.id.active_visit
import com.example.spgunlp.databinding.ActivityVisitBinding
import com.example.spgunlp.model.AppVisit
import com.example.spgunlp.model.VISIT_ITEM
import com.google.gson.Gson
import java.util.logging.Level
import java.util.logging.Logger

class VisitActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVisitBinding
    private lateinit var visit: AppVisit
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVisitBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction()
            .add(active_visit, VisitFragment())
            .commit()

        val visitGson= intent.getStringExtra(VISIT_ITEM)
        visit= Gson().fromJson(visitGson, AppVisit::class.java)
        //Logger.getGlobal().log(Level.SEVERE,visit.quintaResponse?.nombreProductor) TODO(remove)
    }

}