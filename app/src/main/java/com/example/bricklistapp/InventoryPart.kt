package com.example.bricklistapp

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
    var colorID: Int? = null
    var extra: String? = null
    var image: Bitmap? = null
    var src: String?=null
    var srcLastChance: String?=null
    var code: Int?=null
    var commentToShow: String? = null
    var quantityToShowSet: String? = null
    var quantityToShowStore: Int?=null

    constructor(
        id: Int,
        inventoryID: Int,
        typeID: String,
        itemID: String,
        quantityInSet: Int,
        quantityInStore: Int,
        colorID: Int,
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

    constructor()
}