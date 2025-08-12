from PyPDF2 import PdfReader, PdfWriter
from reportlab.pdfgen import canvas
from reportlab.lib.pagesizes import letter
from reportlab.lib.colors import HexColor
from reportlab.pdfbase.ttfonts import TTFont
from reportlab.pdfbase import pdfmetrics
from io import BytesIO
import os

def add_text_watermark(input_pdf_path, output_pdf_path, watermark_text, font_size, transparency, rotation,
                       vertical_position, horizontal_position, color, font_bytes, start_page, end_page):
    packet = BytesIO()
    can = canvas.Canvas(packet, pagesize=letter)

    font_name = "custom_font"
    font_stream = BytesIO(font_bytes)
    pdfmetrics.registerFont(TTFont(font_name, font_stream))
    can.setFont(font_name, font_size)
    can.setFillColor(HexColor(color))
    can.setFillAlpha(transparency / 100)

    width, height = letter
    x, y = get_position(horizontal_position, vertical_position, width, height, rotation)

    can.drawString(x, y, watermark_text)
    can.rotate(rotation)
    can.save()

    packet.seek(0)
    watermark_pdf = PdfReader(packet)

    pdf_reader = PdfReader(input_pdf_path)
    pdf_writer = PdfWriter()

    total_pages = len(pdf_reader.pages)
    end_page = end_page if end_page != -1 else total_pages
    start_page = max(1, min(start_page, total_pages)) - 1
    end_page = max(start_page, min(end_page, total_pages)) - 1

    for i in range(total_pages):
        page = pdf_reader.pages[i]
        if start_page <= i <= end_page:
            page.merge_page(watermark_pdf.pages[0])
        pdf_writer.add_page(page)

    with open(output_pdf_path, "wb") as output_pdf:
        pdf_writer.write(output_pdf)

    return output_pdf_path

def add_image_watermark(input_pdf_path, output_pdf_path, image_path, transparency, rotation,
                        vertical_position, horizontal_position, start_page, end_page):
    packet = BytesIO()
    can = canvas.Canvas(packet, pagesize=letter)

    can.setFillAlpha(transparency / 100)
    width, height = letter
    x, y = get_position(horizontal_position, vertical_position, width, height, rotation)

    can.drawImage(image_path, x, y, mask='auto')
    can.rotate(rotation)
    can.save()

    packet.seek(0)
    watermark_pdf = PdfReader(packet)

    pdf_reader = PdfReader(input_pdf_path)
    pdf_writer = PdfWriter()

    total_pages = len(pdf_reader.pages)
    end_page = end_page if end_page != -1 else total_pages
    start_page = max(1, min(start_page, total_pages)) - 1
    end_page = max(start_page, min(end_page, total_pages)) - 1

    for i in range(total_pages):
        page = pdf_reader.pages[i]
        if start_page <= i <= end_page:
            page.merge_page(watermark_pdf.pages[0])
        pdf_writer.add_page(page)

    with open(output_pdf_path, "wb") as output_pdf:
        pdf_writer.write(output_pdf)

    return output_pdf_path

def get_position(horizontal_position, vertical_position, width, height, rotation):
    x = width / 2 if horizontal_position == "center" else 0 if horizontal_position == "left" else width
    y = height / 2 if vertical_position == "middle" else height if vertical_position == "top" else 0

    if rotation in [90, 270]:
        x, y = y, x
    return x, y

























# from PyPDF2 import PdfReader, PdfWriter
# from reportlab.pdfgen import canvas
# from reportlab.lib.pagesizes import letter
# from reportlab.lib.colors import HexColor
# from reportlab.pdfbase.ttfonts import TTFont
# from reportlab.pdfbase import pdfmetrics
# from io import BytesIO
# import os
#
# def add_watermark(input_pdf_path, output_pdf_path, watermark_text, font_size, transparency, rotation,
#                   vertical_position, horizontal_position, color, font_bytes, start_page, end_page):
#     # Prepare the watermark canvas
#     packet = BytesIO()
#     can = canvas.Canvas(packet, pagesize=letter)
#
#     # Register font from byte data
#     font_name = "custom_font"
#     font_stream = BytesIO(font_bytes)
#     pdfmetrics.registerFont(TTFont(font_name, font_stream))
#     can.setFont(font_name, font_size)
#
#     # Set color and transparency
#     can.setFillColor(HexColor(color))
#     can.setFillAlpha(transparency / 100)  # Apply transparency
#
#     # Calculate position based on alignment
#     width, height = letter
#     x, y = get_position(horizontal_position, vertical_position, width, height, rotation)
#
#     # Draw the watermark text
#     can.drawString(x, y, watermark_text)
#     can.rotate(rotation)
#     can.save()
#
#     # Read watermark as a PDF
#     packet.seek(0)
#     watermark_pdf = PdfReader(packet)
#
#     # Open the original PDF
#     pdf_reader = PdfReader(input_pdf_path)
#     pdf_writer = PdfWriter()
#
#     # Define pages to watermark
#     total_pages = len(pdf_reader.pages)
#     end_page = end_page if end_page != -1 else total_pages  # Default to last page if -1
#     start_page = max(1, min(start_page, total_pages)) - 1  # Zero-indexed
#     end_page = max(start_page, min(end_page, total_pages)) - 1  # Zero-indexed
#
#     # Apply watermark to each specified page
#     for i in range(total_pages):
#         page = pdf_reader.pages[i]
#         if start_page <= i <= end_page:
#             page.merge_page(watermark_pdf.pages[0])
#         pdf_writer.add_page(page)
#
#     # Write out the new PDF with watermark
#     with open(output_pdf_path, "wb") as output_pdf:
#         pdf_writer.write(output_pdf)
#
#     return output_pdf_path
#
# def get_position(horizontal_position, vertical_position, width, height, rotation):
#     """Calculate (x, y) position for watermark based on alignment."""
#     x = width / 2 if horizontal_position == "center" else 0 if horizontal_position == "left" else width
#     y = height / 2 if vertical_position == "middle" else height if vertical_position == "top" else 0
#
#     if rotation in [90, 270]:
#         x, y = y, x  # Adjust for rotated text
#     return x, y
