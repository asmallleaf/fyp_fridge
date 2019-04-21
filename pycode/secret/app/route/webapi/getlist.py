from flask import request
from app.route.webapi.blueprint import webapi
from app.database.models import db,items
from app.route.supportTool import SupportTool
from app.toolbox.iotool import IOtool
from flask import jsonify

# it is the api to ask for the stored items of a user
# it will return the whole list or one of the items
@webapi.route('/getlist',methods=['POST','GET'])
def getlist():
    # get arguments from request
    if request.method=='POST':
        item_list_num = request.form.get('itemListNum')
        item_name = request.form.get('itemName')
        # build implement to verify level
        verify = SupportTool()
        json,result = verify.authorized(request, 'POST')
        if not result:
            return jsonify(json),404
        # if the item name is not transferred, it will return all of the item list of the user
        if item_name is None:
            raws = db.session.query(items).filter(items.item_list_code==item_list_num).all()
            item_list = []
            # the item list should be transferred into json file with the help of IO toolbox
            for raw in raws:
                item_list.append(IOtool.toJson(raw))
            return jsonify(item_list),200
        else:
            # if the item name is provided, it will only return the information of the specific item
            raws = db.session.query(items).filter(items.item_list_code==item_list_num,\
                                                  items.item_name==item_name).all()
            item_list = []
            for raw in raws:
                item_list.append(IOtool.toJson(raw))
            return jsonify(item_list),200
    # the get method is nearly the same as POST method
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