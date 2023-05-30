#!/bin/bash

echo "Smojify Installer"
echo "Downloading Spotify libs"
mkdir -pv 'app/libs'
wget 'https://github.com/spotify/android-sdk/releases/download/v0.7.2-appremote_v1.2.3-auth/spotify-app-remote-release-0.7.2.aar'
mv 'spotify-app-remote-release-0.7.2.aar' 'app/libs/'
wget 'https://github.com/spotify/android-sdk/releases/download/v0.7.1-appremote_v1.2.3-auth/spotify-auth-release-1.2.3.aar'
mv 'spotify-auth-release-1.2.3.aar' 'app/libs/'
./gradlew clean build --refresh-dependencies
echo "Done"
