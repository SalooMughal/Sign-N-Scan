package com.pixelz360.docsign.imagetopdf.creator.PdfPythonEditorTools

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pdf_files_check")
data class PdfFile(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "file_path") val filePath: String
)