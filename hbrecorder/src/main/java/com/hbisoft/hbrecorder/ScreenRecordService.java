package com.hbisoft.hbrecorder;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;

import androidx.annotation.RequiresApi;

import android.util.Log;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;

public class ScreenRecordService extends Service {

    private static final String TAG = "ScreenRecordService";

    private int mScreenWidth;
    private int mScreenHeight;
    private int mScreenDensity;
    private int mResultCode;
    private Intent mResultData;
    private boolean isVideoHD;
    private boolean isAudioEnabled;
    private String path;

    private MediaProjection mMediaProjection;
    private MediaRecorder mMediaRecorder;
    private VirtualDisplay mVirtualDisplay;
    private String name;
    private int audioBitrate;
    private int audioSamplingRate;
    private static String filePath;
    private static String fileName;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        /*
         *   Notification Start
         */
        byte[] notificationSmallIcon = intent.getByteArrayExtra("notificationSmallBitmap");
        String notificationTitle = intent.getStringExtra("notificationTitle");
        String notificationDescription = intent.getStringExtra("notificationDescription");
        boolean shouldShowNotification = intent.getBooleanExtra("shouldShowNotification", true);
        String notificationButtonText = intent.getStringExtra("notificationButtonText");
        mResultCode = intent.getIntExtra("code", -1);
        mResultData = intent.getParcelableExtra("data");
        mScreenWidth = intent.getIntExtra("width", 720);
        mScreenHeight = intent.getIntExtra("height", 1280);
        mScreenDensity = intent.getIntExtra("density", 1);
        isVideoHD = intent.getBooleanExtra("quality", true);
        isAudioEnabled = intent.getBooleanExtra("audio", true);
        path = intent.getStringExtra("path");
        name = intent.getStringExtra("fileName");

        filePath = name;
        audioBitrate = intent.getIntExtra("audioBitrate", 128000);
        audioSamplingRate = intent.getIntExtra("audioSamplingRate", 44100);

        if (notificationButtonText==null){
            notificationButtonText = "STOP RECORDING";
        }

        if (audioBitrate == 0){
            audioBitrate = 128000;
        }
        if (audioSamplingRate == 0){
            audioSamplingRate = 44100;
        }

        if (notificationTitle == null || notificationTitle.equals("")){
            notificationTitle = "Recording your screen";
        }

        if (notificationDescription == null || notificationDescription.equals("")){
            notificationDescription = "Drag down to stop the recording";
        }

        //Check if notification should be shown
        if (shouldShowNotification) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String channelId = "001";
                String channelName = "RecordChannel";
                NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_NONE);
                channel.setLightColor(Color.BLUE);
                channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
                NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                if (manager != null) {
                    manager.createNotificationChannel(channel);
                    Notification notification;

                    Intent myIntent = new Intent(this, NotificationReceiver.class);

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, myIntent, 0);

                    Notification.Action action = new Notification.Action.Builder(
                            Icon.createWithResource(this, android.R.drawable.presence_video_online),
                            notificationButtonText,
                            pendingIntent).build();

                    if (notificationSmallIcon != null) {
                        Bitmap bmp = BitmapFactory.decodeByteArray(notificationSmallIcon, 0, notificationSmallIcon.length);
                        //Modify notification badge
                        notification = new Notification.Builder(getApplicationContext(), channelId).setOngoing(true).setSmallIcon(Icon.createWithBitmap(bmp)).setContentTitle(notificationTitle).setContentText(notificationDescription).addAction(action).build();

                    } else {
                        //Modify notification badge
                        notification = new Notification.Builder(getApplicationContext(), channelId).setOngoing(true).setSmallIcon(R.drawable.icon).setContentTitle(notificationTitle).setContentText(notificationDescription).addAction(action).build();

                    }
                    startForeground(101, notification);
                }
            } else {
                startForeground(101, new Notification());
            }
        }
        /*
         *   Notification End
         */


        if (path == null) {
            path = String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES));
        }

        mMediaProjection = createMediaProjection();
        mMediaRecorder = createMediaRecorder();
        mVirtualDisplay = createVirtualDisplay();
        mMediaRecorder.start();

        return Service.START_STICKY;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private MediaProjection createMediaProjection() {
        return ((MediaProjectionManager) Objects.requireNonNull(getSystemService(Context.MEDIA_PROJECTION_SERVICE))).
                getMediaProjection(mResultCode, mResultData);
    }

    public static String getFilePath() {
        return filePath;
    }

    public static String getFileName() {
        return fileName;
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private MediaRecorder createMediaRecorder() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault());
        Date curDate = new Date(System.currentTimeMillis());
        String curTime = formatter.format(curDate).replace(" ", "");
        String videoQuality = "HD";
        if (!isVideoHD) {
            videoQuality = "SD";
        }
        if (name == null) {
            name = videoQuality + curTime;
        }

        filePath = path + "/" + name + ".mp4";

        fileName = name + ".mp4";

        MediaRecorder mediaRecorder = new MediaRecorder();


        if (isAudioEnabled) {
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        }
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        if (isAudioEnabled) {
            mediaRecorder.setAudioEncodingBitRate(audioBitrate);
            mediaRecorder.setAudioSamplingRate(audioSamplingRate);
        }

        mediaRecorder.setOutputFile(path + "/" + name + ".mp4");
        mediaRecorder.setVideoSize(mScreenWidth, mScreenHeight);
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        if (isAudioEnabled) {
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        }
        if (!isVideoHD) {
            mediaRecorder.setVideoEncodingBitRate(5000000);
            mediaRecorder.setVideoFrameRate(30);
            Log.e("info - ", String.valueOf(2 * mScreenWidth * mScreenHeight));
        } else {
            mediaRecorder.setVideoEncodingBitRate(5 * mScreenWidth * mScreenHeight);
            mediaRecorder.setVideoFrameRate(60); //after setVideoSource(), setOutFormat()
        }


        try {
            mediaRecorder.prepare();
        } catch (IllegalStateException | IOException e) {
            Log.e(TAG, "createMediaRecorder: e = " + e.toString());
        }

        return mediaRecorder;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private VirtualDisplay createVirtualDisplay() {
        return mMediaProjection.createVirtualDisplay(TAG, mScreenWidth, mScreenHeight, mScreenDensity, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mMediaRecorder.getSurface(), null, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        if (mVirtualDisplay != null) {
            mVirtualDisplay.release();
            mVirtualDisplay = null;
        }
        if (mMediaRecorder != null) {
            mMediaRecorder.setOnErrorListener(null);

            mMediaProjection.stop();
            mMediaRecorder.reset();
        }
        if (mMediaProjection != null) {
            mMediaProjection.stop();
            mMediaProjection = null;
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
