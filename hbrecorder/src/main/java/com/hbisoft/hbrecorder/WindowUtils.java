package com.hbisoft.hbrecorder;

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.util.Pair;

import androidx.window.layout.WindowMetrics;
import androidx.window.layout.WindowMetricsCalculator;

public class WindowUtils {

    /**
     * Returns the Recording video width and height which makes up resolution.
     *
     * @param targetHeight takes target height and calculates the width according to the target
     *                     height, while maintaining device aspect ratio.
     **/
    public static Pair<Integer, Integer> getCustomDimensions(int targetHeight, Context context) {

        Pair<Integer, Integer> deviceDimensions = getWindowSize(context);
        int deviceWidth = deviceDimensions.first;
        int deviceHeight = deviceDimensions.second;

        Log.i("TAG", "getCustomDimensions: " + deviceDimensions);

        float aspectRatio = (float) deviceWidth / deviceHeight;

        // Calculate the corresponding width for the target height
        int targetWidth = (int) (targetHeight * aspectRatio);

        // Ensure the width is divisible by 16 to prevent unsupported dimensions error.
        if (targetWidth % 16 != 0) {
            targetWidth += (16 - targetWidth % 16);
        }

        return new Pair<>(targetWidth, targetHeight);
    }

    private static Pair<Integer, Integer> getWindowSize(Context context) {
        WindowMetricsCalculator calculator = WindowMetricsCalculator.getOrCreate();
        WindowMetrics metrics = calculator.computeMaximumWindowMetrics(context);
        Rect bounds = metrics.getBounds();
        int width = bounds.width();
        int height = bounds.height();
        return new Pair<>(width, height);
    }

    /**
     * Returns the Recording video width and height which makes up resolution.
     * takes height and width for the video
     *
     * @param scaleFactor scales down the resolution (height & width) of the video according
     *                    to the value provided to this parameter should be between 0 and 1
     *                    otherwise default is 0.8 means 80%.
     **/
    public static Pair<Integer, Integer> getScaledDimensions(
            int maxWidth,
            int maxHeight,
            float scaleFactor
    ) {
        if (scaleFactor <= 0 || scaleFactor > 1) {
            scaleFactor = 0.8f; // Default value if invalid scaleFactor is passed
        }

        float aspectRatio = (float) maxWidth / maxHeight;

        int newWidth = (int) (maxWidth * scaleFactor);
        int newHeight = (int) (newWidth / aspectRatio);

        if (newHeight > (int) (maxHeight * scaleFactor)) {
            newHeight = (int) (maxHeight * scaleFactor);
            newWidth = (int) (newHeight * aspectRatio);
        }

        return new Pair<>(newWidth, newHeight);
    }
}
