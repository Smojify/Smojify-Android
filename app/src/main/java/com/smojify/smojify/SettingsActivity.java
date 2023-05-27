package com.smojify.smojify;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        syncSettings();
    }

    private void syncSettings() {
        syncWorldContribution();
        syncPlaylistPrivacy();
        syncPlaylistContribution();
    }

    private void syncWorldContribution() {
        Switch switchWorldContribution = findViewById(R.id.switchWorldContribution);
        TextView textViewContributionStatus = findViewById(R.id.switchWorldContributionStatus);
        if (switchWorldContribution.isChecked()) {
            textViewContributionStatus.setText("You're contributing to world wide playlists");
            textViewContributionStatus.setTextColor(getResources().getColor(R.color.green));
        } else {
            textViewContributionStatus.setText("You aren't contributing to world wide playlists");
            textViewContributionStatus.setTextColor(getResources().getColor(R.color.red));
        }
        switchWorldContribution.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    textViewContributionStatus.setText("You're contributing to world wide playlists");
                    textViewContributionStatus.setTextColor(getResources().getColor(R.color.green));
                } else {
                    textViewContributionStatus.setText("You aren't contributing to world wide playlists");
                    textViewContributionStatus.setTextColor(getResources().getColor(R.color.red));
                }
            }
        });
    }

    private void syncPlaylistPrivacy() {
        Switch switchPlaylistPrivacy = findViewById(R.id.switchPlaylistPrivacy);
        TextView textViewContributionStatus = findViewById(R.id.switchPlaylistPrivacyStatus);
        if (switchPlaylistPrivacy.isChecked()) {
            textViewContributionStatus.setText("Your playlists will be public");
            textViewContributionStatus.setTextColor(getResources().getColor(R.color.green));
        } else {
            textViewContributionStatus.setText("Your playlists will be private");
            textViewContributionStatus.setTextColor(getResources().getColor(R.color.red));
        }
        switchPlaylistPrivacy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    textViewContributionStatus.setText("Your playlists will be public");
                    textViewContributionStatus.setTextColor(getResources().getColor(R.color.green));
                } else {
                    textViewContributionStatus.setText("Your playlists will be private");
                    textViewContributionStatus.setTextColor(getResources().getColor(R.color.red));
                }
            }
        });
    }

    private void syncPlaylistContribution() {
        Switch switchPlaylistContribution = findViewById(R.id.switchPlaylistContribution);
        TextView textViewContributionStatus = findViewById(R.id.switchPlaylistContributionStatus);
        if (switchPlaylistContribution.isChecked()) {
            textViewContributionStatus.setText("Your playlist will be collaborative");
            textViewContributionStatus.setTextColor(getResources().getColor(R.color.green));
        } else {
            textViewContributionStatus.setText("Your playlist won't be collaborative");
            textViewContributionStatus.setTextColor(getResources().getColor(R.color.red));
        }
        switchPlaylistContribution.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    textViewContributionStatus.setText("Your playlist will be collaborative");
                    textViewContributionStatus.setTextColor(getResources().getColor(R.color.green));
                } else {
                    textViewContributionStatus.setText("Your playlist won't be collaborative");
                    textViewContributionStatus.setTextColor(getResources().getColor(R.color.red));
                }
            }
        });
	}
}
