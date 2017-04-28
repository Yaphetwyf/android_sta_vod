package org.annew.impl;

public class MediaImp {
	public native void moduleInit();
	public native long mp4SaverInit(String savePath, int audiotype, int samples, int channel, int bytesWidth, int width, int height, int framerate, int bitrate);

	public native int writeAudioData(long handle, byte[] audiodata, int len);
	public native int writeNalu(long handle,byte[] nalu, int nalulen);
	public native int writeSpsPpsInfo(long handle,byte[] spspps, int len);
	public native int stopMp4Saver(long handle);

	public native long aacEncodeInit(int samples, int channel,int bitrate,int bitwidth,int usrdata);
	public native int aacEncodeWriteData(long handle,byte[] pcmdata, int samples);
	public native int aacEncodeClose(long handle);

	public native long aacDecodeInit(int samplerate,int channel, int bitwidth,int usrdata);
	public native int aacDecodeWriteData(long handle,byte[] aacdata, int size);
	public native int aacDecodeClose(long handle);
	public native int moduleDeinit();


	public void aacEncodeCallback(byte[] data, int len, int usrdata)
	{
		mAudioEncodeFrameListener.onAudioEncodeFrame(data,usrdata);
	}
	public void aacDecodeCallback(byte[] data, int len, int usrdata)
	{
		mAudioDecodeFrameListener.onAudioDecodeFrame(data,usrdata);
	}

	private OnAudioEncodeFrameListener mAudioEncodeFrameListener;
	public interface OnAudioEncodeFrameListener {
		public void onAudioEncodeFrame(byte[] audioData, int usr_data);
	}
	private OnAudioDecodeFrameListener mAudioDecodeFrameListener;
	public interface OnAudioDecodeFrameListener {
		public void onAudioDecodeFrame(byte[] audioData, int usrdata);
	}
	public void setOnAudioDecodeFrameListener(OnAudioDecodeFrameListener listener) {
		mAudioDecodeFrameListener = listener;
	}
	public void setOnAudioEncodeFrameListener(OnAudioEncodeFrameListener listener) {
		mAudioEncodeFrameListener = listener;
	}
	 static {
	        System.loadLibrary("mp4imp");
	    }
}
