package com.pixelz360.docsign.imagetopdf.creator.RoomDb;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {PdfFile.class}, version = 2) // Increment version to 2
public abstract class AppDatabase extends RoomDatabase {

    public abstract PdfFileDao pdfFileDao();

    // Migration from version 1 to 2
    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Example: Adding a new column "description"
            database.execSQL("ALTER TABLE PdfFile ADD COLUMN description TEXT DEFAULT ''");
        }
    };
}



