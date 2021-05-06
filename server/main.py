from flask import Flask, request, Response
from flask_cors import CORS
import json
import pyrebase
import config
from requests.exceptions import HTTPError
import time

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
    body = request.json
    body['status'] = 0
    db.child('requests').push(body)

    return json.dumps({'Success' : True})

@app.route('/volunteer-for-request/<string:request_id>', methods=['PUT'])
def volunteer_for_request(request_id):
    '''
    Adds a user to volunteer for a request. Takes in email through JSON body
    '''
    try:
        body = request.json
        db = firebase.database()
        if '.' in body['email']:
            body['email'] = body['email'].replace('.', ',')
        db.child('requests').child(request_id).child('volunteers').set({ body['email']: int(time.time()) })

        return json.dumps({'Success' : True})
    except:
        print('Something went wrong.')
        return Response('1', status=400)
    
@app.route('/remove-volunteer/<string:request_id>', methods=['PUT'])
def remove_volunteer(request_id):
    '''
    Remove a volunteer from a request. Takes in email through JSON body
    '''
    try:
        body = request.json
        db = firebase.database()
        if '.' in body['email']:
            body['email'] = body['email'].replace('.', ',')
        db.child('requests').child(request_id).child('volunteers').child(body['email']).remove()

        return json.dumps({'Success' : True})
    except:
        print('Something went wrong.')
        return Response('1', status=400)


@app.route('/update-request/<string:request_id>', methods=['PUT'])
def update_request(request_id):
    '''
    Updates a request based on request_id and JSON body
    '''
    db = firebase.database()
    db.child('requests').child(request_id).update(request.json)

    return json.dumps({'Success' : True})

@app.route('/upvote-request/<string:request_id>', methods=['PUT'])
def upvote_request(request_id):
    '''
    Upvotes a request based on request_id
    '''
    db = firebase.database()
    current_upvotes = db.child('requests').child(request_id).child('upvotes').get().val()
    db.child('requests').child(request_id).update({ 'upvotes' : current_upvotes + 1 })

    return json.dumps({'Success' : True})

@app.route('/add-request-comment/<string:request_id>', methods=['POST'])
def add_request_comment(request_id):
    '''
    Adds comment to request with request_id. Takes in JSON body with user_id, name, and comment parameters.
    '''
    body = request.json
    db = firebase.database()
    comments = db.child('requests').child(request_id).child('comments').get().val()
    if not comments:
        data = { 
            0 : { 
                'comment' : body['comment'], 
                'name' : body['name'],
                'user_id' : body['user_id'] 
            } 
        }

        db.child('requests').child(request_id).child('comments').set(data)
        return json.dumps({'Success' : True})
    next_index = len(comments)
    data = { 
        'comment' : body['comment'], 
        'name' : body['name'], 
        'user_id' : body['user_id'] 
    }
    db.child('requests').child(request_id).child('comments').child(next_index).set(data)
    return json.dumps({'Success' : True})


@app.route('/get-user/<string:email>')
def get_user(email):
    '''
    Gets user info based on email. 
    MAKE SURE TO REPLACE PERIODS WITH COMMAS IN EMAIL BEFORE CALLING THIS ROUTE
    '''
    if '.' in email:
        email = email.replace('.', ',')
    db = firebase.database()
    user_data = db.child('users').child(email).get().val()
    return json.dumps(user_data)

@app.route('/update-user/<string:email>', methods=['PUT'])
def update_user(email):
    '''
    Updates a user's first name, last name, and/or bio. Accepts JSON body and email url parameter
    MAKE SURE TO REPLACE PERIODS WITH COMMAS IN EMAIL BEFORE CALLING THIS ROUTE
    '''
    db = firebase.database()
    try:
        body = request.json
        data = {}
        if 'first_name' in body:
            data['first_name'] = body['first_name']
            
        if 'last_name' in body:
            data['last_name'] = body['last_name']

        if 'bio' in body:
            data['bio'] = body['bio']
        if '.' in email:
            email = email.replace('.', ',')
        
        db.child('users').child(email).update(data)

        return json.dumps({'Success' : True})
    except:
        print('Something went wrong')
        return Response('1', status=406)
    
@app.route('/add-user-comment/<string:email>', methods=['POST'])
def add_user_comment(email):
    '''
    Adds comment to user profile with email. Takes in JSON body with user_id, name, and comment parameters.
    '''
    body = request.json
    db = firebase.database()
    comments = db.child('users').child(email).child('comments').get().val()
    if not comments:
        data = { 
            0 : { 
                'comment' : body['comment'], 
                'name' : body['name'],
                'user_id' : body['user_id'] 
            } 
        }

        db.child('users').child(email).child('comments').set(data)
        return json.dumps({'Success' : True})
    next_index = len(comments)
    data = { 
        'comment' : body['comment'], 
        'name' : body['name'], 
        'user_id' : body['user_id'] 
    }
    db.child('users').child(email).child('comments').child(next_index).set(data)
    return json.dumps({'Success' : True})


@app.route('/login', methods=['POST'])
def login():
    '''
    Takes in JSON body with username and password parameters and attempts to log user in.
    Creates a token that lasts an hour if user is logs in successfully.
    If successful, return '0'. Otherwise, return '1'
    '''
    try:
        body = request.json
        auth = firebase.auth()
        user = auth.sign_in_with_email_and_password(body['email'], body['password'])
        # db = firebase.database()
        # data = { 'email' : body['email'] }
        # results = db.child('users').push(data, user['idToken'])
        return json.dumps({'Success' : True})
    except HTTPError as e:
        print(e)
        return Response('1', status=406)

@app.route('/create-account', methods=['POST'])
def create_account():
    '''
    Creates an account based on JSON body with email, password, first_name, last_name, and bio parameters.
    Password is not saved in Realtime Database
    WILL FAIL IF DUPLICATE EMAIL EXISTS OR PASSWORD IS UNDER 6 CHARACTERS
    '''
    try:
        body = request.json
        auth = firebase.auth()
        auth.create_user_with_email_and_password(body['email'], body['password'])
        data = { 
            'email' : body['email'], 
            'first_name' : body['firstName'], 
            'last_name' : body['lastName'], 
            'bio' : body['bio'],
            'people_impacted' : 15,
            'distance_shoveled' : 40
        }
        db = firebase.database()
        # use email with periods replaced as commas because firebase does not allow periods in key
        db.child('users').child(body['email'].replace('.', ',')).set(data) 

        return json.dumps({'Success' : True})
    except HTTPError as e:
        print(e)
        return Response('1', status=406)


if __name__ == '__main__':
    app.run(debug=True)