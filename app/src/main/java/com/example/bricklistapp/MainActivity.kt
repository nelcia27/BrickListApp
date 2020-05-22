package com.example.bricklistapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    var db: DatabaseHandler?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        db = DatabaseHandler(this)
        showProjects()
        var listViewProjects = findViewById<ListView>(R.id.projectList)
        listViewProjects.setOnItemClickListener{parent, view, position, id ->
            Toast.makeText(this, "Clicked item :"+" "+position,Toast.LENGTH_SHORT).show()
            this.startActivity(intent);
            val intent= Intent(this, ProjectActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        showProjects()
    }

    override fun onRestart() {
        super.onRestart()
        showProjects()

    }
    fun editProject(view: View){
        Toast.makeText(this,"Powinno się edytowac projekt",Toast.LENGTH_SHORT).show()
    }

    fun addProject(view: View){
        val intent= Intent(this,NewProject::class.java)
        startActivity(intent)
    }

    fun goToSettings(view: View){
        val intent= Intent(this,SettingsActivity::class.java)
        startActivity(intent)
    }

    fun showProjects(){
        val tmpNameArray = db!!.takeActiveInventoriesNames()
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
