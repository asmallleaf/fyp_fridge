import getScan

# this module is the launcher of the scanning function
# it can provide a UI in terminal 

# create a JsonData intent
jsData = getScan.JsonData()
# firstly ask the user to login their accounts
jsData.regerister()
jsData.getToken()
# no matter login failed or succeeded it will
# run into the main loop
# the comments has been wirtten in help
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
                jsData.isRead = False
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