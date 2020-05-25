package com.example.bricklistapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    var db: DatabaseHandler?=null
    var showArchive="0"
    var onlyNewElem="0"
    var url="http://fcds.cs.put.poznan.pl/MyWeb/BL/"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        db = DatabaseHandler(this)
        settings()
        showProjects()
        val listViewProjects = findViewById<ListView>(R.id.projectList)
        listViewProjects.setOnItemClickListener{parent, view, position, id ->
            db!!.updateLastAccessedOfInventory(listViewProjects.getItemAtPosition(position).toString())
            val intent= Intent(this, ProjectActivity::class.java)
            intent.putExtra("name",listViewProjects.getItemAtPosition(position).toString())
            intent.putExtra("onlyNew",onlyNewElem)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        settings()
        showProjects()
    }

    override fun onRestart() {
        super.onRestart()
        settings()
        showProjects()

    }

    fun addProject(view: View){
        val intent= Intent(this,NewProject::class.java)
        intent.putExtra("url",url)
        startActivity(intent)
    }

    fun goToSettings(view: View){
        val intent= Intent(this,SettingsActivity::class.java)
        startActivity(intent)
    }

    fun showProjects(){
        val tmpNameArray: ArrayList<String>
        if(showArchive.equals("0")){
            tmpNameArray = db!!.takeActiveInventoriesNames()
        }else{
            tmpNameArray =db!!.takeAllInventoriesNames()

        }
        val listViewProjects = findViewById<ListView>(R.id.projectList)
        val listItems = arrayOfNulls<String>(tmpNameArray.size)
        for (i in 0 until tmpNameArray.size) {
            val tmp = tmpNameArray[i]
            listItems[i] = tmp
        }
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listItems)
        listViewProjects.adapter = adapter
    }

    fun settings(){
        try{
            val filename="opcje.txt"
            if (FileExists(filename)){
                val file= InputStreamReader(openFileInput(filename))
                val br= BufferedReader(file)

                var line=br.readLine()
                val opcje=line.split(',')
                if(opcje.size==3){
                    url=opcje[0]
                    showArchive=opcje[1]
                    onlyNewElem=opcje[2]
                }

                file.close()

            }
        }catch (e: Exception){
            Toast.makeText(this, "You use default settings which can be changed", Toast.LENGTH_LONG).show()
        }
    }

    private fun FileExists(path:String):Boolean{
        val file=baseContext.getFileStreamPath(path)
        return file.exists()
    }
}
