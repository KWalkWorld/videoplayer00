package com.kwalkworld.videoplayer;

import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final SimpleDateFormat mySDF = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
    private static final String TAG = "main";
    private VideoView videoView;
    private Button openFile;
    private SeekBar seekBar;
    private TextView totalTime;
    private TextView curTime;
    private BarTask seekBarTask;
    private String videoPath;
    private int curProgress;
    private boolean isPlaying;
    private Button playkey;


    private class BarTask extends AsyncTask<Void, Integer, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            while(videoView.isPlaying()){
                publishProgress(videoView.getCurrentPosition());
                SystemClock.sleep(100);
            }
            return null;
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            seekBar.setProgress(values[0]);
            curProgress = values[0];
            curTime.setText(mySDF.format(new Date(values[0])));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }*/

        setContentView(R.layout.activity_main);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            getSupportActionBar().hide();
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        if (savedInstanceState != null){
            curProgress = savedInstanceState.getInt("curProgress");
            isPlaying = savedInstanceState.getBoolean("isPlaying");
        }

        setTitle("Video Player");
        seekBar = findViewById(R.id.seekBar);
        totalTime = findViewById(R.id.totalTime);
        curTime = findViewById(R.id.curTime);
        videoView = findViewById(R.id.videoView);
        playkey = findViewById(R.id.btn_playkey);
        openFile = findViewById(R.id.btn_Open);

        MediaController mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);

        RelativeLayout.LayoutParams layoutParams =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        videoView.setLayoutParams(layoutParams);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int process = seekBar.getProgress();
                if(videoView != null) {
                    videoView.seekTo(process);
                    seekBarTask = new BarTask();
                    seekBarTask.execute();
                    curProgress = process;
                }
            }
        });

        playkey.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (videoView.isPlaying()){
                    videoView.pause();
                }
                else{
                    videoView.start();
                    seekBarTask = new BarTask();
                    seekBarTask.execute();
                }
            }
        });

        videoView.setVideoPath(getVideoPath(R.raw.bytedance));

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                seekBar.setMax(videoView.getDuration());
                Log.d(TAG, "getDuration: " + videoView.getDuration());
                totalTime.setText(mySDF.format(videoView.getDuration()));
                curTime.setText(mySDF.format(curProgress));
                Log.d(TAG, "getCurrentPosition: " + videoView.getCurrentPosition());
                videoView.seekTo(curProgress);
                seekBar.setProgress(curProgress);
                if (isPlaying) {
                    videoView.start();
                    seekBarTask = new BarTask();
                    seekBarTask.execute();
                }
            }
        });
    }

    protected void onPause() {
        curProgress = videoView.getCurrentPosition();
        isPlaying = videoView.isPlaying();
        super.onPause();
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("curProgress", curProgress);
        outState.putBoolean("playingFlag", isPlaying);
        super.onSaveInstanceState(outState);
    }


    private String getVideoPath(int resId) {
        return "android.resource://" + this.getPackageName() + "/" + resId;
    }
}




