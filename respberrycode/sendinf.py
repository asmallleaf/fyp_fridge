from getScan import JsonData
import time

# this is a file to send temperature information
# it will run in background automatically

# create a JsonData intent
jsonData = JsonData()
while(True):
    # send a request
    jsonData.sendPost2()
    # sleep for one minute
    time.sleep(60)