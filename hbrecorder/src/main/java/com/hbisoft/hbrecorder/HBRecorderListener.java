package com.hbisoft.hbrecorder;

import android.os.Build;

import androidx.annotation.RequiresApi;

public interface HBRecorderListener {
    void HBRecorderOnStart();
    void HBRecorderOnComplete();
    @RequiresApi(api = Build.VERSION_CODES.N)
    void HBRecorderOnPause();
    @RequiresApi(api = Build.VERSION_CODES.N)
    void HBRecorderOnResume();
    void HBRecorderOnError(int errorCode, String reason);
}
