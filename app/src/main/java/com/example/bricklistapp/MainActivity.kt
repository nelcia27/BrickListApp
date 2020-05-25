package com.example.bricklistapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.core.view.get

class MainActivity : AppCompatActivity() {
    var db: DatabaseHandler?=null
    var showArchive="0"
    var onlyNewElem="1"
    var url="http://fcds.cs.put.poznan.pl/MyWeb/BL/"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        db = DatabaseHandler(this)
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
        onActivityResult(101,Activity.RESULT_OK,intent)
        showProjects()
    }

    override fun onRestart() {
        super.onRestart()
        //onActivityResult(101,Activity.RESULT_OK,intent)
        showProjects()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 101 && resultCode == Activity.RESULT_OK && data != null){
            url = data.extras!!.getString("url")!!
            showArchive=data.extras!!.getString("archive")!!
            onlyNewElem=data.extras!!.getString("onlyNew")!!
        }
    }

    fun addProject(view: View){
        val intent= Intent(this,NewProject::class.java)
        intent.putExtra("url",url)
        startActivity(intent)
    }

    fun goToSettings(view: View){
        val intent= Intent(this,SettingsActivity::class.java)
        intent.putExtra("url",url)
        intent.putExtra("archive",showArchive)
        intent.putExtra("onlyNew",onlyNewElem)
        startActivityForResult(intent,101)
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
}
