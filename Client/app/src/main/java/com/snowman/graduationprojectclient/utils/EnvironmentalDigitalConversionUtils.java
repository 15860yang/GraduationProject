package com.snowman.graduationprojectclient.utils;

import android.widget.TextView;

public class EnvironmentalDigitalConversionUtils {

    public static void ConversionNumberToLightIntensity(TextView textView, int num) {
        textView.setText(num+"");
    }

    public static void ConversionNumberToRainFall(TextView textView, int num) {
        if (num > 255) {

        }

        textView.setText(num+"");
    }

}

