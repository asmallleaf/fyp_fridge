import random
import string
from app.database.models import db,fridges

from app.toolbox.verifytool import VerifyError

class tempclass():
    msg = False

    def show(self):
        print('hello\n')
        if self.msg:
            return 1

temp = []
ss = []
tempClass = tempclass()
index= {}
temp.append(tempClass.show())
print(temp)

temp = ''.join(random.sample(string.digits,8))
print(temp)

temp = db.session.query(fridges).filter(fridges.fridge_code=='122').all()
print(temp)