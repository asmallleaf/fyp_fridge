from app.route.usrapi.blueprint import usrapi
from flask import request,jsonify
from app.verify.userVerify import UserVerifyFactory
from app.database.models import db,users
from app.toolbox.verifytool import VerifyTool
from secret.config import Dev_config

@usrapi.route('/login',methods=['POST','GET'])
def login():
    user_name = None
    passwd = None
    token =None
    if request.method == 'POST':
        user_name = request.form.get('userName')
        passwd = request.form.get('password')
        token = request.form.get('token')
    elif request.method == 'GET':
        user_name = request.args.get('userName')
        passwd = request.args.get('password')
        token = request.args.get('token')
    verify = UserVerifyFactory().build()
    verify.setName(user_name).setPasswd(passwd).setPasswd2(passwd)
    verify.verifyName()
    verify.verifyPasswd()
    error_index = verify.getError()
    if verify.ifError():
        return jsonify(error_index),404
    raws = db.session.query(users).filter(users.user_name==user_name).all()
    if not raws:
        verify.raiseVerifyError('LoginFailed','LoginFailed')
        error_index = verify.getError()
        return jsonify(error_index),404
    for raw in raws:
        if verify.verifyLogin(raw.user_passwd):
            temptoken = VerifyTool.generateToken(Dev_config().TOKEN,86400,{'user_name':raw.user_name,
                                                                         'fridge_code':raw.fridge_code})
            return jsonify({'state':'success','token':temptoken.decode('ascii')}),200
    error_index = verify.getError()
    return jsonify(error_index),404
