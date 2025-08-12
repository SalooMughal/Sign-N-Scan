from PyPDF2 import PdfReader, PdfWriter

def add_metadata(input_path, output_path, title=None, author=None, subject=None):
    reader = PdfReader(input_path)
    writer = PdfWriter()

    # Copy all pages to the writer
    for page in reader.pages:
        writer.add_page(page)

    # Add metadata
    metadata = {}
    if title:
        metadata['/Title'] = title
    if author:
        metadata['/Author'] = author
    if subject:
        metadata['/Subject'] = subject

    writer.add_metadata(metadata)

    # Save the modified PDF
    with open(output_path, "wb") as output_pdf:
        writer.write(output_pdf)

    return output_path  # Return the path of the PDF with added metadata
