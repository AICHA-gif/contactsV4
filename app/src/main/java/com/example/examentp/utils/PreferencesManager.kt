package com.example.examenTP.utils

import android.content.Context
import android.graphics.Color
import androidx.preference.PreferenceManager

class PreferencesManager(context: Context) {

    private val prefs =
        PreferenceManager.getDefaultSharedPreferences(context)

    fun getTaille(): Float {
        val sizeStr = prefs.getString("taille_police", "16")
        return sizeStr?.toFloatOrNull() ?: 16f
    }

    fun getCouleur(): Int {
        val colorStr = prefs.getString("couleur_preferee", "#000000") ?: "#000000"
        return try {
            Color.parseColor(colorStr)
        } catch (e: Exception) {
            Color.BLACK
        }
    }

    fun getAppel(): Boolean {
        return prefs.getBoolean("appel_depuis_liste", false)
    }
}