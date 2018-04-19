package com.example.bsproperty.utils;

import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.SeekBar;

import org.w3c.dom.Text;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by wdxc1 on 2018/4/5.
 */

public class Player implements MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener {

    public MediaPlayer mediaPlayer;
    private SeekBar skbProgress;
    private Timer mTimer = new Timer();
    private String videoUrl;
    private boolean pause;
    private int playPosition;
    private AssetFileDescriptor mAfd;
    private OnPlayListener onPlayListener;

    public Player(String videoUrl, SeekBar skbProgress, OnPlayListener onPlayListener) {
        this.skbProgress = skbProgress;
        this.videoUrl = videoUrl;
        this.onPlayListener = onPlayListener;
        initPlay();

    }

    public Player(AssetFileDescriptor afd, SeekBar skbProgress, OnPlayListener onPlayListener) {
        this.skbProgress = skbProgress;
        mAfd = afd;
        this.onPlayListener = onPlayListener;
        initPlay();
    }

    private void initPlay() {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnBufferingUpdateListener(this);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnCompletionListener(this);
            if (TextUtils.isEmpty(videoUrl)) {
                mediaPlayer.setDataSource(mAfd.getFileDescriptor(),
                        mAfd.getStartOffset(), mAfd.getLength());
            } else {
                mediaPlayer.setDataSource(videoUrl);
            }
            mediaPlayer.prepare();
        } catch (Exception e) {
            Log.e("mediaPlayer", "error", e);
        }

        mTimer.schedule(mTimerTask, 0, 1000);
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }

    TimerTask mTimerTask = new TimerTask() {
        @Override
        public void run() {
            if (mediaPlayer == null)
                return;
            if (mediaPlayer.isPlaying() && skbProgress != null && skbProgress.isPressed() == false) {
                handleProgress.sendEmptyMessage(0);
            }
        }
    };

    Handler handleProgress = new Handler() {
        public void handleMessage(Message msg) {
            int position = mediaPlayer.getCurrentPosition();
            int duration = mediaPlayer.getDuration();
            onPlayListener.onProgress(position);
            if (duration > 0 && skbProgress != null) {
                long pos = skbProgress.getMax() * position / duration;
                skbProgress.setProgress((int) pos);
            }
        }

    };

    /**
     * 播放
     */
    public void play(boolean flag) {
        try {
            if (!mediaPlayer.isPlaying()) {
                if (flag) {
                    mediaPlayer.setVolume(1.0f, 1.0f);
                } else {
                    mediaPlayer.setVolume(0.5f, 0.5f);
                }

                mediaPlayer.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 重播
     */
    public void replay() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(0);// 从开始位置开始播放音乐
        } else {
            play(true);
        }
    }

    /**
     * 暂停
     */
    public boolean pause() {
        if (mediaPlayer.isPlaying()) {// 如果正在播放
            mediaPlayer.pause();// 暂停
            pause = true;
        } else {
            if (pause) {// 如果处于暂停状态
                mediaPlayer.start();// 继续播放
                pause = false;
            }
        }
        return pause;
    }

    /**
     * 停止
     */
    public void stop() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    public void release() {
        if (mediaPlayer != null) {
            mTimer.cancel();
            mediaPlayer.release();
        }
    }


    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        if (skbProgress != null) {
            skbProgress.setSecondaryProgress(percent);
            int currentProgress = skbProgress.getMax()
                    * mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration();
            Log.e(currentProgress + "% play", percent + "% buffer");
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        onPlayListener.onCompletion();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        onPlayListener.onLoad(mp.getDuration());
    }

    public interface OnPlayListener {
        void onLoad(int duration);

        void onProgress(int position);

        void onCompletion();
    }
}
