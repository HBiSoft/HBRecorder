package com.hbisoft.hbrecorder;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.RequiresApi;

import android.os.Handler;
import android.os.ResultReceiver;
import android.util.DisplayMetrics;


import java.io.ByteArrayOutputStream;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class HBRecorder implements MyListener {
    private int mScreenWidth;
    private int mScreenHeight;
    private int mScreenDensity;
    private final Context context;
    private int resultCode;
    private boolean isAudioEnabled = true;
    private boolean isVideoHDEnabled = true;
    private Activity activity;
    private String outputPath;
    private String fileName;
    private String notificationTitle;
    private String notificationDescription;
    private String notificationButtonText;
    private int audioBitrate = 0;
    private int audioSamplingRate = 0;
    private FileObserver observer;
    private final HBRecorderListener hbRecorderListener;
    private byte[] byteArray;
    private boolean shouldShowNotification = false;
    private String audioSource = "MIC";
    private String videoEncoder = "DEFAULT";
    private boolean enableCustomSettings = false;
    private int videoFrameRate = 30;
    private int videoBitrate = 40000000;
    private String outputFormat = "DEFAULT";

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public HBRecorder(Context context, HBRecorderListener listener) {
        this.context = context.getApplicationContext();
        this.hbRecorderListener = listener;
        setScreenDensity();
    }

    /*Set output path*/
    public void setOutputPath(String path) {
        outputPath = path;
    }

    /*Set file name*/
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /*Set audio bitrate*/
    public void setAudioBitrate(int audioBitrate) {
        this.audioBitrate = audioBitrate;

    }

    /*Set audio sample rate*/
    public void setAudioSamplingRate(int audioSamplingRate) {
        this.audioSamplingRate = audioSamplingRate;
    }

    /*Enable/Disable audio*/
    public void isAudioEnabled(boolean bool) {
        this.isAudioEnabled = bool;
    }

    /*Set Audio Source*/
    //MUST BE ONE OF THE FOLLOWING - https://developer.android.com/reference/android/media/MediaRecorder.AudioSource.html
    public void setAudioSource(String source){
        audioSource = source;

    }

    /*Enable/Disable HD recording*/
    public void recordHDVideo(boolean bool) {
        this.isVideoHDEnabled = bool;
    }

    /*Set Video Encoder*/
    //MUST BE ONE OF THE FOLLOWING - https://developer.android.com/reference/android/media/MediaRecorder.VideoEncoder.html
    public void setVideoEncoder(String encoder){
        videoEncoder = encoder;

    }

    //Enable Custom Settings
    public void enableCustomSettings(){
        enableCustomSettings = true;

    }

    //Set Video Frame Rate
    public void setVideoFrameRate(int fps){
        videoFrameRate = fps;
    }

    //Set Video BitRate
    public void setVideoBitrate(int bitrate){
        videoBitrate = bitrate;
    }

    //Set Output Format
    //MUST BE ONE OF THE FOLLOWING - https://developer.android.com/reference/android/media/MediaRecorder.OutputFormat.html
    public void setOutputFormat(String format){
        outputFormat = format;
    }

    // Set screen densityDpi
    private void setScreenDensity() {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        mScreenDensity = metrics.densityDpi;
    }

    //Set Custom Dimensions (NOTE - YOUR DEVICE MIGHT NOT SUPPORT THE SIZE YOU PASS IT)
    public void setScreenDimensions(int heightInPX, int widthInPX){
        mScreenHeight = heightInPX;
        mScreenWidth = widthInPX;
    }

    /*Get file path including file name and extension*/
    public String getFilePath() {
        return ScreenRecordService.getFilePath();
    }

    /*Get file name and extension*/
    public String getFileName() {
        return ScreenRecordService.getFileName();
    }

    /*Start screen recording*/
    public void startScreenRecording(Intent data, int resultCode, Activity activity) {
        this.resultCode = resultCode;
        this.activity = activity;
        startService(data);
    }

    /*Stop screen recording*/
    public void stopScreenRecording() {
        Intent service = new Intent(context, ScreenRecordService.class);
        context.stopService(service);
    }

    /*Check if recording is in progress*/
    @SuppressWarnings("deprecation") //For more on why this is added, see - https://stackoverflow.com/q/45519439/5550161
    public boolean isBusyRecording() {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (ScreenRecordService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /*Change notification icon*/
    public void setNotificationSmallIcon(int drawable) {
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), drawable);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        icon.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byteArray = stream.toByteArray();
    }

    /*Enable/Disable notifications*/
    public void shouldShowNotification(boolean bool) {
        shouldShowNotification = bool;
    }

    /*Set notification title*/
    public void setNotificationTitle(String Title) {
        notificationTitle = Title;
    }

    /*Set notification description*/
    public void setNotificationDescription(String Description) {
        notificationDescription = Description;
    }

    public void setNotificationButtonText(String string){
        notificationButtonText = string;
    }

    /*Start recording service*/
    private void startService(Intent data) {
        observer = new FileObserver(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)), activity, HBRecorder.this);
        observer.startWatching();

        Intent service = new Intent(context, ScreenRecordService.class);
        service.putExtra("code", resultCode);
        service.putExtra("data", data);
        service.putExtra("audio", isAudioEnabled);
        service.putExtra("width", mScreenWidth);
        service.putExtra("height", mScreenHeight);
        service.putExtra("density", mScreenDensity);
        service.putExtra("quality", isVideoHDEnabled);
        service.putExtra("path", outputPath);
        service.putExtra("fileName", fileName);
        service.putExtra("audioBitrate", audioBitrate);
        service.putExtra("audioSamplingRate", audioSamplingRate);
        service.putExtra("notificationSmallBitmap", byteArray);
        service.putExtra("notificationTitle", notificationTitle);
        service.putExtra("notificationDescription", notificationDescription);
        service.putExtra("shouldShowNotification", shouldShowNotification);
        service.putExtra("notificationButtonText", notificationButtonText);
        service.putExtra("enableCustomSettings", enableCustomSettings);
        service.putExtra("audioSource",audioSource);
        service.putExtra("videoEncoder", videoEncoder);

        service.putExtra("videoFrameRate", videoFrameRate);
        service.putExtra("videoBitrate", videoBitrate);
        service.putExtra("outputFormat", outputFormat);
        service.putExtra(ScreenRecordService.BUNDLED_LISTENER, new ResultReceiver(new Handler()) {
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                super.onReceiveResult(resultCode, resultData);
                if (resultCode == Activity.RESULT_OK) {
                    String result = resultData.getString("errorReason");
                    if (result != null) {
                        observer.stopWatching();
                        hbRecorderListener.HBRecorderOnError(100, result);
                        Intent mservice = new Intent(context, ScreenRecordService.class);
                        context.stopService(mservice);
                    }
                }
            }
        });


        context.startService(service);
    }

    /*Complete callback method*/
    @Override
    public void callback() {
        observer.stopWatching();
        hbRecorderListener.HBRecorderOnComplete();
    }
}
