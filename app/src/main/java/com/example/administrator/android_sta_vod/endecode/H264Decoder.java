package com.example.administrator.android_sta_vod.endecode;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.view.SurfaceView;

import java.io.IOException;
import java.nio.ByteBuffer;

public class H264Decoder {
	private MediaCodec mCodec;
	private int mCount = 0;
	private long mBaseTick = 0;
	private long mNowTick = 0;

	private static int HEAD_OFFSET = 512;
	private int timeInternal = 0;
	public H264Decoder(SurfaceView surfaceView, String mimeType, int w, int h, int timeInternal)
	{

		try {
			mCodec = MediaCodec.createDecoderByType(mimeType);
		} catch (IOException e) {
			e.printStackTrace();
		}

		mBaseTick  = System.currentTimeMillis();
		this.timeInternal = timeInternal;
		MediaFormat mediaFormat = MediaFormat.createVideoFormat(mimeType,
				w, h);
		
		//mediaFormat.setByteBuffer("csd-0"  , ByteBuffer.wrap(sps));
		//mediaFormat.setByteBuffer("csd-1", ByteBuffer.wrap(pps));
		mCodec.configure(mediaFormat, surfaceView.getHolder().getSurface(),
				null, 0);
		mCodec.start();
		
	}
	

	 public boolean onFrame(byte[] buf, int offset, int length) {
//			Log.e("Media", "onFrame start");
			// Get input buffer index
			ByteBuffer[] inputBuffers = mCodec.getInputBuffers();
			int inputBufferIndex = mCodec.dequeueInputBuffer(100000);

			if (mCount == 0)
			{
				mBaseTick = System.currentTimeMillis();
			}
			if (inputBufferIndex >= 0) {
				ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
				inputBuffer.clear();
				inputBuffer.put(buf, offset, length);
				mCodec.queueInputBuffer(inputBufferIndex, 0, length,  0, 0);
				mCount++;
			} else {
//				Log.e("Media", "onFrame index:" + inputBufferIndex);
				return false;
			}

			// Get output buffer index
			MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
			int outputBufferIndex = mCodec.dequeueOutputBuffer(bufferInfo,0);
			while (outputBufferIndex >= 0) {
				mCodec.releaseOutputBuffer(outputBufferIndex, true);
				outputBufferIndex = mCodec.dequeueOutputBuffer(bufferInfo, 0);
			}
		 /*
			do
			{
				mNowTick = System.currentTimeMillis();

				if ((mNowTick - mBaseTick) < (mCount*(1000/this.timeInternal)))
				{
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				else
					break;
			}while(true);
			*/
			//Log.e("Media", "onFrame end");
        	
//			Log.e("Media", "one frame take time:"+ (mNowTick - mBaseTick));

			return true;
		}

	/**
	 * Find H264 frame head
	 * 
	 * @param buffer
	 * @param len
	 * @return the offset of frame head, return 0 if can not find one
	 */
	public int findHead(byte[] buffer, int len) {
		int i;
		for (i = HEAD_OFFSET; i < len; i++) {
			if (checkHead(buffer, i))
				break;
		}
		if (i == len)
			return 0;
		if (i == HEAD_OFFSET)
			return 0;
		return i;
	}

	/**
	 * Check if is H264 frame head
	 * 
	 * @param buffer
	 * @param offset
	 * @return whether the src buffer is frame head
	 */
	public boolean checkHead(byte[] buffer, int offset) {
		// 00 00 00 01
		if (buffer[offset] == 0 && buffer[offset + 1] == 0
				&& buffer[offset + 2] == 0 && buffer[3] == 1)
			return true;
		// 00 00 01
		if (buffer[offset] == 0 && buffer[offset + 1] == 0
				&& buffer[offset + 2] == 1)
			return true;
		return false;
	}
	public void DecoderClose()
	{
		mCodec.release();
		//mCodec.stop();
	}
}
