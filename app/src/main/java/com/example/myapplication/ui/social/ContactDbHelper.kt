// app/src/main/java/com/example/myapplication/ui/social/ContactDbHelper.kt
package com.example.myapplication.ui.social

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ContactDbHelper(context: Context) :
    SQLiteOpenHelper(context, "contacts.db", null, 1) {

    companion object {
        private const val TBL = "contacts"
        private const val COL_ID       = "id"
        private const val COL_NAME     = "name"
        private const val COL_NOTES    = "notes"
        private const val COL_PHONE    = "phone"
        private const val COL_EMAIL    = "email"
        private const val COL_CATEGORY = "category"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
      CREATE TABLE $TBL (
        $COL_ID       INTEGER PRIMARY KEY AUTOINCREMENT,
        $COL_NAME     TEXT NOT NULL,
        $COL_NOTES    TEXT,
        $COL_PHONE    TEXT,
        $COL_EMAIL    TEXT,
        $COL_CATEGORY TEXT
      )
    """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldV: Int, newV: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TBL")
        onCreate(db)
    }

    fun insert(r: ContactRecord): Long {
        val cv = ContentValues().apply {
            put(COL_NAME,     r.name)
            put(COL_NOTES,    r.notes)
            put(COL_PHONE,    r.phone)
            put(COL_EMAIL,    r.email)
            put(COL_CATEGORY, r.category)
        }
        return writableDatabase.insert(TBL, null, cv)
    }

    fun delete(id: Long) {
        writableDatabase.delete(TBL, "$COL_ID=?", arrayOf(id.toString()))
    }

    fun getAll(): List<ContactRecord> {
        val c: Cursor = readableDatabase.query(TBL, null, null, null, null, null, null)
        val out = mutableListOf<ContactRecord>()
        while (c.moveToNext()) {
            out += ContactRecord(
                id       = c.getLong   (c.getColumnIndexOrThrow(COL_ID)),
                name     = c.getString (c.getColumnIndexOrThrow(COL_NAME)),
                notes    = c.getString (c.getColumnIndexOrThrow(COL_NOTES)),
                phone    = c.getString (c.getColumnIndexOrThrow(COL_PHONE)),
                email    = c.getString (c.getColumnIndexOrThrow(COL_EMAIL)),
                category = c.getString (c.getColumnIndexOrThrow(COL_CATEGORY))
            )
        }
        c.close()
        return out
    }
}
