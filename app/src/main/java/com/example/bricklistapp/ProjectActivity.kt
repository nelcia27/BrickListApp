package com.example.bricklistapp

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import android.widget.Toast
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.io.File
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

class ProjectActivity : AppCompatActivity() {
    var db: DatabaseHandler?=null
    var name: String?=null
    var onlyNew: String="0"
    var parts: ArrayList<InventoryPart> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project2)
        db = DatabaseHandler(this)
        name= intent.extras!!.getString("name")
        onlyNew=intent.extras!!.getString("onlyNew")!!
        parts=db!!.prepareToShowProject(name!!)
        val dataNames : ArrayList<String> =ArrayList()
        val dataQuantities : ArrayList<String> =ArrayList()
        val dataQuantitiesStore: ArrayList<Int> =ArrayList()
        val quantitiesSet: ArrayList<Int> = ArrayList()
        val ids: ArrayList<Int> =ArrayList()
        val imgsSrc: ArrayList<String?> = ArrayList()
        val imgsSrcLastChance: ArrayList<String?> = ArrayList()
        val images: ArrayList<Bitmap?> = ArrayList()
        val codes: ArrayList<Int?> = ArrayList()
        for(i in parts){
            if(i.commentToShow.equals("")){
                val info: String="Element with itemID "+i.itemID+" and colorID "+i.colorID+" not found in database"
                Toast.makeText(this,info, Toast.LENGTH_SHORT).show()
            }else{
                dataNames.add(i.commentToShow!!)
                dataQuantities.add(i.quantityToShowSet!!)
                dataQuantitiesStore.add(i.quantityToShowStore!!)
                ids.add(i.id!!)
                imgsSrc.add(i.src)
                imgsSrcLastChance.add(i.srcLastChance)
                images.add(i.image)
                codes.add(i.code)
                quantitiesSet.add(i.quantityInSet)
            }
        }
        val listViewItems = findViewById<ListView>(R.id.elementList)
        val adapter = ProjectListAdapter(this,db!!,dataNames,dataQuantities,dataQuantitiesStore,ids,images,imgsSrc,imgsSrcLastChance,codes,quantitiesSet)
        listViewItems.adapter = adapter

        val actionbar = supportActionBar
        actionbar!!.title = name
        actionbar.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.getItemId()

        if (id == R.id.action_one) {
            val parm: String
            if(onlyNew.equals("1"))
                parm="U"
            else
                parm="N"
            writeXML(db!!.prepareToShowProject(name!!),name!!,parm)
            Toast.makeText(this, "Succesfully saved, in file are also elements which were not found in database", Toast.LENGTH_LONG).show()
            return true
        }
        if (id == R.id.action_two) {
            db!!.deleteInventory(name!!)
            Toast.makeText(this, "Succesfully deleted project", Toast.LENGTH_LONG).show()
            val intent= Intent(this, MainActivity::class.java)
            startActivity(intent)
            return true
        }
        if (id == R.id.action_three) {
            db!!.archivizeInventory(name!!)
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    fun writeXML(parts: ArrayList<InventoryPart>, inventoryName: String, saveParm: String) {
        val docBuilder: DocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val doc: Document = docBuilder.newDocument()

        val rootElement: Element = doc.createElement("INVENTORY")

        parts.forEach{
            if (it.quantityInStore!! < it.quantityInSet!!) {
                val item: Element = doc.createElement("ITEM")

                val itemType: Element = doc.createElement("ITEMTYPE")
                itemType.appendChild(doc.createTextNode(it.typeID))

                val itemId: Element = doc.createElement("ITEMID")
                itemId.appendChild(doc.createTextNode(it.itemID))

                val itemColor: Element = doc.createElement("COLOR")
                itemColor.appendChild(doc.createTextNode(it.colorID.toString()))

                val itemQty: Element = doc.createElement("QTYFILLED")
                itemQty.appendChild(doc.createTextNode((it.quantityInSet!! - it.quantityInStore!!).toString()))

                val itemCondt: Element = doc.createElement("CONDITION")
                itemCondt.appendChild(doc.createTextNode(saveParm))

                item.appendChild(itemType)
                item.appendChild(itemId)
                item.appendChild(itemColor)
                item.appendChild(itemQty)
                item.appendChild(itemCondt)

                rootElement.appendChild(item)
            }
        }
        doc.appendChild(rootElement)

        val transformer: Transformer = TransformerFactory.newInstance().newTransformer()
        transformer.setOutputProperty(OutputKeys.INDENT, "yes")
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")

        val path=this.filesDir
        val outDir = File(path, "GeneretedXML")
        outDir.mkdir()

        val file = File(outDir, inventoryName + ".xml")

        transformer.transform(DOMSource(doc), StreamResult(file))
    }


}
