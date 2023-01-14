from pytesseract import pytesseract
from PIL import Image
import argparse

parser = argparse.ArgumentParser(description="Get the text from an image")
parser.add_argument('path', type=str, help='Filepath')

args = parser.parse_args()

tesseract_path = r"C:\Program Files\Tesseract-OCR\tesseract.exe"

image = args.path

pytesseract.tesseract_cmd = tesseract_path

img = Image.open(image)

text = pytesseract.image_to_string(img)

print(text)