package com.pixelz360.docsign.imagetopdf.creator.viewmodel

import android.content.Context
import androidx.room.Room
import com.pixelz360.docsign.imagetopdf.creator.RoomDb.AppDatabase
import com.pixelz360.docsign.imagetopdf.creator.RoomDb.PdfFileDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "pdf_database"
        )
            .addMigrations(AppDatabase.MIGRATION_1_2) // Add migration logic here
            .fallbackToDestructiveMigration() // Optional: Use if you don't need to preserve data
            .build();
    }

    @Provides
    fun providePdfFileDao(appDatabase: AppDatabase): PdfFileDao {
        return appDatabase.pdfFileDao()
    }
}

