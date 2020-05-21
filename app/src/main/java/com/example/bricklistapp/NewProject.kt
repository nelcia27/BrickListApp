package com.example.bricklistapp

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_new_project.*
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.xml.sax.InputSource
import java.io.IOException
import java.lang.Exception
import java.net.URL
import java.sql.SQLException
import javax.xml.parsers.DocumentBuilderFactory

class NewProject : AppCompatActivity() {
    var urlPost: String?=null
    var name: String?=null
    var ifAdd: Int=0
    var db: DatabaseHandler?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_project)
        btnCheck.isEnabled=true
        btnAdd.isEnabled=false
        db = DatabaseHandler(baseContext)
        /*try {
            db!!.createDataBase()
        } catch (e: IOException) {
            throw Error("Error creating database")
        }
        try {
            db!!.openDataBase()
        } catch (e: SQLException) {
            throw Error("Error opening database")
        }*/
    }

    fun addNewProject(view: View){
        ifAdd=1
        val cg=CheckerAndGeneretor(db!!)
        cg.execute("")
        val intent= Intent(this,MainActivity::class.java)
        startActivity(intent)
    }

    fun checkURL(view: View){
        urlPost=editURLElem.text.toString()
        name=editProjectName.text.toString()
        editProjectName.isEnabled=false
        editURLElem.isEnabled=false
        val cg=CheckerAndGeneretor(db!!)
        cg.execute("")
    }

    @SuppressLint("StaticFieldLeak")
    private inner class CheckerAndGeneretor(var db: DatabaseHandler): AsyncTask<String, Int, String>() {
        var urlInventory =
            "http://fcds.cs.put.poznan.pl/MyWeb/BL/" + urlPost + ".xml"
        var doc: Document?=null

        override fun onPreExecute() {
            super.onPreExecute()
            btnCheck.isEnabled = true
            btnAdd.isEnabled = false
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            if(result.equals("OK")) {
                btnCheck.isEnabled = false
                btnAdd.isEnabled = true
                editProjectName.isEnabled=false
                editURLElem.isEnabled=false
                if(ifAdd==1){
                    if(doc!=null) {
                        if (xmlloader(doc!!) == "OK") {
                            Toast.makeText(baseContext,"Succesfully load project data",Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(baseContext,xmlloader(doc!!),Toast.LENGTH_LONG).show()
                        }
                        ifAdd=0
                    }
                }
            }else{
                editProjectName.isEnabled=true
                editURLElem.isEnabled=true
                btnCheck.isEnabled = true
                btnAdd.isEnabled = false
                Toast.makeText(baseContext,result,Toast.LENGTH_LONG).show()
            }
        }

        override fun doInBackground(vararg params: String?): String {
            if (name != null && urlPost != null) {
                if (urlInventory != null) {
                    try {
                        val url_ = URL(urlInventory)
                        val builderFactory = DocumentBuilderFactory.newInstance()
                        val docBuilder = builderFactory.newDocumentBuilder()
                        doc = docBuilder.parse(InputSource(url_.openStream()))
                        return "OK"
                    } catch (e: Exception) {
                        return e.message.toString()
                    }
                }else {
                    return "NOT"
                }
            }else{
                return "NOT"
            }
        }

        private fun xmlloader(document: Document) :String{
            try {
                val inventory= Inventory(db.maxInventoryNum()+1, name!!, 1, 0)
                try {
                    db.addInventory(inventory)
                }catch (e: Exception){
                    Toast.makeText(baseContext,"Problem"+e.message.toString(),Toast.LENGTH_LONG).show()
                }
                val nList = document.getElementsByTagName("ITEM")
                for (i in 0 until nList.length) {
                    val itemNode: Node = nList.item(i)
                    if (itemNode.nodeType == Node.ELEMENT_NODE) {
                        val elem = itemNode as Element
                        val children = elem.childNodes
                        val item = InventoryPart()
                        for (j in 0 until children.length) {
                            val node = children.item(j)
                            if (node is Element) {
                                when (node.nodeName) {
                                    "ITEMTYPE" -> {
                                        item.typeID = node.textContent
                                    }
                                    "ITEMID" -> {
                                        item.itemID = node.textContent
                                    }
                                    "QTY" -> {
                                        item.quantityInSet = node.textContent.toInt()
                                    }
                                    "COLOR" -> {
                                        item.colorID = node.textContent
                                    }
                                    "EXTRA" -> {
                                        if(node.textContent == "N")
                                            item.extra = node.textContent
                                        else
                                            item.extra = "0"
                                    }
                                    "ALTERNATE" -> {
                                        if(node.textContent == "N") {
                                            item.id = db.maxInventoryPartNum()+1
                                            item.inventoryID=db.maxInventoryNum()
                                            item.quantityInStore=0
                                            db.addInventoryPart(item)

                                        }

                                    }
                                }
                            }
                        }
                    }
                }
                return "OK"
            }catch (e: Exception){
                Toast.makeText(baseContext,e.message.toString(),Toast.LENGTH_LONG).show()
                return e.message.toString()
            }
        }




    }
}
