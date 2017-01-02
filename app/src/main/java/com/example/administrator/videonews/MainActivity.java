package com.example.administrator.videonews;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.videoplayer.part.SimpleVideoPlayer;

public class MainActivity extends AppCompatActivity {

    private SimpleVideoPlayer simpleVideoPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        simpleVideoPlayer = (SimpleVideoPlayer) findViewById(R.id.main_svp);

        simpleVideoPlayer.setVideoPath(VideoUrlRes.getTestUrl1());
    }

    @Override
    protected void onResume() {
        super.onResume();
        simpleVideoPlayer.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        simpleVideoPlayer.onPause();
    }
}
