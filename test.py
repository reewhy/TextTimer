from pytesseract import pytesseract
from PIL import Image
import os

tesseract_path = r"C:\Program Files\Tesseract-OCR\tesseract.exe"

image = "file.png"

pytesseract.tesseract_cmd = tesseract_path

img = Image.open(image)

text = pytesseract.image_to_string(img)

print(text)

os.remove(image)