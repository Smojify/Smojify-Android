#!/usr/bin/env python3

import requests
import json
import pprint
import os
from dotenv import load_dotenv



def retrieve_library(token, url):
    headers = {
        'Authorization': f'Bearer {token}'
    }

    params = {
        'operationName': 'libraryV3',
        'variables': '{"filters":[],"order":"Recently Added","textFilter":"","features":["LIKED_SONGS","YOUR_EPISODES"],"limit":50,"offset":0,"flatten":false,"expandedFolders":[],"folderUri":null,"includeFoldersWhenFlattening":true,"withCuration":false}',
        'extensions': '{"persistedQuery":{"version":1,"sha256Hash":"3582b7cd31c22b1fb0bb32d31b96d29b0211ac1bba714fcc980248cb0a162f6d"}}'
    }
    # Make the GET request
    response = requests.get(url, headers=headers, params=params)
    if response.status_code != 200:
        print("Unable to retrieve playlists from spotify")
        print(response.text)
        print(response.status_code)
        return None
    else:
        json_object = json.loads(response.text)
        data_json = json_object["data"]
        me_json = data_json["me"]
        library_json = me_json["libraryV3"]
        lib_items_json = library_json["items"]
        return lib_items_json

def print_item_details(items):
    for item in items:
        print("---")
        if 'item' in item:
            if 'data' in item["item"]:
                if 'name' in item["item"]["data"]:
                    name = item["item"]["data"]["name"]
                    print("Name: " + name)
                else:
                    print("Name not found")
                if 'description' in item["item"]["data"]:
                    description = item["item"]["data"]["description"]
                    print("Description: " + description)
                else:
                    print("Description not found")

if __name__ == '__main__':
    load_dotenv()
    # 1. Get Web Token
    spotifyBearerWebToken = os.getenv("BEARER_AUTH_TOKEN")
    print(spotifyBearerWebToken)
    spotifyPartnerUrl = os.getenv("SPOTIFY_PARTNER_URL")
    print(spotifyPartnerUrl)
    spotifyUserUri = os.getenv("SPOTIFY_USER_URI")
    # 2. Get Song URI
    # 3. Get Emoji Reaction
    # 4. Publish Reaction

    # 5. Retrieve / Create Emoji's Playlist





    items = retrieve_library(spotifyBearerWebToken, spotifyPartnerUrl)
    if items:
        print_item_details(items)
        quit(0)
    else:
        print("Error...\nAbort")
        quit(1)
