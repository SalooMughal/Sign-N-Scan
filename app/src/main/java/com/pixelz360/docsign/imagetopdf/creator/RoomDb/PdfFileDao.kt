package com.pixelz360.docsign.imagetopdf.creator.RoomDb

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PdfFileDao {

    @Insert
    fun insert(pdfFile: PdfFile)

//    @Query("SELECT * FROM pdf_files")
//    fun getAllPdfFiles(): Flow<List<PdfFile>>

    @Query("SELECT * FROM pdf_files WHERE isAccountName = :isAccountName AND isFileExtention = :extention")
    fun getPdfFilesByUserType(isAccountName: String,extention:String): Flow<List<PdfFile>>

//    @Query("SELECT * FROM pdf_files WHERE isAccountName = :isAccountName")
//    fun getPdfFilesByUserType(isAccountName: String): Flow<List<PdfFile>>


//    @Query("SELECT * FROM pdf_files WHERE isFavorite = 1")
//    fun getFavoritePdfFiles(): Flow<List<PdfFile>>


    @Query("SELECT * FROM pdf_files WHERE isAccountName = :isAccountName AND isFileExtention = :extention AND isFavorite = 1")
    fun getFavoritePdfFileByUserType(isAccountName: String,extention:String): Flow<List<PdfFile>>


//    @Query("SELECT * FROM pdf_files ORDER BY createdDate DESC LIMIT 15")
//    fun getIdRecentPdfFiles(): Flow<List<PdfFile>>

    @Query("SELECT * FROM pdf_files WHERE isAccountName = :isAccountName AND isFileExtention = :extention ORDER BY createdDate DESC LIMIT 15")
    fun getRecentPdfFilesByUserType(isAccountName: String,extention:String): Flow<List<PdfFile>>

    @Query("SELECT * FROM pdf_files WHERE isAccountName = :isAccountName AND isFileExtention = :extention AND isPdfToJpgImages = 1")
    fun getPdfToJpjImagesByUserType(isAccountName: String,extention:String): Flow<List<PdfFile>>


    @Query(
        """
        SELECT * FROM pdf_files WHERE isAccountName = :isAccountName
        ORDER BY 
        CASE WHEN :isAsc = 1 THEN fileSize END ASC, 
        CASE WHEN :isAsc = 0 THEN fileSize END DESC, 
        CASE WHEN :isAsc = 3 THEN createdDate END ASC, 
        CASE WHEN :isAsc = 2 THEN createdDate END DESC
        """
    )
    fun getAscendingDescendingByUserType(isAccountName: String,isAsc: Int): Flow<List<PdfFile>>

    @Query(
        """
        SELECT * FROM pdf_files WHERE isAccountName = :isAccountName AND isFavorite = 1
        ORDER BY 
        CASE WHEN :isAsc = 1 THEN fileSize END ASC, 
        CASE WHEN :isAsc = 0 THEN fileSize END DESC, 
        CASE WHEN :isAsc = 3 THEN createdDate END ASC, 
        CASE WHEN :isAsc = 2 THEN createdDate END DESC
        """
    )
    fun getFavAscendingDescendingByUserType(isAccountName: String,isAsc: Int): Flow<List<PdfFile>>


//    fun getFavoritePdfFiles(): List<PdfFile>

    @Query("SELECT * FROM pdf_files WHERE isSignedFiles = 1")
    fun getSignedPdfFiles(): List<PdfFile>

    @Query("SELECT * FROM pdf_files WHERE isIdCardFiles = 1")
    fun getIdCardPdfFiles(): List<PdfFile>

    @Query("SELECT * FROM pdf_files WHERE isMargeFiles = 1")
    fun getIdMargePdfFiles(): List<PdfFile>

    @Query("SELECT * FROM pdf_files WHERE isSplitFiles = 1")
    fun getIdSplitPdfFiles(): List<PdfFile>



//    @Query(
//        """
//        SELECT * FROM pdf_files
//        ORDER BY
//        CASE WHEN :isAsc = 1 THEN fileSize END ASC,
//        CASE WHEN :isAsc = 0 THEN fileSize END DESC,
//        CASE WHEN :isAsc = 3 THEN createdDate END ASC,
//        CASE WHEN :isAsc = 2 THEN createdDate END DESC
//        """
//    )
//    fun getAscendingDescending(isAsc: Int): List<PdfFile>

//    @Query(
//        """
//        SELECT * FROM pdf_files WHERE isFavorite = 1
//        ORDER BY
//        CASE WHEN :isAsc = 1 THEN fileSize END ASC,
//        CASE WHEN :isAsc = 0 THEN fileSize END DESC,
//        CASE WHEN :isAsc = 3 THEN createdDate END ASC,
//        CASE WHEN :isAsc = 2 THEN createdDate END DESC
//        """
//    )
//    fun getFavAscendingDescending(isAsc: Int): List<PdfFile>

    @Query(
        """
        SELECT * FROM pdf_files WHERE isSignedFiles = 1 
        ORDER BY 
        CASE WHEN :isAsc = 1 THEN fileSize END ASC, 
        CASE WHEN :isAsc = 0 THEN fileSize END DESC, 
        CASE WHEN :isAsc = 3 THEN createdDate END ASC, 
        CASE WHEN :isAsc = 2 THEN createdDate END DESC
        """
    )
    fun getSignedAscendingDescending(isAsc: Int): List<PdfFile>

    @Query(
        """
        SELECT * FROM pdf_files WHERE isIdCardFiles = 1 
        ORDER BY 
        CASE WHEN :isAsc = 1 THEN fileSize END ASC, 
        CASE WHEN :isAsc = 0 THEN fileSize END DESC, 
        CASE WHEN :isAsc = 3 THEN createdDate END ASC, 
        CASE WHEN :isAsc = 2 THEN createdDate END DESC
        """
    )
    fun getIdCardAscendingDescending(isAsc: Int): List<PdfFile>

    @Query(
        """
        SELECT * FROM pdf_files WHERE isMargeFiles = 1 
        ORDER BY 
        CASE WHEN :isAsc = 1 THEN fileSize END ASC, 
        CASE WHEN :isAsc = 0 THEN fileSize END DESC, 
        CASE WHEN :isAsc = 3 THEN createdDate END ASC, 
        CASE WHEN :isAsc = 2 THEN createdDate END DESC
        """
    )
    fun getIdMargeAscendingDescending(isAsc: Int): List<PdfFile>

    @Query(
        """
        SELECT * FROM pdf_files WHERE isSplitFiles = 1 
        ORDER BY 
        CASE WHEN :isAsc = 1 THEN fileSize END ASC, 
        CASE WHEN :isAsc = 0 THEN fileSize END DESC, 
        CASE WHEN :isAsc = 3 THEN createdDate END ASC, 
        CASE WHEN :isAsc = 2 THEN createdDate END DESC
        """
    )
    fun getIdSplitAscendingDescending(isAsc: Int): List<PdfFile>

    @Query("UPDATE pdf_files SET filePath = :newFileName WHERE filePath = :currentFileName")
    fun renameFile(currentFileName: String, newFileName: String)

    @Update
    fun updatePdfFile(pdfFile: PdfFile)

    @Query(
        """
        UPDATE pdf_files 
        SET filePath = :filePath, 
            fileName = :previousName, 
            password = :password, 
            fileSize = :fileSize, 
            createdDate = :createdDate, 
            isFavorite = :isFavorite, 
            isSignedFiles = :isSignedFiles 
        WHERE id = :id
        """
    )
    fun updateTest(
        id: Int,
        filePath: String,
        previousName: String,
        password: String,
        fileSize: Double,
        createdDate: Long,
        isFavorite: Boolean,
        isSignedFiles: Boolean
    )

    @Delete
    fun deletePdfFile(pdfFile: PdfFile)

}
