package com.example.ffmpegmc264demo;

import android.Manifest;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import kim.yt.ffmpegmc264.MC264Encoder;


public class MainActivity extends AppCompatActivity {
    private final static String TAG = "MainActivity";
    EditText urlSrc;
    private View spinnerContainer;
    private static boolean spinnerActive = false;

    private MyTask ffmpeg_task = null;
    private static MC264Encoder mH264Encoder;
    private static int ffmpeg_retcode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_new);

        /*
         * request an android permission : WRITE_EXTERNAL_STORAGE
         * It is required when to save the ffmpeg output into a file.
         */
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1337);

        mH264Encoder = new MC264Encoder();   //should be before new LibFFMpeg(); because libavcodec call putYUVFrameData() during new LibFFMpeg();

        EditText usagelabel = findViewById(R.id.usageLabel);
        //usagelabel.setFocusable(false);         //show normally but not editable
        usagelabel.setEnabled(false);        //show as gray

        EditText usagebody = findViewById(R.id.usageBody);
        //usagebody.setFocusable(false);     //show normally but not editable
        usagebody.setEnabled(false);        //show as gray

        EditText usagebody2 = findViewById(R.id.usageBody2);
        //usagebody2.setFocusable(false);     //show normally but not editable
        usagebody2.setEnabled(false);        //show as gray

        urlSrc = findViewById(R.id.editText4url);
        Button process = findViewById(R.id.btn_process);
        Button btn_stop = findViewById(R.id.btn_stop);

        String defaultCmd = getResources().getString(R.string.ffmpeg_cmd_pc1);
        String savedCmd = PreferenceManager.getDefaultSharedPreferences(this).getString("cmd", defaultCmd);
        if( savedCmd != null && savedCmd.length() > 6 ) // 6 == sizeof("ffmpeg");
            urlSrc.setText( savedCmd );
        else {
            urlSrc.setText(defaultCmd);
            saveCmd(defaultCmd);
        }

        spinnerContainer = findViewById(R.id.v_spinner_container);
        spinnerContainer.setVisibility(View.VISIBLE);

        process.setOnClickListener(this::processClicked);
        btn_stop.setOnClickListener(this::btnStopClicked);

        spinnerContainer.setVisibility(View.GONE);
    }

    private void saveCmd( String cmdStr ) {
        //String[] sArrays = cmdStr.split("\\s+");   //+ : to remove duplicate whitespace
        PreferenceManager.getDefaultSharedPreferences(this).edit().putString("cmd", cmdStr ).apply();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if( !spinnerActive )
            spinnerContainer.setVisibility(View.GONE);
        else
            spinnerContainer.setVisibility(View.VISIBLE);
    }

    private void processClicked(View ignored) {
        Toast.makeText(this, "Start Clicked !!!", Toast.LENGTH_SHORT).show();

        spinnerContainer.setVisibility(View.VISIBLE);
        spinnerActive = true;

        String fullUrl = new String("") + urlSrc.getText();
        String[] sArrays = fullUrl.split("\\s+");   //+ : to remove duplicate whitespace
        saveCmd(fullUrl);

        ffmpeg_task = new MyTask(this);
        ffmpeg_task.execute( sArrays );
    }

    private int stopCnt = 0;
    private void btnStopClicked(View ignored) {
        Toast.makeText(this, "Stop Clicked !!!", Toast.LENGTH_SHORT).show();

        String fullUrl = new String("") + urlSrc.getText();
        saveCmd(fullUrl);

        mH264Encoder.ffmpegStop();
//        mH264Encoder.reset();     //Please, call mH264Encoder.reset() inside onPostExecute() not here

        if( ++stopCnt >= 3 )
            mH264Encoder.ffmpegForceStop();
    }

    void finished() {
        Log.d( TAG, "finished() : Enter ...");
        spinnerContainer.setVisibility(View.GONE);
        spinnerActive = false;

        ffmpeg_task = null;
        ffmpeg_retcode = 0;
        stopCnt = 0;
    }

    private static class MyTask extends AsyncTask<String, Void, Void> {
        private final WeakReference<MainActivity> activityWeakReference;

        MyTask(MainActivity activity) {
            activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        protected Void doInBackground(String... strings) {
            mH264Encoder.H264MediaCodecReady();
            ffmpeg_retcode = mH264Encoder.ffmpegRun(strings);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mH264Encoder.reset();
            final MainActivity activity = activityWeakReference.get();
            if (activity != null) {
                activity.finished();
            }
        }
    }
}
