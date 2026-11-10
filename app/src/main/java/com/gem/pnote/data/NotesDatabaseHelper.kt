package com.gem.pnote.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class NotesDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "pnotes.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_NOTES = "notes"
        const val COLUMN_ID = "id"
        const val COLUMN_HEADER = "header"
        const val COLUMN_CONTENT = "content"
        const val COLUMN_CREATED_AT = "created_at"
        const val COLUMN_UPDATED_AT = "updated_at"
        const val COLUMN_IS_PINNED = "is_pinned"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_NOTES (
                $COLUMN_ID TEXT PRIMARY KEY,
                $COLUMN_HEADER TEXT NOT NULL,
                $COLUMN_CONTENT TEXT NOT NULL,
                $COLUMN_CREATED_AT INTEGER NOT NULL,
                $COLUMN_UPDATED_AT INTEGER NOT NULL,
                $COLUMN_IS_PINNED INTEGER NOT NULL DEFAULT 0
            )
        """.trimIndent()
        db.execSQL(createTableQuery)

        // Seed with modern sample notes to showcase headers-first layout and CSV extraction
        seedInitialNotes(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NOTES")
        onCreate(db)
    }

    private fun seedInitialNotes(db: SQLiteDatabase) {
        val now = System.currentTimeMillis()
        val sampleNotes = listOf(
            Note(
                header = "Welcome to PNote! 🚀",
                content = "A streamlined notes app engineered for focus and rapid CSV extraction. Notice how headers are prominently displayed first. Tap any entry to edit or view.",
                createdAt = now - 3600000 * 3,
                updatedAt = now - 3600000 * 3,
                isPinned = true
            ),
            Note(
                header = "Extract to CSV Guide 📊",
                content = "Tap the 'Extract CSV' button at the top to export all your notes. You can save directly to your device storage or share directly to Google Sheets and Excel.",
                createdAt = now - 3600000 * 2,
                updatedAt = now - 3600000 * 2,
                isPinned = false
            ),
            Note(
                header = "Project Roadmap & Tasks ⚡",
                content = "- Refine black & orange visual hierarchy\n- Verify rounded entry corners (20dp)\n- Validate CSV RFC 4180 export compliance\n- Edge-to-edge Android system navigation support",
                createdAt = now - 3600000,
                updatedAt = now - 3600000,
                isPinned = false
            )
        )

        for (note in sampleNotes) {
            val values = ContentValues().apply {
                put(COLUMN_ID, note.id)
                put(COLUMN_HEADER, note.header)
                put(COLUMN_CONTENT, note.content)
                put(COLUMN_CREATED_AT, note.createdAt)
                put(COLUMN_UPDATED_AT, note.updatedAt)
                put(COLUMN_IS_PINNED, if (note.isPinned) 1 else 0)
            }
            db.insert(TABLE_NOTES, null, values)
        }
    }

    fun getAllNotes(): List<Note> {
        val notes = mutableListOf<Note>()
        val db = readableDatabase
        val cursor: Cursor = db.query(
            TABLE_NOTES,
            null,
            null,
            null,
            null,
            null,
            "$COLUMN_IS_PINNED DESC, $COLUMN_UPDATED_AT DESC"
        )

        cursor.use {
            val idIndex = it.getColumnIndexOrThrow(COLUMN_ID)
            val headerIndex = it.getColumnIndexOrThrow(COLUMN_HEADER)
            val contentIndex = it.getColumnIndexOrThrow(COLUMN_CONTENT)
            val createdAtIndex = it.getColumnIndexOrThrow(COLUMN_CREATED_AT)
            val updatedAtIndex = it.getColumnIndexOrThrow(COLUMN_UPDATED_AT)
            val isPinnedIndex = it.getColumnIndexOrThrow(COLUMN_IS_PINNED)

            while (it.moveToNext()) {
                notes.add(
                    Note(
                        id = it.getString(idIndex),
                        header = it.getString(headerIndex),
                        content = it.getString(contentIndex),
                        createdAt = it.getLong(createdAtIndex),
                        updatedAt = it.getLong(updatedAtIndex),
                        isPinned = it.getInt(isPinnedIndex) == 1
                    )
                )
            }
        }
        return notes
    }

    fun insertNote(note: Note): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ID, note.id)
            put(COLUMN_HEADER, note.header)
            put(COLUMN_CONTENT, note.content)
            put(COLUMN_CREATED_AT, note.createdAt)
            put(COLUMN_UPDATED_AT, note.updatedAt)
            put(COLUMN_IS_PINNED, if (note.isPinned) 1 else 0)
        }
        return db.insertWithOnConflict(
            TABLE_NOTES,
            null,
            values,
            SQLiteDatabase.CONFLICT_REPLACE
        )
    }

    fun updateNote(note: Note): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_HEADER, note.header)
            put(COLUMN_CONTENT, note.content)
            put(COLUMN_UPDATED_AT, note.updatedAt)
            put(COLUMN_IS_PINNED, if (note.isPinned) 1 else 0)
        }
        return db.update(
            TABLE_NOTES,
            values,
            "$COLUMN_ID = ?",
            arrayOf(note.id)
        )
    }

    fun deleteNote(id: String): Int {
        val db = writableDatabase
        return db.delete(
            TABLE_NOTES,
            "$COLUMN_ID = ?",
            arrayOf(id)
        )
    }
}
