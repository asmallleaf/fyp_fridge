from enum import Enum
import json,urllib3
from pyDes import *
import base64

class DataLoc(Enum):
    NAME = 0
    NUM = 1
    TAB = 2
    LIST = 4
    MCODE = 3

class Item():
    def __init__(self,list):
        if len(list)!=5:
            print("scan error\n")
        else:
            self.itemName=list[DataLoc.NAME.value]
            self.itemNum=list[DataLoc.NUM.value]
            self.tab=list[DataLoc.TAB.value]
            self.itemListNum=list[DataLoc.LIST.value]
            self.mCode = list[DataLoc.MCODE.value]

    @classmethod
    def toJson(cls,obj):
        jsonStr={"itemName":obj.itemName,
                 "itemNum":obj.itemNum,
                 "tab":obj.tab,
                 "itemListNum":obj.itemListNum}
        return jsonStr




class JsonData():
    def __init__(self):
        self.data = None
        self.jsonList = None
        self.isRead = False
        self.URI = "http://47.101.45.81"
        self.secretPath = "/home/pi/Desktop/code/respberry/secret.txt"
        self.userinfPath = "/home/pi/Desktop/code/respberry/userinf.txt"
        self.listNumberPath = "/home/pi/Desktop/code/respberry/listNumber.txt"
        self.tokenPath="/home/pi/Desktop/code/respberry/token.txt"
        self.storagePath="/home/pi/Desktop/code/respberry/storage.txt"
        self.fridgePath="/home/pi/Desktop/code/respberry/fridgeNum.txt"
        self.tempPath="/sys/class/thermal_zone0/temp"

    def regerister(self):
        print("please enter your user name:")
        name = input()
        print("please enter your password:")
        passwd = input()
        print("please enter your item list code")
        listCode = input()
        f = open(self.secretPath)
        key = f.readline(8)
        f.close()
        k=des(key,CBC,IV=key,pad=None,padmode=PAD_PKCS5)
        secPasswdb = k.encrypt(passwd)
        secPasswd = base64.b64encode(secPasswdb)
        with open(self.userinfPath,'w') as f:
            f.write(name+'\n')
            f.write(secPasswd.decode())
        with open(self.listNumberPath,'w') as f:
            f.write(str(listCode))

    def getToken(self):
        uri = self.URI+'/login'
        http = urllib3.PoolManager()
        with open(self.userinfPath,'r') as f:
            data = f.readlines()
        name = data[0].strip('\n')
        secPasswd = data[1].encode()
        with open(self.secretPath) as f:
            key = f.readline(8)
        k = des(key,CBC,IV=key,pad=None,padmode=PAD_PKCS5)
        passwdb = k.decrypt(base64.b64decode(secPasswd))
        passwd = str(passwdb,encoding='utf-8')
        field = {"userName":name,"password":passwd}
        rep = http.request("post",uri,fields=field)
        reponse = json.loads(rep.data.decode())
        if reponse["state"] == "error":
            print(reponse)
        else:
            print("get successfully")
            with open(self.tokenPath,"w") as f:
                f.write(reponse["token"])

    def read(self):
        if self.isRead is False:
            self.isRead = True
            self.data=input()
            self.jsonList=self.data.split(" ")
            f = open(self.listNumberPath)
            tempData = f.readline().strip('\n')
            f.close()
            self.jsonList.append(tempData)
            return self.jsonList

    def sendPost(self):
        if self.isRead is True:
            item = Item(self.jsonList)
            http = urllib3.PoolManager()
            field = Item.toJson(item)
            with open(self.tokenPath,'r') as f:
                token = f.readline()
            field["token"]=token
            uri = self.URI
            mcode = self.jsonList[DataLoc.NAME.value]+\
                    self.jsonList[DataLoc.TAB.value]+\
                    self.jsonList[DataLoc.MCODE.value]
            ifw = False
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
            print(req.data.decode())
            self.isRead=False
        else:
            print("please read first\n")

    def sendPost2(self):
        http = urllib3.PoolManager()
        temp = 0.0
        fridgeNum = None
        token = None
        with open(self.tempPath,'r') as f:
            temp = f.readline()
        itemp = int(temp)/1000
        with open(self.fridgePath,'r') as f:
            fridgeNum = f.readline()
        with open(self.tokenPath,'r') as f:
            token = f.readline()
            uri = self.URI+'/sendinf'
        jsonData = {"token":token,"temperature":str(itemp),"fridgeNum":fridgeNum}
        req = http.request("post",uri,fields=jsonData)
        data = req.data.decode()
        dic = json.loads(data)
        if dic["state"]=='error':
            self.getToken()
        print(dic)


if __name__=='__main__':
    jsData = JsonData()
    #jsData.regerister()
    #jsData.getToken()
    jsData.read()
    jsData.sendPost()

