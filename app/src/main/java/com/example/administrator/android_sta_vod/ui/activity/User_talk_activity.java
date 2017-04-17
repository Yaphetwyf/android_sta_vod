package com.example.administrator.android_sta_vod.ui.activity;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.AudioFormat;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.broadcast.android.android_sta_jni.ndk_wrapper;
import com.example.administrator.android_sta_vod.R;
import com.example.administrator.android_sta_vod.endecode.H264Decoder;
import com.example.administrator.android_sta_vod.endecode.Packet;
import com.example.administrator.android_sta_vod.endecode.Pcm_packet;
import com.example.administrator.android_sta_vod.event.Avsz_info_event;
import com.example.administrator.android_sta_vod.utils.AudioCapturer;
import com.example.administrator.android_sta_vod.utils.Audio_track_util;
import com.example.administrator.android_sta_vod.utils.T;
import com.example.administrator.android_sta_vod.utils.Ui_utils;
import com.example.administrator.android_sta_vod.utils.Video_util;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.R.attr.start;

/**
 * Created by Administrator on 2017/3/23 0023.
 */

public class User_talk_activity extends AppCompatActivity implements SurfaceHolder.Callback {


   private SurfaceView svNear;

    private SurfaceView svFar;

    @BindView(R.id.btn_cancel)
    Button btnCancel;
    @BindView(R.id.ll_button)
    LinearLayout llButton;
    public static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000;//定义屏蔽参数
    private String tag = "USER_TALK_ACTIVITY";
    private Video_util video_util;
    private int decode_width = 1280;
    private int decode_height = 720;
    private int framerate = 25, bitrate;
    private H264Decoder h264Decode;
    private AudioTrack track;
    private boolean is_talking;
    private BlockingQueue<Packet> h264dataQueue;
    private boolean h264playing = true;
    private ArrayBlockingQueue<Pcm_packet> pcmdata_queue;
    private boolean audio_playing = true;
    private ExecutorService audio_send_pool;
    private long last_time = System.currentTimeMillis();
    //采集音频参数
    private int sampleRate = 8000;   //采样率，默认44.1k
    private int channelCount = 2;     //音频采样通道，默认2通道
    private int channelConfig = AudioFormat.CHANNEL_IN_MONO;        //通道设置，默认立体声
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;     //设置采样数据格式，默认16比特PCM
    private Thread user_videothread = new Thread() {
        private int setHeader = 0;

        @Override
        public synchronized void run() {
            while (h264playing) {
                try {
                    if (null == h264dataQueue) {
                        break;
                    }
                    Packet p = h264dataQueue.take();
                    if (p == null) {
                        break;
                    }

                    if (null == h264Decode && null != p && svFar != null) {
                        //开始有数据回调时初始化decode
                        try {
                            h264Decode = new H264Decoder(svFar, "video/avc", p.width, p.height, framerate);

                        } catch (Exception e) {
                            Log.e(tag, "" + e.getMessage());
                            e.printStackTrace();
                        }
                    }

                    do {
                        try {

                            Log.d("ndkndk1", "length == " + p.data.length);
                            //解码
                            if (h264Decode.onFrame(p.data, 0, p.data.length) == false) {
                                Thread.sleep(10);
                            } else
                                break;

                        } catch (Exception e) {
                            e.printStackTrace();
                            //跳出内层循环
                            Log.d("ndkndk1", "Error" + e.getMessage());
                            break;
                        }

                    } while (true);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };


    private Thread user_audiothread = new Thread(new Runnable() {
        @Override
        public synchronized void run() {
            while (audio_playing) {
                try {
                    if (null == pcmdata_queue) {
                        break;
                    }
                    Pcm_packet ppacket = pcmdata_queue.take();
                    if (null != ppacket && ppacket.getData().length > 0) {
                        if (null != track) {
                            try {
                                track.play();
                                //然后将数据写入到AudioTrack中
                                track.write(ppacket.getData(), 0, ppacket.getData().length);
                                Log.d("aac_encordthread", "pcm_time" + ppacket.getTime() + "cur_time" + System
                                        .currentTimeMillis());
                            } catch (Exception e) {

                            }

                        }
                    } else {
                        Thread.sleep(10);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    });
    private String username;
    private AudioCapturer capturer;
    private long curtime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_talk);
        svFar=(SurfaceView) findViewById(R.id.sv_far);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        init_view();
        init_video();
        init_audio();
        init_event();
        video_util.initMediaCodec();
        Log.e(tag,"onCreate");

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().setFlags(FLAG_HOMEKEY_DISPATCHED, FLAG_HOMEKEY_DISPATCHED);
    }

    private void init_view() {

        username = getIntent().getStringExtra("user_name");

        svNear=(SurfaceView) findViewById(R.id.sv_near);

        Log.d("svNear",svNear+"===");
        if(svNear!=null){
            svNear.setZOrderOnTop(true);
            svNear.getHolder().setFormat(PixelFormat.TRANSPARENT);
        }else{

            svNear.setVisibility(View.GONE);
        }

    }

    private void init_video() {
        video_util = new Video_util(svNear);
       svNear.getHolder().addCallback(this);
        svFar.getHolder().addCallback(this);


        h264dataQueue = new ArrayBlockingQueue<Packet>(10000);
//        h264Decode = new H264Decoder(svFar, "video/avc", decode_width, decode_height, framerate);

    }

    private void init_audio() {

        pcmdata_queue = new ArrayBlockingQueue<Pcm_packet>(10000);
        if (null == audio_send_pool) {
            audio_send_pool = Executors.newCachedThreadPool();
        }
        ndk_wrapper.instance().avsz_aec_start(8000);
        open_close_talk();

    }

    //
    private void init_event() {
        //远方视频监听
        ndk_wrapper.instance().set_video_listener(new ndk_wrapper.Video_listener() {
            @Override
            public void video_play(String sender, int sender_type, byte[] buf, int key_frm, int width, int height,
                                   int fps) {
                if (null != svFar) {
                    if (h264playing) {
                        Packet p = new Packet(buf, key_frm, width, height);
                        try {
                            h264dataQueue.put(p);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        //远方音频监听
        ndk_wrapper.instance().set_audio_listener(new ndk_wrapper.Audio_listener() {

            @Override
            public void audio_play(String sender, int sender_type, byte[] buf, int channel, int bitrate, int sample) {
                last_time = System.currentTimeMillis();
                if (null == track) {
                    track = Audio_track_util.create_audio_track(channel, bitrate, sample);
                }
                try {
                    if (null != pcmdata_queue) {
                        if (audio_playing) {
                            Pcm_packet pcmpacket = new Pcm_packet(buf, System.currentTimeMillis());
                            pcmdata_queue.put(pcmpacket);
                        }

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(Avsz_info_event info) {
        String type = info.get_type();
        String key = info.get_key();
        String value = info.get_value();

        if ("usr_msg".equals(type) && "user_stop".equals(value)) {
            Log.d(tag,"usr_msg_user_stop");
            btnCancel.performClick();
        }
        if ("tcp".equals(type)) {
            if ("exception".equals(key) || "close".equals(key) || "timeout".equals(key) || "finished".equals(key)) {
                T.show_short(Ui_utils.get_string(R.string.server_connect_failure));
                ndk_wrapper.instance().avsz_async_send(username, "user_stop");
                btnCancel.performClick();
            }
        }

        if ("usr_call_req".equals(type)) {
            T.show_short(key + Ui_utils.get_string(R.string.user_call));
            ndk_wrapper.instance().avsz_usr_call_req_ret(key, 1);
        }

        if ("term_call_req".equals(type)) {
            T.show_short(value + Ui_utils.get_string(R.string.term_call));
            ndk_wrapper.instance().avsz_term_call_req_ret(Integer.valueOf(key), 1);
        }
    }

    private void open_close_talk() {
        audio_send_pool.execute(new Runnable() {
            @Override
            public void run() {

                //初始化音频
                capturer = new AudioCapturer();
                Log.d("audiostart", "capturer");
                capturer.setOnAudioFrameCapturedListener(new AudioCapturer.OnAudioFrameCapturedListener() {
                    @Override
                    public void onAudioFrameCaptured(byte[] audioData) {
                        Log.d("audiostart", audioData.length + "");
                        curtime = System.currentTimeMillis();
                        Log.d("timetime", "" + curtime);
                        Log.d("timetime", "" + last_time);
                        if ((curtime - last_time) > 2000) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    btnCancel.performClick();
                                }
                            });
                        }
                        if (null != audioData) {
                            ndk_wrapper.instance().assz_async_aud(audioData, 1, 16, 8000);
                        }
                    }
                });
                boolean start = capturer.startCapture(MediaRecorder.AudioSource.MIC, sampleRate,
                        channelConfig,
                        audioFormat);
                is_talking = true;
                Log.d("audiostart", start + "");
            }
        });
        Log.d("audiostart", start + "");
    }

    @OnClick({R.id.btn_cancel})
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_cancel:

                ndk_wrapper.instance().avsz_async_send(username, "user_stop");
                setResult(0, new Intent());
                finish();
                break;
        }
    }



    @Override
    protected void onStop() {
        super.onStop();
        if(null != pcmdata_queue){
            btnCancel.performClick();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            btnCancel.performClick();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (null != capturer) {
            if (capturer.isCaptureStarted()) {
                capturer.stopCapture();
            }
            ndk_wrapper.instance().avsz_aec_stop();
        }
        ndk_wrapper.instance().avsz_usr_call_req_cancel(username);
        if (null != track) {
            track.stop();
            track.release();
            track = null;
        }

        if (null != audio_send_pool) {
            audio_send_pool.shutdown();
            audio_send_pool = null;
        }


        if (h264Decode != null) {
            h264Decode.DecoderClose();
            h264Decode = null;
        }
        try {
            if (null != video_util) {
                video_util.stopPreview();
                video_util.destroyCamera();
                Log.d("destroyCamera", "destroyCamera");
            }
            h264playing = false;
            user_videothread.interrupt();
            user_videothread.join(10);
            audio_playing = false;
            user_audiothread.interrupt();
            user_audiothread.join(10);
            if (null != audio_send_pool) {
                audio_send_pool.shutdown();
                audio_send_pool = null;
            }

            ndk_wrapper.instance().user_stop();
            svFar = null;
            svNear = null;

            video_util = null;
            h264dataQueue = null;
            pcmdata_queue = null;

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        EventBus.getDefault().unregister(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Log.e(tag, "surfaceCreated");
        if (null == video_util) {
            video_util = new Video_util(svNear);
        }
        if (surfaceHolder == svNear.getHolder()) {
            Log.d("surfaceCreated", "svNear");
            video_util.ctreateCamera(surfaceHolder);
            video_util.startPreview();
        }

        if (surfaceHolder == svFar.getHolder()) {
            user_videothread.start();
            user_audiothread.start();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        Log.e(tag,"surfaceChanged");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.e(tag,"surfaceDestroyed");
    }
}