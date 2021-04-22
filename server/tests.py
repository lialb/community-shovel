import requests

'''
Used to test API with requests.
'''

HOST = 'http://localhost:5000'

def login_test():
    r = requests.post(f'{HOST}/login', json={'email' : 'mbailey@fake.com', 'password' : 'password'})
    return r

def create_account_test():
    # r = requests.post(f'{HOST}/create-account', json={ 'firstName' : 'Brian', 'lastName' : 'Bailey', 'password': 'password', 'email' : 'mbailey7@fake.com', 'bio' : 'CS 465 Professor'})
    r = requests.post(f'{HOST}/create-account', json={'firstName': 'greg', 'lastName': 'johnson', 'password': 'abcdefg', 'email': 'test@fake.com', 'bio': 'my bio'})
    return r

def update_user_test():
    r = requests.put(f'{HOST}/update-user/albert@fake,com', json={'bio': 'changed bio'})

update_user_test()
