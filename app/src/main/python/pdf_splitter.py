from PyPDF2 import PdfReader, PdfWriter
import os

def split_by_range(input_pdf_path, output_folder_path, start_page, end_page):
    reader = PdfReader(input_pdf_path)
    writer = PdfWriter()

    for i in range(start_page - 1, end_page):
        writer.add_page(reader.pages[i])

    output_path = os.path.join(output_folder_path, f"split_range_{start_page}_to_{end_page}.pdf")
    with open(output_path, "wb") as output_pdf:
        writer.write(output_pdf)

    return [output_path]

def split_into_equal_ranges(input_pdf_path, output_folder_path, pages_per_split):
    reader = PdfReader(input_pdf_path)
    output_paths = []

    for i in range(0, len(reader.pages), pages_per_split):
        writer = PdfWriter()
        for j in range(i, min(i + pages_per_split, len(reader.pages))):
            writer.add_page(reader.pages[j])

        output_path = os.path.join(output_folder_path, f"split_part_{i // pages_per_split + 1}.pdf")
        with open(output_path, "wb") as output_pdf:
            writer.write(output_pdf)

        output_paths.append(output_path)

    return output_paths

def delete_pages_by_range(input_pdf_path, output_folder_path, start_page, end_page):
    reader = PdfReader(input_pdf_path)
    writer = PdfWriter()

    for i, page in enumerate(reader.pages):
        if not (start_page - 1 <= i <= end_page - 1):
            writer.add_page(page)

    output_path = os.path.join(output_folder_path, f"deleted_range_{start_page}_to_{end_page}.pdf")
    with open(output_path, "wb") as output_pdf:
        writer.write(output_pdf)

    return [output_path]

def extract_all_pages(input_pdf_path, output_folder_path):
    reader = PdfReader(input_pdf_path)
    output_paths = []

    for i, page in enumerate(reader.pages):
        writer = PdfWriter()
        writer.add_page(page)

        output_path = os.path.join(output_folder_path, f"page_{i + 1}.pdf")
        with open(output_path, "wb") as output_pdf:
            writer.write(output_pdf)

        output_paths.append(output_path)

    return output_paths
