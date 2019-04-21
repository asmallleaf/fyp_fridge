from enum import Enum
import json,urllib3
from pyDes import *
import base64

# this is the a module for scanning function and sending 
# information module. It has enum class DataLoc, calss Item
# class JsonData

# this is an enum class to define the location of each variable
# in a text file, Name is item name, num is item number
# tab is item tab, list is item list number, mcode is company code
class DataLoc(Enum):
    NAME = 0
    NUM = 1
    TAB = 2
    LIST = 4
    MCODE = 3

# this is a model class for Item. It is used to generate a Json file
class Item():
    def __init__(self,list):
        if len(list)!=5:
            print("scan error\n")
            self.ifError = True
        else:
            self.itemName=list[DataLoc.NAME.value]
            self.itemNum=list[DataLoc.NUM.value]
            self.tab=list[DataLoc.TAB.value]
            self.itemListNum=list[DataLoc.LIST.value]
            self.mCode = list[DataLoc.MCODE.value]
            self.ifError = False

    # this is method can transfer the class into json data after setting
    # the values of each member
    @classmethod
    def toJson(cls,obj):
        jsonStr={"itemName":obj.itemName,
                 "itemNum":obj.itemNum,
                 "tab":obj.tab,
                 "itemListNum":obj.itemListNum}
        return jsonStr

# this is main class in this module, which provide several methods for
# the other module.
# most of the methods is used to send HTTP behaviors
class JsonData():
    def __init__(self):
        self.data = None
        self.jsonList = None
        self.isRead = False
        self.URI = "http://47.101.45.81"
        # the fill path of used txt file
        self.secretPath = "/home/pi/Desktop/code/respberry/secret.txt"
        self.userinfPath = "/home/pi/Desktop/code/respberry/userinf.txt"
        self.listNumberPath = "/home/pi/Desktop/code/respberry/listNumber.txt"
        self.tokenPath="/home/pi/Desktop/code/respberry/token.txt"
        self.storagePath="/home/pi/Desktop/code/respberry/storage.txt"
        self.fridgePath="/home/pi/Desktop/code/respberry/fridgeNum.txt"
        self.tempPath="/sys/class/thermal_zone0/temp"

    # this mehtod is used to login an account 
    def regerister(self):
        # get the username, password and item list code
        print("please enter your user name:")
        name = input()
        print("please enter your password:")
        passwd = input()
        print("please enter your item list code")
        listCode = input()
        # store the value into txt files
        f = open(self.secretPath)
        key = f.readline(8)
        f.close()
        # encoded the user password with Des encoding and base64 encoding
        k=des(key,CBC,IV=key,pad=None,padmode=PAD_PKCS5)
        secPasswdb = k.encrypt(passwd)
        secPasswd = base64.b64encode(secPasswdb)
        # store the valus into file
        with open(self.userinfPath,'w') as f:
            f.write(name+'\n')
            f.write(secPasswd.decode())
        with open(self.listNumberPath,'w') as f:
            f.write(str(listCode))

    # this is a method to get a token from the web server
    def getToken(self):
        # add Api routs
        uri = self.URI+'/login'
        http = urllib3.PoolManager()
        # read user information from txt file
        with open(self.userinfPath,'r') as f:
            data = f.readlines()
        name = data[0].strip('\n')
        # decode the password stored in txt file
        secPasswd = data[1].encode()
        with open(self.secretPath) as f:
            key = f.readline(8)
        k = des(key,CBC,IV=key,pad=None,padmode=PAD_PKCS5)
        passwdb = k.decrypt(base64.b64decode(secPasswd))
        passwd = str(passwdb,encoding='utf-8')
        # send a request to web server
        field = {"userName":name,"password":passwd}
        rep = http.request("post",uri,fields=field)
        # resolve the json file in response
        reponse = json.loads(rep.data.decode())
        # if error, report the error
        if reponse["state"] == "error":
            print(reponse)
        else:
            # if success, record the token
            print("get successfully")
            with open(self.tokenPath,"w") as f:
                f.write(reponse["token"])

    # this is a method to scan a item and store it into a list
    # the list is used to decide weather send a delete request or
    # add request
    def read(self):
        # open the lock
        if self.isRead is False:
            self.isRead = True
            # read QR code
            self.data=input()
            # resolve data stored in QR code
            self.jsonList=self.data.split(" ")
            # read te list number
            f = open(self.listNumberPath)
            tempData = f.readline().strip('\n')
            f.close()
            # generate a json list
            self.jsonList.append(tempData)
            return self.jsonList

    # this is used to send a POST request to web server for scanning 
    # function
    def sendPost(self):
        # check the lock
        if self.isRead is True:
            # convert item list into Item class
            item = Item(self.jsonList)
            if item.ifError is True:
                return None
            # open a http pool
            http = urllib3.PoolManager()
            # convert Item class into Json file
            field = Item.toJson(item)
            # read the token from file
            with open(self.tokenPath,'r') as f:
                token = f.readline()
            # load token into request
            field["token"]=token
            uri = self.URI
            # generate company code to make a kind of product unique
            mcode = self.jsonList[DataLoc.NAME.value]+\
                    self.jsonList[DataLoc.TAB.value]+\
                    self.jsonList[DataLoc.MCODE.value]
            ifw = False
            # check weather the mcode has been stored in storage list
            # if true, send deleteitem request
            # if false, send additem request
            with open(self.storagePath,"r") as f:
                data = f.readline()
                items = data.split(' ')
                if mcode in items:
                    uri = self.URI+"/deleteitem"
                    ifw = True
                else:
                    uri = self.URI+"/additem"
            if ifw is False:
                with open(self.storagePath,'a') as f:
                    f.write(mcode+' ')
            else:
                with open(self.storagePath,'w') as f:
                    items.pop(items.index(mcode))
                    data = ' '.join(items)
                    f.write(data)
            req = http.request("post",uri,fields=field)
            # print the feedback
            print(req.data.decode())
            self.isRead=False
        else:
            print("please read first\n")
        return None

    # this is a method to send a POST request for sending 
    # temperature information function
    def sendPost2(self):
        # create a http pool
        http = urllib3.PoolManager()
        # set the temperture in default 
        temp = 0.0
        fridgeNum = None
        token = None
        # read the temperature , token and fridge number
        with open(self.tempPath,'r') as f:
            temp = f.readline()
        itemp = int(temp)/1000
        with open(self.fridgePath,'r') as f:
            fridgeNum = f.readline()
        with open(self.tokenPath,'r') as f:
            token = f.readline()
            uri = self.URI+'/sendinf'
        # generate a json file
        jsonData = {"token":token,"temperature":str(itemp),"fridgeNum":fridgeNum}
        # send a request
        req = http.request("post",uri,fields=jsonData)
        data = req.data.decode()
        dic = json.loads(data)
        # check the token 
        if dic["state"]=='error':
            self.getToken()
        # print feedback
        print(dic)


if __name__=='__main__':
    jsData = JsonData()
    #jsData.regerister()
    #jsData.getToken()
    jsData.read()
    jsData.sendPost()

