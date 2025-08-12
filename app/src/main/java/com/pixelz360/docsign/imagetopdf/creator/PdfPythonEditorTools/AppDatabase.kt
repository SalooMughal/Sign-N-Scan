package com.pixelz360.docsign.imagetopdf.creator.PdfPythonEditorTools

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [PdfFile::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun pdfFileDao(): PdfFileDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Example: Add a column named "description" to PdfFile table
                database.execSQL("ALTER TABLE PdfFile ADD COLUMN description TEXT DEFAULT ''")
            }
        }
    }
}
