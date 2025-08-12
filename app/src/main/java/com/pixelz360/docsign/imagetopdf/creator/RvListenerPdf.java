package com.pixelz360.docsign.imagetopdf.creator;


import com.pixelz360.docsign.imagetopdf.creator.adapters.AdapterPdfDeviceFolder;
import com.pixelz360.docsign.imagetopdf.creator.models.ModelPdf;

public interface RvListenerPdf {

    void onPdfClick(ModelPdf modelPdf, int position);

    void onItemSelectionChanged();
    void onItemSelectionChangedCount(int selectionCount);

    void onPdfMoreClick(ModelPdf modelPdf, int position, AdapterPdfDeviceFolder.HolderPdf holder);
}
