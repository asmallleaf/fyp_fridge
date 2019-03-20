import getScan

jsData = getScan.JsonData()
jsData.regerister()
jsData.getToken()
commend = input()
while(commend!="quitAll"):
    if commend == "login":
        print("relogin!!")
        jsData.regerister()
        jsData.getToken()
    elif commend == "start":
        print("scan mode")
        while(True):
            jsData.read()
            if jsData.data == "exit":
                print("out of scan mode")
                break;
            jsData.sendPost()
    elif commend == "token":
        print("get token")
        jsData.getToken()
    elif commend == "help":
        print("quitAll --stop the whole programme.Do not do it if you indeed wanting to stop.")
        print("start --start to scan, enter \"exit\" to leave the scan mode.")
        print("token --get the token, need to do everyday before using.")
        print("help -- get help with the commend.")
        print("login -- switch the user or the item list code.")
    else:
        print("unkown commend")
    commend = input()
print("have a nice day!!")