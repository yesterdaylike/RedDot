package com.zwh.mimimao.reddot;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.SparseIntArray;

public class Sound {

	public static int SUCCESS = 0x80;
	public static int BTNPRESS = 0x81;
	public static int FAIL = 0x82;
	public static int MOVE = 0x83;

	private static float volumnRatio;

	static SoundPool soundPool;
	static SparseIntArray intArray;

	private static void initSoundPool(Context context) {
		soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);  
		intArray = new SparseIntArray(4);
		intArray.put(SUCCESS, soundPool.load(context, R.raw.success, 1));
		intArray.put(FAIL, soundPool.load(context, R.raw.fail, 1));
		intArray.put(MOVE, soundPool.load(context, R.raw.move, 1));
		intArray.put(BTNPRESS, soundPool.load(context, R.raw.btnpress, 1));

		AudioManager am = (AudioManager) context.getSystemService(Activity.AUDIO_SERVICE);  
		float audioMaxVolumn = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);  
		float audioCurrentVolumn = am.getStreamVolume(AudioManager.STREAM_MUSIC);  
		volumnRatio = audioCurrentVolumn / audioMaxVolumn;
	}

	public static void palySound(Context context, int sound) {

		if( null == soundPool ){
			initSoundPool(context);
		}
		soundPool.play(intArray.get(sound), volumnRatio, volumnRatio, 1, 0, 1);
	}
}
