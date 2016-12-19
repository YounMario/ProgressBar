package com.example.progressbar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

    private VideoProgressBar seekBar;
    private MyVideoController myVideoController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        seekBar = (VideoProgressBar) findViewById(R.id.mySeekBar);
        myVideoController = new MyVideoController();
        seekBar.setController(new WeakReference<VideoController>(myVideoController));
        seekBar.setSeekListener(new VideoProgressBar.SeekListener() {
            @Override
            public void onSeek(int progress) {
                myVideoController.setProgress(progress);
            }
        });
    }

    class MyVideoController implements VideoController {

        private int mProgress;

        @Override
        public int getProgress() {
            return mProgress;
        }

        @Override
        public void setProgress(int progress) {
            this.mProgress = progress;
        }
    }
}
