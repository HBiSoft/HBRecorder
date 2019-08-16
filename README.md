# HBRecorder
Lightweight screen and audio capturing Android library

![hbicon](https://user-images.githubusercontent.com/35602540/63006287-7df6e500-be7e-11e9-82b6-40814d8201df.png)


**Requires API level 21>**


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
    implementation 'com.github.HBiSoft:HBRecorder:0.1.2'
}
```
    

**Implementing the library:**
--- 
1. In your `Activity`, first implement `HBRecorder`, as shown below:

```java
public class MainActivity extends AppCompatActivity implements HBRecorderListener {
```

2. `Alt+Enter` to implement the following method:

```java
@Override
public void HBRecorderOnComplete() {
    //This is called once the file was created
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
<!--This is only necessary if you are displaying notifications-->
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
```
</br>    
That's it `HBRecorder` is now ready to be used.


When ever you want to start capturing your screen, it is important you do it as shown below:
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
            //It is important to call this before starting the recording
            hbRecorder.onActivityResult(resultCode, data, this);
            //Start screen recording
            hbRecorder.startScreenRecording(data);

        }
    }
}
```


All available methods:
---
```java
// Set the output path as a String
// Defaults to - Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
hbrecorder.setOutputPath(String);
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
// Stop screen recording
hbrecorder.stopScreenRecording();
// Check if recording is in progress
hbrecorder.isBusyRecording();
// Enable/Disable notification while recording by passing a boolean
// Defaults to false
hbrecorder.shouldShowNotification(boolean);
// Set notification icon by passing, for example R.drawable.myicon
// Defaults to R.drawable.icon
hbrecorder.setNotificationSmallIcon(int);
// Set notification title 
// Defaults to "Recording your screen"
hbrecorder.setNotificationTitle(String);
// Set notification description
// Defaults to "Drag down to stop the recording"
hbrecorder.setNotificationDescription(String);
//Set notification stop button text
// Defaults to "STOP RECORDING"
hbrecorder.setNotificationButtonText(String);
```
    

    
    
    
