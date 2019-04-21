from flask import request, jsonify
from app.route.webapi.getlist import webapi
from app.database.models import db,items
from app.toolbox.dbtool import DBTool
from app.route.supportTool import SupportTool

# it is the api to delete item in items table
@webapi.route('/deleteitem',methods=['POST','GET'])
def deleteitem():
    # get arguments from request
    if request.method=='POST':
        item_list_num = request.form.get('itemListNum')
        item_name = request.form.get('itemName')
        item_num = request.form.get('itemNum')
        # build implement to verify level
        verify = SupportTool()
        json,result = verify.authorized(request=request, method='POST')
        if not result:
            json['state'] = 'error'
            return jsonify(json),404
        # if item name is not transferred, it will delete the whole list
        # it is dangerous to delete all of the list since it will expire the user
        # it is developed for possible function in the future
        if item_name is None:
            raws = db.session.query(items).filter(items.item_list_code==item_list_num).all()
            for raw in raws:
                DBTool.drop(raw=raw,db=db)
        # if item name is transferred, it will just reduce the specific item number
        else:
            raws = db.session.query(items).filter(items.item_list_code == item_list_num,\
                                                  items.item_name==item_name).all()
            for raw in raws:
                raw.item_num = raw.item_num-int(item_num)
                # if the result is less than 1, it will delete the whole raw
                if raw.item_num < 1:
                    DBTool.drop(raw=raw,db=db)
                else:
                    DBTool.insert(raw,db)
    # the get method is nearly the same as POST method
    elif request.method=='GET':
        item_list_num = request.args.get('itemListNum')
        item_name = request.args.get('itemName')
        item_num = request.args.get('itemNum')

        verify = SupportTool()
        json,result = verify.authorized(request=request, method='GET')
        if not result:
            json['state']='error'
            return jsonify(json),404

        if item_name is None:
            raws = db.session.query(items).filter(items.item_list_code==item_list_num).all()
            for raw in raws:
                DBTool.drop(raw=raw, db=db)
        else:
            raws = db.session.query(items).filter(items.item_list_code == item_list_num,items.item_name==item_name).all()
            for raw in raws:
                raw.item_num = raw.item_num-item_num
                if raw.item_num < 1:
                    DBTool.drop(raw=raw,db=db)
                else:
                    DBTool.insert(raw,db)

    return jsonify({'state':'success','success':'delete successfully'}),200