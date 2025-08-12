package com.pixelz360.docsign.imagetopdf.creator.viewmodel



import com.pixelz360.docsign.imagetopdf.creator.RoomDb.PdfFile
import com.pixelz360.docsign.imagetopdf.creator.RoomDb.PdfFileDao
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext



class PdfFileRepository @Inject constructor(
    private val pdfFileDao: PdfFileDao
) {





    // Insert a PDF file
    suspend fun insert(pdfFile: PdfFile) = withContext(Dispatchers.IO) {
        pdfFileDao.insert(pdfFile)
    }



    // Retrieve all PDF files
//    suspend fun getAllPdfFiles(): List<PdfFile> = withContext(Dispatchers.IO) {
//        pdfFileDao.getAllPdfFiles()
//    }

//    val getAllPdfFiles: Flow<List<PdfFile>> = pdfFileDao.getAllPdfFiles()


        fun getPdfFilesByUserType(userType: String,extention: String): Flow<List<PdfFile>> {
            return pdfFileDao.getPdfFilesByUserType(userType,extention)
        }


//    fun getPdfFilesByUserType(userType: String): Flow<List<PdfFile>> {
//        return pdfFileDao.getPdfFilesByUserType(userType)
//    }



    // Retrieve favorite PDF files
//    suspend fun getFavoritePdfFiles(): List<PdfFile> = withContext(Dispatchers.IO) {
//        pdfFileDao.getFavoritePdfFiles()
//    }

//    val getFavoritePdfFiles: Flow<List<PdfFile>> = pdfFileDao.getFavoritePdfFiles()

    fun getFavoritePdfFileByUserType(userType: String,extention: String): Flow<List<PdfFile>> {
        return pdfFileDao.getFavoritePdfFileByUserType(userType,extention)
    }

//    val getRecentPdfFiles: Flow<List<PdfFile>> = pdfFileDao.getIdRecentPdfFiles()

    fun getRecentPdfFilesByUserType(userType: String,extention: String): Flow<List<PdfFile>> {
        return pdfFileDao.getRecentPdfFilesByUserType(userType,extention)
    }

    fun getPdfToJpjImagesByUserType(userType: String,extention: String): Flow<List<PdfFile>> {
        return pdfFileDao.getPdfToJpjImagesByUserType(userType,extention)
    }

    fun getAscendingDescendingByUserType(userType: String,isAsc: Int): Flow<List<PdfFile>> {
        return pdfFileDao.getAscendingDescendingByUserType(userType,isAsc)
    }

    fun getFavAscendingDescendingByUserType(userType: String,isAsc: Int): Flow<List<PdfFile>> {
        return pdfFileDao.getFavAscendingDescendingByUserType(userType,isAsc)
    }


    // Retrieve signed PDF files
    suspend fun getSignedPdfFiles(): List<PdfFile> = withContext(Dispatchers.IO) {
        pdfFileDao.getSignedPdfFiles()
    }

    // Retrieve ID card PDF files
    suspend fun getIdCardPdfFiles(): List<PdfFile> = withContext(Dispatchers.IO) {
        pdfFileDao.getIdCardPdfFiles()
    }

    // Retrieve ID Marge PDF files
    suspend fun getIdMargePdfFiles(): List<PdfFile> = withContext(Dispatchers.IO) {
        pdfFileDao.getIdMargePdfFiles()
    }

    // Retrieve ID Split PDF files
    suspend fun getIdSplitPdfFiles(): List<PdfFile> = withContext(Dispatchers.IO) {
        pdfFileDao.getIdSplitPdfFiles()
    }

//    // Retrieve sorted PDF files
//    suspend fun getRecentPdfFiles(): List<PdfFile> = withContext(Dispatchers.IO) {
//        pdfFileDao.getIdRecentPdfFiles()
//    }





    // Retrieve sorted PDF files
//    suspend fun getAscandingDescending(isAsc: Int): List<PdfFile> = withContext(Dispatchers.IO) {
//        pdfFileDao.getAscendingDescending(isAsc)
//    }

    // Retrieve sorted favorite PDF files
//    suspend fun getFavAscandingDescending(isAsc: Int): List<PdfFile> = withContext(Dispatchers.IO) {
//        pdfFileDao.getFavAscendingDescending(isAsc)
//    }

    // Retrieve sorted signed PDF files
    suspend fun getSignedAscandingDescending(isAsc: Int): List<PdfFile> = withContext(Dispatchers.IO) {
        pdfFileDao.getSignedAscendingDescending(isAsc)
    }

    // Retrieve sorted ID card PDF files
    suspend fun getIdCardAscandingDescending(isAsc: Int): List<PdfFile> = withContext(Dispatchers.IO) {
        pdfFileDao.getIdCardAscendingDescending(isAsc)
    }

    // Retrieve sorted ID Marge PDF files
    suspend fun getIdMargeAscandingDescending(isAsc: Int): List<PdfFile> = withContext(Dispatchers.IO) {
        pdfFileDao.getIdMargeAscendingDescending(isAsc)
    }

    // Retrieve sorted ID Split PDF files
    suspend fun getIdSplitAscandingDescending(isAsc: Int): List<PdfFile> = withContext(Dispatchers.IO) {
        pdfFileDao.getIdSplitAscendingDescending(isAsc)
    }

    // Rename a PDF file based on the file path
    suspend fun renameFile(currentFileName: String, newFileName: String) = withContext(Dispatchers.IO) {
        pdfFileDao.renameFile(currentFileName, newFileName)
    }

    // Update a PDF file
    suspend fun updatePdfFile(pdfFile: PdfFile) = withContext(Dispatchers.IO) {
        pdfFileDao.updatePdfFile(pdfFile)
    }

    // Update specific fields in a PDF file by ID
    suspend fun updateTest(id: Int, filePath: String, previousName: String, password: String, fileSize: Double, createdDate: Long, isFavorite: Boolean, isSignedFiles: Boolean) = withContext(Dispatchers.IO) {
        pdfFileDao.updateTest(id, filePath, previousName, password, fileSize, createdDate, isFavorite, isSignedFiles)
    }

    // Delete a PDF file
    suspend fun deletePdfFile(pdfFile: PdfFile) = withContext(Dispatchers.IO) {
        pdfFileDao.deletePdfFile(pdfFile)
    }
}

