## Server 

Our HTTP Web Server uses Flask to interface our database on Firebase.

We utilize Firebases Realtime Database and Firebase Authentication.

### How To Run:

Requires Python 3.3 or higher

1. Install all libraries: `pip install -r requirements.txt`
2. Generate `serviceAccountCredentials.json` from Firebase and create a `config.py` for secrets
3. Run the server: `python3 main.py`
