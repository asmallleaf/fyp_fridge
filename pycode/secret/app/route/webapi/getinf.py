from flask import request, jsonify
from app.route.webapi.sendinf import webapi
from app.route.supportTool import SupportTool
from app.toolbox.dbtool import DBTool
from app.database.models import db,fridge_inf

# it is the api to ask for temperature information
@webapi.route("/getinf",methods=['POST',"GET"])
def getinf():
    # get arguments from request
    fridge_num = None
    # build implement to verify level
    verify = SupportTool()
    # verify the arguments for different methods
    if request.method == 'POST':
        json,result = verify.authorizedInf(request=request, method='POST')
        if not result:
            json['state']='error'
            return jsonify(json),404
        fridge_num = request.form.get('fridgeNum')
    elif request.method == 'GET':
        json,result = verify.authorizedInf(request=request, method='GET')
        if not result:
            json['state']='error'
            return jsonify(json),404
        fridge_num = request.args.get('fridgeNum')
    # if succeeded. inquire the information in database and return it
    raw = db.session.query(fridge_inf).filter(fridge_inf.fridge_code==fridge_num).first()
    if raw.is_show_inf is True:
        return jsonify({'state':'success','temperature':raw.temperature}),200
    else:
        return jsonify({'state':'success','temperature':0}),200