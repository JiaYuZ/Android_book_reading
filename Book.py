class Book():

   def __init__(self,  bookId, bookName, authorName, bookURL, bookImageURL):
      self.bookURL = bookURL
      self.bookName = bookName
      self.authorName = authorName
      self.bookImageURL = bookImageURL

   def getBookId(self):
      return self.bookId

   def setBookId(newBookId):
      self.bookId = newBookId

   def getBookName(self):
      return self.bookName

   def setBookName(newBookName):
      self.bookName = newBookName

   def getBookURL(self):
      return self.bookURL

   def setBookURL(newBookURL):
      self.bookURL = newBookURL

   def getAuthorName(self):
      return self.authorName

   def setAuthorName(newAuthorName):
      self.authorName = newAuthorName

   def getBookImageURL(self):
      return self.bookImageURL

   def setBookImageURL(newBookImageURL):
      self.bookImageURL = newBookImageURL

   def as_json(self):
      return dict(
         bookId = self.bookId,
         bookName = self.bookName,
         bookURL = self.bookURL,
         authorName = self.authorName,
         bookImageURL = self.bookImageURL )
