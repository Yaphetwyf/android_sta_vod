package com.example.administrator.android_sta_vod.ui.activity.fragment;

import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afa.tourism.greendao.gen.DaoMaster;
import com.afa.tourism.greendao.gen.DaoSession;
import com.afa.tourism.greendao.gen.TermDao;
import com.broadcast.android.android_sta_jni_official.ndk_wrapper;
import com.example.administrator.android_sta_vod.R;
import com.example.administrator.android_sta_vod.base.Const;
import com.example.administrator.android_sta_vod.bean.Term;
import com.example.administrator.android_sta_vod.bean.Terms;
import com.example.administrator.android_sta_vod.event.Avsz_info_event;
import com.example.administrator.android_sta_vod.utils.AudioCapturer;
import com.example.administrator.android_sta_vod.utils.L;
import com.example.administrator.android_sta_vod.utils.T;
import com.example.administrator.android_sta_vod.utils.Ui_utils;
import com.example.administrator.android_sta_vod.utils.XmlUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;

/**
 * Created by Administrator on 2017/4/26.
 */

public class Real_time_talk_fragment extends BaseFragment {

    private ImageView iv_real_time;

    private String tag = "REAL_TIME_TALK_FRAGMENT";
    private TextView tv_real_time;
private TermDao termDao;
    private AudioCapturer capturer;
    //采集音频参数
    private int sampleRate = 8000;   //采样率，默认44.1k
    private int channelCount = 2;     //音频采样通道，默认2通道
    private int channelConfig = AudioFormat.CHANNEL_IN_MONO;        //通道设置，默认立体声
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;     //设置采样数据格式，默认16比特PCM
    private ExecutorService audio_send_pool;
    private boolean speaking = Const.speaking;
    private ArrayBlockingQueue<byte[]> audio_queue;
    private boolean thread_start;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg)
        {
        }
    };
    private Thread thread_sendaudio = new Thread(new Runnable() {
        @Override
        public void run()
        {
            while (Const.speaking)
            {
                try
                {
                    L.d("time","before" + System.currentTimeMillis()+"");
                    byte[] data = audio_queue.take();
                    L.d("time","after" + System.currentTimeMillis()+"");
                    if (null != data)
                    {
                        ndk_wrapper.instance().assz_async_aud(data, 1, 16, 8000);
                        L.d("data.length", data.length + "");
                    } else
                    {
                        Thread.sleep(10);
                    }
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }
    });
    private long mLast_time;

    @Override
    public int get_layout_res() {
        return R.layout.fragment_real_time_talk;
    }

    @Override
    public void init_view() {
        iv_real_time = findView(R.id.iv_real_time);
        tv_real_time = findView(R.id.tv_real_time_start);

    }

    @Override
    public void init_listener() {
      iv_real_time.setOnClickListener(this);

    }

    @Override
    public void init_data() {
        audio_queue = new ArrayBlockingQueue<byte[]>(100000);
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(getActivity().getApplicationContext(), "term.db", null);
        DaoMaster daoMaster = new DaoMaster(devOpenHelper.getWritableDb());
        DaoSession daoSession = daoMaster.newSession();
        termDao = daoSession.getTermDao();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(Avsz_info_event avsz_info_event)
    {
        String value = avsz_info_event.get_value();
        String type = avsz_info_event.get_type();
        Log.d("start_value",""+value );
        if(type.equals("real_cap_start_ret")){
            if (value.contains("-915"))
            {
                T.show_short(Ui_utils.get_string(R.string.term_busy));
            } else if(value.contains("all_busy"))
            {
                T.show_short(Ui_utils.get_string(R.string.term_busy));

            }else {
                iv_real_time.setImageDrawable(Ui_utils.get_drawable(R.drawable.real_time_stop));
                tv_real_time.setText(Ui_utils.get_string(R.string.real_time_stop));
                Const.speaking = true;
                start_audiocapture();
                Log.d("start_audiocapture","start_audiocapture");
            }
        }else {
            return;
        }


    }

    private void start_audiocapture() {
        if (!thread_start)
        {
            thread_sendaudio.start();
            thread_start = true;
        }

        if (null == capturer)
        {
            capturer = new AudioCapturer();
        }
        if (!capturer.isCaptureStarted())
        {
            boolean start = capturer.startCapture(MediaRecorder.AudioSource.MIC, sampleRate,
                    channelConfig,
                    audioFormat);
            Log.d(tag, "start == " + start);
        }
        capturer.setOnAudioFrameCapturedListener(new AudioCapturer.OnAudioFrameCapturedListener() {
            @Override
            public void onAudioFrameCaptured(byte[] audioData)
            {
                try
                {
                    audio_queue.put(audioData);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onClick(View v, int btnId) {
       switch (btnId){
       case   R.id.iv_real_time:
           if (System.currentTimeMillis() - mLast_time < 100)
           {
               L.d(tag, "click too fast" + System.currentTimeMillis());
               return;
           }
           if (Const.speaking)
           {
               if(capturer!=null){
                   if (capturer.isCaptureStarted())
                   {
                       capturer.stopCapture();
                   }

               }
               iv_real_time.setImageDrawable(Ui_utils.get_drawable(R.drawable.real_time_start));
               tv_real_time.setText(Ui_utils.get_string(R.string.real_time_start));
               Log.d("tv_real_time","tv_real_time");
               Const.speaking=false;
               ndk_wrapper.instance().avsz_real_cap_stop();

           }else {
               /*Intent intent = getActivity().getIntent();
               Bundle bundle = intent.getExtras();
               if(bundle==null){
                   T.show_short(Ui_utils.get_string(R.string.please_add_terminal));
                   return;
               }
               ArrayList<Term> checked_terms = bundle.getParcelableArrayList("terms");*/
             /*  List<Terminal> checked_terms = terminalDao.loadAll();*/
               List<Term> checked_terms = termDao.loadAll();
               if(checked_terms!=null&&checked_terms.size()>0){
                   Terms terms= new Terms(checked_terms);
                   String terms_xml = XmlUtils.toXml(terms);
                   if (Const.speaking)
                   {
                       handler.postDelayed(new Runnable() {
                           @Override
                           public void run()
                           {
                               int ret = ndk_wrapper.instance().avsz_real_cap_start(terms_xml);
                               Log.d(tag, "ret ==" + ret + "time" + System.currentTimeMillis());
                           }
                       }, 500);
                   } else
                   {
                       int ret = ndk_wrapper.instance().avsz_real_cap_start(terms_xml);
                       Log.d(tag, "ret ==" + ret);
                       if(ret==-2){
                           T.show_short(Ui_utils.get_string(R.string.server_connect_failure));
                       }
                   }
                   mLast_time = System.currentTimeMillis();
               }else if(checked_terms.size()==0){
                   T.show_short(Ui_utils.get_string(R.string.please_add_terminal));
               }
           }

         break;

       }
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != thread_sendaudio)
        {
            ndk_wrapper.instance().avsz_real_cap_stop();
            Const.speaking = false;
            thread_sendaudio.interrupt();
            thread_sendaudio = null;
        }
        if(capturer!=null){
            if (capturer.isCaptureStarted())
            {
                capturer.stopCapture();
            }
        }

        EventBus.getDefault().unregister(this);
    }
}
