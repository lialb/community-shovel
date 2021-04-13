import requests

'''
Used to test API with requests.
'''

HOST = 'http://localhost:5000'

def login_test():
    r = requests.post(f'{HOST}/login', json={'email' : 'mbailey@fake.com', 'password' : 'password'})
    return r

def create_account_test():
    r = requests.post(f'{HOST}/create-account', json={ 'firstName' : 'Brian', 'lastName' : 'Bailey', 'password': 'password', 'email' : 'mbailey2@fake.com', 'bio' : 'CS 465 Professor'})
    return r

print(login_test())

