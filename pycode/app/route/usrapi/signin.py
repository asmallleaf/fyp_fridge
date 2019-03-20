from flask import request, jsonify
from app.verify.userVerify import UserVerifyFactory
from app.database.models import db, users, fridges, fridge_inf
from app.toolbox.dbtool import DBTool
from app.route.usrapi.login import usrapi
from app.toolbox.iotool import IOtool
from app.route.supportTool import SupportTool

@usrapi.route('/signin',methods=['POST','GET'])
def signin():
    user_name = None
    user_passwd = None
    user_passwd2 = None
    fridge_code = None
    if request.method == 'POST':
        user_name = request.form.get('userName')
        user_passwd = request.form.get('password')
        user_passwd2 = request.form.get('password2')
        fridge_code = request.form.get('fridgeNum')
    elif request.method == 'GET':
        user_name = request.args.get('userName')
        user_passwd = request.args.get('password')
        user_passwd2 = request.args.get('password2')
        fridge_code = request.args.get('fridgeNum')

    verify = UserVerifyFactory().build()
    verify.setName(user_name).setCode(fridge_code).setPasswd(user_passwd).setPasswd2(user_passwd2)
    verify.verifySignIn()
    error_index = verify.getError()
    if verify.ifError():
        return jsonify(error_index),404
    raws = db.session.query(users).filter(users.user_name==user_name).all()
    if not raws:
        user_passwd = IOtool.toHash(user_passwd2)
        raw = users(user_name=user_name,user_passwd=user_passwd,fridge_code=fridge_code)
        DBTool.insert(raw=raw,db=db)
        list_code = SupportTool.generate_list_code()
        raw = fridges(fridge_code=fridge_code,item_list_code=list_code,inf_show=False)
        DBTool.insert(raw=raw,db=db)
        raw = fridge_inf(fridge_code=fridge_code,temperature=0.0,is_show_inf=False)
        DBTool.insert(raw=raw,db=db)
        return jsonify({'state':'success','success':'signin completed','listCode':list_code}),201
    else:
        return jsonify({'state':'error','error':'repeated name or fridge code'}),404