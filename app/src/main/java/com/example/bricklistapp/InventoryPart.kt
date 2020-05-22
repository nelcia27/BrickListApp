package com.example.bricklistapp

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.AsyncTask
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_new_project.*
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.xml.sax.InputSource
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.lang.Exception
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory

class InventoryPart {
    var id: Int = 0
    var inventoryID: Int = 0
    var typeID: String? = null
    var itemID: String? = null
    var quantityInSet: Int = 0
    var quantityInStore: Int = 0
    var colorID: String? = null
    var extra: String? = null
    var image: Bitmap? = null
    var commentToShow: String? = null
    var quantityToShow: String? = null

    constructor(
        id: Int,
        inventoryID: Int,
        typeID: String,
        itemID: String,
        quantityInSet: Int,
        quantityInStore: Int,
        colorID: String,
        extra: String
    ) {
        this.id = id
        this.inventoryID = inventoryID
        this.typeID = typeID
        this.itemID = itemID
        this.quantityInSet = quantityInSet
        this.quantityInStore = quantityInStore
        this.colorID = colorID
        this.extra = extra
    }

    constructor(
        inventoryID: Int,
        typeID: String,
        itemID: String,
        quantityInSet: Int,
        quantityInStore: Int,
        colorID: String,
        extra: String
    ) {
        this.inventoryID = inventoryID
        this.typeID = typeID
        this.itemID = itemID
        this.quantityInSet = quantityInSet
        this.quantityInStore = quantityInStore
        this.colorID = colorID
        this.extra = extra
    }

    constructor()

    @SuppressLint("StaticFieldLeak")
    inner class FotosCollecter(var db: DatabaseHandler, var code: String, color: Int?) : AsyncTask<String, Int, String>() {

        var image: ByteArray? = null
        var url1: String="https://www.lego.com/service/bricks/5/2/"+code
        var url2: String="http://img.bricklink.com/P/"+color.toString()+"/"+code+".gif"
        var url3: String="https://www.bricklink.com/PL/"+code+".jpg"

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            if (result.equals("OK")) {
                db.saveImageToDatabase(image!!,code.toInt())
            }else{
                println("Nie znalazło")
            }
        }

        override fun doInBackground(vararg params: String?): String {
            try{
                BufferedInputStream(URL(url1).content as InputStream).use {
                    val img = ArrayList<Byte>()
                    var current: Int
                    while (true) {
                        current = it.read()
                        if (current == -1)
                            break
                        img.add(current.toByte())
                    }
                    image = img.toByteArray()
                }
                return "OK"
            }catch (e: IOException){
               try{
                   BufferedInputStream(URL(url2).content as InputStream).use {
                       val img = ArrayList<Byte>()
                       var current: Int
                       while (true) {
                           current = it.read()
                           if (current == -1)
                               break
                           img.add(current.toByte())
                       }
                       image = img.toByteArray()
                   }
                   return "OK"
               }catch (e: IOException){
                   try{
                       BufferedInputStream(URL(url1).content as InputStream).use {
                           val img = ArrayList<Byte>()
                           var current: Int
                           while (true) {
                               current = it.read()
                               if (current == -1)
                                   break
                               img.add(current.toByte())
                           }
                           image = img.toByteArray()
                       }
                       return "OK"
                   }catch (e: IOException){
                        return "NOT"
                   }
               }
            }

        }
    }
}