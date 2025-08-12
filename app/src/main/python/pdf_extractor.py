from PyPDF2 import PdfReader

def extract_text_from_pdf(pdf_path):
    reader = PdfReader(pdf_path)
    extracted_text = ""

    # Loop through each page in the PDF
    for page in reader.pages:
        extracted_text += page.extract_text() + "\n"

    return extracted_text  # Return the extracted text
