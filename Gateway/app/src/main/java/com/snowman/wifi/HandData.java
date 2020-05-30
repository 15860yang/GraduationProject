package com.snowman.wifi;

public class HandData {

    public static int mergeByteToInt(byte l ,byte h){
        int res = 0;
        res |= (l & 0x00ff);
        res |= (h<<8 & 0xff00);
        return res;
    }
}
