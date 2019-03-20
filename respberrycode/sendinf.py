from getScan import JsonData
import time

jsonData = JsonData()
while(True):
    jsonData.sendPost2()
    time.sleep(60)