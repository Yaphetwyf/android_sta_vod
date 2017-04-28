package com.broadcast.android.android_sta_jni;

import android.util.Log;

import com.example.administrator.android_sta_vod.bean.Root;
import com.example.administrator.android_sta_vod.event.Avsz_info_event;
import com.example.administrator.android_sta_vod.utils.File_utils;
import com.example.administrator.android_sta_vod.utils.XmlUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2016/10/20.
 */
public class ndk_wrapper {
    static {
        System.loadLibrary("jni_station");
    }

    private String tag = "NDK_WRAPPER";
    private static ndk_wrapper inst_ = null;
    private ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
    private Video_listener video_listener;
    private Audio_listener audio_listener;
    private Cb_file_listener cb_file_listener;
    int i = 0;
    int j = 0;
    public static ndk_wrapper instance() {
        if (null == inst_)
            inst_ = new ndk_wrapper();
        return inst_;
    }

    //**********************************************************************************************
    //sender 发送者， 可以是用户名 或 终端ID
    //sender_type,类似， 1（用户），2（CTS终端）
    //buf 数据
    //key_frm 关键帧标识， 1（关键帧），其它（非关键帧）
    //width, height, 宽高，分辨率
    //fps 帧率
    public void avsz_cb_h264(String sender, int sender_type, final byte[] buf, final int key_frm, final int width,
                             final int height,
                             final int fps) {
        Log.d("h264h264", " sender: " + sender + " tp: " + sender_type + " len:" + buf.length + " k: " + key_frm + " " +
                "w: "
                + width + " h: " + height + " fps: " + fps + "frm length == " + i);

        if(null != video_listener){
            video_listener.video_play(sender,sender_type,buf,key_frm,width,height,fps);
        }


    }

    public void user_stop(){
        video_listener = null;
        audio_listener = null;
    }

    public void set_video_listener(Video_listener video_listener){
        this.video_listener = video_listener;
    }

    public interface Video_listener{
        void video_play(String sender, int sender_type, final byte[] buf, final int key_frm, final int width,
                        final int height,
                        final int fps);
    }

    //******************************************************************************
    //channel 通道数  1 或 2
    // bitrate 比特率 16
    // sample  采样率 8000
    public void avsz_cb_pcm(String sender, int sender_type, final byte[] buf, int channel, int bitrate, int sample) {
            Log.d("pcmpcm", " sender: " + sender + " tp: " + sender_type + " len:" + buf.length + " ch: " + channel + " b: "
                    + bitrate + " s: " + sample + "audio length ==" + j);

        if(null != audio_listener){
            audio_listener.audio_play(sender,sender_type,buf,channel,bitrate,sample);
        }
    }
    public void set_audio_listener(Audio_listener audio_listener){
        this.audio_listener = audio_listener;
    }
    public interface Audio_listener{
        void audio_play(String sender, int sender_type, final byte[] buf, int channel, int bitrate, int sample);
    }
    /**********************************文件下载********************************************/
    //id 文件接收任务ID, avsz_file_transfer请求下载文件后，会生成下载任务ID
    //文件大小
    //full_path 请求下载的文件全路径[服务端PC上]，同avsz_file_transfer的参数
    //buf 文件数据，及长度
    //key 消息类型 如下：
    // file_recv_start[文件传输开始]    file_recv_data[文件数据]     file_recv_end[文件传输线束]
    // no_file[文件不存在]     open_failed[文件打开失败]     empty_file[空文件]
    public void avsz_cb_file(int id, long file_size, String full_path,  String key,  byte[] buf)
    {
        Log.e("##### ", "id:" + id + " file_size:" + file_size + " full_path:" + full_path + " key:" + key + " data_len:" + buf.length);
       if(cb_file_listener!=null){
           cb_file_listener.cb_file_listener(id,file_size,full_path,key,buf);
       }
    }
    public void set_cb_file_listener(Cb_file_listener cb_file_listener){
    this.cb_file_listener=cb_file_listener;
}
    public interface Cb_file_listener{
    void cb_file_listener(int id, long file_size, String full_path,  String key,  byte[] buf);
}
    //*************************************************************************
    public void avsz_callback(String type, String key, byte[] buf) {



        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run()
            {

                if (!"xml".equals(key))
                {
                    EventBus.getDefault().post(new Avsz_info_event(type, key, new String(buf)));
                }
                if ("login_ret_usr_list".equals(type))
                {
                    EventBus.getDefault().post(new Avsz_info_event(type, key, new String(buf)));
                }
                if("real_cap_start_ret".equals(type)){
                    EventBus.getDefault().post(new Avsz_info_event(type, key, new String(buf)));
                }
                if (new String(buf).contains("invalid_pwd"))
                {
                    EventBus.getDefault().post(new Avsz_info_event(type, key, new String(buf)));
                }
                if ("login_ret_usr_list".equals(type))
                {
                    Root root = XmlUtils.toBean(Root.class, buf);
                    Log.d(tag, "root == " + root.toString());
                    EventBus.getDefault().post(root);
                    String text = XmlUtils.toXml(root);
                    Log.d("texttext", text);
                    if(null != root.getAreas()){
                        EventBus.getDefault().post(root.getAreas());
                    }
                    if(null != root.getTasks()){
                        EventBus.getDefault().post(root.getTasks());
                    }
                    if(null != root.getTerms()){
                        EventBus.getDefault().post(root.getTerms());
                    }
                    if(null != root.getProgs()){
                        EventBus.getDefault().post(root.getProgs());
                    }
                    if(null != root.getUsers()){
                        EventBus.getDefault().post(root.getUsers());
                    }

                }
            }
        });
        Log.d("#####", "type:" + type + " key:" + key + " value:" + new String(buf));
    }


    public native int avsz_init(String ip, short port, String usr_name, String usr_pwd);

    public native int avsz_fini();

    public native String avsz_get_value(String key);

    public native int avsz_set_value(String key, String str);

    public native int avsz_async_send(String usr_name, String data);

    public native int avsz_term_av_start(int id);

    public native int avsz_term_av_stop(int id);

    public native int avsz_term_av_talk_start(int term_id);

    public native int avsz_term_av_talk_stop(int term_id);

    public native int avsz_usr_av_start(String usr_name);

    public native int avsz_usr_av_stop(String usr_name);

    public native int avsz_usr_call_req(String callee_name);

    public native int avsz_usr_call_req_cancel(String callee_name);

    public native int avsz_file_transfer(String full_path);

    public native int avsz_usr_av_talk_start(String usr_name);

    public native int avsz_usr_av_talk_stop(String usr_name);

    public native int avsz_async_vid(byte[] h264, int key_frm, int width, int height, int fps);

    //数据,通道,比特,采样率
    public native int assz_async_aud(byte[] pcm, int channel, int bitrate, int sample);


    public native int avsz_prog_mkdir(String path);

    public native int avsz_prog_rename(String path, String name, String new_name);

    public native int avsz_prog_del_multi(String xml);

    public native int avsz_task_add_or_edit(String xml);

    public native int avsz_task_del(int task_id);

    public native int avsz_task_refresh();

    public native int avsz_task_start(int task_id);

    public native int avsz_task_stop(int task_id);
    //status 0[deny] 1[busy] 2[ok], use this function after avsz_callback(type: term_cal_req)
    public native int avsz_term_call_req_ret(int term_id, int status);
    //use before  avsz_term_av_talk_start or avsz_usr_av_start ...
    public native int avsz_usr_call_req_ret(String caller_name, int self_status);


    //xml :
    // <terms>
    //<term id="" />
    //<term id="" />
    // ...
    //<term id="" />
    // </terms>
    //请求后 ，在avsz_callback中回调返回xml
    public native int avsz_real_cap_start(String xml);

    //io_idx [1或2]
    //status [0或1]
    public native int avsz_term_io_ctrl(int term_id, int io_idx, int status);
    public native int avsz_real_cap_stop();

    public native int avsz_aec_start(int sample);

    public native int avsz_query_talk_sto(int log_id, String caller_name, String callee_name, String call_time, int
            caller_type, int callee_type);

    public native void avsz_aec_stop();
}
//fps             ----->     // 25 [default]
//bitstream_ctl   ----->    //ABR 2 [default]    //CQP 0     //CRF 1
//public native int h264_enc_init(int width, int height, int bitrate, int fps, int bitstream_ctl);
//public native byte[] h264_enc_do(byte[] yuv, int force_key_frame);
//public native int h264_enc_do_len(byte[] yuv, int force_key_frame);
//public native void h264_enc_fini();
//}
