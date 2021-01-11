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

    MediaPlayer mSuccessPlayer = null;
    MediaPlayer mFailPlayer = null;
    MediaPlayer mScanExistPlayer = null;

    public void playSuccess(Context context){
        if (mSuccessPlayer != null) {
            mSuccessPlayer.stop();
            mSuccessPlayer.release();
            mSuccessPlayer = null;
        }
        mSuccessPlayer = MediaPlayer.create(context, R.raw.success);
        mSuccessPlayer.start();
    }

    public void playFail(Context context){
        if (mFailPlayer != null) {
            mFailPlayer.stop();
            mFailPlayer.release();
            mFailPlayer = null;
        }
        mFailPlayer= MediaPlayer.create(context, R.raw.error);
        mFailPlayer.start();
    }

    public void playScanExist(Context context){
        if (mScanExistPlayer != null) {
            mScanExistPlayer.stop();
            mScanExistPlayer.release();
            mScanExistPlayer = null;
        }
        mScanExistPlayer= MediaPlayer.create(context, R.raw.scannotexists);
        mScanExistPlayer.start();
    }

    public void releaseAllSound(){
        if (mSuccessPlayer != null) {
            mSuccessPlayer.stop();
            mSuccessPlayer.release();
            mSuccessPlayer = null;
        }

        if (mFailPlayer != null) {
            mFailPlayer.stop();
            mFailPlayer.release();
            mFailPlayer = null;
        }

        if (mScanExistPlayer != null) {
            mScanExistPlayer.stop();
            mScanExistPlayer.release();
            mScanExistPlayer = null;
        }

    }

}
