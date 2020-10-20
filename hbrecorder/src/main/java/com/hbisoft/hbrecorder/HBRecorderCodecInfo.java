package com.hbisoft.hbrecorder;

import android.content.Context;
import android.content.res.Configuration;
import android.media.CamcorderProfile;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Range;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.Context.WINDOW_SERVICE;
import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static android.content.res.Configuration.ORIENTATION_PORTRAIT;

/**
 * Created by HBiSoft on 13 Aug 2019
 * Copyright (c) 2019 . All rights reserved.
 */

public class HBRecorderCodecInfo {

    int getMaxSupportedWidth(){
        RecordingInfo recordingInfo = getRecordingInfo();
        return recordingInfo.width;
    }

    int getMaxSupportedHeight(){
        RecordingInfo recordingInfo = getRecordingInfo();
        return recordingInfo.height;
    }

    private RecordingInfo getRecordingInfo() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        wm.getDefaultDisplay().getRealMetrics(displayMetrics);
        int displayWidth = displayMetrics.widthPixels;
        int displayHeight = displayMetrics.heightPixels;
        int displayDensity = displayMetrics.densityDpi;

        Configuration configuration = context.getResources().getConfiguration();
        boolean isLandscape = configuration.orientation == ORIENTATION_LANDSCAPE;

        CamcorderProfile camcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
        int cameraWidth = camcorderProfile != null ? camcorderProfile.videoFrameWidth : -1;
        int cameraHeight = camcorderProfile != null ? camcorderProfile.videoFrameHeight : -1;
        int cameraFrameRate = camcorderProfile != null ? camcorderProfile.videoFrameRate : 30;


        return calculateRecordingInfo(displayWidth, displayHeight, displayDensity, isLandscape,
                cameraWidth, cameraHeight, cameraFrameRate, 100);
    }

    private Context context;

    public void setContext(Context c) {
        context = c;
    }

    static final class RecordingInfo {
        final int width;
        final int height;
        final int frameRate;
        final int density;

        RecordingInfo(int width, int height, int frameRate, int density) {
            this.width = width;
            this.height = height;
            this.frameRate = frameRate;
            this.density = density;
        }
    }

    static RecordingInfo calculateRecordingInfo(int displayWidth, int displayHeight, int displayDensity, boolean isLandscapeDevice, int cameraWidth, int cameraHeight, int cameraFrameRate, int sizePercentage) {
        // Scale the display size before any maximum size calculations.
        displayWidth = displayWidth * sizePercentage / 100;
        displayHeight = displayHeight * sizePercentage / 100;

        if (cameraWidth == -1 && cameraHeight == -1) {
            // No cameras. Fall back to the display size.
            return new RecordingInfo(displayWidth, displayHeight, cameraFrameRate, displayDensity);
        }

        int frameWidth = isLandscapeDevice ? cameraWidth : cameraHeight;
        int frameHeight = isLandscapeDevice ? cameraHeight : cameraWidth;
        if (frameWidth >= displayWidth && frameHeight >= displayHeight) {
            // Frame can hold the entire display. Use exact values.
            return new RecordingInfo(displayWidth, displayHeight, cameraFrameRate, displayDensity);
        }

        // Calculate new width or height to preserve aspect ratio.
        if (isLandscapeDevice) {
            frameWidth = displayWidth * frameHeight / displayHeight;
        } else {
            frameHeight = displayHeight * frameWidth / displayWidth;
        }
        return new RecordingInfo(frameWidth, frameHeight, cameraFrameRate, displayDensity);
    }


    // ALL PUBLIC METHODS
    // This is for testing purposes only

    // Select the default video encoder
    // This will only return the default video encoder, it does not mean that the encoder supports a particular set of parameters
    public final MediaCodecInfo selectVideoCodec(final String mimeType) {
        MediaCodecInfo result = null;
        // get the list of available codecs
        final int numCodecs = MediaCodecList.getCodecCount();
        for (int i = 0; i < numCodecs; i++) {
            final MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);
            if (!codecInfo.isEncoder()) {    // skipp decoder
                continue;
            }
            String[] types = codecInfo.getSupportedTypes();
            for (String type : types) {
                if (type.equalsIgnoreCase(mimeType)) {
                    return codecInfo;
                }
            }
        }
        return result;
    }

    private String selectCodecByMime(String mimeType) {
        int numCodecs = MediaCodecList.getCodecCount();
        for (int i = 0; i < numCodecs; i++) {
            MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);
            if (!codecInfo.isEncoder()) {
                continue;
            }
            String[] types = codecInfo.getSupportedTypes();
            for (String type : types) {
                if (type.equalsIgnoreCase(mimeType)) {
                    return codecInfo.getName();
                }
            }
        }
        return "Mime not supported";
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private MediaCodecInfo selectDefaultCodec() {
        MediaCodecInfo result = null;
        // get the list of available codecs
        final int numCodecs = MediaCodecList.getCodecCount();
        for (int i = 0; i < numCodecs; i++) {
            final MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);
            if (!codecInfo.isEncoder()) {    // skipp decoder
                continue;
            }
            final String[] types = codecInfo.getSupportedTypes();
            for (int j = 0; j < types.length; j++) {
                if (types[j].contains("video")){
                    MediaCodecInfo.CodecCapabilities codecCapabilities = codecInfo.getCapabilitiesForType(types[j]);
                    boolean formatSup = codecCapabilities.isFormatSupported(codecCapabilities.getDefaultFormat());
                    if (formatSup) {
                        return codecInfo;
                    }
                }
            }
        }
        return result;
    }

    // Get the default video encoder name
    // The default one will be returned first
    public String getDefaultVideoEncoderName(String mimeType){
        String defaultEncoder = "";
        try {
            defaultEncoder = selectCodecByMime(mimeType);
        }catch (Exception e){
            e.printStackTrace();
        }
        return defaultEncoder;
    }

    // Get the default video format
    // The default one will be returned first
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public String getDefaultVideoFormat(){
        String supported = "";
        try {
            final MediaCodecInfo codecInfo = selectDefaultCodec();
            if (codecInfo != null) {
                String[] types = codecInfo.getSupportedTypes();
                for (String type : types) {
                    if (type.contains("video")) {
                        MediaCodecInfo.CodecCapabilities codecCapabilities = codecInfo.getCapabilitiesForType(type);
                        String result = codecCapabilities.getDefaultFormat().toString();
                        return returnTypeFromMime(result.substring(result.indexOf("=") + 1, result.indexOf(",")));
                    }
                }
            }else {
                supported = "null";
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return supported;
    }

    private String returnTypeFromMime(String mimeType){
        switch (mimeType) {
            case "video/MP2T":
                return "MPEG_2_TS";
            case "video/mp4v-es":
                return "MPEG_4";
            case "video/mp4v":
                return "MPEG_4";
            case "video/mp4":
                return "MPEG_4";
            case "video/avc":
                return "MPEG_4";
            case "video/3gpp":
                return "THREE_GPP";
            case "video/webm":
                return "WEBM";
            case "video/x-vnd.on2.vp8":
                return "WEBM";
        }
        return "";
    }

    // Example usage - isSizeAndFramerateSupported(hbrecorder.getWidth(), hbrecorder.getHeight(), 30, "video/mp4", ORIENTATION_PORTRAIT);
    // int width - The width of the view to be recorder
    // int height - The height of the view to be recorder
    // String mimeType - for ex. video/mp4
    // int orientation - ORIENTATION_PORTRAIT or ORIENTATION_LANDSCAPE
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public boolean isSizeAndFramerateSupported(int width, int height, int fps, String mimeType, int orientation){
        boolean supported = false;
        try {
            final MediaCodecInfo codecInfo = selectVideoCodec(mimeType);
            String[] types = codecInfo.getSupportedTypes();
            for (String type : types) {
                if (type.contains("video")) {
                    MediaCodecInfo.CodecCapabilities codecCapabilities = codecInfo.getCapabilitiesForType(type);
                    MediaCodecInfo.VideoCapabilities videoCapabilities = codecCapabilities.getVideoCapabilities();

                    // Flip around the width and height in ORIENTATION_PORTRAIT because android's default orientation is ORIENTATION_LANDSCAPE
                    if (ORIENTATION_PORTRAIT == orientation) {
                        supported = videoCapabilities.areSizeAndRateSupported(height, width, fps);
                    }else {
                        supported = videoCapabilities.areSizeAndRateSupported(width, height, fps);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return supported;
    }

    public boolean isMimeTypeSupported(String mimeType){
        try {
            final MediaCodecInfo codecInfo = selectVideoCodec(mimeType);
            String[] types = codecInfo.getSupportedTypes();
            for (String type : types) {
                if (type.contains("video")) {
                    //ignore
                }
            }
        }catch (Exception e){
            return false;
        }
        return true;
    }

    // Check if a particular size is supported
    // Provide width and height in Portrait mode, for example. isSizeSupported(1080, 1920, "video/mp4");
    // We do this because android's default orientation is landscape, so we have to flip around the width and height
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public boolean isSizeSupported(int width, int height, String mimeType){
        boolean supported = false;
        try {
            final MediaCodecInfo codecInfo = selectVideoCodec(mimeType);
            String[] types = codecInfo.getSupportedTypes();
            for (String type : types) {
                if (type.contains("video")) {
                    MediaCodecInfo.CodecCapabilities codecCapabilities = codecInfo.getCapabilitiesForType(type);
                    MediaCodecInfo.VideoCapabilities videoCapabilities = codecCapabilities.getVideoCapabilities();
                    supported = videoCapabilities.isSizeSupported(height, width);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return supported;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public double getMaxSupportedFrameRate(int width, int height, String mimeType){
        double maxFPS = 0;
        try {
            final MediaCodecInfo codecInfo = selectVideoCodec(mimeType);
            String[] types = codecInfo.getSupportedTypes();
            for (String type : types) {
                if (type.contains("video")) {
                    MediaCodecInfo.CodecCapabilities codecCapabilities = codecInfo.getCapabilitiesForType(type);
                    MediaCodecInfo.VideoCapabilities videoCapabilities = codecCapabilities.getVideoCapabilities();
                    Range<Double> bit = videoCapabilities.getSupportedFrameRatesFor(height, width);
                    maxFPS = bit.getUpper();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return maxFPS;
    }

    // Get the max supported bitrate for a particular mime type
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public int getMaxSupportedBitrate(String mimeType){
        int bitrate = 0;
        try {
            final MediaCodecInfo codecInfo = selectVideoCodec(mimeType);
            String[] types = codecInfo.getSupportedTypes();
            for (String type : types) {
                if (type.contains("video")) {
                    MediaCodecInfo.CodecCapabilities codecCapabilities = codecInfo.getCapabilitiesForType(type);
                    MediaCodecInfo.VideoCapabilities videoCapabilities = codecCapabilities.getVideoCapabilities();
                    Range<Integer> bit = videoCapabilities.getBitrateRange();

                    bitrate = bit.getUpper();

                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return bitrate;
    }

    // Get supported video formats
    ArrayList<String> supportedVideoFormats = new ArrayList<>();
    public ArrayList<String> getSupportedVideoFormats(){
        String[] allFormats = {"video/MP2T", "video/mp4v-es", "video/m4v", "video/mp4", "video/avc", "video/3gpp", "video/webm", "video/x-vnd.on2.vp8"};

        for (String allFormat : allFormats) {
            checkSupportedVideoFormats(allFormat);
        }
        return supportedVideoFormats;
    }
    private void checkSupportedVideoFormats(String mimeType){
        // get the list of available codecs
        final int numCodecs = MediaCodecList.getCodecCount();
        LOOP:
        for (int i = 0; i < numCodecs; i++) {
            final MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);
            if (!codecInfo.isEncoder()) {    // skipp decoder
                continue;
            }
            final String[] types = codecInfo.getSupportedTypes();
            for (int j = 0; j < types.length; j++) {
                if (types[j].contains("video")){
                    switch (mimeType) {
                        case "video/MP2T":
                            supportedVideoFormats.add("MPEG_2_TS");
                            break LOOP;
                        case "video/mp4v-es":
                            if(!supportedVideoFormats.contains("MPEG_4")) {
                                supportedVideoFormats.add("MPEG_4");
                            }
                            break LOOP;
                        case "video/mp4v":
                            if(!supportedVideoFormats.contains("MPEG_4")) {
                                supportedVideoFormats.add("MPEG_4");
                            }
                            break LOOP;
                        case "video/mp4":
                            if(!supportedVideoFormats.contains("MPEG_4")) {
                                supportedVideoFormats.add("MPEG_4");
                            }
                            break LOOP;
                        case "video/avc":
                            if(!supportedVideoFormats.contains("MPEG_4")) {
                                supportedVideoFormats.add("MPEG_4");
                            }
                            break LOOP;
                        case "video/3gpp":
                            supportedVideoFormats.add("THREE_GPP");
                            break LOOP;

                        case "video/webm":
                            if(!supportedVideoFormats.contains("WEBM")) {
                                supportedVideoFormats.add("WEBM");
                            }
                            break LOOP;
                        case "video/video/x-vnd.on2.vp8":
                            if(!supportedVideoFormats.contains("WEBM")) {
                                supportedVideoFormats.add("WEBM");
                            }
                            break LOOP;

                    }
                }
            }
        }
    }

    // Get supported audio formats
    ArrayList<String> supportedAudioFormats = new ArrayList<>();
    public ArrayList<String> getSupportedAudioFormats(){
        String[] allFormats = {"audio/amr_nb", "audio/amr_wb", "audio/x-hx-aac-adts", "audio/ogg"};

        for (String allFormat : allFormats) {
            checkSupportedAudioFormats(allFormat);
        }
        return supportedAudioFormats;
    }
    private void checkSupportedAudioFormats(String mimeType){
        // get the list of available codecs
        final int numCodecs = MediaCodecList.getCodecCount();
        LOOP:
        for (int i = 0; i < numCodecs; i++) {
            final MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);
            if (!codecInfo.isEncoder()) {    // skipp decoder
                continue;
            }
            final String[] types = codecInfo.getSupportedTypes();
            label:
            for (int j = 0; j < types.length; j++) {
                if (types[j].contains("audio")){
                    switch (mimeType) {
                        case "audio/amr_nb":
                            supportedAudioFormats.add("AMR_NB");
                            break LOOP;
                        case "audio/amr_wb":
                            supportedAudioFormats.add("AMR_WB");
                            break LOOP;
                        case "audio/x-hx-aac-adts":
                            supportedAudioFormats.add("AAC_ADTS");
                            break LOOP;
                        case "audio/ogg":
                            supportedAudioFormats.add("OGG");
                            break LOOP;
                    }
                }
            }
        }
    }

    // Get supported video mime types
    HashMap<String, String> mVideoMap= new HashMap<>();
    public HashMap<String, String> getSupportedVideoMimeTypes(){
        checkIfSupportedVideoMimeTypes();
        return mVideoMap;
    }
    private void checkIfSupportedVideoMimeTypes(){
        // get the list of available codecs
        final int numCodecs = MediaCodecList.getCodecCount();
        for (int i = 0; i < numCodecs; i++) {
            final MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);
            if (!codecInfo.isEncoder()) {    // skipp decoder
                continue;
            }
            final String[] types = codecInfo.getSupportedTypes();
            for (String type : types) {
                if (type.contains("video")) {
                    mVideoMap.put(codecInfo.getName(), type);
                }
            }
        }
    }

    // Get supported audio mime types
    HashMap<String, String> mAudioMap= new HashMap<>();
    public HashMap<String, String> getSupportedAudioMimeTypes(){
        checkIfSupportedAudioMimeTypes();
        return mAudioMap;
    }
    private void checkIfSupportedAudioMimeTypes(){
        // get the list of available codecs
        final int numCodecs = MediaCodecList.getCodecCount();
        for (int i = 0; i < numCodecs; i++) {
            final MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);
            if (!codecInfo.isEncoder()) {    // skipp decoder
                continue;
            }
            final String[] types = codecInfo.getSupportedTypes();
            for (String type : types) {
                if (type.contains("audio")) {
                    mAudioMap.put(codecInfo.getName(), type);
                }
            }
        }
    }

}
