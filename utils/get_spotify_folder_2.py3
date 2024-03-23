#!/usr/bin/env python3

import requests

# Define the authorization token and the URL with parameters
AUTH_TOKEN = 'BQAt6_aGQjEh36EnwMlvqjXnfeiPIsGFMqpM8sfskhU0fYXFWM4Wdq7RhH_DCklzTgOrDyqD1kkNt_LID8jxZaFsK8y5xShlF9PBZ4VuNZ9jw_sW33-BBjK7sApQ38Nmi7PTBrMJhg1tARSSQ9cjnHBIsRKjAMd45r6rZzQ5lfBMmJyhpnAyL0eYCznGAVmh8XZEzXZ57nqVGnue4LBfBmny6yAXqVO9vtJlEwZJ1FmW3E7S8gm3lj8hBaqFnhSednSW6taPDNDXbn9MJrXVh4jed4oNrPTxTbERaT2_XLjucujKwUolKr2DADH_JglgarppaDnDiQSt3QvLgW9aBL1s_X_JXY9R'
url = 'https://api-partner.spotify.com/pathfinder/v1/query'

# Define headers and parameters for the request
headers = {
    'Authorization': f'Bearer {AUTH_TOKEN}'
}

params = {
    'operationName': 'libraryV3',
    'variables': '{"filters":[],"order":"Recently Added","textFilter":"","features":["LIKED_SONGS","YOUR_EPISODES"],"limit":50,"offset":0,"flatten":false,"expandedFolders":[],"folderUri":"spotify:user:31xyt7khqn7ujom2jvmttstq2r44:folder:41642576effc8bfe","includeFoldersWhenFlattening":true,"withCuration":false}',
    'extensions': '{"persistedQuery":{"version":1,"sha256Hash":"808f0e4288442f2cc56e25e2b80029d873316a40c2d63952d20ac40c9f049d9e"}}'
}

# Make the GET request
response = requests.get(url, headers=headers, params=params)

# Print the response text
print(response.text)
