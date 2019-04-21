from app.route.usrapi.blueprint import usrapi
from flask import request,jsonify
from app.verify.userVerify import UserVerifyFactory
from app.database.models import db,users
from app.toolbox.verifytool import VerifyTool
from secret.config import Dev_config

# it is the api for login
# it will return a token without successful state if succeeded
@usrapi.route('/login',methods=['POST','GET'])
def login():
    # get arguments from request
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
    # build implement to verify level
    verify = UserVerifyFactory().build()
    # verify arguments
    verify.setName(user_name).setPasswd(passwd).setPasswd2(passwd)
    verify.verifyName()
    verify.verifyPasswd()
    # return error if failed
    error_index = verify.getError()
    if verify.ifError():
        return jsonify(error_index),404
    # inquire the hash encoded password stored in database
    raws = db.session.query(users).filter(users.user_name==user_name).all()
    if not raws:
        verify.raiseVerifyError('LoginFailed','LoginFailed')
        error_index = verify.getError()
        return jsonify(error_index),404
    # if there is an account, verify the password
    for raw in raws:
        if verify.verifyLogin(raw.user_passwd):
            # generate the token and set the valid time to 24 hours
            temptoken = VerifyTool.generateToken(Dev_config().TOKEN,86400,{'user_name':raw.user_name,
                                                                         'fridge_code':raw.fridge_code})
            return jsonify({'state':'success','token':temptoken.decode('ascii')}),200
    error_index = verify.getError()
    return jsonify(error_index),404
