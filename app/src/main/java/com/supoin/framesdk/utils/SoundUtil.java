package com.supoin.framesdk.utils;

import android.content.Context;
import android.media.MediaPlayer;

import com.supoin.framesdk.R;

/**
 * @Author zwei
 * Create Date is  2020-03-27
 * Des
 */
public class SoundUtil {
    private SoundUtil(){}
    private static class SystemSound{
        private static final SoundUtil INSTANCE = new SoundUtil();
    }

    public static SoundUtil getInstance(){
        return SystemSound.INSTANCE;
    }

    public void playSuccess(Context context){
        MediaPlayer mMediaPlayer= MediaPlayer.create(context, R.raw.success);
        mMediaPlayer.start();
    }

    public void playFail(Context context){
        MediaPlayer mMediaPlayer= MediaPlayer.create(context, R.raw.error);
        mMediaPlayer.start();
    }

}
