package com.pixelz360.docsign.imagetopdf.creator.viewmodel;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.pixelz360.docsign.imagetopdf.creator.RoomDb.PdfFile;

import java.util.List;

@Dao
public interface ViewModelPdfFileDao {
    @Insert
    void insert(PdfFile pdfFile);



    @Query("SELECT * FROM pdf_files")
    List<PdfFile> getAllPdfFiles();

    @Query("SELECT * FROM pdf_files WHERE isFavorite = 1")
    List<PdfFile> getFavoritePdfFiles();

    @Query("SELECT * FROM pdf_files WHERE isSignedFiles = 1")
    List<PdfFile> getSignedPdfFiles();

    @Query("SELECT * FROM pdf_files WHERE isIdCardFiles = 1")
    List<PdfFile> getIdCardPdfFiles();

    // false Descending
    // true  Ascending

    @Query("SELECT * FROM pdf_files ORDER BY CASE WHEN :isAsc = 1 THEN fileSize END ASC, CASE WHEN :isAsc = 0 THEN fileSize END DESC, CASE WHEN :isAsc = 3 THEN createdDate END ASC, CASE WHEN :isAsc = 2 THEN createdDate END DESC")
                List<PdfFile> getAscandingDescending(int isAsc);

    @Query("SELECT * FROM pdf_files WHERE isFavorite = 1 ORDER BY CASE WHEN :isAsc = 1 THEN fileSize END ASC, CASE WHEN :isAsc = 0 THEN fileSize END DESC, CASE WHEN :isAsc = 3 THEN createdDate END ASC, CASE WHEN :isAsc = 2 THEN createdDate END DESC")
    List<PdfFile> getFavAscandingDescending(int isAsc);

    @Query("SELECT * FROM pdf_files WHERE isSignedFiles = 1 ORDER BY CASE WHEN :isAsc = 1 THEN fileSize END ASC, CASE WHEN :isAsc = 0 THEN fileSize END DESC, CASE WHEN :isAsc = 3 THEN createdDate END ASC, CASE WHEN :isAsc = 2 THEN createdDate END DESC")
    List<PdfFile> getSignedAscandingDescending(int isAsc);

    @Query("SELECT * FROM pdf_files WHERE isIdCardFiles = 1 ORDER BY CASE WHEN :isAsc = 1 THEN fileSize END ASC, CASE WHEN :isAsc = 0 THEN fileSize END DESC, CASE WHEN :isAsc = 3 THEN createdDate END ASC, CASE WHEN :isAsc = 2 THEN createdDate END DESC")
    List<PdfFile> getIdCardAscandingDescending(int isAsc);




    @Query("UPDATE pdf_files SET filePath = :newFileName WHERE filePath = :currentFileName")
    void renameFile(String currentFileName, String newFileName);

    @Update
    void updatePdfFile(PdfFile pdfFile);

    @Query("UPDATE pdf_files SET filePath=:filePath, fileName=:previousName, password=:password, fileSize=:fileSize, createdDate=:createdDate, isFavorite=:isFavorite, isSignedFiles=:isSignedFiles WHERE id = :id")
    void updateTest(int id, String filePath, String previousName, String password, double fileSize, long createdDate, boolean isFavorite, boolean isSignedFiles);

    @Delete
    void deletePdfFile(PdfFile pdfFile);
}

