package com.example.bricklistapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat

class SettingsActivity : AppCompatActivity() {
    var url="http://fcds.cs.put.poznan.pl/MyWeb/BL/"
    var showArchive="0"
    var onlyNewElem="1"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        url = intent.getStringExtra("url")!!
        showArchive=intent.getStringExtra("archive")!!
        onlyNewElem=intent.getStringExtra("onlyNew")!!
        //to trzeba poustawiać i odesłac

    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }
    }
}