package com.snowman.graduationprojectclient.utils.videoutil.h264;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.snowman.graduationprojectclient.Constant;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;

import static com.snowman.graduationprojectclient.utils.UtilKt.log;

public class H264Decoder {

    private MediaCodec mediaCodec;
    private SurfaceView mSurfaceView;
    private MediaCodec.BufferInfo mBufferInfo;

    private CodecStatus mCodecStatus;
    private ArrayBlockingQueue<byte[]> h264Queue = new ArrayBlockingQueue<>(20);

    public H264Decoder(SurfaceView surfaceView) {
        mSurfaceView = surfaceView;
        mBufferInfo = new MediaCodec.BufferInfo();
        mCodecStatus = CodecStatus.PREPARE;
        prepare();
    }

    private CodecStatus getCodecStatus() {
        return mCodecStatus;
    }

    public void put(byte[] data) {
        log("这是网络流来的数据哦");
        try {
            h264Queue.put(data);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void prepare() {
        //首先我们需要一个显示视频的SurfaceView
        //根据视频编码创建解码器，这里是解码AVC编码的视频
        try {
            mediaCodec = MediaCodec.createDecoderByType(MediaFormat.MIMETYPE_VIDEO_AVC);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //创建视频格式信息
        final MediaFormat mediaFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, Constant.Code.WIDTH, Constant.Code.HEIGHT);
//        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar);
//        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, Constant.WIDTH * Constant.HEIGHT * 5);
//        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, Constant.FRAME_RATE);
//        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);
        //！！！注意，这行代码需要界面绘制完成之后才可以调用！！！
        log("准备配置解码器");
        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mediaCodec.configure(mediaFormat, mSurfaceView.getHolder().getSurface(), null, 0);
                log("配置成功");
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }
        });
    }

    public void startDecode() {
        mediaCodec.start();
        mCodecStatus = CodecStatus.DECODING;
        new DecoderThread().start();
    }

    public void stopDecode() {
        mCodecStatus = CodecStatus.STOP;
    }

    private class DecoderThread extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    byte[] data = h264Queue.poll();
                    if (data == null || data.length == 0) {
                        continue;
                    }
                    //获取MediaCodec的输入流
                    ByteBuffer[] inputBuffers = mediaCodec.getInputBuffers();
                    //设置解码等待时间，0为不等待，-1为一直等待，其余为时间单位
                    int inputBufferIndex = mediaCodec.dequeueInputBuffer(0);
                    //填充数据到输入流
                    if (inputBufferIndex >= 0) {
                        ByteBuffer inputBuffer = mediaCodec.getInputBuffer(inputBufferIndex);
                        inputBuffer.put(data, 0, data.length);
                        mediaCodec.queueInputBuffer(inputBufferIndex, 0, data.length, System.nanoTime() / 1000, 0);
                    }
                    //解码数据到surface，实际项目中最好将以下代码放入另一个线程，不断循环解码以降低延迟
                    int outputBufferIndex = mediaCodec.dequeueOutputBuffer(mBufferInfo, 0);
                    if (outputBufferIndex >= 0) {
                        mediaCodec.releaseOutputBuffer(outputBufferIndex, true);
                    } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                        //此处可以或得到视频的实际分辨率，用以修正宽高比
                        //fixHW();
                    }
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
                if (mCodecStatus == CodecStatus.STOP) {
                    break;
                }
            }
            if (mediaCodec != null) {
                //停止解码，此时可以再次调用configure()方法
                mediaCodec.stop();
                //释放内存
                mediaCodec.release();
            }
        }
    }
}
