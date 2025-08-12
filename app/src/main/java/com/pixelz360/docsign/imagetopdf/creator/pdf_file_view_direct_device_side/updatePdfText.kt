package com.pixelz360.docsign.imagetopdf.creator.pdf_file_view_direct_device_side

import android.net.Uri
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.canvas.PdfCanvas
import com.itextpdf.kernel.geom.Rectangle
import com.itextpdf.kernel.font.PdfFontFactory
import java.io.File

fun updatePdfText(
    context: PdfEditActivity,
    uri: Uri,
    updatedTextData: List<PdfTextData>,
    outputFilePath: String
) {
    val inputFile = File(getFilePathFromUri(context,uri))
    val pdfReader = PdfReader(inputFile)
    val pdfWriter = PdfWriter(outputFilePath)
    val pdfDocument = PdfDocument(pdfReader, pdfWriter)

    for (textData in updatedTextData) {
        val page = pdfDocument.getPage(1) // Use the correct page number
        val pdfCanvas = PdfCanvas(page)

        // Clear existing text area
        val rect = Rectangle(textData.x, textData.y - 5, 200f, 15f) // Adjust rectangle as needed
        pdfCanvas.saveState()
        pdfCanvas.rectangle(rect)
        pdfCanvas.fill()
        pdfCanvas.restoreState()

        // Write updated text
        pdfCanvas.beginText()
        val font = PdfFontFactory.createFont()
        pdfCanvas.setFontAndSize(font, textData.fontSize)
        pdfCanvas.moveText(textData.x.toDouble(), textData.y.toDouble())
        pdfCanvas.showText(textData.text)
        pdfCanvas.endText()
    }

    pdfDocument.close()
}


