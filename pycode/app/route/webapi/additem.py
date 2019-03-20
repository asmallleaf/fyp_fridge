from flask import request,jsonify
from app.route.webapi.deleteitem import webapi
from app.toolbox.dbtool import DBTool
from app.database.models import items,db
from app.verify.dbVerify import Verify
from datetime import datetime
from app.route.supportTool import SupportTool

@webapi.route('/additem',methods=['POST','GET'])
def additem():
    if request.method == 'POST':
        item_list_num = request.form.get('itemListNum')
        item_name = request.form.get('itemName')
        item_num = int(request.form.get('itemNum'))
        item_time = datetime.utcnow()
        item_tab = request.form.get('tab')

        verify = SupportTool()
        json,result = verify.authorized(request=request, method='POST')
        if not result:
            json['state']='error'
            return jsonify(json),404

        new_raw = items(item_list_code=item_list_num,item_name=item_name,item_num=item_num,\
                        storage_time=item_time,tab=item_tab)
        raws = db.session.query(items).filter(items.item_list_code==item_list_num,\
                                              items.item_name==item_name).all()
        raws_num = Verify.check(raws,max=1,min=0,error_arg="number error",error_tab="MutilableObjects")
        if raws_num == 1:
            for raw in raws:
                raw.item_num=raw.item_num+item_num
                DBTool.insert(raw,db)
        else:
            DBTool.insert(raw=new_raw,db=db)

        return jsonify({'state':'success','success':'added successfully'}),200
