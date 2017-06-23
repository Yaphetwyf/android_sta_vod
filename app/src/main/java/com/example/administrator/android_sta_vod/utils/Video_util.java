package com.example.administrator.android_sta_vod.utils;

import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.broadcast.android.android_sta_jni_official.ndk_wrapper;
import com.example.administrator.android_sta_vod.app.My_application;
import com.example.administrator.android_sta_vod.endecode.EncoderDebugger;
import com.example.administrator.android_sta_vod.endecode.H264Decoder;
import com.example.administrator.android_sta_vod.endecode.NV21Convertor;
import com.example.administrator.android_sta_vod.endecode.Packet;
import com.example.administrator.android_sta_vod.endecode.Util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by AVSZ on 2017/3/3.
 */

public class Video_util {

    private int framerate = 25;
    private int width = 640;
    private int height = 480;
    private int bitrate;
    private String path = "/mnt/sdcard/videoutil.mp4";
    private NV21Convertor mConvertor;
    private MediaCodec mMediaCodec;
    private int mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;//Camera.CameraInfo.CAMERA_FACING_BACK;
    private Camera mCamera;
    private boolean started = false;
    private BlockingQueue<Packet> h264dataQueue;
    private boolean h264playing = true;
    private SurfaceView svTalkback;
    private static final String tag = "VIDEO_UTIL";
    public static long video_capture_time = System.currentTimeMillis();
    private ByteBuffer outputBuffer;
    byte[] mPpsSps=new byte[0];
    public Video_util(SurfaceView surfaceView) {
        this.svTalkback = surfaceView;
        this.h264dataQueue = new ArrayBlockingQueue<Packet>(10000);

    }

    public void initMediaCodec() {
        int dgree = getDgree();
        // framerate = 25;
        bitrate = 2 * width * height * framerate / 20;
        EncoderDebugger debugger = EncoderDebugger.debug(Ui_utils.get_context(), width, height);
        mConvertor = debugger.getNV21Convertor();
        try {
            mMediaCodec = MediaCodec.createByCodecName(debugger.getEncoderName());
            Log.d("mMediaCodec", mMediaCodec.toString());
            MediaFormat mediaFormat;
            if (dgree == 0) {
                mediaFormat = MediaFormat.createVideoFormat("video/avc", height, width);
            } else {
                mediaFormat = MediaFormat.createVideoFormat("video/avc", width, height);
            }
            mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, bitrate);
            mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, framerate);
            mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT,
                    debugger.getEncoderColorFormat());
            mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 5);
            mMediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            mMediaCodec.start();
            Log.d(tag, "initMediaCodec");
        } catch (Exception e) {
            Log.e("mMediaCodec", "" + e.getMessage());
            e.printStackTrace();
        }
    }

    private int getDgree() {
        int rotation = 0;
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break; // Natural orientation
            case Surface.ROTATION_90:
                degrees = 90;
                break; // Landscape left
            case Surface.ROTATION_180:
                degrees = 180;
                break;// Upside down
            case Surface.ROTATION_270:
                degrees = 270;
                break;// Landscape right
        }
        //return degrees;
        return 90;
    }

    public static int[] determineMaximumSupportedFramerate(Camera.Parameters parameters) {
        int[] maxFps = new int[]{0, 0};
        List<int[]> supportedFpsRanges = parameters.getSupportedPreviewFpsRange();
        for (Iterator<int[]> it = supportedFpsRanges.iterator(); it.hasNext(); ) {
            int[] interval = it.next();
            if (interval[1] > maxFps[1] || (interval[0] > maxFps[0] && interval[1] == maxFps[1])) {
                maxFps = interval;
            }
        }
        return maxFps;
    }

    //init camera
    public boolean ctreateCamera(SurfaceHolder surfaceHolder) {
        try {
            mCamera = Camera.open(mCameraId);
            Camera.Parameters parameters = mCamera.getParameters();
            int[] max = determineMaximumSupportedFramerate(parameters);
            Camera.CameraInfo camInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(mCameraId, camInfo);
            int cameraRotationOffset = camInfo.orientation;
//            int rotate = (360 + cameraRotationOffset - getDgree()) % 360;
             int rotate = (360 + cameraRotationOffset - 180) % 360;
            parameters.setRotation(rotate);
            parameters.setPreviewFormat(ImageFormat.NV21);
            List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
            parameters.setPreviewSize(width, height);
            parameters.setPreviewFpsRange(max[0], max[1]);
            mCamera.setParameters(parameters);
            mCamera.autoFocus(null);
            int displayRotation;
            displayRotation = (cameraRotationOffset - getDgree() + 360) % 360;
            mCamera.setDisplayOrientation(0);//设置预览窗口方向
            mCamera.setPreviewDisplay(surfaceHolder);
            Log.d(tag, "ctreateCamera");
            return true;
        } catch (Exception e) {
            Log.d(tag, "create_camera" + e.getMessage());
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String stack = sw.toString();
            destroyCamera();
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 销毁Camera
     */
    public synchronized void destroyCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            try {
                mCamera.release();
            } catch (Exception e) {

            }
            if (h264Decode != null) {
                h264Decode.DecoderClose();
                h264Decode = null;
            }
            mCamera = null;
            Log.d(tag, "destroyCamera");
        }
    }

    /**
     * 开启预览
     */
    public synchronized void startPreview() {
        if (mCamera != null && !started) {
            mCamera.startPreview();
            int previewFormat = mCamera.getParameters().getPreviewFormat();
            Camera.Size previewSize = mCamera.getParameters().getPreviewSize();
            int size = previewSize.width * previewSize.height
                    * ImageFormat.getBitsPerPixel(previewFormat)
                    / 8;
            mCamera.addCallbackBuffer(new byte[size]);
            mCamera.setPreviewCallbackWithBuffer(previewCallback);
            started = true;
            user_send_video_thread.start();
            Log.d(tag, "startPreview");

        }
    }

    /**
     * 停止预览
     */
    public synchronized void stopPreview() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.setPreviewCallbackWithBuffer(null);
            started = false;
            h264playing = false;
            user_send_video_thread.interrupt();
            try {
                user_send_video_thread.join(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mMediaCodec.stop();
            mMediaCodec.release();
            mMediaCodec = null;
            Log.d(tag, "stopPreview");
        }
    }

    Camera.PreviewCallback previewCallback =  new Camera.PreviewCallback() {


        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            if (data == null) {
                return;
            }
            ByteBuffer[] inputBuffers = mMediaCodec.getInputBuffers();
            ByteBuffer[] outputBuffers = mMediaCodec.getOutputBuffers();
            byte[] dst = new byte[data.length];
            Camera.Size previewSize = mCamera.getParameters().getPreviewSize();
            if (getDgree() == 0) {
                dst = Util.rotateNV21Degree90(data, previewSize.width, previewSize.height);
            } else {
                dst = data;
            }

            try {
                int bufferIndex = mMediaCodec.dequeueInputBuffer(5000000);
                if (bufferIndex >= 0) {
                    inputBuffers[bufferIndex].clear();
                    mConvertor.convert(dst, inputBuffers[bufferIndex]);
                    mMediaCodec.queueInputBuffer(bufferIndex, 0,
                            inputBuffers[bufferIndex].position(),
                            System.nanoTime() / 1000, 0);
                    MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
                    int outputBufferIndex = mMediaCodec.dequeueOutputBuffer(bufferInfo, 0);
                    String uniqueTimeStrLock = "fenghuo";
                    while (outputBufferIndex >= 0) {
                        outputBuffer =  outputBuffers[outputBufferIndex];
                        My_application.sbyteCache.put(uniqueTimeStrLock, new
                                byte[bufferInfo.size]);
                        byte[] outData= My_application.sbyteCache.get(uniqueTimeStrLock);
                        outputBuffer.get(outData);
                        //记录pps和sps
                        int x = outData[3] & 0x1f;
                        int y = outData[4] & 0x1f;
                        Packet packet = new Packet(outData, 0, width, height);
                        if ((outData[0] == 0 && outData[1] == 0 && outData[2] == 1 && x == 7) || (outData[0] == 0 &&
                                outData[1] == 0 && outData[2] == 0 && outData[3] == 1 && y == 7)){
                            mPpsSps = outData;
//                            Packet packet = new Packet(outData, 0, width, height);
                        } else if ((outData[0] == 0 && outData[1] == 0 && outData[2] == 1 && x == 5) || (outData[0]
                                == 0 && outData[1] == 0 && outData[2] == 0 && outData[3] == 1 && y == 5)) {
                            //在关键帧前面加上pps和sps数据
//                            Log.e("easypusher", "Current I Frame:%d " + System.currentTimeMillis());

                            My_application.sbyteCache.put(uniqueTimeStrLock, new byte[mPpsSps
                                    .length + outData.length]);
                            System.arraycopy(mPpsSps, 0,  My_application.sbyteCache.get
                                    (uniqueTimeStrLock), 0, mPpsSps.length);
                            System.arraycopy(outData, 0,  My_application.sbyteCache.get
                                    (uniqueTimeStrLock), mPpsSps.length, outData.length);
                            outData =  My_application.sbyteCache.get(uniqueTimeStrLock);
                            packet = new Packet(outData, 1, width, height);
                        }
                        Log.i(tag, "outdata" + outData.length);
                        try {
                            h264dataQueue.put(packet);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mMediaCodec.releaseOutputBuffer(outputBufferIndex, false);
                      //  outputBuffer.clear();
                    }
                } else {
                    Log.e("easypusher", "No buffer available !");
                }
            } catch (Exception e) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                String stack = sw.toString();
                Log.e("save_log", stack);
                e.printStackTrace();
            } finally {
                mCamera.addCallbackBuffer(dst);
            }
        }

    };

    private void makePpsSps(byte[] outData) {
        outputBuffer.get(outData);
        //记录pps和sps
        if ((outData[0] == 0 && outData[1] == 0 && outData[2] == 1 &&
                (outData[3] & 0x1f) == 7) || (outData[0] == 0 && outData[1] == 0 &&
                outData[2] == 0 && outData[3] == 1 && (outData[4] & 0x1f) == 7)) {
            mPpsSps = outData;
        } else if ((outData[0] == 0 && outData[1] == 0 && outData[2] == 1 && (outData[3] &
                0x1f) == 5) || (outData[0] == 0 && outData[1] == 0 && outData[2] == 0 &&
                outData[3] == 1 && (outData[4] & 0x1f) == 5)) {
            //在关键帧前面加上pps和sps数据
            String uniqueTimeStrLock ="fenghuo";
            My_application.sbyteCache.put(uniqueTimeStrLock, new byte[mPpsSps
                    .length + outData.length]);
            System.arraycopy(mPpsSps, 0,  My_application.sbyteCache.get
                    (uniqueTimeStrLock), 0, mPpsSps.length);
            System.arraycopy(outData, 0,  My_application.sbyteCache.get
                    (uniqueTimeStrLock), mPpsSps.length, outData.length);
            outData=My_application.sbyteCache.get(uniqueTimeStrLock);
        }
    }
    private H264Decoder h264Decode;

    public Thread user_send_video_thread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (h264playing) {

                try {
                    Packet p = h264dataQueue.take();
                    if (p == null) {
                        break;
                    }
                    //发送数据
                    ndk_wrapper.instance().avsz_async_vid(p.data, p.timestamp, p.width, p.height, framerate);
                    Log.d(tag,"send_data");
                    video_capture_time = System.currentTimeMillis();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    });

}
