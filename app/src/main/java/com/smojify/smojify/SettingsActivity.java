package com.smojify.smojify;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedPreferences = getSharedPreferences("SmojifySettings", Context.MODE_APPEND);
        editor = sharedPreferences.edit();
        syncSettings();
    }

    private void syncSettings() {
        syncWorldContribution();
        syncPlaylistPrivacy();
        syncPlaylistContribution();
        syncEmojiStyle();
    }

    private void syncEmojiStyle() {
        RadioGroup radioGroupEmojiStyle = findViewById(R.id.radioGroupEmojiStyle);
        CheckBox preexistingApply = findViewById(R.id.checkboxUpdateEmojiStyle);

        preexistingApply.setChecked(sharedPreferences.getBoolean("retroactiveEmojiStyle", false));
        preexistingApply.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener () {
            @Override
            public void onCheckedChanged(CompoundButton button, boolean isChecked) {
                editor.putBoolean("retroactiveEmojiStyle", isChecked);
                editor.apply();
            }
            });
        radioGroupEmojiStyle.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    RadioButton radioButton = findViewById(checkedId);
                    String selectedStyle = radioButton.getText().toString();

                    // Store the selected style in SharedPreferences
                    editor.putString("selectedEmojiStyle", selectedStyle);
                    editor.apply();
                }
            });

            String selectedStyle = sharedPreferences.getString("selectedEmojiStyle", "Device");

            // Set the default selection based on the stored value
            if (selectedStyle.equals("Device")) {
                radioGroupEmojiStyle.check(R.id.radioButtonDevice);
            } else if (selectedStyle.equals("Google")) {
                radioGroupEmojiStyle.check(R.id.radioButtonGoogle);
            } else if (selectedStyle.equals("Apple")) {
                radioGroupEmojiStyle.check(R.id.radioButtonApple);
            } else if (selectedStyle.equals("Twitter")) {
                radioGroupEmojiStyle.check(R.id.radioButtonTwitter);
            } else if (selectedStyle.equals("Openmoji")) {
                radioGroupEmojiStyle.check(R.id.radioButtonOpenmoji);
            }
        }

    private void syncWorldContribution() {
        Switch switchWorldContribution = findViewById(R.id.switchWorldContribution);
        TextView textViewContributionStatus = findViewById(R.id.switchWorldContributionStatus);
        switchWorldContribution.setChecked(sharedPreferences.getBoolean("isContributionEnabled", false));
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
                editor.putBoolean("isContributionEnabled", isChecked);
                editor.apply();
            }
        });
    }

    private void syncPlaylistPrivacy() {
        Switch switchPlaylistPrivacy = findViewById(R.id.switchPlaylistPrivacy);
        TextView textViewContributionStatus = findViewById(R.id.switchPlaylistPrivacyStatus);
        switchPlaylistPrivacy.setChecked(sharedPreferences.getBoolean("isPlaylistsPublic", false));
        CheckBox preexistingApply = findViewById(R.id.checkboxUpdatePlaylistPrivacy);

        preexistingApply.setChecked(sharedPreferences.getBoolean("retroactivePlaylistPrivacy", false));
        preexistingApply.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener () {
            @Override
            public void onCheckedChanged(CompoundButton button, boolean isChecked) {
                editor.putBoolean("retroactivePlaylistPrivacy", isChecked);
                editor.apply();
            }
        });
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
                editor.putBoolean("isPlaylistsPublic", isChecked);
                editor.apply();
            }
        });
    }

    private void syncPlaylistContribution() {
        Switch switchPlaylistContribution = findViewById(R.id.switchPlaylistContribution);
        TextView textViewContributionStatus = findViewById(R.id.switchPlaylistContributionStatus);
        CheckBox preexistingApply = findViewById(R.id.checkboxUpdatePlaylistContribution);
        preexistingApply.setChecked(sharedPreferences.getBoolean("retroactivePlaylistCollaborative", false));
        preexistingApply.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener () {
            @Override
            public void onCheckedChanged(CompoundButton button, boolean isChecked) {
                editor.putBoolean("retroactivePlaylistCollaborative", isChecked);
                editor.apply();
            }
        });
        switchPlaylistContribution.setChecked(sharedPreferences.getBoolean("isPlaylistsCollaborative", false));
        if (switchPlaylistContribution.isChecked()) {
            textViewContributionStatus.setText("Your playlists will be collaborative");
            textViewContributionStatus.setTextColor(getResources().getColor(R.color.green));
        } else {
            textViewContributionStatus.setText("Your playlists won't be collaborative");
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
                editor.putBoolean("isPlaylistsCollaborative", isChecked);
                editor.apply();
            }
        });
	}
}
