package com.pixelz360.docsign.imagetopdf.creator.RoomDb;


public interface RvListenerPdfRoom {

    void onPdfClick(PdfFile pdfFile, int position);

    void onItemSelectionChanged();
    void onItemSelectionChangedCount(int selectionCount);

    void onPdfMoreClick(PdfFile pdfFile, int position);

//    void onPdfMoreClick(PdfFile pdfFile, int position, FavoritePdfAdapterRoom.ViewHolder holder);
//    void onPdfMoreClick(PdfFile pdfFile, int position, SignedPdfAdapterRoom.ViewHolder holder);
//    void onPdfMoreClick(PdfFile pdfFile, int position, ScannerAdapterRoom.ViewHolder holder);
//    void onPdfMoreClick(PdfFile pdfFile, int position, MargePdfAdapterRoom.ViewHolder holder);
//    void onPdfMoreClick(PdfFile pdfFile, int position, SplitPdfAdapterRoom.ViewHolder holder);


    void onFavoriteClick(PdfFile pdfFile);
}
