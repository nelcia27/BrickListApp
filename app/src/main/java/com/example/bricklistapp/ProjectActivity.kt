package com.example.bricklistapp

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView

class ProjectActivity : AppCompatActivity() {
    var db: DatabaseHandler?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project)
        db = DatabaseHandler(this)
        //musze miec nazwę zestawu na który kliknęłam i niech to bedzie name
        val name= "Wózek"
        val parts=db!!.prepareToShowProject(name)
        val dataNames : ArrayList<String> =ArrayList<String>()
        val dataQuantities : ArrayList<String> =ArrayList<String>()
        val images: ArrayList<Bitmap> = ArrayList<Bitmap>()
        for(i in parts){
            dataNames.add(i.commentToShow!!)
            dataQuantities.add(i.quantityToShow!!)
            //images.add(i.image!!)
        }
        val listViewItems = findViewById<ListView>(R.id.elementList)
        val adapter = ProjectListAdapter(this,dataNames,dataQuantities/*,images*/)
        listViewItems.adapter = adapter
    }
}
