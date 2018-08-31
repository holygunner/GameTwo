package com.holygunner.halves_into_whole.sound;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;

import com.holygunner.halves_into_whole.R;
import com.holygunner.halves_into_whole.database.Saver;

public class SoundPoolWrapper {
    private SoundPool mSoundPool;
    private Context mContext;
    public static final int APPEAR_FIGURE = 1;
    public static final int LEVEL_COMPLETE = 2;
    public static final int LEVEL_LOSE = 3;
    public static final int LEVEL_START = 4;
    public static final int REPLACE_FIGURE = 5;
    public static final int PRESS_BUTTON = 6;
    public static final int SELECT_FIGURE = 7;
    public static final int TURN_FIGURE = 8;
    public static final int UNITE_FIGURE = 9;

    @SuppressLint("StaticFieldLeak")
    private static SoundPoolWrapper instance;

    public static SoundPoolWrapper getInstance(Context context){
        if (instance == null){
            instance = new SoundPoolWrapper(context);
        }
        return instance;
    }

    private SoundPoolWrapper(Context context) {
        mContext = context;
        init();
        load();
    }

    private void init(){
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        int MAX_STREAM = 4;
        mSoundPool = new SoundPool.Builder()
                .setMaxStreams(MAX_STREAM)
                .setAudioAttributes(attributes)
                .build();
    }

    private void load(){
        mSoundPool.load(mContext, R.raw.appear_figure, APPEAR_FIGURE);
        mSoundPool.load(mContext, R.raw.level_complete, LEVEL_COMPLETE);
        mSoundPool.load(mContext, R.raw.level_lose2, LEVEL_LOSE);
        mSoundPool.load(mContext, R.raw.level_start, LEVEL_START);
        mSoundPool.load(mContext, R.raw.move_figure, REPLACE_FIGURE);
        mSoundPool.load(mContext, R.raw.press_button, PRESS_BUTTON);
        mSoundPool.load(mContext, R.raw.select_figure, SELECT_FIGURE);
        mSoundPool.load(mContext, R.raw.turn_figure, TURN_FIGURE);
        mSoundPool.load(mContext, R.raw.unite_figure, UNITE_FIGURE);
    }

    public void playSound(int soundId){
        if (isSoundOn()) {
            AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
            assert audioManager != null;
            float curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            float maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            float leftVolume = curVolume / maxVolume;
            float rightVolume = curVolume / maxVolume;
            int priority = 1;
            int no_loop = 0;
            float normal_playback_rate = 1f;
            mSoundPool.play(soundId, leftVolume, rightVolume, priority,
                    no_loop, normal_playback_rate);
        }
    }

    private boolean isSoundOn(){
        return Saver.readIsSoundOn(mContext);
    }

    public void release(){
        mSoundPool.release();
        instance = null;
    }
}
