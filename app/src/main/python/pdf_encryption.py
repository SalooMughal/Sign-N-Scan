from PyPDF2 import PdfReader, PdfWriter

def encrypt_pdf(input_path, output_path, password):
    reader = PdfReader(input_path)
    writer = PdfWriter()

    # Add pages to the writer
    for page in reader.pages:
        writer.add_page(page)

    # Encrypt the PDF with the given password
    writer.encrypt(password)

    # Write the encrypted PDF to the output file
    with open(output_path, "wb") as output_pdf:
        writer.write(output_pdf)

    return output_path  # Return the path of the encrypted PDF

def decrypt_pdf(input_path, output_path, password):
    reader = PdfReader(input_path)

    # Check if the PDF is encrypted
    if reader.is_encrypted:
        # Try to decrypt with the given password
        reader.decrypt(password)

    writer = PdfWriter()

    # Add pages to the writer
    for page in reader.pages:
        writer.add_page(page)

    # Write the decrypted PDF to the output file
    with open(output_path, "wb") as output_pdf:
        writer.write(output_pdf)

    return output_path  # Return the path of the decrypted PDF
