package com.example.bricklistapp


import android.content.ContentValues
import android.content.Context
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.core.database.getBlobOrNull
import androidx.core.database.getIntOrNull
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper

class DatabaseHandler : SQLiteAssetHelper {

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

    fun changeQuantityForPlus(id: Int) : Boolean{
        var res=false
        val db = this.readableDatabase
        val query = "SELECT QuantityInSet, QuantityInStore FROM InventoriesParts where id="+id
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            val fullSet=cursor.getInt(0)
            var mySet=cursor.getInt(1)
            if(fullSet>=mySet+1){
                mySet+=1
                val query1 = "update InventoriesParts set QuantityInStore="+mySet+" where id="+id
                db.execSQL(query1)
                res=true
            }else{
                Toast.makeText(mContext,"Quantity cannot be higher than quantity in set",Toast.LENGTH_LONG).show()
            }
            cursor.close()
            }
        db.close()
        return res
    }

    fun changeQuantityForMinus(id: Int): Boolean{
        var res=false
        val db = this.readableDatabase
        val query = "SELECT QuantityInStore FROM InventoriesParts where id="+id
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            var mySet=cursor.getInt(0)
            if(mySet-1>=0){
                mySet-=1
                val query1 = "update InventoriesParts set QuantityInStore="+mySet+" where id=" + id
                db.execSQL(query1)
                res=true
            }else{
                Toast.makeText(mContext,"Quantity cannot be less than 0",Toast.LENGTH_LONG).show()
            }
            cursor.close()
        }
        db.close()
        return res
    }

    fun updateLastAccessedOfInventory(name: String){
        val db = this.writableDatabase
        val query = "update Inventories set LastAccessed=0 where Name=\"" + name + "\""
        db.execSQL(query)
        val query1 = "update Inventories set LastAccessed=LastAccessed+1 where Name<>\"" + name + "\""
        db.execSQL(query1)
        db.close()
    }

    fun archivizeInventory(name : String){
        val db = this.writableDatabase
        val qtmp="SELECT Active FROM Inventories where Name=\""+name+"\""
        val cursor=db.rawQuery(qtmp,null)
        val type: Int
        if(cursor.moveToFirst()){
            type=cursor.getInt(0)
            cursor.close()
            var query=""
            var comm=""
            if(type==1){
                query = "update Inventories set Active=0 where Name=\"" + name + "\""
                comm="Project archived"
            }else{
                query = "update Inventories set Active=1 where Name=\"" + name + "\""
                comm="Project unarchived"
            }
            db.execSQL(query)
            Toast.makeText(mContext,comm,Toast.LENGTH_LONG).show()
        }
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
        val query1 = "delete from Inventories where Name=\"" + name + "\""
        if(num!=null){
            val query2 = "delete from InventoriesParts where InventoryID=" + num
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
        val query = "SELECT Name FROM Inventories where Active=1 ORDER BY LastAccessed ASC"
        val cursor = db.rawQuery(query, null)
        var result: ArrayList<String> = ArrayList<String>()
        if (cursor.moveToFirst()) {
            result.add(cursor.getString(0))
            while (cursor.moveToNext()) {
                result.add(cursor.getString(0))
            }
            cursor.close()
        }
        db.close()
        return  result
    }

    fun takeAllInventoriesNames(): ArrayList<String> {
        val db = this.readableDatabase
        val query = "SELECT Name FROM Inventories ORDER BY LastAccessed ASC"
        val cursor = db.rawQuery(query, null)
        var result: ArrayList<String> = ArrayList<String>()
        if (cursor.moveToFirst()) {
            result.add(cursor.getString(0))
            while (cursor.moveToNext()) {
                result.add(cursor.getString(0))
            }
            cursor.close()
        }
        db.close()
        return  result
    }

    fun collectInfoAndFoto(invp: InventoryPart): InventoryPart?{
        val db = this.writableDatabase

        var comment=""
        var itemID=0
        val query="SELECT Name, NamePL, id FROM Parts where Code=\""+invp.itemID+"\""
        val cursor = db.rawQuery(query, null)
        if(cursor.moveToFirst()){
            if(cursor.getString(1)!=null){
                comment=cursor.getString(1)
            }else if(cursor.getString(0)!=null){
                comment=cursor.getString(0)
            }
            itemID=cursor.getInt(2)
            cursor.close()
        }

        var comment1=""
        var colorID=0
        val query1="SELECT Name, NamePL, id FROM Colors where Code="+invp.colorID
        val cursor1 = db.rawQuery(query1, null)
        if(cursor1.moveToFirst()){
            if(cursor1.getString(1)!=null){
                comment1=cursor1.getString(1)
            }else if(cursor1.getString(0)!=null){
                comment1=cursor1.getString(0)
            }
            colorID=cursor1.getInt(2)
            cursor1.close()
        }

        val queryImg="SELECT Image, Code FROM Codes where ItemID="+itemID+" and ColorID="+colorID
        val cursorImg = db.rawQuery(queryImg, null)
        val img: ByteArray?
        if(cursorImg.moveToFirst()){
            img=cursorImg.getBlobOrNull(0)
            if(img!=null){
                invp.image=BitmapFactory.decodeByteArray(img, 0, img.size)
            }else if(cursorImg.getIntOrNull(1)!=null){
                val url="https://www.lego.com/service/bricks/5/2/"+cursorImg.getInt(1).toString()
                invp.src=url
                invp.code=cursorImg.getInt(1)
            }
            cursorImg.close()
        }else{
            val queryTmp="SELECT Max(id) FROM Codes"
            val cursorTmp = db.rawQuery(queryTmp, null)
            var id=0
            if(cursorTmp.moveToFirst()){
                id=cursorTmp.getInt(0)
                cursorTmp.close()
            }
            id+=1
            val queryTmp1="SELECT Max(Code) FROM Codes"
            val cursorTmp1 = db.rawQuery(queryTmp1, null)
            var code=0
            if(cursorTmp1.moveToFirst()){
                code=cursorTmp1.getInt(0)
                cursorTmp1.close()
            }
            id+=1
            code+=1
            val values = ContentValues()
            values.put("id", id)
            values.put("ItemID", itemID)
            values.put("ColorID", colorID)
            values.put("Code",code)
            db.insert("Codes", null, values)
            val url1="http://img.bricklink.com/P/"+colorID.toString()+"/"+invp.itemID+".gif"
            invp.src=url1
            val url2="https://www.bricklink.com/PL/"+invp.itemID+".jpg"
            invp.srcLastChance=url2
            invp.code=code
        }

        db.close()

        if(comment.equals("(Not Applicable)")){
            invp.commentToShow=""
            invp.quantityToShowSet=""
            invp.quantityToShowStore=0
        }
        invp.commentToShow=comment+" "+comment1+" ["+invp.itemID+"]"
        invp.quantityToShowSet=" of "+invp.quantityInSet.toString()
        invp.quantityToShowStore=invp.quantityInStore

        return invp
    }

    fun prepareToShowProject(name: String): ArrayList<InventoryPart> {
        val db = this.readableDatabase
        val query = "SELECT id FROM Inventories where Name=\"" + name + "\""
        val cursor = db.rawQuery(query, null)
        var listToShow: ArrayList<InventoryPart> = ArrayList<InventoryPart>()
        if (cursor.moveToFirst()) {
            val code = cursor.getInt(0)
            val query1 = "SELECT * FROM InventoriesParts where InventoryID=" + code+" ORDER BY QuantityInStore,QuantityInSet"
            val cursor1 = db.rawQuery(query1, null)
            if (cursor1.moveToFirst()) {
                var invp = InventoryPart(
                    cursor1.getInt(0),
                    cursor1.getInt(1),
                    cursor1.getString(2),
                    cursor1.getString(3),
                    cursor1.getInt(4),
                    cursor1.getInt(5),
                    cursor1.getInt(6),
                    cursor1.getString(7)
                )
                if (this.collectInfoAndFoto(invp) != null) {
                    listToShow.add(this.collectInfoAndFoto(invp)!!)
                }
                while (cursor1.moveToNext()) {
                    invp = InventoryPart(
                        cursor1.getInt(0),
                        cursor1.getInt(1),
                        cursor1.getString(2),
                        cursor1.getString(3),
                        cursor1.getInt(4),
                        cursor1.getInt(5),
                        cursor1.getInt(6),
                        cursor1.getString(7)
                    )
                    if (this.collectInfoAndFoto(invp) != null) {
                        listToShow.add(this.collectInfoAndFoto(invp)!!)
                    }
                }
                cursor1.close()
            }
            cursor.close()
        }
        db.close()
        return listToShow
    }

    fun saveImageToDatabase(image: ByteArray, code: Int) {
        val db = this.writableDatabase
        val query = "update Codes set Image=\"" + image + "\"" + " where code=" + code
        db.execSQL(query)
        db.close()
    }


}