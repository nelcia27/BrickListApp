package com.example.bricklistapp

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.settings_activity.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.lang.Exception


class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initSettings()

    }

    override fun onRestart() {
        super.onRestart()
        initSettings()
    }

    override fun onResume() {
        super.onResume()
        initSettings()
    }

    override fun onPause() {
        super.onPause()
        saveSettings()
    }

    override fun onSupportNavigateUp(): Boolean {
        saveSettings()
        onBackPressed()
        return true
    }

    fun initSettings(){

        try{
            val filename="opcje.txt"
            if (FileExists(filename)){
                val file= InputStreamReader(openFileInput(filename))
                val br= BufferedReader(file)

                var line=br.readLine()
                val opcje=line.split(',')
                if(opcje.size==3){
                    if(opcje[0].equals(""))
                        editTextURL.setText("http://fcds.cs.put.poznan.pl/MyWeb/BL/")
                    else
                        editTextURL.setText(opcje[0])

                    if(opcje[1].equals("1")){
                        switch1.isChecked=true
                    }else{
                        switch1.isChecked=false
                    }

                    if(opcje[2].equals("1")){
                        switch2.isChecked=true
                    }else{
                        switch2.isChecked=false
                    }
                }
                else{
                    switch1.isChecked=false
                    switch2.isChecked=true
                    editTextURL.setText("http://fcds.cs.put.poznan.pl/MyWeb/BL/")
                }
                file.close()

            }
        }catch (e: Exception){
            Toast.makeText(this, "Korzystasz z domyślnych ustawień, ale możesz je zmienić", Toast.LENGTH_LONG).show()
        }
    }

    fun saveSettings(){
        val filename="opcje.txt"
        val file= OutputStreamWriter(openFileOutput(filename, Context.MODE_PRIVATE))
        val tmp1: String
        val tmp2: String
        if(switch1.isChecked)
            tmp1="1"
        else
            tmp1="0"
        if(switch2.isChecked)
            tmp2="1"
        else
            tmp2="0"

        val comm:String=editTextURL.text.toString()+","+tmp1+","+tmp2

        file.write(comm)

        file.flush()
        file.close()
    }

    private fun FileExists(path:String):Boolean{
        val file=baseContext.getFileStreamPath(path)
        return file.exists()
    }

}