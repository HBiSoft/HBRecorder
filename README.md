# HBRecorder
[![](https://jitpack.io/v/HBiSoft/HBRecorder.svg)](https://jitpack.io/#HBiSoft/HBRecorder)
[![Android Arsenal]( https://img.shields.io/badge/Android%20Arsenal-HBRecorder-green.svg?style=flat )]( https://android-arsenal.com/details/1/7897 )

<p align="center">Lightweight screen and audio recording Android library </br></br><b>Requires API level 21></b></p>

<p align="center"><img src="https://user-images.githubusercontent.com/35602540/63006287-7df6e500-be7e-11e9-82b6-40814d8201df.png"></p>

</br>

---

**<p align="center">If you want to thank me for this library, you can do so below:</p>**

<p align="center"><a href="https://www.buymeacoffee.com/HBiSoft" target="_blank" ><img src="https://www.buymeacoffee.com/assets/img/custom_images/orange_img.png" alt="Buy Me A Coffee" style="height: 41px !important;width: 164px !important;box-shadow: 0px 3px 2px 0px rgba(190, 190, 190, 0.5) !important;-webkit-box-shadow: 0px 3px 2px 0px rgba(190, 190, 190, 0.5) !important;" ></a></p>

---

<h2 align="center"><b>Demo:</b></h2>

<p align="center">Download the demo app  <a href="https://github.com/HBiSoft/HBRecorder/releases/download/2.0.5/HBRecorderDemo.apk"><nobr>here</nobr></a></p>

<p align="center"><img src="https://user-images.githubusercontent.com/35602540/66485516-3e78fd80-eaa9-11e9-9fea-f59bfa7c1389.gif" width="247" height="480" </p>

</br></br>


<h2 align="center">Take part in the poll</h2>

<p align="center">Let other developers know what version you have tested:</p></br></br>

<p align="center">Android 12 was added to the poll on 30 June 2022</p></br>
<p align="center"><a href="https://api.gh-polls.com/poll/01G6R5G13N1Z7D75SE8CMCW8ZN/Android%2012/vote"><img src="https://api.gh-polls.com/poll/01G6R5G13N1Z7D75SE8CMCW8ZN/Android%2012" alt=""></a></p>
<p align="center"><a href="https://api.gh-polls.com/poll/01EEA8FRQ42BVQ4XB6JGDAM8BH/Android%2011/vote"><img src="https://api.gh-polls.com/poll/01EEA8FRQ42BVQ4XB6JGDAM8BH/Android%2011" alt=""></a></p>
<p align="center"><a href="https://api.gh-polls.com/poll/01EEA8FRQ42BVQ4XB6JGDAM8BH/Android%2010/vote"><img src="https://api.gh-polls.com/poll/01EEA8FRQ42BVQ4XB6JGDAM8BH/Android%2010" alt=""></a></p>
<p align="center"><a href="https://api.gh-polls.com/poll/01EEA8FRQ42BVQ4XB6JGDAM8BH/Android%209/vote"><img src="https://api.gh-polls.com/poll/01EEA8FRQ42BVQ4XB6JGDAM8BH/Android%209" alt=""></a></p>
<p align="center"><a href="https://api.gh-polls.com/poll/01EEA8FRQ42BVQ4XB6JGDAM8BH/Android%208/vote"><img src="https://api.gh-polls.com/poll/01EEA8FRQ42BVQ4XB6JGDAM8BH/Android%208" alt=""></a></p>
<p align="center"><a href="https://api.gh-polls.com/poll/01EEA8FRQ42BVQ4XB6JGDAM8BH/Android%207/vote"><img src="https://api.gh-polls.com/poll/01EEA8FRQ42BVQ4XB6JGDAM8BH/Android%207" alt=""></a></p>
<p align="center"><a href="https://api.gh-polls.com/poll/01EEA8FRQ42BVQ4XB6JGDAM8BH/Android%206/vote"><img src="https://api.gh-polls.com/poll/01EEA8FRQ42BVQ4XB6JGDAM8BH/Android%206" alt=""></a></p>
<p align="center"><a href="https://api.gh-polls.com/poll/01EEA8FRQ42BVQ4XB6JGDAM8BH/Android%205/vote"><img src="https://api.gh-polls.com/poll/01EEA8FRQ42BVQ4XB6JGDAM8BH/Android%205" alt=""></a></p>

</br>
    

**Adding the library to your project:**
---
Add the following in your root build.gradle at the end of repositories:

```java
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }	    
    }
}
```
    
Implement library in your app level build.gradle:

```java
dependencies {
    implementation 'com.github.HBiSoft:HBRecorder:2.0.5'
}
```
    

**Implementing the library:**
--- 
1. In your `Activity`, first implement `HBRecorder`, as shown below:

```java
public class MainActivity extends AppCompatActivity implements HBRecorderListener {
```

2. `Alt+Enter` to implement the following methods:

```java
@Override
public void HBRecorderOnStart() {
    //When the recording starts
}

@Override
public void HBRecorderOnComplete() {
    //After file was created
}
@Override
public void HBRecorderOnError(int errorCode) {
    //When an error occurs
}
```
    
3. Init `HBRecorder` as shown below:
```java
public class MainActivity extends AppCompatActivity implements HBRecorderListener {
    HBRecorder hbRecorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);     

        //Init HBRecorder
        hbRecorder = new HBRecorder(this, this);        

}
```
    
4. Add the following permissions in your manifest:
```java
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_INTERNAL_STORAGE" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
```   

That's it, `HBRecorder` is now ready to be used.

---

When you want to start capturing your screen, it is important you do it as shown below:
---
```java
private void startRecordingScreen() {
    MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
    Intent permissionIntent = mediaProjectionManager != null ? mediaProjectionManager.createScreenCaptureIntent() : null;
    startActivityForResult(permissionIntent, SCREEN_RECORD_REQUEST_CODE);
}

@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == SCREEN_RECORD_REQUEST_CODE) {
        if (resultCode == RESULT_OK) {
            //Start screen recording
            hbRecorder.startScreenRecording(data, resultCode);

        }
    }
}
```

All available methods:
---
```java
// Set the output path as a String
// Only use this on devices running Android 9 and lower or you have to add android:requestLegacyExternalStorage="true" in your manifest
// Defaults to - Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
hbrecorder.setOutputPath(String);
// Set output Uri
// Only use this on devices running Android 10>
// When setting a Uri ensure you pass the same name to HBRecorder as what you set in ContentValues (DISPLAY_NAME and TITLE)
hbRecorder.setOutputUri(Uri);
// Set file name as String
// Defaults to - quality + time stamp. For example HD-2019-08-14-10-09-58.mp4
hbrecorder.setFileName(String);
// Set audio bitrate as int
// Defaults to - 128000
hbrecorder.setAudioBitrate(int);
// Set audio sample rate as int 
// Defaults to - 44100
hbrecorder.setAudioSamplingRate(int);
// Enable/Disable audio
// Defaults to true
hbrecorder.isAudioEnabled(boolean);
// Enable/Disable HD Video
// Defaults to true
hbrecorder.recordHDVideo(boolean);
// Get file path as String
hbrecorder.getFilePath();
// Get file name as String
hbrecorder.getFileName();
// Start recording screen by passing it as Intent inside onActivityResult
hbrecorder.startScreenRecording(Intent);
// Pause screen recording (only available for devices running 24>)
hbrecorder.pauseScreenRecording();
// Resume screen recording
hbreccorder.resumeScreenRecording();
// Stop screen recording
hbrecorder.stopScreenRecording();
// Check if recording is in progress
hbrecorder.isBusyRecording();
// Set notification icon by passing, for example R.drawable.myicon
// Defaults to R.drawable.icon
hbrecorder.setNotificationSmallIcon(int);
// Set notification icon using byte array
hbrecorder.setNotificationSmallIcon(byte[]);
// Set notification icon using vector drawable
hbRecorder.setNotificationSmallIconVector(vector);
// Set notification title
// Defaults to "Recording your screen"
hbrecorder.setNotificationTitle(String);
// Set notification description
// Defaults to "Drag down to stop the recording"
hbrecorder.setNotificationDescription(String);
// Set notification stop button text
// Defaults to "STOP RECORDING"
hbrecorder.setNotificationButtonText(String);
// Set output orientation (in degrees)
hbrecorder.setOrientationHint(int);
// Set max output file size
hbrecorder.setMaxFileSize(long);
// Set max time (in seconds)
hbRecorder.setMaxDuration(int);
```

Custom setting:
---
When you want to enable custom settings you must first call:
```java
hbRecorder.enableCustomSettings();
```
Then you can set the following:
```java
//MUST BE ONE OF THE FOLLOWING - https://developer.android.com/reference/android/media/MediaRecorder.AudioSource.html
hbRecorder.setAudioSource(String);
//MUST BE ONE OF THE FOLLOWING - https://developer.android.com/reference/android/media/MediaRecorder.VideoEncoder.html
hbRecorder.setVideoEncoder(String);
//If nothing is provided, it will select the highest value supported by your device
hbRecorder.setScreenDimensions(HeightInPx, WidthInPx);
//Frame rate is device dependent
//You can use Camcoderprofile to determine the frame rate
hbRecorder.setVideoFrameRate(int);
//The bitrate is also dependent on the device and the frame rate that is set
hbRecorder.setVideoBitrate(int);
//MUST BE ONE OF THE FOLLOWING - https://developer.android.com/reference/android/media/MediaRecorder.OutputFormat.html
hbRecorder.setOutputFormat(String);
```

---
It is important to note that limitations are device dependent. It is best to set the video encoder to "DEFAULT" and let `MediaRecorder` pick the best encoder.

In the demo app you will have the option to test different video encoders, bitrate, frame rate and output formats. If your device does not support any of the parameters you have selected `HBRecorderOnError` will be called.
