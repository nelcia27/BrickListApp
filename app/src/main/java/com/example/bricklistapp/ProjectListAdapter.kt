package com.example.bricklistapp

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.squareup.picasso.Picasso
import com.google.android.gms.common.util.IOUtils.toByteArray
import android.graphics.drawable.BitmapDrawable
import java.io.ByteArrayOutputStream
import java.io.PipedOutputStream


class ProjectListAdapter: BaseAdapter {

    var con: Context
    var db: DatabaseHandler
    var dataNames : ArrayList<String>
    var dataQuantities : ArrayList<String>
    var dataQuantitiesInStore: ArrayList<Int> = ArrayList<Int>()
    var ids: ArrayList<Int> = ArrayList<Int>()
    var images: ArrayList<Bitmap?> = ArrayList<Bitmap?>()
    var src1: ArrayList<String?> = ArrayList<String?>()
    var src2: ArrayList<String?> = ArrayList<String?>()
    var codes: ArrayList<Int?> = ArrayList<Int?>()
    constructor (context: Context , db: DatabaseHandler, dataN:  ArrayList<String>, dataQ:  ArrayList<String>,dataQS: ArrayList<Int>, ids: ArrayList<Int>,imgs: ArrayList<Bitmap?>,src1: ArrayList<String?>, src2: ArrayList<String?>, codes: ArrayList<Int?>){
        this.con = context
        this.db=db
        this.dataNames = dataN
        this.dataQuantities=dataQ
        this.dataQuantitiesInStore=dataQS
        this.ids=ids
        this.images=imgs
        this.src1=src1
        this.src2=src2
        this.codes=codes
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
       val text3 =convertView.findViewById<TextView>(R.id.txtQuantity)
       val imageView = convertView.findViewById<ImageView>(R.id.imageView)
       val btnPlus=convertView.findViewById<Button>(R.id.btnPlus)
       val btnMinus=convertView.findViewById<Button>(R.id.btnMinus)

       text1.setText(dataNames[position])
       text2.setText(dataQuantities[position])
       text3.setText(dataQuantitiesInStore[position].toString())
       if (images[position]!=null)
           imageView.setImageBitmap(images[position])
       else if(src1[position]!=null){
           Picasso.get().load(src1[position]).into(imageView)
           if(imageView!=null){
               try{
                   val bitmap = (imageView.drawable as BitmapDrawable).bitmap
                   val baos = ByteArrayOutputStream()
                   bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                   val imageInByte = baos.toByteArray()
                   db.saveImageToDatabase(imageInByte,codes[position]!!)
               }catch (e: Exception){

               }

           }
       }else if(src2[position]!=null){
           Picasso.get().load(src2[position]).into(imageView)
           if(imageView!=null){
               try {
                   val bitmap = (imageView.drawable as BitmapDrawable).bitmap
                   val baos = ByteArrayOutputStream()
                   bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                   val imageInByte = baos.toByteArray()
                   db.saveImageToDatabase(imageInByte, codes[position]!!)
               }catch (e: Exception){

               }
           }
       }
       btnPlus.setOnClickListener{
           if(db.changeQuantityForPlus(ids[position])){
               var num=text3.getText().toString().toInt()
               num+=1
               text3.setText(num.toString())
           }
       }
       btnMinus.setOnClickListener{
           if(db.changeQuantityForMinus(ids[position])){
               var num=text3.getText().toString().toInt()
               num-=1
               text3.setText(num.toString())
           }
       }

        return convertView
    }
}