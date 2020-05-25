package com.example.bricklistapp

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import kotlinx.android.synthetic.main.settings_activity.*


class SettingsActivity : AppCompatActivity() {
    var url: String="http://fcds.cs.put.poznan.pl/MyWeb/BL/"
    var showArchive: String="0"
    var onlyNewElem: String="1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        url = intent.getStringExtra("url")!!
        showArchive=intent.getStringExtra("archive")!!
        onlyNewElem=intent.getStringExtra("onlyNew")!!

        editTextURL.setText(url)

        if(showArchive.equals("1"))
            switch1.isChecked=true
        else
            switch1.isChecked=false

        if(onlyNewElem.equals("1"))
            switch2.isChecked=true
        else
            switch2.isChecked=false

        /*switch1.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                showArchive="1"
            }else{
                showArchive="0"
            }
        }

        switch2.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked)
                onlyNewElem="1"
            else
                onlyNewElem="0"
        }*/
    }

    override fun onSupportNavigateUp(): Boolean {
        val intent= Intent(this,MainActivity::class.java)
        url=editTextURL.text.toString()
        if(switch1.isChecked){
            showArchive="1"
        }else{
            showArchive="0"
        }
        if(switch2.isChecked){
            onlyNewElem="1"
        }else{
            onlyNewElem="0"
        }
        intent.putExtra("url",url)
        intent.putExtra("archive",showArchive)
        intent.putExtra("onlyNew",onlyNewElem)
        startActivityForResult(intent,101)
        //super.finish()
        return true
    }
}