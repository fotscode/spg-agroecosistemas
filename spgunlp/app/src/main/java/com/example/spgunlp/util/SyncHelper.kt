package com.example.spgunlp.util;

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.spgunlp.io.AuthService
import com.example.spgunlp.io.VisitService
import com.example.spgunlp.model.AppUser
import com.example.spgunlp.model.AppVisitUpdate
import com.example.spgunlp.model.MODIFIED_VISIT
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.example.spgunlp.util.PreferenceHelper.get
import com.example.spgunlp.util.PreferenceHelper.set
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

suspend fun performSync(context: Context): Boolean {
    val preferences = context?.let { PreferenceHelper.defaultPrefs(it) }
    val idVisits = preferences?.get("VISIT_IDS", "")?.split(",")
    Log.i("ALARM_RECEIVER", "onReceive, viendo si hay visitas")
    val jwt = preferences?.get("jwt", "")
    var stringToDelete = ""
    var result = true
    if (idVisits != null && jwt != null && jwt != "") {
        for (id in idVisits) {
            if (id != "") {
                Log.i("ALARM_RECEIVER", "visita: $id")
                val visitService = VisitService.create()
                val tag = id + "_" + MODIFIED_VISIT
                val visitGson = preferences[tag, ""]
                val gson = Gson()
                val type = object : TypeToken<AppVisitUpdate>() {}.type
                if (visitGson != "") {
                    val visitToUpdate = gson.fromJson<AppVisitUpdate>(visitGson, type)
                    try {
                        val header = "Bearer $jwt"
                        val response =
                            visitService.updateVisitById(header, id.toInt(), visitToUpdate)
                        if (response.isSuccessful) {
                            Log.i("ALARM_RECEIVER", "Actualizado: $id")
                            preferences[tag] = ""
                            stringToDelete += "$id,"
                        } else {
                            result = false
                            preferences["COLOR_FAB"] = Color.RED
                            Log.i("ALARM_RECEIVER", "Error actualizando: $id")
                        }
                    } catch (e: Exception) {
                        result = false
                        preferences["COLOR_FAB"] = Color.YELLOW
                        Log.i("ALARM_RECEIVER", "onReceive: $e")
                    }
                }
            }
        }
    }
    if (stringToDelete != "") {
        val newString = preferences?.get("VISIT_IDS", "")?.replace(stringToDelete, "")
        preferences?.set("VISIT_IDS", newString)
    }
    val ids = preferences?.get("VISIT_IDS", "")
    if (ids == "")
        preferences["COLOR_FAB"] = Color.GREEN

    return result
}

private fun createSessionPreferences(jwt: String, email: String, context: Context) {
    val preferences = PreferenceHelper.defaultPrefs(context)
    preferences["jwt"] = jwt
    preferences["email"] = email
}

suspend fun performLogin(
    mail: String,
    password: String,
    context: Context,
    authService: AuthService
): Boolean {
    // make the call to the remote API with coroutines
    val user = AppUser(mail, password)
    try {

        val response = authService.login(user)

        if (response.code() == 400 && (password.isNotEmpty() || mail.isNotEmpty())) {
            Toast.makeText(
                context,
                "El usuario no se encuentra autorizado",
                Toast.LENGTH_SHORT
            ).show()
        }
        if (response.isSuccessful) {
            val loginResponse = response.body()
            if (loginResponse != null) {
                createSessionPreferences(loginResponse.token, loginResponse.usuario, context)

                return true
            }
        } else {
            Toast.makeText(context, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
        }
    } catch (e: Exception) {
        Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show()
    }
    return false
}