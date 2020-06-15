package com.snowman.wifi.utils;

import android.widget.TextView;

public class EnvironmentalDigitalConversionUtils {

    public static void ConversionNumberToLightIntensity(TextView textView, int num) {
        //15   168
        String res;
        if (num > 168) {
            res = "强光";
        } else if (num > 80) {
            res = "明亮";
        } else {
            res = "弱光";
        }
        textView.setText(res);
    }

    public static void ConversionNumberToRainFall(TextView textView, int num) {
        String res;
        //max 331   min  139
        if (num > 300) {
            res = "无雨";
        } else if (num > 200) {
            res = "小雨";
        } else if (num > 150) {
            res = "中雨";
        } else {
            res = "大雨";
        }
        textView.setText(res);
    }
}

