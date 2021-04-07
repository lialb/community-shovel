from flask import Flask, request
from flask_cors import CORS
import json
import pyrebase
import config

'''
HTTP Server API interfacing firebase for the Community Shovel App
'''

firebase = pyrebase.initialize_app(config.secrets)

app = Flask(__name__)
CORS(app)

@app.route('/get-request/<string:request_id>')
def get_request(request_id):
    '''
    Returns request data based on the request_id
    '''
    db = firebase.database()
    req = db.child('requests').child(request_id).get()

    return json.dumps(req.val())

@app.route('/get-all-requests')
def get_all_requests():
    '''
    Gets all requests in the real time database
    '''
    db = firebase.database()
    reqs = db.child('requests').get()

    return json.dumps(reqs.val())

@app.route('/create-request', methods=['POST'])
def create_request():
    '''
    Creates a request through JSON body. Needs creator_id, info, time, x_coord, y_coord, and comments
    '''
    db = firebase.database()
    db.child('requests').push(request.json)

    return 'Successfully created request'

@app.route('/update-request/<string:request_id>', methods=['PUT'])
def update_request(request_id):
    '''
    Updates a request based on request_id and JSON body
    '''
    db = firebase.database()
    db.child('requests').child(request_id).update(request.json)

    return 'Succesfully updated request'

@app.route('/upvote-request/<string:request_id>')
def upvote_request(request_id):
    '''
    Upvotes a request based on request_id
    '''
    db = firebase.database()
    current_upvotes = db.child('requests').child(request_id).child('upvotes').get().val()
    db.child('requests').child(request_id).update({ 'upvotes' : current_upvotes + 1 })

    return 'Succesfully upvoted request'

@app.route('/add-comment/<string:request_id>', methods=['POST'])
def add_comment(request_id):
    '''
    Adds comment to request with request_id. Takes in JSON body with user_id, name, and comment parameters.
    '''
    body = request.json
    db = firebase.database()
    comments = db.child('requests').child(request_id).child('comments').get().val()
    if not comments:
        data = { 0 : { 'comment' : body['comment'], 'name' : body['name'], 'user_id' : body['user_id'] } }
        db.child('requests').child(request_id).push({ 'comments' : data })
        return 'Successfully added comment'

    next_index = len(comments)
    data = { next_index : { 'comment' : body['comment'], 'name' : body['name'], 'user_id' : body['user_id'] } }
    db.child('reqeuests').child(request_id).child('comments').push(data)
    return 'Successfully added comment'

@app.route('/get-user/<string:user_id>')
def get_user(user_id):
    '''
    Gets user info based on user_id
    '''
    db = firebase.database()
    user_data = db.child('users').child(user_id).get().val()
    return json.dumps(user_data)

if __name__ == '__main__':
    app.run(debug=True)