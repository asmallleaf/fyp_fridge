from flask import Flask
from app.config.config import configs
from app.database import models
from app.route.blueprints import webapi,usrapi
from app.toolbox.iotool import JsonEncoder

# an intent of flask will be created here. However, it is just a method.
# it should be used in launcher to divide the launcher from create process
def createapp(objectName):
    # create a intent of flask
    app = Flask(__name__)
    # read the config
    app.config.from_object(configs[objectName])
    # select the json encode for jsonify
    app.json_encoder = JsonEncoder
    # map model classes to database
    models.db.init_app(app)
    # register blueprints on app, the intent of flask
    app.register_blueprint(webapi)
    app.register_blueprint(usrapi)

    # a basic route, should be deleted
    @app.route('/')
    def welcome():
        return '<h>hello world<\h>'

    return app