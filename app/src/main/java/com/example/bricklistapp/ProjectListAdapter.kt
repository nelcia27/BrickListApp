package com.example.bricklistapp

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class ProjectListAdapter: BaseAdapter {

    var con: Context
    var dataNames : ArrayList<String>
    var dataQuantities : ArrayList<String>
    var images: ArrayList<Bitmap> = ArrayList<Bitmap>()
    constructor (context: Context , dataN:  ArrayList<String>, dataQ:  ArrayList<String>/*,imgs: ArrayList<Bitmap>*/){
        this.con = context
        this.dataNames = dataN
        this.dataQuantities=dataQ
        //this.images=imgs
    }

    override fun getCount(): Int {
        return dataNames.size
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getItem(position: Int): Any {
        return dataNames[position]
    }

   override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater: LayoutInflater =  LayoutInflater.from(con)
        val convertView= inflater.inflate(R.layout.custom_list_element, parent, false)
        val text1 =convertView.findViewById<TextView>(R.id.txtViewName)
        val text2 =convertView.findViewById<TextView>(R.id.txtViewQuantity)
        //val imageView = convertView.findViewById<ImageView>(R.id.imageView)
        text1.setText(dataNames[position])
        text2.setText(dataQuantities[position])
        //imageView.setImageBitmap(images[position])
        return convertView
    }
}