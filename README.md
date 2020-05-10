# VideoPlayer

## 效果展示：  
![screenrecord](https://github.com/KWalkWorld/videoplayer00/blob/master/screenshot/record0.webm)   
<div align=center><img width="300" height="500" src="https://github.com/KWalkWorld/videoplayer00/blob/master/screenshot/Screenshot_1.png"/></div>  
<div align=center><img width="500" height="300" src="https://github.com/KWalkWorld/videoplayer00/blob/master/screenshot/Screenshot_2.png"/></div>  
<div align=center><img width="500" height="300" src="https://github.com/KWalkWorld/videoplayer00/blob/master/screenshot/Screenshot_3.png"/></div>  
<div align=center><img width="300" height="500" src="https://github.com/KWalkWorld/videoplayer00/blob/master/screenshot/Screenshot_4.png"/></div>  


## 代码说明：
### 1.设置布局：
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    tools:context=".MainActivity">

    <RelativeLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:background="#000000">

        <VideoView
            android:id="@+id/videoView"
            android:layout_width="match_parent"
            android:layout_height="236dp"
            android:layout_centerInParent="true"/>


    </RelativeLayout>

<!--    <MediaController
        android:id="@+id/mController"
        android:layout_width="match_parent"
        android:layout_height="76dp">
    </MediaController>-->

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:layout_margin="5dp">

        <TextView
            android:id="@+id/curTime"
            android:layout_width="70dp"
            android:layout_height="match_parent"
            android:layout_gravity="start"
            android:gravity="center" />

        <Button
            android:id="@+id/btn_playkey"
            android:layout_width="28dp"
            android:layout_height="match_parent"
            android:background="#EF0606"/>

        <SeekBar
            android:id="@+id/seekBar"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_gravity="center"
            android:layout_weight="1" />

        <TextView
            android:id="@+id/totalTime"
            android:layout_width="70dp"
            android:layout_height="match_parent"
            android:layout_gravity="end"
            android:gravity="center" />

        <Button
            android:id="@+id/btn_Open"
            android:layout_width="52dp"
            android:layout_height="match_parent"
            android:text="more"
            android:background="#fff"
            android:textColor="#000"/>

    </LinearLayout>
    
</LinearLayout>

### 2.准备好视频后准备播放：
```
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
```

### 3.设置播放键：按下可以播放或暂停视频
```
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
```

### 4.设置SeekBar点击和拖拽事件:
```
    seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}

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
```

### 5.SeekBar随视频进度移动：
```
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
```
在视频进度更改时，加入:  
```
seekBarTask = new BarTask();
seekBarTask.execute();
```

### 6.增加MediaController:
```
    MediaController mediaController = new MediaController(this);
    videoView.setMediaController(mediaController);

    RelativeLayout.LayoutParams layoutParams =
            new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
    layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
    videoView.setLayoutParams(layoutParams);
```

### 7.手机横置后全屏播放：
```
     if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
         getSupportActionBar().hide();
         getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
     }
```

### 8. 视频播放状态保存和恢复
保存：  
```
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
```
恢复：  
```
        if (savedInstanceState != null){
            curProgress = savedInstanceState.getInt("curProgress");
            isPlaying = savedInstanceState.getBoolean("isPlaying");
        }
```
