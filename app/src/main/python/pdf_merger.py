from PyPDF2 import PdfReader, PdfWriter
import os

def merge_pdfs(pdf_paths, output_path):
    writer = PdfWriter()

    # Add each PDF file to the writer
    for pdf_path in pdf_paths:
        reader = PdfReader(pdf_path)
        for page in reader.pages:
            writer.add_page(page)

    # Write the merged PDF to the output file
    with open(output_path, "wb") as output_pdf:
        writer.write(output_pdf)

    return output_path  # Return the path of the merged PDF file
