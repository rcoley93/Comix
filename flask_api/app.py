#!flask/bin/python
from flask import Flask, request,render_template
from werkzeug import secure_filename
import pdfkit
import time

app = Flask(__name__)

@app.route('/')
def index():
    return "Hello, World!"

@app.route('/rest_test')
def rest_test():
    return render_template('rest_test.html')

@app.route('/download/app')
def download_app():
	return send_file("app.py")

@app.route('/download/test')
def download_test():
	return send_file("rest_test.html")
	
@app.route('/download/collection')
def download_collection():
	return send_file("collection.html")

@app.route('/comix/api/v1.0/pdf', methods=['POST'])
def convert_html_pdf():
    htmlFile = request.files['html_file']
    htmlFilename = secure_filename(str(time.time())+"_html_"+htmlFile.filename)
    htmlFile.save(htmlFilename)
    pdfFilename = secure_filename(str(time.time())+"_pdf_.pdf")
    pdfkit.from_file(htmlFilename,pdfFilename)
    print htmlFilename + "\n"
    print pdfFilename + "\n"
    return send_file(pdfFilename,attachment_filename="collection.pdf")

if __name__ == '__main__':
    app.run(debug=True)
