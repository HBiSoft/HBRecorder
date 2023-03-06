package com.hbisoft.hbrecorder;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;

import androidx.annotation.DrawableRes;
import androidx.annotation.RequiresApi;

import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.util.DisplayMetrics;
import android.util.Log;


import java.io.ByteArrayOutputStream;
import java.io.File;

import static com.hbisoft.hbrecorder.Constants.ERROR_KEY;
import static com.hbisoft.hbrecorder.Constants.ERROR_REASON_KEY;
import static com.hbisoft.hbrecorder.Constants.GENERAL_ERROR;
import static com.hbisoft.hbrecorder.Constants.MAX_FILE_SIZE_KEY;
import static com.hbisoft.hbrecorder.Constants.NO_SPECIFIED_MAX_SIZE;
import static com.hbisoft.hbrecorder.Constants.ON_COMPLETE_KEY;
import static com.hbisoft.hbrecorder.Constants.ON_START_KEY;
import static com.hbisoft.hbrecorder.Constants.ON_PAUSE_KEY;
import static com.hbisoft.hbrecorder.Constants.ON_RESUME_KEY;

/**
 * Created by HBiSoft on 13 Aug 2019
 * Copyright (c) 2019 . All rights reserved.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class HBRecorder implements MyListener {
    private int mScreenWidth;
    private int mScreenHeight;
    private int mScreenDensity;
    private final Context context;
    private int resultCode;
    private boolean isAudioEnabled = true;
    private boolean isVideoHDEnabled = true;
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
    private int vectorDrawable = 0;
    private String audioSource = "MIC";
    private String videoEncoder = "DEFAULT";
    private boolean enableCustomSettings = false;
    private int videoFrameRate = 30;
    private int videoBitrate = 40000000;
    private String outputFormat = "DEFAULT";
    private int orientation;
    private long maxFileSize = NO_SPECIFIED_MAX_SIZE; // Default no max size
    boolean wasOnErrorCalled = false;
    Intent service;
    boolean isPaused = false;
    boolean isMaxDurationSet = false;
    int maxDuration = 0;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public HBRecorder(Context context, HBRecorderListener listener) {
        this.context = context.getApplicationContext();
        this.hbRecorderListener = listener;
        setScreenDensity();
    }

    public void setOrientationHint(int orientationInDegrees){
        orientation = orientationInDegrees;
    }

    /*Set output path*/
    public void setOutputPath(String path) {
        outputPath = path;
    }

    Uri mUri;
    boolean mWasUriSet = false;
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void setOutputUri(Uri uri){
        mWasUriSet = true;
        mUri = uri;
    }

    // WILL IMPLEMENT THIS AT A LATER STAGE
    // DEVELOPERS ARE WELCOME TO LOOK AT THIS AND CREATE A PULL REQUEST
    /*Mute microphone*/
    /*public void setMicMuted(boolean state){
        if (context!=null) {
            try {
                ((AudioManager)context.getSystemService(Context.AUDIO_SERVICE)).setStreamMute(AudioManager.STREAM_SYSTEM,true);

                AudioManager myAudioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);

                // get the working mode and keep it
                int workingAudioMode = myAudioManager.getMode();

                myAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);

                // change mic state only if needed
                if (myAudioManager.isMicrophoneMute() != state) {
                    myAudioManager.setMicrophoneMute(state);
                }

                // set back the original working mode
                myAudioManager.setMode(workingAudioMode);
            }catch (Exception e){
                Log.e("HBRecorder", "Muting mic failed with the following exception:");
                e.printStackTrace();
            }

        }
    }*/

    /*Set max duration in seconds */
    public void setMaxDuration(int seconds){
        isMaxDurationSet = true;
        maxDuration = seconds * 1000;
    }

    /*Set max file size in kb*/
    public void setMaxFileSize(long fileSize) {
        maxFileSize = fileSize;
    }

    public boolean wasUriSet(){
        return mWasUriSet;
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

    //Get default width
    public int getDefaultWidth(){
        HBRecorderCodecInfo hbRecorderCodecInfo = new HBRecorderCodecInfo();
        hbRecorderCodecInfo.setContext(context);
        return hbRecorderCodecInfo.getMaxSupportedWidth();
    }

    //Get default height
    public int getDefaultHeight(){
        HBRecorderCodecInfo hbRecorderCodecInfo = new HBRecorderCodecInfo();
        hbRecorderCodecInfo.setContext(context);
        return hbRecorderCodecInfo.getMaxSupportedHeight();
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
    public void startScreenRecording(Intent data, int resultCode) {
        this.resultCode = resultCode;
        startService(data);
    }

    /*Stop screen recording*/
    public void stopScreenRecording() {
        Intent service = new Intent(context, ScreenRecordService.class);
        context.stopService(service);
    }

    /*Pause screen recording*/
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void pauseScreenRecording(){
        if (service != null){
            isPaused = true;
            service.setAction("pause");
            context.startService(service);
        }
    }

    /*Pause screen recording*/
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void resumeScreenRecording(){
        if (service != null){
            isPaused = false;
            service.setAction("resume");
            context.startService(service);
        }
    }

    /*Check if video is paused*/
    public boolean isRecordingPaused(){
        return isPaused;
    }

    /*Check if recording is in progress*/
    public boolean isBusyRecording() {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (ScreenRecordService.class.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        }
        return false;
    }

    /*Change notification icon Drawable*/
    public void setNotificationSmallIcon(@DrawableRes int drawable) {
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), drawable);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        icon.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byteArray = stream.toByteArray();
    }

    /*Change notification icon using Vector Drawable*/
    public void setNotificationSmallIconVector(@DrawableRes int VectorDrawable) {
        vectorDrawable = VectorDrawable;
    }

    /*Change notification icon using byte[]*/
    public void setNotificationSmallIcon(byte[] bytes)  {
        byteArray = bytes;
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
        try {
            if (!mWasUriSet) {
                if (outputPath != null) {
                    File file = new File(outputPath);
                    String parent = file.getParent();
                    observer = new FileObserver(parent, HBRecorder.this);
                } else {
                    observer = new FileObserver(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)), HBRecorder.this);
                }
                observer.startWatching();
            }

            service = new Intent(context, ScreenRecordService.class);
            if (mWasUriSet) {
                service.putExtra("mUri", mUri.toString());
            }
            service.putExtra("code", resultCode);
            service.putExtra("data", data);
            service.putExtra("audio", isAudioEnabled);
            service.putExtra("width", mScreenWidth);
            service.putExtra("height", mScreenHeight);
            service.putExtra("density", mScreenDensity);
            service.putExtra("quality", isVideoHDEnabled);
            service.putExtra("path", outputPath);
            service.putExtra("fileName", fileName);
            service.putExtra("orientation", orientation);
            service.putExtra("audioBitrate", audioBitrate);
            service.putExtra("audioSamplingRate", audioSamplingRate);
            service.putExtra("notificationSmallBitmap", byteArray);
            service.putExtra("notificationSmallVector", vectorDrawable);
            service.putExtra("notificationTitle", notificationTitle);
            service.putExtra("notificationDescription", notificationDescription);
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
                        String errorListener = resultData.getString(ERROR_REASON_KEY);
                        String onComplete = resultData.getString(ON_COMPLETE_KEY);
                        int onStartCode = resultData.getInt(ON_START_KEY);
                        int errorCode = resultData.getInt(ERROR_KEY);
                        // There was an error
                        if (errorListener != null) {
                            //Stop countdown if it was set
                            stopCountDown();
                            if (!mWasUriSet) {
                                observer.stopWatching();
                            }
                            wasOnErrorCalled = true;
                            if ( errorCode > 0 ) {
                                hbRecorderListener.HBRecorderOnError(errorCode, errorListener);
                            } else {
                                hbRecorderListener.HBRecorderOnError(GENERAL_ERROR, errorListener);
                            }
                            try {
                                Intent mService = new Intent(context, ScreenRecordService.class);
                                context.stopService(mService);
                            }catch (Exception e){
                                // Can be ignored
                            }

                        }
                        // OnComplete was called
                        else if (onComplete != null){
                            //Stop countdown if it was set
                            stopCountDown();
                            //OnComplete for when Uri was passed
                            if (mWasUriSet && !wasOnErrorCalled) {
                                hbRecorderListener.HBRecorderOnComplete();
                            }
                            wasOnErrorCalled = false;
                        }
                        // OnStart was called
                        else if (onStartCode != 0){
                            hbRecorderListener.HBRecorderOnStart();
                            //Check if max duration was set and start count down
                            if (isMaxDurationSet){
                                startCountdown();
                            }
                        }
                        // OnPause/onResume was called
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            String onPause = resultData.getString(ON_PAUSE_KEY);
                            String onResume = resultData.getString(ON_RESUME_KEY);
                            if (onPause != null) {
                                hbRecorderListener.HBRecorderOnPause();
                            } else if (onResume != null) {
                                hbRecorderListener.HBRecorderOnResume();
                            }
                        }
                    }
                }
            });
            // Max file size
            service.putExtra(MAX_FILE_SIZE_KEY, maxFileSize);
            context.startService(service);
        }catch (Exception e){
            hbRecorderListener.HBRecorderOnError(0, Log.getStackTraceString(e));
        }

    }

    /*CountdownTimer for when max duration is set*/
    Countdown countDown = null;
    private void startCountdown() {
        countDown = new Countdown(maxDuration, 1000, 0) {
            @Override
            public void onTick(long timeLeft) {
                // Could add a callback to provide the time to the user
                // Will add if users request this
            }

            @Override
            public void onFinished() {
                onTick(0);
                // Since the timer is running on a different thread
                // UI chances should be called from the UI Thread
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            stopScreenRecording();
                            observer.stopWatching();
                            hbRecorderListener.HBRecorderOnComplete();
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onStopCalled() {
                // Currently unused, but might be helpful in the future
            }
        };
        countDown.start();
    }

    private void stopCountDown(){
        if (countDown != null) {
            countDown.stop();
        }
    }

    /*Complete callback method*/
    @Override
    public void onCompleteCallback() {
        observer.stopWatching();
        hbRecorderListener.HBRecorderOnComplete();
    }
}
