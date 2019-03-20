from flask import Flask
from app.config.config import configs
from app.database import models
from app.route.blueprints import webapi,usrapi
from app.toolbox.iotool import JsonEncoder

def createapp(objectName):
    app = Flask(__name__)
    app.config.from_object(configs[objectName])
    app.json_encoder = JsonEncoder
    models.db.init_app(app)
    app.register_blueprint(webapi)
    app.register_blueprint(usrapi)

    @app.route('/')
    def welcome():
        return '<h>hello world<\h>'

    return app