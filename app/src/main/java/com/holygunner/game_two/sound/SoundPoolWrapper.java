package com.holygunner.game_two.sound;

import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;

import com.holygunner.game_two.R;

public class SoundPoolWrapper {
    private SoundPool mSoundPool;
    private Context mContext;
    public static final int SOUND_ID_1 = 1;
    public static final int SOUND_ID_2 = 2;
    private int mStreamId;

    private static SoundPoolWrapper instance;

    public static SoundPoolWrapper getInstance(Context context){
        if (instance == null){
            instance = new SoundPoolWrapper(context);
        }
        return instance;
    }

    public SoundPoolWrapper(Context context) {
        mContext = context;
        init();
        load();
    }

    private void init(){
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        mSoundPool = new SoundPool.Builder()
                .setAudioAttributes(attributes)
                .build();
    }

    public void load(){
        mSoundPool.load(mContext, R.raw.test_sound_4, SOUND_ID_1);
        mSoundPool.load(mContext, R.raw.test_sound_2, SOUND_ID_2);
    }

    public void playSound(int soundId){
        AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        float curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float leftVolume = curVolume / maxVolume;
        float rightVolume = curVolume / maxVolume;
        int priority = 1;
        int no_loop = 0;
        float normal_playback_rate = 1f;
        mStreamId = mSoundPool.play(soundId, leftVolume, rightVolume, priority, no_loop, // SOUND_ID_1 - id звука
                normal_playback_rate);
    }

    public void release(){
        mSoundPool.release();
    }
}
