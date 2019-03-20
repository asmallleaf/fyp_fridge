from sqlalchemy import Column,String,Integer,ForeignKey,TIMESTAMP,Boolean,Float
from flask_sqlalchemy import SQLAlchemy

db = SQLAlchemy()

class users(db.Model):
    id = Column(Integer,autoincrement=True,primary_key=True,nullable=False)
    user_name = Column(String(20),nullable=False)
    user_passwd = Column(String(255),nullable=False)
    fridge_code = Column(String(8),nullable=False,unique=True)
    token = Column(TIMESTAMP)


class fridges(db.Model):
    id = Column(Integer,autoincrement=True,primary_key=True,nullable=False)
    fridge_code = Column(String(8),ForeignKey('users.fridge_code'),nullable=False)
    item_list_code = Column(String(8),nullable=False,unique=True)
    inf_show = Column(Boolean,nullable=False)


class items(db.Model):
    id = Column(Integer, autoincrement=True, primary_key=True, nullable=False)
    item_list_code = Column(String(8),ForeignKey('fridges.item_list_code'),nullable=False)
    item_name = Column(String(20),default='other')
    item_num = Column(Integer,default=1)
    storage_time = Column(TIMESTAMP)
    tab = Column(String(20),default='unknown')


class fridge_inf(db.Model):
    id = Column(Integer, autoincrement=True, primary_key=True, nullable=False)
    fridge_code = Column(String(8),ForeignKey('users.fridge_code'),nullable=False,unique=True)
    is_show_inf = Column(Boolean,nullable=False)
    temperature = Column(Float,default=0.0)

