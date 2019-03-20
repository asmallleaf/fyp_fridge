from app.route.supportTool import SupportTool
from app.route.usrapi.signin import usrapi
from flask import request, jsonify
from app.verify.userVerify import UserVerifyFactory
from app.database.models import db, fridges, fridge_inf


@usrapi.route("/getuser",methods=['POST','GET'])
def getuser():
    token = None
    if request.method=='POST':
        token = request.form.get('token')
    elif request.method=='GET':
        token = request.args.get('token')
    verify = SupportTool()
    json,result = verify.authorizedToken2(token)
    if result is False:
        json['state'] = 'error'
        return jsonify(json),404
    verify2 = UserVerifyFactory().build()
    data = verify2.checkToken(token,verify.KEY)
    raw = db.session.query(fridges).filter(fridges.fridge_code==data['fridge_code']).first()
    if raw is not None and raw.item_list_code is not None:
        data['listCode']=raw.item_list_code
    raw = db.session.query(fridge_inf).filter(fridge_inf.fridge_code==data['fridge_code']).first()
    if raw is not None:
        data['isShow']=raw.is_show_inf
    data['state']='success'
    return jsonify(data),200