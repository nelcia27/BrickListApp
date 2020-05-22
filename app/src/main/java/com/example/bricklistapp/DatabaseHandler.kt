package com.example.bricklistapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
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

    fun checkIfInventoryExists(name: String) : Boolean{
        val db = this.readableDatabase
        var result=false
        val query = "SELECT * FROM Inventories where Name=\""+name+"\""
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            result=true
            cursor.close()
        }
        db.close()
        return result
    }

    fun changeQuantityForPlus(name: String){
        val db = this.readableDatabase
        val query = "SELECT QuantityInSet, QuantityInStore FROM Inventories where Name=\""+name+"\""
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            var fullSet=cursor.getInt(0)
            var mySet=cursor.getInt(1)
            if(fullSet>mySet+1){
                mySet+=1
                val query1 = "update InventoriesParts set QuantityInStore="+mySet+" where Name=\"" + name + "\""
                db.execSQL(query1)
            }else{
                Toast.makeText(mContext,"Quantity cannot be higher than quantity in set",Toast.LENGTH_LONG).show()
            }

            cursor.close()
        }
        db.close()
    }

    fun changeQuantityForMinus(name: String){
        val db = this.readableDatabase
        val query = "SELECT QuantityInStore FROM Inventories where Name=\""+name+"\""
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            var mySet=cursor.getInt(0)
            if(mySet-1>=0){
                mySet-=1
                val query1 = "update InventoriesParts set QuantityInStore="+mySet+" where Name=\"" + name + "\""
                db.execSQL(query1)
            }else{
                Toast.makeText(mContext,"Quantity cannot be less than 0",Toast.LENGTH_LONG).show()
            }
            cursor.close()
        }
        db.close()

    }

    fun updateLastAccessedOfInventory(name: String){
        val db = this.writableDatabase
        val query = "update Inventories set LastAccessed=0 where Name=\"" + name + "\""
        db.execSQL(query)
        val query1 = "update Inventories set LastAccessed=LastAccessed+1 where Name<>\"" + name + "\""
        db.execSQL(query1)
        db.close()
    }

    fun achivizeInventory(name : String){
        val db = this.writableDatabase
        val query = "update Inventories set Active=0 where Name=\"" + name + "\""
        db.execSQL(query)
        db.close()
    }

    fun makeNotArchived(name: String){
        val db = this.writableDatabase
        val query = "update Inventories set Active=0 where Name=\"" + name + "\""
        db.execSQL(query)
        db.close()
    }

    fun deleteInventory(name : String){
        val db = this.writableDatabase
        val query = "SELECT id FROM Inventories where Name=\""+name+"\""
        val cursor = db.rawQuery(query, null)
        var num :Int?=null
        if(cursor.moveToFirst()){
            num=cursor.getInt(0)
            cursor.close()
        }
        val query1 = "delete Inventories where Name=\"" + name + "\""
        if(num!=null){
            val query2 = "delete InventoriesParts where InventoryID=" + num
            db.execSQL(query2)
        }
        db.execSQL(query1)
        db.close()
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

    fun takeNotActiveInventoriesNames(): ArrayList<String> {
        val db = this.readableDatabase
        val query = "SELECT Name FROM Inventories where Active=0"
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
        }else{
            val q="SELECT Code FROM Codes where ItemID=\""+invp.itemID+"\" and ColorID=\""+invp.colorID+"\""
            val c = db.rawQuery(q, null)
            if(c.moveToFirst()){
                invp.FotosCollecter(this,c.getString(0),invp.colorID!!.toInt())
                c.close()
            }

        }

        var comment: String?=null
        val query1="SELECT NamePL FROM Parts where TypeID=\""+invp.typeID+"\""
        val cursor1 = db.rawQuery(query1, null)
        if (cursor1.moveToFirst()) {
            if(cursor1.getString(0)!=null){
                comment=cursor1.getString(0)
            }else{
                val query2="SELECT Name FROM Parts where TypeID=\""+invp.typeID+"\""
                val cursor2 = db.rawQuery(query2, null)
                if(cursor2.moveToFirst()){
                    comment=cursor2.getString(0)
                    cursor2.close()
                }
            }

            cursor1.close()
        }

        var comment1: String?=null
        val query2="SELECT NamePL FROM Colors where Code=\""+invp.colorID+"\""
        val cursor2 = db.rawQuery(query2, null)
        if (cursor2.moveToFirst()) {
            if(cursor2.getString(0)!=null){
                comment1=cursor2.getString(0)
            }else{
                val query3="SELECT Name FROM Colors where Code=\""+invp.colorID+"\""
                val cursor3 = db.rawQuery(query3, null)
                if(cursor3.moveToFirst()){
                    comment1=cursor3.getString(0)
                    cursor3.close()
                }
            }
            cursor1.close()
        }

        val query4="SELECT Code FROM Codes where ItemID=\""+invp.itemID+"\" and ColorID=\""+invp.colorID+"\""
        val cursor4 = db.rawQuery(query4, null)
        var comment2: String?=null
        if (cursor4.moveToFirst()) {
            comment2 = cursor.getInt(0).toString()
            cursor4.close()
        }

        invp.commentToShow="aaaa"//comment!!+" "+comment1!!+" ["+comment2!!+"]"
        invp.quantityToShow=invp.quantityInStore.toString()+" of "+invp.quantityInSet.toString()

        db.close()
        return invp
    }

    fun prepareToShowProject(name: String): ArrayList<InventoryPart>{
        val db = this.readableDatabase
        val query = "SELECT id FROM Inventories where Name=\"" +name+"\""
        val cursor = db.rawQuery(query, null)
        var listToShow: ArrayList<InventoryPart> = ArrayList<InventoryPart>()
        if(cursor.moveToFirst()){
            val code=cursor.getInt(0)
            val query1 = "SELECT * FROM InventoriesParts where InventoryID="+code
            val cursor1 = db.rawQuery(query1, null)
            if(cursor1.moveToFirst()){
                var invp=InventoryPart(cursor1.getInt(1),cursor1.getString(2),cursor1.getString(3),cursor1.getInt(4),cursor1.getInt(5),cursor1.getString(6),cursor1.getString(7))
                listToShow.add(this.collectInfoAndFoto(invp))
                while(cursor1.moveToNext()){
                    invp=InventoryPart(cursor1.getInt(1),cursor1.getString(2),cursor1.getString(3),cursor1.getInt(4),cursor1.getInt(5),cursor1.getString(6),cursor1.getString(7))
                    listToShow.add(this.collectInfoAndFoto(invp))
                }
                cursor1.close()
            }
            cursor.close()
        }
        db.close()
        return listToShow
    }

    fun saveImageToDatabase(image : ByteArray, code: Int){
        val db = this.writableDatabase
        val query = "update Codes set Image=\"" + image + "\""+" where code="+code
        db.execSQL(query)
        db.close()
    }





}