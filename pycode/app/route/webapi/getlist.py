from flask import request
from app.route.webapi.blueprint import webapi
from app.database.models import db,items
from app.route.supportTool import SupportTool
from app.toolbox.iotool import IOtool
from flask import jsonify

@webapi.route('/getlist',methods=['POST','GET'])
def getlist():
    if request.method=='POST':
        item_list_num = request.form.get('itemListNum')
        item_name = request.form.get('itemName')

        verify = SupportTool()
        json,result = verify.authorized(request, 'POST')
        if not result:
            return jsonify(json),404

        if item_name is None:
            raws = db.session.query(items).filter(items.item_list_code==item_list_num).all()
            item_list = []
            for raw in raws:
                item_list.append(IOtool.toJson(raw))
            return jsonify(item_list),200
        else:
            raws = db.session.query(items).filter(items.item_list_code==item_list_num,\
                                                  items.item_name==item_name).all()
            item_list = []
            for raw in raws:
                item_list.append(IOtool.toJson(raw))
            return jsonify(item_list),200
    elif request.method == 'GET':
        item_list_num = request.args.get('itemListNum')
        item_name = request.args.get('itemName')

        verify = SupportTool()
        json, result = verify.authorized(request, 'GET')
        if not result:
            return jsonify(json),404

        if item_name is None:
            raws = db.session.query(items).filter(items.item_list_code==item_list_num).all()
            item_list = []
            for raw in raws:
                item_list.append(IOtool.toJson(raw))
            return jsonify(item_list),200
        else:
            raws = db.session.query(items).filter(items.item_list_code==item_list_num,\
                                                  items.item_name==item_name).all()
            item_list = []
            for raw in raws:
                item_list.append(IOtool.toJson(raw))
            return jsonify(item_list),200
    else:
        return jsonify({'state':'error','error':'undefined behavior'})