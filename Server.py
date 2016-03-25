import os, glob, json, re
from flask import Flask, url_for, request, jsonify, Response
from Book import Book
from os import listdir
from bs4 import BeautifulSoup
from urllib2 import urlopen
#from flask.ext.login import LoginManager, current_user, login_user, logout_user

app = Flask(__name__)
app.debug = True
URLHeader =  "http://12fa1cc9.ngrok.io"

#login_manager = LoginManager()
#login_manager.init_app(app)

#Return files under ChallengeBooks/ direction
booksList = os.listdir("static/ChallengeBooks/")
booksImageList = os.listdir("static/BooksImages/")

@app.route('/books')
def getBooksJson():
    jsonarr = []

    for index in range(len(booksList)):
        #Filter html file under the director
        if booksList[index].endswith(".html"):

            book = BeautifulSoup(open('static/ChallengeBooks/'+booksList[index]))
            bookName = book.title.string
            authorName = book.h4.find_next(re.compile('^h')).string
            bookURL = URLHeader+url_for('static', filename='ChallengeBooks/')+booksList[index]
            bookImageURL = URLHeader+url_for('static', filename='BooksImages/')+booksImageList[index]

            i = Book(index, bookName, authorName, bookURL, bookImageURL)

            jsonarr.append({'bookId':i.getBookId(), 'bookName':i.getBookName(), 'authorName':i.getAuthorName(),
                'bookURL':i.getBookURL(), 'bookImageURL':i.getBookImageURL()})


    #Turn jsonarr into json object
    return json.dumps(jsonarr)

@app.route('/login', methods = ['POST'])
def login():

    username = request.form['username']
    password = request.form['password']

    if username != 'jess' or password != '123':
        abort(403)
    else:
        return Response(status=200, \
                        mimetype="application/json")

if __name__ == "__main__":
    app.run (host = 'localhost', port = 8080)