from flask import request, jsonify
from app.route.webapi.additem import webapi
from app.route.supportTool import SupportTool
from app.toolbox.dbtool import DBTool
from app.database.models import db,fridge_inf

# it is an api designed to send temperature information or change the state of is show information
@webapi.route("/sendinf",methods=['POST',"GET"])
def sendinf():
    # get arguments from request
    temperature = None
    fridge_num = None
    isShow = None
    # build implement to verify level
    verify = SupportTool()
    # verify the arguments for different methods
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
    # if both of the temperature and isShow is blank, it is a bad request
    if temperature is None and isShow is None:
        return jsonify({'state':'error','error':'temperature is blank'}),404
    raw = db.session.query(fridge_inf).filter(fridge_inf.fridge_code==fridge_num).first()
    # if the temperature is not None, the temperature should be changed after verification
    if temperature is not None:
        raw.temperature = temperature
    # if the isShow is not None, the isSHow should be changed after verification
    if isShow is not None:
        raw.is_show_inf = DBTool.string2Bool(isShow)
    # update database
    DBTool.insert(raw,db)
    # return success state and message
    return jsonify({'state':'success','success':'change Information Successfully'}),200

