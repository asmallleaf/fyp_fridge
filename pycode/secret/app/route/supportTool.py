from app.database.models import db,users,fridges
from app.toolbox.dbtool import DBTool
from secret.config import Dev_config,Test_config
from app.verify.userVerify import UserVerifyFactory

# it is the support tool provided implements for verify level
# it has instantiation a UserVerify class in the support class
# the KEY should provide with a setter function in the future
class SupportTool():
    def __init__(self):
        self.verify = UserVerifyFactory().build()
        self.request = None
        self.code = None
        self.KEY = Dev_config.TOKEN

    # it is used to authorized transferred user information, which is currently good enough
    # it need to pass the intent of request and appoint the method of request , POST or GET
    # if the user name and password are not provide in request, the token will be verified
    # if any of the verification fails, it will return the error
    # the result can be checked by calling is None or check the boolean value
    # it is not a well designed method since it concludes two kinds of check method for the results
    def authorized(self, request, method):
        self.request = request
        name = None
        passwd = None
        if method == 'POST':
            name = request.form.get('userName')
            passwd = request.form.get('password')
            self.code = request.form.get('itemListNum')
        elif method == 'GET':
            name = request.args.get('userName')
            passwd = request.args.get('password')
            self.code = request.args.get('itemListNum')
        if name is None or passwd is None:
            return self.authorizedToken(request.form.get('token'))
        else:
            self.verify.setName(name)
            self.verify.setPasswd(passwd)
            self.verify.setPasswd2(passwd)
            return self.authorizedUser()

    # it is used to authorize the token
    # it is specialised for the webapi and only need to pass token
    # it will return a error index and boolean value if it is failed
    # the error index will be {'success':'token correct'} if it is successful
    # the error index will be {'error': 'token is invalid'} or 'authoriated failed'if it is failed
    def authorizedToken(self, token):
        if token is not None:
            data = self.verify.checkToken(token,self.KEY)
            if data is None:
                error_index = self.verify.getError()
                return error_index,False
            user = db.session.query(users).filter(users.user_name==data['user_name'],
                                                  users.fridge_code==data['fridge_code']).first()
            if not user:
                return {'error':'token is invalid'},False
            else:
                list = db.session.query(fridges).filter(fridges.fridge_code==data['fridge_code']).first()
                if self.code == list.item_list_code:
                    return {'success':'token correct'},True
                else:
                    return {'error':'token is invalid'},False
        else:
            return {'error':'authoriated failed'},False

    # it is a more loose verification of token compared to autorizedToken
    # it will not check the item list number in the token
    # some situations will call this
    def authorizedToken2(self, token):
        if token is not None:
            data = self.verify.checkToken(token, self.KEY)
            if data is None:
                error_index = self.verify.getError()
                return error_index, False
            user = db.session.query(users).filter(users.user_name == data['user_name'],
                                                  users.fridge_code == data['fridge_code']).first()
            if not user:
                return {'error': 'token is invalid'}, False
            else:
                return {'success': 'token correct'}, True
        else:
            return {'error': 'authoriated failed'}, False

    # it is used to raise a simple verification of user information
    # compared to verification verify level, it will inquire the information in database
    # it will return a dictionary and boolean value
    # it need to set the data ast first
    def authorizedUser(self):
        raws = db.session.query(users).filter(users.user_name == self.verify.getName()).all()
        for raw in raws:
            if raw is None:
                return {'error': 'name or password is not correct'}, False
            if self.verify.verifyLogin(raw.user_passwd):
                return {'success':'user confirmed'},True
        return {'error': 'token is invalid'},False

    # it is used to raise a verification for temperature information
    # Since the data structure of fridge information is  different from the one of user information
    # it will return a dictionary and boolean value
    def authorizedInf(self, request, method):
        self.request = request
        name = None
        passwd = None
        fridgeNum = None
        token = None
        if method=='POST':
            name = self.request.form.get('userName')
            passwd = self.request.form.get('password')
            fridgeNum = self.request.form.get('fridgeNum')
            token = self.request.form.get('token')
        elif method == 'GET':
            name = self.request.args.get('userName')
            passwd = self.request.args.get('password')
            fridgeNum = self.request.args.get('fridgeNum')
            token = self.request.args.get('token')
        if fridgeNum is None:
            return {'error':'fridgeNum is blank'},False
        if name is None or passwd is None:
            msg,result =  self.authorizedToken2(token)
            if not result:
                return msg,result
            data = self.verify.checkToken(token,self.KEY)
            result = self.verify.verifyAccuracyWithToken(tokenData=data,
                                                         cmpData=fridgeNum,loc='fridge_code')
            if result is not None:
                return self.verify.getError(),False
            else:
                return {'success':'infConfirmed'},True
        else:
            self.verify.setName(name)
            self.verify.setPasswd(passwd)
            self.verify.setPasswd2(passwd)
            msg,result = self.authorizedUser()
            if not result:
                return msg,result
            raws = db.session.query(users).filter(users.user_name==name).all()
            for raw in raws:
                if raw.fridge_code==fridgeNum:
                    return {'success':'infConfirmed'},True
            return {'error':'invalid token or not matched'},False

    # it is used to generate a item list number for a user
    # it will generate a random number at first and will inquire it in database
    # it will return if it is checked as the unique one and will try again if repeated
    @classmethod
    def generate_list_code(cls):
        code = DBTool.generate_randnum(8)
        raw = db.session.query(fridges).filter(fridges.item_list_code==code).first()
        if not raw:
            return code
        else:
            code = cls.generate_list_code()
            return code
