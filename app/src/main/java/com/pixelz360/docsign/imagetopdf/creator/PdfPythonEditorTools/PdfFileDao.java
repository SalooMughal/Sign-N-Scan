package com.pixelz360.docsign.imagetopdf.creator.PdfPythonEditorTools;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PdfFileDao {
    @Insert
    void insert(PdfFile pdfFile);

    @Query("SELECT * FROM pdf_files_check")
    List<PdfFile> getAllFiles();

    // Insert multiple PdfFile objects
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<PdfFile> pdfFiles);
}

