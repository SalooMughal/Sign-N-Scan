package com.pixelz360.docsign.imagetopdf.creator.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.pixelz360.docsign.imagetopdf.creator.RoomDb.PdfFile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PdfFileViewModel @Inject constructor(private val repository: PdfFileRepository) : ViewModel() {


//    private val _allPdfFiles = MutableLiveData<List<PdfFile>>()
//    val allPdfFiles: LiveData<List<PdfFile>> get() = _allPdfFiles

//    val allPdfFiles: LiveData<List<PdfFile>> = repository.getAllPdfFiles.asLiveData()

    fun getPdfFilesByUserType(userType: String,extention: String): LiveData<List<PdfFile>> {
        return repository.getPdfFilesByUserType(userType,extention).asLiveData()
    }

//    fun getPdfFilesByUserType(userType: String): LiveData<List<PdfFile>> {
//        return repository.getPdfFilesByUserType(userType).asLiveData()
//    }


//    private val _favoritePdfFiles = MutableLiveData<List<PdfFile>>()
//    val favoritePdfFiles: LiveData<List<PdfFile>> get() = _favoritePdfFiles

//    val favoritePdfFiles: LiveData<List<PdfFile>> = repository.getFavoritePdfFiles.asLiveData()

    fun getFavoritePdfFileByUserType(userType: String,extention: String): LiveData<List<PdfFile>> {
        return repository.getFavoritePdfFileByUserType(userType,extention).asLiveData()
    }

//    val idRecentPdfFiles: LiveData<List<PdfFile>> = repository.getRecentPdfFiles.asLiveData()

    fun getRecentPdfFilesByUserType(userType: String,extention: String): LiveData<List<PdfFile>> {
        return repository.getRecentPdfFilesByUserType(userType,extention).asLiveData()
    }

    fun getPdfToJpjImagesByUserType(userType: String,extention: String): LiveData<List<PdfFile>> {
        return repository.getPdfToJpjImagesByUserType(userType,extention).asLiveData()
    }

    fun getAscendingDescendingByUserType(userType: String,isAsc: Int): LiveData<List<PdfFile>> {
        return repository.getAscendingDescendingByUserType(userType,isAsc).asLiveData()
    }

    fun getFavAscendingDescendingByUserType(userType: String,isAsc: Int): LiveData<List<PdfFile>> {
        return repository.getFavAscendingDescendingByUserType(userType,isAsc).asLiveData()
    }

    private val _signedPdfFiles = MutableLiveData<List<PdfFile>>()
    val signedPdfFiles: LiveData<List<PdfFile>> get() = _signedPdfFiles

    private val _idCardPdfFiles = MutableLiveData<List<PdfFile>>()
    val idCardPdfFiles: LiveData<List<PdfFile>> get() = _idCardPdfFiles

    private val _idMargePdfFiles = MutableLiveData<List<PdfFile>>()
    val idMargePdfFiles: LiveData<List<PdfFile>> get() = _idMargePdfFiles


    private val _idSplitPdfFiles = MutableLiveData<List<PdfFile>>()
    val idSplitPdfFiles: LiveData<List<PdfFile>> get() = _idSplitPdfFiles

//    private val _idRecentPdfFiles = MutableLiveData<List<PdfFile>>()
//    val idRecentPdfFiles: LiveData<List<PdfFile>> get() = _idRecentPdfFiles




    private val _sortedPdfFiles = MutableLiveData<List<PdfFile>>()
    val sortedPdfFiles: LiveData<List<PdfFile>> get() = _sortedPdfFiles

    private val _favSortedPdfFiles = MutableLiveData<List<PdfFile>>()
    val favSortedPdfFiles: LiveData<List<PdfFile>> get() = _favSortedPdfFiles

    private val _signedSortedPdfFiles = MutableLiveData<List<PdfFile>>()
    val signedSortedPdfFiles: LiveData<List<PdfFile>> get() = _signedSortedPdfFiles

    private val _idCardSortedPdfFiles = MutableLiveData<List<PdfFile>>()
    val idCardSortedPdfFiles: LiveData<List<PdfFile>> get() = _idCardSortedPdfFiles

    private val _idMargeSortedPdfFiles = MutableLiveData<List<PdfFile>>()
    val idMargeSortedPdfFiles: LiveData<List<PdfFile>> get() = _idMargeSortedPdfFiles

    private val _idSplitSortedPdfFiles = MutableLiveData<List<PdfFile>>()
    val idSplitSortedPdfFiles: LiveData<List<PdfFile>> get() = _idSplitSortedPdfFiles

    init {
//        loadAllPdfFiles()
        loadIdCardPdfFiles()
        loadSignedPdfFiles()
//        loadFavoritePdfFiles()
        loadIdMargePdfFiles()
        loadIdSplitPdfFiles()
//        getRecentPdfFiles()
    }

    // Load all PDF files
//    fun loadAllPdfFiles() {
//        viewModelScope.launch {
//            _allPdfFiles.value = repository.getAllPdfFiles()
//        }
//    }

    // Load favorite PDF files
//    fun loadFavoritePdfFiles() {
//        viewModelScope.launch {
//            _favoritePdfFiles.value = repository.getFavoritePdfFiles()
//        }
//    }

    // Load signed PDF files
    fun loadSignedPdfFiles() {
        viewModelScope.launch {
            _signedPdfFiles.value = repository.getSignedPdfFiles()
        }
    }

    // Load ID card PDF files
    fun loadIdCardPdfFiles() {
        viewModelScope.launch {
            _idCardPdfFiles.value = repository.getIdCardPdfFiles()
//            Log.d("checkfiles123", "Files retrieved: " + files.size)

        }
    }

    // Load ID marge PDF files
    fun loadIdMargePdfFiles() {
        viewModelScope.launch {
            _idMargePdfFiles.value = repository.getIdMargePdfFiles()
//            Log.d("checkfiles123", "Files retrieved: " + files.size)

        }
    }

    // Load ID split PDF files
    fun loadIdSplitPdfFiles() {
        viewModelScope.launch {
            _idSplitPdfFiles.value = repository.getIdSplitPdfFiles()
//            Log.d("checkfiles123", "Files retrieved: " + files.size)

        }

    }

    // Load recent PDF files based on limit
//    fun  getRecentPdfFiles() {
//        viewModelScope.launch {
//            _idRecentPdfFiles.value = repository.getRecentPdfFiles()
//
//        }
//    }

    // Load sorted PDF files based on ascending or descending order
//    fun  loadSortedPdfFiles(isAsc: Int) {
//        viewModelScope.launch {
//            _sortedPdfFiles.value = repository.getAscandingDescending(isAsc)
//        }
//    }

    // Load sorted favorite PDF files
//    fun loadFavSortedPdfFiles(isAsc: Int) {
//        viewModelScope.launch {
//            _favSortedPdfFiles.value = repository.getFavAscandingDescending(isAsc)
//        }
//    }

    // Load sorted signed PDF files
    fun loadSignedSortedPdfFiles(isAsc: Int) {
        viewModelScope.launch {
            _signedSortedPdfFiles.value = repository.getSignedAscandingDescending(isAsc)
        }
    }

    // Load sorted ID card PDF files
    fun loadIdCardSortedPdfFiles(isAsc: Int) {
        viewModelScope.launch {
            _idCardSortedPdfFiles.value = repository.getIdCardAscandingDescending(isAsc)
        }
    }

    // Load sorted ID Marge PDF files
    fun loadIdMargeSortedPdfFiles(isAsc: Int) {
        viewModelScope.launch {
            _idMargeSortedPdfFiles.value = repository.getIdMargeAscandingDescending(isAsc)
        }

    }

    // Load sorted ID Split PDF files
    fun loadIdSplitSortedPdfFiles(isAsc: Int) {
        viewModelScope.launch {
            _idSplitSortedPdfFiles.value = repository.getIdSplitAscandingDescending(isAsc)
        }

    }

    // Insert a PDF file
    fun insertPdfFile(pdfFile: PdfFile) {
        viewModelScope.launch {
            repository.insert(pdfFile)
//            loadAllPdfFiles() // Refresh the list
        }
    }



    // Update a PDF file
    fun updatePdfFile(pdfFile: PdfFile) {
        viewModelScope.launch {
            repository.updatePdfFile(pdfFile)
//            loadAllPdfFiles() // Refresh the list

//            loadAllPdfFiles()
            loadIdCardPdfFiles()
            loadSignedPdfFiles()
//            loadFavoritePdfFiles()
            loadIdMargePdfFiles()
            loadIdSplitPdfFiles()
//            getRecentPdfFiles()


        }
    }

    // Rename a PDF file
    fun renamePdfFile(currentFileName: String, newFileName: String) {
        viewModelScope.launch {
            repository.renameFile(currentFileName, newFileName)
//            loadAllPdfFiles() // Refresh the list
        }
    }

    // Update specific fields in a PDF file by ID
    fun updateTest(id: Int, filePath: String, previousName: String, password: String, fileSize: Double, createdDate: Long, isFavorite: Boolean, isSignedFiles: Boolean) {
        viewModelScope.launch {
            repository.updateTest(id, filePath, previousName, password, fileSize, createdDate, isFavorite, isSignedFiles)
//            loadAllPdfFiles() // Refresh the list
        }
    }

    // Delete a PDF file
    fun deletePdfFile(pdfFile: PdfFile) {
        viewModelScope.launch {
            repository.deletePdfFile(pdfFile)
//            loadAllPdfFiles() // Refresh the list
        }
    }
}
