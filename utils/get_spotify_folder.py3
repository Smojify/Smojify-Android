#!/usr/bin/env python3

import sys
import urllib.parse
import json
import requests

SPOTIFY_PARTNER_URL = 'https://api-partner.spotify.com'
BEARER_TOKEN = 'BQAt6_aGQjEh36EnwMlvqjXnfeiPIsGFMqpM8sfskhU0fYXFWM4Wdq7RhH_DCklzTgOrDyqD1kkNt_LID8jxZaFsK8y5xShlF9PBZ4VuNZ9jw_sW33-BBjK7sApQ38Nmi7PTBrMJhg1tARSSQ9cjnHBIsRKjAMd45r6rZzQ5lfBMmJyhpnAyL0eYCznGAVmh8XZEzXZ57nqVGnue4LBfBmny6yAXqVO9vtJlEwZJ1FmW3E7S8gm3lj8hBaqFnhSednSW6taPDNDXbn9MJrXVh4jed4oNrPTxTbERaT2_XLjucujKwUolKr2DADH_JglgarppaDnDiQSt3QvLgW9aBL1s_X_JXY9R'

# Function to URL decode a string
def urldecode(s):
    return urllib.parse.unquote_plus(s)

# Function to URL encode a string
def urlencode(s):
    return urllib.parse.quote_plus(s)

# Function to extract parameters from URL
def extract_params(url):
    params = urllib.parse.parse_qs(urllib.parse.urlsplit(url).query)
    return {key: value[0] for key, value in params.items()}

# Decoding parameters from URL
SPOTIFY_QUERY = "operationName=libraryV3&filters=&order=Recently+Added&textFilter=&limit=50&offset=0&flatten=false&folderUri=spotify%3Auser%3A31xyt7khqn7ujom2jvmttstq2r44%3Afolder%3A41642576effc8bfe&includeFoldersWhenFlattening=true&withCuration=false&extensions=%7B%22persistedQuery%22%3A%7B%22version%22%3A1%2C%22sha256Hash%22%3A%22808f0e4288442f2cc56e25e2b80029d873316a40c2d63952d20ac40c9f049d9e%22%7D%7D"
params = extract_params(SPOTIFY_QUERY)

offset=0
user_uri='31xyt7khqn7ujom2jvmttstq2r44'

# Encoding parameters for use in the request
encoded_params = {
    'operationName': 'libraryV3',
    'variables': json.dumps({
        'filters': [],
        'order': 'Recently+Added',
        'textFilter': '',
        'features': ["LIKED_SONGS", "YOUR_EPISODES"],
        'limit': 50,
        'offset': offset,
        'flatten': False,
        'expandedFolders': [],
        'folderUri': 'spotify' + '%3A' + 'user' + '%3A' + user_uri,
        'includeFoldersWhenFlattening': False,
        'withCuration': False
    }),
    'extensions': '%7B%22persistedQuery%22%3A%7B%22version%22%3A1%2C%22sha256Hash%22%3A%22808f0e4288442f2cc56e25e2b80029d873316a40c2d63952d20ac40c9f049d9e%22%7D%7D'
}

extensions_json = json.dumps({
    "persistedQuery": {
        "version": 1,
        "sha256Hash": "808f0e4288442f2cc56e25e2b80029d873316a40c2d63952d20ac40c9f049d9e"
    }
})

encoded_params['extensions'] = urlencode(extensions_json)

# Making the request
headers = {
    'Authorization': f'Bearer {BEARER_TOKEN}'
}

url = f"{SPOTIFY_PARTNER_URL}/pathfinder/v1/query"
response = requests.get(url, params=encoded_params, headers=headers)

# Print the response
print(response.text)

