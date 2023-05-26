package com.smojify.smojify;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button settingsButton;
    private Button playerButton;
    private Button userMoodButton;
    private Button worldMoodButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the buttons by their IDs
        settingsButton = findViewById(R.id.btnSettings);
        playerButton = findViewById(R.id.btnPlayer);
        userMoodButton = findViewById(R.id.btnUserMood);
        worldMoodButton = findViewById(R.id.btnWorldMood);



// Set click listeners for the buttons
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Logic to navigate to the Settings view
                // Replace "SettingsActivity" with the actual activity or fragment for the Settings view
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        playerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Logic to navigate to the Player view
                // Replace "PlayerActivity" with the actual activity or fragment for the Player view
                Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
                startActivity(intent);
            }
        });

        userMoodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Logic to navigate to the User Mood view
                // Replace "UserMoodActivity" with the actual activity or fragment for the User Mood view
                Intent intent = new Intent(MainActivity.this, UserPlaylistsActivity.class);
                startActivity(intent);
            }
        });

        worldMoodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Logic to navigate to the World Mood view
                // Replace "WorldMoodActivity" with the actual activity or fragment for the World Mood view
                Intent intent = new Intent(MainActivity.this, WorldPlaylistsActivity.class);
                startActivity(intent);
            }
        });
    }
}