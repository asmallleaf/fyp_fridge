from flask import request, jsonify
from app.route.webapi.additem import webapi
from app.route.supportTool import SupportTool
from app.toolbox.dbtool import DBTool
from app.database.models import db,fridge_inf

@webapi.route("/sendinf",methods=['POST',"GET"])
def sendinf():
    temperature = None
    fridge_num = None
    isShow = None
    verify = SupportTool()
    if request.method == 'POST':
        json,result = verify.authorizedInf(request=request, method='POST')
        if not result:
            json['state']='error'
            return jsonify(json),404
        temperature = request.form.get('temperature')
        fridge_num = request.form.get('fridgeNum')
        isShow = request.form.get('isShow')
    elif request.method == 'GET':
        json,result = verify.authorizedInf(request=request, method='GET')
        if not result:
            json['state'] = 'error'
            return jsonify(json),404
        temperature = request.args.get('temperature')
        fridge_num = request.args.get('fridgeNum')
        isShow = request.args.get('isShow')
    if temperature is None and isShow is None:
        return jsonify({'state':'error','error':'temperature is blank'}),404
    raw = db.session.query(fridge_inf).filter(fridge_inf.fridge_code==fridge_num).first()
    if temperature is not None:
        raw.temperature = temperature
    if isShow is not None:
        raw.is_show_inf = DBTool.string2Bool(isShow)
    DBTool.insert(raw,db)
    return jsonify({'state':'success','success':'change Information Successfully'}),200

