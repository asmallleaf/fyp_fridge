from flask import request, jsonify
from app.verify.userVerify import UserVerifyFactory
from app.database.models import db, users, fridges, fridge_inf
from app.toolbox.dbtool import DBTool
from app.route.usrapi.login import usrapi
from app.toolbox.iotool import IOtool
from app.route.supportTool import SupportTool

# it is the api for signin
# it will return successful state if succeeded
@usrapi.route('/signin',methods=['POST','GET'])
def signin():
    # get arguments from request
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
    # build implement to verify level
    verify = UserVerifyFactory().build()
    # verify arguments
    verify.setName(user_name).setCode(fridge_code).setPasswd(user_passwd).setPasswd2(user_passwd2)
    verify.verifySignIn()
    # return error if failed
    error_index = verify.getError()
    if verify.ifError():
        return jsonify(error_index),404
    # inquire the hash encoded password stored in database
    raws = db.session.query(users).filter(users.fridge_code==fridge_code).all()
    if not raws:
        # hash encoding the password
        user_passwd = IOtool.toHash(user_passwd2)
        # build the users table in database
        raw = users(user_name=user_name,user_passwd=user_passwd,fridge_code=fridge_code)
        DBTool.insert(raw=raw,db=db)
        # generate a item list number
        list_code = SupportTool.generate_list_code()
        # build the fridges table in database
        raw = fridges(fridge_code=fridge_code,item_list_code=list_code,inf_show=False)
        DBTool.insert(raw=raw,db=db)
        # build the fridge_inf table in database
        raw = fridge_inf(fridge_code=fridge_code,temperature=0.0,is_show_inf=False)
        DBTool.insert(raw=raw,db=db)
        return jsonify({'state':'success','success':'signin completed','listCode':list_code}),201
    else:
        return jsonify({'state':'error','error':'repeated name or fridge code'}),404