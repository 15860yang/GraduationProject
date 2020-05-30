package com.snowman.graduationprojectclient.utils.videoutil.auxiliary;

import android.os.Environment;


import com.snowman.graduationprojectclient.utils.UtilKt;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class OutPutManager implements DataStream {

    private OutPutManager() {
    }

    private static OutPutManager instance = null;

    private OutputStream mOutputStream = null;
    private InputStream mInputStream = null;

    public static OutPutManager getInstance() {
        if (instance == null) {
            synchronized (OutPutManager.class) {
                if (instance == null) {
                    instance = new OutPutManager();
                }
            }
        }
        return instance;
    }

    @Override
    public void data(byte[] data) {
        try {
            UtilKt.log("输出到文件");
            mOutputStream.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopOutput() {
        try {
            mOutputStream.flush();
            mOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public OutPutManager prepare() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/test.mp4";
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
        try {
            mOutputStream = new BufferedOutputStream(new FileOutputStream(file));
            UtilKt.log("文件创建成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }
}
