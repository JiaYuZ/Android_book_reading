from flask.ext.login import UserMixin

class UserNotFoundError(Exception):
    pass

class User(UserMixin):

    user = { "Jess" : "123" }

    def __init__(self, id, password):
        self.id = id
        self.password = password

    @classmethod
    def get(self_class,id):
        try:
            return self_class(id)
        except UserNotFoundError:
            return None