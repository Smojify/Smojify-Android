package com.example.smojifysample;

import android.os.Bundle;
import android.transition.Scene;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;

public class MainScene extends AppCompatActivity {

    private Scene connectionScene;
    private Scene currentSongScene;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sample);

        connectionScene = Scene.getSceneForLayout((ViewGroup) findViewById(R.id.rootContainer), R.layout.connection, this);
        currentSongScene = Scene.getSceneForLayout((ViewGroup) findViewById(R.id.rootContainer), R.layout.current_song, this);

        connectionScene.enter();
    }

    public void onConnection(View view) {
        Transition slide = new Slide(Gravity.LEFT);

        TransitionManager.go(currentSongScene, slide);
    }

}
