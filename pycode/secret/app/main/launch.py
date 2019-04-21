from app.main.createapp import createapp

# it is a launcher for the whole web server application
# create a intent of flask
app = createapp('development')

# run the flask, it should set to run on 0.0.0.0 to open to the public web
if __name__=="__main__":
    app.run()