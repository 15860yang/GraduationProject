package com.snowman.graduationprojectclient.utils;

public class ByteDataUtil {

    public static int mergeByteToInt(byte l ,byte h){
        int res = 0;
        res |= (l & 0x00ff);
        res |= (h<<8 & 0xff00);
        return res;
    }
}
