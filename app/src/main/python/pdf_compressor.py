from PyPDF2 import PdfReader, PdfWriter
from PIL import Image
import io

def compress_pdf(input_path, output_path, compression_level="extreme"):
    # Define even more aggressive compression settings
    compression_settings = {
        "extreme": {"quality": 5, "dpi": (30, 30)},    # Very aggressive, lowest quality
        "high": {"quality": 10, "dpi": (50, 50)},      # High compression, low quality
        "medium": {"quality": 30, "dpi": (72, 72)},    # Medium compression, moderate quality
        "low": {"quality": 50, "dpi": (100, 100)}      # Lower compression, higher quality
    }

    settings = compression_settings.get(compression_level, {"quality": 30, "dpi": (72, 72)})
    quality = settings["quality"]
    dpi = settings["dpi"]

    # Open the PDF
    reader = PdfReader(input_path)
    writer = PdfWriter()

    for page_num, page in enumerate(reader.pages):
        writer.add_page(page)
        print(f"Processing page {page_num + 1}")

        try:
            # Access images in the page
            xobject = page["/Resources"]["/XObject"].get_object()
            for obj_name in xobject:
                xobj = xobject[obj_name]
                if xobj["/Subtype"] == "/Image":
                    image_data = xobj._data

                    # Open the image with Pillow
                    image = Image.open(io.BytesIO(image_data))

                    # Aggressive resizing for more reduction
                    new_width = max(1, int(image.width * 0.3))
                    new_height = max(1, int(image.height * 0.3))
                    image = image.resize((new_width, new_height), Image.ANTIALIAS)

                    # Convert and compress to JPEG
                    output_io = io.BytesIO()
                    image.convert("RGB").save(output_io, format="JPEG", quality=quality)

                    # Replace the original image with the compressed one
                    xobj._data = output_io.getvalue()
                    print(f"Compressed image to {(new_width, new_height)} with quality {quality}")

        except KeyError:
            print("No images found on this page or unsupported content")

    # Write compressed PDF
    with open(output_path, "wb") as out_file:
        writer.write(out_file)

    print(f"PDF saved to {output_path}")
    return output_path
































# from PyPDF2 import PdfReader, PdfWriter
# from PIL import Image
# import io
#
# def compress_pdf(input_path, output_path, compression_level="medium"):
#     reader = PdfReader(input_path)
#     writer = PdfWriter()
#
#     # Define image quality and DPI based on compression level
#     compression_settings = {
#         "high": {"quality": 30, "dpi": (72, 72)},    # Lowest quality, highest compression
#         "medium": {"quality": 50, "dpi": (100, 100)},  # Medium quality, moderate compression
#         "low": {"quality": 70, "dpi": (150, 150)}      # Highest quality, lowest compression
#     }
#
#     # Get the settings for the selected compression level
#     settings = compression_settings.get(compression_level, {"quality": 50, "dpi": (100, 100)})
#     print(f"Selected compression level: {compression_level}")
#     print(f"Quality setting: {settings['quality']}")
#     print(f"DPI setting: {settings['dpi']}")
#
#     for page_number, page in enumerate(reader.pages):
#         writer.add_page(page)
#         print(f"\nProcessing page {page_number + 1}")
#
#         # Access image objects in the page if available
#         try:
#             xobject = page["/Resources"]["/XObject"].get_object()
#             for obj_name in xobject:
#                 xobj = xobject[obj_name]
#                 if xobj["/Subtype"] == "/Image":
#                     # Retrieve the original image data
#                     image_data = xobj._data
#                     image = Image.open(io.BytesIO(image_data))
#
#                     # Log the original image size
#                     print(f"Original image size: {image.size}")
#
#                     # Resize image based on selected DPI
#                     image = image.resize(settings["dpi"], Image.ANTIALIAS)
#
#                     # Compress and save image as JPEG
#                     output_io = io.BytesIO()
#                     image.save(output_io, format="JPEG", quality=settings["quality"])
#                     xobj._data = output_io.getvalue()
#
#                     # Log the compressed image size
#                     print(f"Compressed image size: {settings['dpi']} with quality {settings['quality']}")
#         except KeyError:
#             # Skip pages with no images or unsupported content
#             print("No images found on this page or unsupported content")
#
#     # Write the compressed PDF to the output file
#     with open(output_path, "wb") as out_file:
#         writer.write(out_file)
#
#     print(f"PDF saved to {output_path}")
#     return output_path









































# from PyPDF2 import PdfReader, PdfWriter
# from PIL import Image
# import io
#
# def compress_pdf(input_path, output_path, compression_level="medium"):
#     reader = PdfReader(input_path)
#     writer = PdfWriter()
#
#
#     # Define image quality based on compression level
#     quality_map = {
#         "high": 30,   # Lowest quality, highest compression
#         "medium": 60,
#         "low": 85     # Highest quality, lowest compression
#     }
#     quality = quality_map.get(compression_level, 60)
#
#     for page in reader.pages:
#         writer.add_page(page)
#
#         # Try to access image objects in the page
#         try:
#             xobject = page["/Resources"]["/XObject"]
#             for obj_name in xobject:
#                 xobj = xobject[obj_name]
#                 if xobj["/Subtype"] == "/Image":
#                     image_data = xobj.get_data()
#
#                     # Process and compress the image data
#                     image = Image.open(io.BytesIO(image_data))
#                     output_io = io.BytesIO()
#                     image.save(output_io, format="JPEG", quality=quality)
#                     xobj._data = output_io.getvalue()
#         except KeyError:
#             # Handle cases where the page has no images or resources
#             pass
#
#     # Write the compressed PDF to the output file
#     with open(output_path, "wb") as out_file:
#         writer.write(out_file)
#
#     return output_path
