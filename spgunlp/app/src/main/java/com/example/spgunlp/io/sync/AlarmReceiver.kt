package com.example.spgunlp.io.sync

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.example.spgunlp.io.VisitService
import com.example.spgunlp.model.AppUser
import com.example.spgunlp.model.AppVisit
import com.example.spgunlp.model.AppVisitUpdate
import com.example.spgunlp.model.MODIFIED_VISIT
import com.example.spgunlp.util.PreferenceHelper
import com.example.spgunlp.util.PreferenceHelper.get
import com.example.spgunlp.util.PreferenceHelper.set
import com.example.spgunlp.util.performSync
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

class AlarmReceiver : BroadcastReceiver() {
    fun BroadcastReceiver.goAsync(
        context: CoroutineContext = EmptyCoroutineContext,
        block: suspend CoroutineScope.() -> Unit
    ) {
        val pendingResult = goAsync()
        @OptIn(DelicateCoroutinesApi::class) // Must run globally; there's no teardown callback.
        GlobalScope.launch(context) {
            try {
                block()
            } finally {
                pendingResult.finish()
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onReceive(context: Context?, intent: Intent?) = goAsync {
        if (context != null) {
            performSync(context)
        }
    }
}