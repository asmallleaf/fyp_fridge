from app.route.webapi.getinf import webapi
from app.route.usrapi.getuser import usrapi

# ALl of the blueprints will be collected here
# the source of each blueprint should be updated if there is a new api built
# the createapp will import the webapi and usrapi from this file
webapi = webapi
usrapi = usrapi
