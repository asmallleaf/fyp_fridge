from .basetool import BaseTool
from json import JSONEncoder
from sqlalchemy import TIMESTAMP
from passlib.apps import custom_app_context as pwd_context
import datetime

class IOtool(BaseTool):
    def _help(self):
        print('this is a toolbox for IO problem\n')
        print('Till now, has fnc toJson, toHash,class JsonEncoder\n')

    def _helpfor(self,fnc_name):
        if fnc_name == 'toJson':
            print('this is developed for sqlalchemy, the object should be \n')
            print('model class from sqlalchemy\n')
        elif fnc_name == 'JsonEncoder':
            print('this is a class inherit from JSONEncoder, please inherit it if needed \n')
            print('has defined the encoder way of datetime class\n')
        elif fnc_name == 'toHash':
            print('this is a function to translate passwd to Hash\n')

    @classmethod
    def toJson(cls,object):
        dict = object.__dict__
        if '_sa_instance_state' in dict:
            del dict['_sa_instance_state']
        return dict

    @classmethod
    def toHash(cls,passwd):
        return pwd_context.encrypt(passwd)



class JsonEncoder(JSONEncoder):
    def default(self, obj):
        if isinstance(obj, datetime.datetime):
            return obj.strftime('%Y-%m-%d %H:%M:%S')
        elif isinstance(obj, datetime.date):
            return obj.strftime('%Y-%m-%d')
        else:
            return JSONEncoder.default(self, obj)

