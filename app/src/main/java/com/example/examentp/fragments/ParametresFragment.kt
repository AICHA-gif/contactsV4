package com.example.examenTP.fragments

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.example.examenTP.R

class ParametresFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(
        savedInstanceState: Bundle?,
        rootKey: String?
    ) {
        setPreferencesFromResource(
            R.xml.preferences,
            rootKey
        )
    }
}