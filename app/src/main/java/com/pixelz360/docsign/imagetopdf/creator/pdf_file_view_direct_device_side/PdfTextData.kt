package com.pixelz360.docsign.imagetopdf.creator.pdf_file_view_direct_device_side

import android.net.Uri
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor
import com.itextpdf.kernel.pdf.canvas.parser.listener.LocationTextExtractionStrategy
//import com.itextpdf.kernel.pdf.canvas.parser.TextRenderInfo
import com.itextpdf.kernel.pdf.canvas.parser.data.TextRenderInfo
import java.io.File

data class PdfTextData(
    val text: String,
    val x: Float,
    val y: Float,
    val fontName: String,
    val fontSize: Float
)

fun extractPdfTextWithFormatting(context: PdfEditActivity, uri: Uri): List<PdfTextData> {
    val pdfFile = File(getFilePathFromUri(context,uri)) // Ensure getFilePathFromUri is correctly implemented
    val pdfReader = PdfReader(pdfFile)
    val pdfDocument = PdfDocument(pdfReader)

    val textDataList = mutableListOf<PdfTextData>()

    val strategy = object : LocationTextExtractionStrategy() {
        fun renderText(renderInfo: TextRenderInfo) {
            val text = renderInfo.text
            if (!text.isNullOrEmpty()) {
                val position = renderInfo.baseline.startPoint
                val fontName = renderInfo.font?.fontProgram?.fontNames?.fullName ?: "Unknown"
                val fontSize = renderInfo.fontSize

                textDataList.add(
                    PdfTextData(
                        text = text,
                        x = position[0],
                        y = position[1],
                        fontName = fontName.toString(),
                        fontSize = fontSize
                    )
                )
            }
        }
    }

    for (i in 1..pdfDocument.numberOfPages) {
        val processor = PdfCanvasProcessor(strategy)
        processor.processPageContent(pdfDocument.getPage(i))
    }

    pdfDocument.close()
    return textDataList
}

