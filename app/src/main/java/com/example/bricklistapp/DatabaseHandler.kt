package com.example.bricklistapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.BitmapFactory
import android.widget.Toast
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.security.AccessController.getContext
import java.sql.SQLException
import kotlin.coroutines.coroutineContext


class DatabaseHandler : SQLiteAssetHelper {

    private val DATABASE_VERSION = 1
    private val DATABASE_NAME = "BrickList.db"
    private val mContext: Context

    constructor(context: Context) : super(context, "BrickList.db", null, 1) {
        this.mContext = context

    }

    fun checkIfInventoryExists(urlElem: String) : Boolean{
        return true
    }

    fun changeQuantityForPlus(name: String){
        val db = this.readableDatabase
        val query = "SELECT QuantityInSet, QuantityInStore FROM Inventories where Name=\""+name+"\""
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            var fullSet=cursor.getInt(0)
            var mySet=cursor.getInt(1)
            if(fullSet>mySet+1){
                //dodaj jeden
            }
            while (cursor.moveToNext()) {
                fullSet=cursor.getInt(0)
                mySet=cursor.getInt(1)
                if(fullSet>mySet+1){
                    //dodaj jeden
                }
            }
            cursor.close()
        }
        db.close()
    }

    fun changeQuantityForMinus(name: String){
        val db = this.readableDatabase
        val query = "SELECT QuantityInSet, QuantityInStore FROM Inventories where Name=\""+name+"\""
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            var fullSet=cursor.getInt(0)
            var mySet=cursor.getInt(1)
            if(mySet-1>=0){
                //odejmij jeden
            }
            while (cursor.moveToNext()) {
                fullSet=cursor.getInt(0)
                mySet=cursor.getInt(1)
                if(mySet-1>=0){
                    //odejmij jeden
                }
            }
            cursor.close()
        }
        db.close()

    }

    fun updateLastAccesedOfInventory(){

    }

    fun achivizeInventory(name : String){
        val db = this.readableDatabase
        val query = "SELECT QuantityInSet, QuantityInStore FROM Inventories where Name=\""+name+"\""
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {

        cursor.close()
        }
        
        db.close()

    }

    fun deleteInventory(){

    }

    fun prepereProjectToPresent(){

    }

    fun addInventory(inv: Inventory){
        val values = ContentValues()
        values.put("id", inv.id)
        values.put("Name", inv.name)
        values.put("Active", inv.active)
        values.put("LastAccessed", inv.lastAccessed)

        val db = this.writableDatabase

        db.insert("Inventories", null, values)
        db.close()
    }

    fun addInventoryPart(invp: InventoryPart){
        val values = ContentValues()
        values.put("id", invp.id)
        values.put("InventoryID", invp.inventoryID)
        values.put("TypeID", invp.typeID)
        values.put("ItemID", invp.itemID)
        values.put("QuantityInSet", invp.quantityInSet)
        values.put("QuantityInStore", invp.quantityInStore)
        values.put("ColorID", invp.colorID)

        val db = this.writableDatabase

        db.insert("InventoriesParts", null, values)
        db.close()
    }

    fun maxInventoryNum():Int{
        var number=0
        val db = this.readableDatabase
        val query="SELECT"+" MAX("+"id"+") FROM "+"Inventories"
        val cursor = db.rawQuery(query, null)


        if (cursor.moveToFirst()) {

            var numtmp: Int?=cursor.getInt(0)
            if(numtmp!=null){
                number=numtmp
            }
            cursor.close()
        }
        db.close()
        return number
    }

    fun maxInventoryPartNum():Int{
        var number=0
        val db = this.readableDatabase
        val query="SELECT"+" MAX(id) FROM"+" InventoriesParts"
        val cursor = db.rawQuery(query, null)


        if (cursor.moveToFirst()) {

            var numtmp: Int?=cursor.getInt(0)
            if(numtmp!=null){
                number=numtmp
            }
            cursor.close()
        }
        db.close()
        return number
    }

    fun takeActiveInventoriesNames(): ArrayList<String> {
        val db = this.readableDatabase
        val query = "SELECT Name FROM Inventories where Active=1"
        val cursor = db.rawQuery(query, null)
        var result: ArrayList<String> = ArrayList<String>()
        if (cursor.moveToFirst()) {
            cursor.getString(0)
            while (cursor.moveToNext()) {
                result.add(cursor.getString(0))
            }
            cursor.close()
        }
        db.close()
        return  result
    }

    fun collectInfoAndFoto(invp: InventoryPart): InventoryPart{
        val db = this.readableDatabase
        val query="SELECT Image FROM Codes where ItemID=\""+invp.itemID+"\" and ColorID=\""+invp.colorID+"\""
        val cursor = db.rawQuery(query, null)
        val blob: ByteArray?
        if (cursor.moveToFirst()) {
            blob = cursor.getBlob(0)
            if (blob != null) {
                invp.image = BitmapFactory.decodeByteArray(blob, 0, blob.size)
            }
            cursor.close()
        }
        db.close()
        return invp
    }

    fun takeImageFromInternet{

    }





}