import string
from .basetool import BaseTool
import random

class DBTool(BaseTool):
    def _help(self):
        print('this is tool of database,include insert, drop\n')

    def _helpfor(self,fnc_name):
        if fnc_name == 'insert' or fnc_name == 'drop':
            print('to use them, a raw in the database and db need to be provided\n')
            print('like insert(raw,db)\n')

    @classmethod
    def insert(cls,raw,db):
        db.session.add(raw)
        db.session.commit()

    @classmethod
    def drop(cls,raw,db):
        db.session.delete(raw)
        db.session.commit()

    @classmethod
    def generate_token(cls,table,db,size):
        min = int(pow(10.0,size-1))
        max = int(pow(10.0,size))
        newtoken = random.randint(min,max-1)
        return newtoken

    @classmethod
    def generate_randnum(cls,position):
        temp = ''.join(random.sample(string.digits,position))
        return temp

    @classmethod
    def string2Bool(cls,str):
        return (str == 'True' or str == 'TRUE' or str == 'true')
