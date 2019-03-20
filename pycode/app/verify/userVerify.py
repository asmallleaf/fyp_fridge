from app.toolbox.verifytool import VerifyFactory,VerifyTool,VerifyError

class UserVerifyFactory(VerifyFactory):
    def build(self):
        return UserVerify()

class UserVerifyError(VerifyError):
    def __init__(self,arg,type):
        super(UserVerifyError,self).__init__(arg,type)

    def feedback(self):
        if self.type == 'nameLength':
            self.msg = 'user name too long'
        elif self.type == 'nameBlank':
            self.msg = 'name can not be blank'
        elif self.type == 'passwdBlank':
            self.msg = 'password can not be blank'
        elif self.type == 'passwd2Blank':
            self.msg = 'please confirm the password'
        elif self.type == 'differentPasswd':
            self.msg = 'confirm password failed'
        elif self.type == 'fridgeBlank':
            self.msg = 'fridge code can not be blank'
        elif self.type == 'codeLength':
            self.msg = 'fridge code is invalid'
        elif self.type == 'SignatureExpired':
            self.msg = 'token has been out of time'
        elif self.type == 'BadSignature':
            self.msg = 'the token does not exited or is invalid'
        elif self.type == 'LoginFailed':
            self.msg = 'User name or password is not correct'
        elif self.type == 'tokenUnmatched':
            self.msg = 'The token is invalid or not matched'
        else:
            self.msg = 'UnknownProblem'
        return self.msg

class UserVerify(VerifyTool):
    def __init__(self):
        self.error_index = {'state':'error'}
        self.name = None
        self.passwd = None
        self.passwd2 = None
        self.fridge_code = None
        self.iferror = False

    def raiseVerifyError(self,args,type):
        try:
            raise UserVerifyError(args,type)
        except UserVerifyError as uve:
            temp = uve.feedback()
            self.error_index[type]=temp
            self.iferror=True
            print(temp)

    def verifySignIn(self):
        self.verifyName()
        self.verifyPasswd()
        self.verifyFridgeCode()

    def checkToken(self,token,key):
        result,data = VerifyTool.verifyToken(token,token_key=key)
        if result == 'SignatureExpired':
            self.raiseVerifyError('SignatureExpired','SignatureExpired')
            return None
        elif result == 'BadSignature':
            self.raiseVerifyError('BadSignature','BadSignature')
            return None
        else:
            return data

    def verifyLogin(self,mpasswd):
        if self.passwdVerify(self.passwd,mpasswd):
            return True
        else:
            self.raiseVerifyError('LoginFailed','LoginFailed')
            return False

    def verifyName(self):
        if not self.name:
            self.raiseVerifyError('nameBlank','nameBlank')
            return 'nameBlank'
        if len(self.name)>20:
            self.raiseVerifyError('nameLength','nameLength')
            return 'nameLength'
        else:
            return None

    def verifyPasswd(self):
        if not self.passwd:
            self.raiseVerifyError('passwdBlank','passwdBlank')
            return 'passwdBlank'
        if not self.passwd2:
            self.raiseVerifyError('passwd2Blank','passwd2Blank')
            return 'passwd2Blank'
        if not VerifyTool.isequal(self.passwd2,self.passwd):
            self.raiseVerifyError('differentPasswd','differentPasswd')
            return 'differentPasswd'
        return None

    def verifyFridgeCode(self):
        if not self.fridge_code:
            self.raiseVerifyError('fridgeBlank','fridgeBlank')
            return 'fridgeBlank'
        if len(self.fridge_code)>8:
            self.raiseVerifyError('codeLength','codeLength')
            return 'codeLength'
        return None

    def verifyAccuracyWithToken(self,tokenData,cmpData,loc):
        if cmpData != tokenData[loc]:
            self.raiseVerifyError('tokenUnmatched','tokenUnmatched')
            return 'tokenUnmatched'
        else:
            return None

    def setName(self,value):
        self.name = value
        return self
    def getName(self):
        return self.name
    def setPasswd(self,value):
        self.passwd = value
        return self
    def getPasswd(self):
        return self.passwd
    def setPasswd2(self,value):
        self.passwd2 = value
        return self
    def getPasswd2(self):
        return self.passwd2
    def setCode(self,value):
        self.fridge_code = value
        return self
    def getCode(self):
        return self.fridge_code
    def getError(self):
        return self.error_index
    def ifError(self):
        return self.iferror
