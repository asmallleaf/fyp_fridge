from flask import request, jsonify
from app.route.webapi.getlist import webapi
from app.database.models import db,items
from app.toolbox.dbtool import DBTool
from app.route.supportTool import SupportTool

@webapi.route('/deleteitem',methods=['POST','GET'])
def deleteitem():
    if request.method=='POST':
        item_list_num = request.form.get('itemListNum')
        item_name = request.form.get('itemName')
        item_num = request.form.get('itemNum')

        verify = SupportTool()
        json,result = verify.authorized(request=request, method='POST')
        if not result:
            json['state'] = 'error'
            return jsonify(json),404

        if item_name is None:
            raws = db.session.query(items).filter(items.item_list_code==item_list_num).all()
            for raw in raws:
                DBTool.drop(raw=raw,db=db)
        else:
            raws = db.session.query(items).filter(items.item_list_code == item_list_num,\
                                                  items.item_name==item_name).all()
            for raw in raws:
                raw.item_num = raw.item_num-int(item_num)
                if raw.item_num < 1:
                    DBTool.drop(raw=raw,db=db)
                else:
                    DBTool.insert(raw,db)
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