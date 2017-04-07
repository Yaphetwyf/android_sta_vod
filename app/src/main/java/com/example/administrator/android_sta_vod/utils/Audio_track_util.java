package com.example.administrator.android_sta_vod.utils;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

/**
 * Created by AVSZ on 2017/3/3.
 */

public class Audio_track_util {

    //采集音频参数
    private int sampleRate = 8000;   //采样率，默认44.1k
    private int channelCount = 2;     //音频采样通道，默认2通道
    private int channelConfig = AudioFormat.CHANNEL_IN_MONO;        //通道设置，默认立体声
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;     //设置采样数据格式，默认16比特PCM

    public static AudioTrack create_audio_track(int channel, int bitrate, int sample){
        int play_channel_canfig = AudioFormat.CHANNEL_CONFIGURATION_MONO;
        int play_audio_format = AudioFormat.ENCODING_PCM_16BIT;
        if (1 == channel) {
            play_channel_canfig = AudioFormat.CHANNEL_CONFIGURATION_MONO;
        } else if (2 == channel) {
            play_channel_canfig = AudioFormat.CHANNEL_OUT_STEREO;
        }
        if (8 == bitrate) {
            play_audio_format = AudioFormat.ENCODING_PCM_8BIT;
        } else if (16 == bitrate) {
            play_audio_format = AudioFormat.ENCODING_PCM_16BIT;
        }
        int size =   AudioTrack.getMinBufferSize(sample, play_channel_canfig, play_audio_format);
//        int size = 1280;
        AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC,

                sample,
                play_channel_canfig,
                play_audio_format,
                size,
                AudioTrack.MODE_STREAM);
        return track;
    }
}
