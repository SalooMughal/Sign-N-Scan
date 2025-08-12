from PyPDF2 import PdfReader, PdfWriter

def rotate_pdf(input_path, output_path, rotation_angle):
    reader = PdfReader(input_path)
    writer = PdfWriter()

    # Rotate each page by the specified angle using the new rotate method
    for page in reader.pages:
        page.rotate(rotation_angle)  # Replace rotate_clockwise with rotate
        writer.add_page(page)

    # Save the rotated PDF
    with open(output_path, "wb") as output_pdf:
        writer.write(output_pdf)

    return output_path  # Return the path of the rotated PDF
