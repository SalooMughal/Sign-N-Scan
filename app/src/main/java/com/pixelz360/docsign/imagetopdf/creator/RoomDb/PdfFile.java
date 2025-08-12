package com.pixelz360.docsign.imagetopdf.creator.RoomDb;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "pdf_files")
public class PdfFile {
//    @PrimaryKey(autoGenerate = true)
//    public int id;
//    public String filePath;
//    public String fileName;
//    public String password; // To store the password
//    public String fileSize; // To store the File Size
//    public String createdDate; // To store the File Size
//    public boolean isFavorite; // To store the favorite status

    @PrimaryKey(autoGenerate = true)
    public int id;
    public String filePath;
    public String fileName;
    public String fileTag;
    public String fileTagBgColor;
    public String password;
    public double fileSize; // Store size in bytes or KB/MB directly
    public long createdDate; // Store timestamp directly
    public boolean isFavorite;
    public boolean isSignedFiles;
    public boolean isRecentFiles;
    public boolean  isIdCardFiles;
    public boolean  isMargeFiles;
    public boolean  isSplitFiles;
    public boolean  isComppressFiles;
    public boolean  isFileSavedInInternalStorage;
    public boolean  isWaterMarkFiles;
    public String  isAccountName;
    public boolean  isPdfToJpgImages;
    public String  isFileExtention;

    public PdfFile(String filePath, String fileName, String fileTag, String fileTagBgColor, String password, double fileSize, long createdDate, boolean isFavorite, boolean isSignedFiles, boolean isRecentFiles, boolean isIdCardFiles, boolean isMargeFiles, boolean isSplitFiles, boolean isComppressFiles, boolean isFileSavedInInternalStorage, boolean isWaterMarkFiles, String isAccountName,boolean isPdfToJpgImages,String isFileExtention) {
        this.filePath = filePath;
        this.fileName = fileName;
        this.fileTag = fileTag;
        this.fileTagBgColor = fileTagBgColor;
        this.password = password;
        this.fileSize = fileSize;
        this.createdDate = createdDate;
        this.isFavorite = isFavorite;
        this.isSignedFiles = isSignedFiles;
        this.isRecentFiles = isRecentFiles;
        this.isIdCardFiles = isIdCardFiles;
        this.isMargeFiles = isMargeFiles;
        this.isSplitFiles = isSplitFiles;
        this.isComppressFiles = isComppressFiles;
        this.isFileSavedInInternalStorage = isFileSavedInInternalStorage;
        this.isWaterMarkFiles = isWaterMarkFiles;
        this.isAccountName = isAccountName;
        this.isPdfToJpgImages = isPdfToJpgImages;
        this.isFileExtention = isFileExtention;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileTag() {
        return fileTag;
    }

    public void setFileTag(String fileTag) {
        this.fileTag = fileTag;
    }

    public String getFileTagBgColor() {
        return fileTagBgColor;
    }

    public void setFileTagBgColor(String fileTagBgColor) {
        this.fileTagBgColor = fileTagBgColor;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public double getFileSize() {
        return fileSize;
    }

    public void setFileSize(double fileSize) {
        this.fileSize = fileSize;
    }

    public long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public boolean isSignedFiles() {
        return isSignedFiles;
    }

    public void setSignedFiles(boolean signedFiles) {
        isSignedFiles = signedFiles;
    }

    public boolean isRecentFiles() {
        return isRecentFiles;
    }

    public void setRecentFiles(boolean recentFiles) {
        isRecentFiles = recentFiles;
    }

    public boolean isIdCardFiles() {
        return isIdCardFiles;
    }

    public void setIdCardFiles(boolean idCardFiles) {
        isIdCardFiles = idCardFiles;
    }

    public boolean isMargeFiles() {
        return isMargeFiles;
    }

    public void setMargeFiles(boolean margeFiles) {
        isMargeFiles = margeFiles;
    }

    public boolean isSplitFiles() {
        return isSplitFiles;
    }

    public void setSplitFiles(boolean splitFiles) {
        isSplitFiles = splitFiles;
    }

    public boolean isComppressFiles() {
        return isComppressFiles;
    }

    public void setComppressFiles(boolean comppressFiles) {
        isComppressFiles = comppressFiles;
    }

    public boolean isFileSavedInInternalStorage() {
        return isFileSavedInInternalStorage;
    }

    public void setFileSavedInInternalStorage(boolean fileSavedInInternalStorage) {
        isFileSavedInInternalStorage = fileSavedInInternalStorage;
    }

    public boolean isWaterMarkFiles() {
        return isWaterMarkFiles;
    }

    public void setWaterMarkFiles(boolean waterMarkFiles) {
        isWaterMarkFiles = waterMarkFiles;
    }

    public String isAccountOrGuesSideFiles() {
        return isAccountName;
    }

    public void setAccountOrGuesSideFiles(String isAccountName) {
        isAccountName = isAccountName;
    }


    public String getIsAccountName() {
        return isAccountName;
    }

    public void setIsAccountName(String isAccountName) {
        this.isAccountName = isAccountName;
    }

    public boolean isPdfToJpgImages() {
        return isPdfToJpgImages;
    }

    public void setPdfToJpgImages(boolean pdfToJpgImages) {
        isPdfToJpgImages = pdfToJpgImages;
    }


    public String getIsFileExtention() {
        return isFileExtention;
    }

    public void setIsFileExtention(String isFileExtention) {
        this.isFileExtention = isFileExtention;
    }
}

