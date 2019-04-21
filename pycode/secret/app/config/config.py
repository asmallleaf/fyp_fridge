from secret.config import Dev_config,Test_config

# this is the class for config in flask
# there is two configuration class defined in this module, development and test
# the test environment should be selected when deploying to web server
# the development should be selected when it is developed

class Dev_Config(Dev_config):
    USERNAME = 'root'
    HOST = 'localhost'
    DATABASENAME = 'db_fridge'
    SQLALCHEMY_DATABASE_URI = 'mysql://'+USERNAME+':'+Dev_config.PASSWORD+\
                              '@'+HOST+'/'+DATABASENAME
    SQLALCHEMY_TRACK_MODIFICATIONS = False
    TOKEN_KEY = Dev_config.TOKEN

class Test_Config(Test_config):
    USERNAME = 'root'
    HOST = 'localhost'
    DATABASENAME = 'db_fridge'
    SQLALCHEMY_DATABASE_URI = 'mysql://'+USERNAME+':'+Test_config.PASSWORD+\
                              '@'+HOST+'/'+DATABASENAME
    SQLALCHEMY_TRACK_MODIFICATIONS = False
    TOKEN_KEY = Test_config.TOKEN

configs = {
    'development': Dev_Config,
    'test': Test_Config
}