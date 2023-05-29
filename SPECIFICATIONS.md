# Finalized Smojify App Specifications

## Overview

Smojify is an Android application that integrates with Spotify, allowing users to react with emojis to the tracks they're listening to. The app works with Android versions 7.0 to 13 (or higher).

## Main Features

### Main Menu

The Main Menu:

- Displays the app icon, centered horizontally in the middle of the top half of the screen.
- Contains buttons redirecting to other views: Settings, Player, User Playlists, World Playlists, and an 'About' page.

### Player View

The Player view is divided into three sections:

1. Current song information, displaying the cover image, track title, and artist name.
2. Emoji reaction section with a transparent background. Here, the user can react to the track with one or multiple emojis using a transparent EditText view. Clicking on the logo or emoji image will focus on the EditText view. The reaction triggers an overlay in the middle of the screen, showing the selected emoji. Initially, the overlay shows the logo, then it shows the last reacted emoji. Emoji images are in PNG format.
3. Spotify player controls, providing previous, play/pause, and next functionalities.

When a user reacts with an emoji:

- The song is added to a playlist that matches the emoji. If a matching playlist does not exist, the app creates it and then adds the song.
- If the world-wide contribution setting is enabled, the song is also added or updated in the matching world-wide playlist.

### Updating a Track to Playlist

The app:

- Checks if the track is already in the playlist; if not, adds it at position 0.
- Retrieves its current position; if the position is more than 0, decreases it by one, and then updates it.

### Creating a Playlist

The app:

- Does not create another playlist if a playlist already exists for an emoji.
- Names the playlist after the emoji.
- Sets the playlist's image to be the respective emoji's image, sourced from websites such as emojiterra.com. Playlist images are in PNG format.

### Settings View

In the Settings view, users can:

- Enable or disable their contribution to world-wide playlists.
- Set the privacy of their playlists to either public or private. This setting also allows updating the privacy status of existing playlists.
- Choose to make their playlists collaborative or non-collaborative. This setting also allows updating the collaboration status of existing playlists.
- Select the preferred emoji style for the app. Multiple options are provided, including Device, Google, Apple, Twitter, and Openmoji. This setting also allows updating the emoji style of existing playlists.

All these settings are stored persistently using SharedPreferences. The default selection is set based on the stored value.

### Spotify Integration

- Uses the Spotify Web API to retrieve and update playlist information.
- Authenticates with Spotify using the Spotify Android SDK.
- Allows users to log out from their Spotify account. The app disconnects from Spotify and clears the session.

## User Interfaces

- **Login Screen**: Contains a login button to initiate the Spotify authentication process.
- **Main Menu**: Includes the app icon and buttons to different app views.
- **Settings View**: Allows users to customize world contribution, playlist privacy, playlist contribution, and emoji style.
- **Player View**: Displays the current state of the Spotify player, including details of the track being played and emoji reactions.
- **User Playlists View & World Playlists View**: Shows the cover image of each playlist on the left and the name of each playlist. World Playlists view also displays user contributions to the worldwide playlists.

## Data Persistence

The app uses SharedPreferences to store and retrieve user settings.

## Dependencies

- **Spotify Web API**: Retrieves

 and updates playlist information.
- **Spotify Android SDK**: Authenticates with Spotify's services.

## Security & Privacy

- **Spotify OAuth**: Provides user authentication and secure access to Spotify Web API.
- **Playlist Privacy**: Playlist privacy options let users control the visibility of their playlists.
