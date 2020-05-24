package com.example.bricklistapp

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
    var onlyNew: String?=null
    var parts: ArrayList<InventoryPart> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project2)
        db = DatabaseHandler(this)
        name= intent.extras!!.getString("name")
        onlyNew=intent.extras!!.getString("onlyNew")
        parts=db!!.prepareToShowProject(name!!)
        val dataNames : ArrayList<String> =ArrayList<String>()
        val dataQuantities : ArrayList<String> =ArrayList<String>()
        val dataQuantitiesStore: ArrayList<Int> =ArrayList<Int>()
        val ids: ArrayList<Int> =ArrayList<Int>()
        val imgsSrc: ArrayList<String?> = ArrayList<String?>()
        val imgsSrcLastChance: ArrayList<String?> = ArrayList<String?>()
        val images: ArrayList<Bitmap?> = ArrayList<Bitmap?>()
        val codes: ArrayList<Int?> = ArrayList<Int?>()
        for(i in parts){
            if(onlyNew.equals("1"))
                i.ifNewToXML="N"
            else
                i.ifNewToXML="U"
            dataNames.add(i.commentToShow!!)
            dataQuantities.add(i.quantityToShowSet!!)
            dataQuantitiesStore.add(i.quantityToShowStore!!)
            ids.add(i.id!!)
            imgsSrc.add(i.src)
            imgsSrcLastChance.add(i.srcLastChance)
            images.add(i.image)
            codes.add(i.code)
        }
        val listViewItems = findViewById<ListView>(R.id.elementList)
        val adapter = ProjectListAdapter(this,db!!,dataNames,dataQuantities,dataQuantitiesStore,ids,images,imgsSrc,imgsSrcLastChance,codes)
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
            //odhardkodowac to n bo on z xmla
            writeXML(parts,name!!)
            Toast.makeText(this, "Succesfully saved", Toast.LENGTH_LONG).show()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    fun writeXML(parts: ArrayList<InventoryPart>, inventoryName: String) {
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
                itemQty.appendChild(doc.createTextNode(it.ifNewToXML))

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
