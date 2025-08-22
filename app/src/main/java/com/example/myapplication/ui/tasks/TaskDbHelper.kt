package com.example.myapplication.ui.tasks

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

data class TaskRecord(
    val id: Long,
    var name: String,
    var priority: String,
    var dueDate: String,
    var plannedDate: String,
    var isCompleted: Boolean,
    val category: String,
    val description: String
)

class TaskDbHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "tasks.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE = "tasks"
        private const val COL_ID = "id"
        private const val COL_NAME = "name"
        private const val COL_DESC = "description"
        private const val COL_PRIORITY = "priority"
        private const val COL_CATEGORY = "category"
        private const val COL_DUE = "dueDate"
        private const val COL_PLANNED = "plannedDate"
        private const val COL_DONE = "isCompleted"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
      CREATE TABLE $TABLE (
        $COL_ID        INTEGER PRIMARY KEY AUTOINCREMENT,
        $COL_NAME      TEXT    NOT NULL,
        $COL_DESC      TEXT,
        $COL_PRIORITY  TEXT,
        $COL_CATEGORY  TEXT,
        $COL_DUE       TEXT,
        $COL_PLANNED   TEXT,
        $COL_DONE      INTEGER NOT NULL DEFAULT 0
      )
    """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldV: Int, newV: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE")
        onCreate(db)
    }

    fun insert(task: TaskRecord): Long {
        val cv = ContentValues().apply {
            put(COL_NAME, task.name)
            put(COL_DESC, task.description)
            put(COL_PRIORITY, task.priority)
            put(COL_CATEGORY, task.category)
            put(COL_DUE, task.dueDate)
            put(COL_PLANNED, task.plannedDate)
            put(COL_DONE, if (task.isCompleted) 1 else 0)
        }
        return writableDatabase.insert(TABLE, null, cv)
    }

    fun delete(id: Long) {
        writableDatabase.delete(
            TABLE,
            "$COL_ID = ?",
            arrayOf(id.toString())
        )
    }

    fun getAll(): List<TaskRecord> {
        val cursor: Cursor = readableDatabase.query(
            TABLE, null, null, null, null, null, null
        )

        val list = mutableListOf<TaskRecord>()
        while (cursor.moveToNext()) {
            list += TaskRecord(
                id          = cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID)),
                name        = cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
                description = cursor.getString(cursor.getColumnIndexOrThrow(COL_DESC)),
                priority    = cursor.getString(cursor.getColumnIndexOrThrow(COL_PRIORITY)),
                category    = cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY)),
                dueDate     = cursor.getString(cursor.getColumnIndexOrThrow(COL_DUE)),
                plannedDate = cursor.getString(cursor.getColumnIndexOrThrow(COL_PLANNED)),
                isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow(COL_DONE)) == 1
            )
        }
        cursor.close()
        return list
    }

    fun update(task: TaskRecord): Int {
        val cv = ContentValues().apply {
            put(COL_NAME, task.name)
            put(COL_DESC, task.description)
            put(COL_PRIORITY, task.priority)
            put(COL_CATEGORY, task.category)
            put(COL_DUE, task.dueDate)
            put(COL_PLANNED, task.plannedDate)
            put(COL_DONE, if (task.isCompleted) 1 else 0)
        }
        return writableDatabase.update(
            TABLE,
            cv,
            "$COL_ID = ?",
            arrayOf(task.id.toString())
        )
    }
}
