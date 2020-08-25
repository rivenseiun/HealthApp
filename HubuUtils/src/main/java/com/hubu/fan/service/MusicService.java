package com.hubu.fan.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.provider.MediaStore;

import com.hubu.fan.bean.MusicListBean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by FAN on 2016/8/30.
 * 音乐播放服务，不支持URI播放，支持本地和网络播放
 * 支持音乐被阻断后进行还原
 */
public class MusicService extends Service {

    MediaPlayer mediaPlayer;
    MusicBinder binder;
    AudioManager audioManager;

    /**
     * binder类
     */
    public static class MusicBinder extends Binder implements MediaPlayer.OnPreparedListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener {
        //静态变量
        public static final int STATE_NONE = 0;//表示停止或者没有设置视频源
        public static final int STATE_LISTENING = 1;//正在播放
        public static final int STATE_PAUSE = 2;//暂停

        private int state = 0;//播放状态
        private boolean isPrepare = false;//资源是否准备好
        private MediaPlayer mediaPlayer;//媒体
        private Callback callback;//回调接口
        private String path;//源路径


        /**
         * 回调函数
         */
        public static interface Callback {

            /**
             * 控制完成后
             *
             * @param state
             */
            public void control(int state);

            /**
             * 播放进度
             */
            public void musicUpdate(int current, int total);
        }

        MusicBinder(MediaPlayer mediaPlayer) throws Exception {
            this.mediaPlayer = mediaPlayer;

        }

        /**
         * 设置音乐源
         *
         * @param path
         * @throws IOException
         */
        public void setSource(String path) throws Exception {
            if (this.path == null || this.path.equals("")) {
                throw new Exception("源文件为空");
            }
            this.path = path.trim();
            mediaPlayer.reset();
            isPrepare = false;
            mediaPlayer.setDataSource(path);
        }

        /**
         * 设置音乐源并播放
         *
         * @param path
         * @throws IOException
         */
        public void setSourceAndPlay(String path) throws Exception {
            if (this.path == null || this.path.equals("")) {
                throw new Exception("源文件为空");
            }
            setSource(path);
            play();
        }


        /**
         * 播放
         */
        public void play() throws Exception {
            if (state != STATE_LISTENING) {
                if (this.path == null || this.path.equals("")) {
                    return;
                }
                if (isPrepare) {
                    mediaPlayer.start();
                    callBack(STATE_LISTENING);
                    return;
                }
                mediaPlayer.setOnPreparedListener(this);
                mediaPlayer.setOnBufferingUpdateListener(this);
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {

                    }
                });
                mediaPlayer.prepareAsync();
            }
        }


        /**
         * 停止
         */
        public void pause() {
            if (state == STATE_LISTENING) {
                mediaPlayer.pause();
                callBack(STATE_PAUSE);
            }
        }


        /**
         * 声音被其它程序占用时
         */
        private void focusLossProcess(int focusChange) {
            switch (focusChange) {
                //获得焦点
                case AudioManager.AUDIOFOCUS_GAIN:
                    if (state == STATE_LISTENING) {
                        mediaPlayer.start();
                        mediaPlayer.setVolume(1.0f, 1.0f);
                    }
                    break;
                //长时间失去焦点
                case AudioManager.AUDIOFOCUS_LOSS:
                    break;
                //暂时失去焦点
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    if (state == STATE_LISTENING) {
                        mediaPlayer.pause();
                    }
                    break;
                //很短时间失去焦点
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    if (state == STATE_LISTENING) {
                        mediaPlayer.setVolume(0.1f, 0.1f);
                    }
                    break;
            }
        }

        /**
         * 停止播放： 删除数据源
         */
        public void stop() {
            if (state != STATE_NONE) {
                mediaPlayer.stop();
                mediaPlayer.reset();
                isPrepare = false;
                path = null;
                callBack(STATE_NONE);

            }
        }

        /**
         * 媒体准备好后调用的函数：开始播放
         *
         * @param mp
         */
        @Override
        public void onPrepared(MediaPlayer mp) {
            isPrepare = true;
            mediaPlayer.start();
            callBack(STATE_LISTENING);
        }

        /**
         * 音乐播放的实时监听
         *
         * @param mp
         * @param percent
         */
        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            callback.musicUpdate(percent, mp.getDuration());
        }

        /**
         * 音乐播放完成
         *
         * @param mp
         */
        @Override
        public void onCompletion(MediaPlayer mp) {
            stop();
        }

        /**
         * 设置播放时回调函数
         *
         * @param callBackListener
         */
        public void setCallBackListener(Callback callBackListener) {
            callback = callBackListener;
        }

        /**
         * 通过回调函数设置回调状态
         *
         * @param state
         */
        private void callBack(int state) {
            this.state = state;
            if (callback != null) {
                callback.control(state);
            }
        }

        /**
         * 获取音乐时长
         *
         * @return
         */
        private int getMusicTime() {
            if (path == null && path.equals("")) {
                return 0;
            }
            return mediaPlayer.getDuration();
        }

        /**
         * 获取当前进度
         *
         * @return
         */
        private int getCurrentPosition() {
            if (path == null && path.equals("")) {
                return 0;
            }
            return mediaPlayer.getCurrentPosition();
        }


        /**
         * 获取内置内存音乐列表(建议用线程获取，不要放在主线程中)
         *
         * @param context
         * @return
         */
        public List<MusicListBean> getInternalMusics(Context context) {
            List<MusicListBean> musics = new ArrayList<MusicListBean>();
            //获取音乐列表
            Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, new String[]{
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.ALBUM,
                    MediaStore.Audio.Media.YEAR,
                    MediaStore.Audio.Media.DURATION,
                    MediaStore.Audio.Media.DATA
            }, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
            if (cursor == null) {
                return musics;
            }
            while (cursor.moveToNext()) {
                MusicListBean music = new MusicListBean();
                music.setAlbum(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)));
                music.setArtist(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
                music.setDuration(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));
                music.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
                music.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
                music.setYear(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.YEAR)));
                musics.add(music);
            }
            return musics;
        }

        /**
         * 获取外置内存音乐列表(建议用线程获取，不要放在主线程中)
         *
         * @param context
         * @return
         */
        public List<MusicListBean> getExternalMusics(Context context) {
            List<MusicListBean> musics = new ArrayList<MusicListBean>();
            //获取音乐列表
            Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.ALBUM,
                    MediaStore.Audio.Media.YEAR,
                    MediaStore.Audio.Media.DURATION,
                    MediaStore.Audio.Media.DATA
            }, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
            if (cursor == null) {
                return musics;
            }
            while (cursor.moveToNext()) {
                MusicListBean music = new MusicListBean();
                music.setAlbum(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)));
                music.setArtist(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
                music.setDuration(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));
                music.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
                music.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
                music.setYear(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.YEAR)));
                musics.add(music);
            }
            return musics;
        }


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }


    /**
     * 获取MusicBinder
     *
     * @param intent
     * @return
     */
    @Override
    public IBinder onBind(Intent intent) {
        mediaPlayer = new MediaPlayer();
        //处理其它音频需要干扰时产生的动作
        audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.requestAudioFocus(new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {
                if (binder != null) {
                    binder.focusLossProcess(focusChange);
                }
            }
        }, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        try {
            binder = new MusicBinder(mediaPlayer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return binder;
    }

    /**
     * 解绑时的操作
     *
     * @param intent
     * @return
     */
    @Override
    public boolean onUnbind(Intent intent) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (audioManager != null) {
            audioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            audioManager = null;
        }
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 设置
     *
     * @param mediaPlayer
     */
    private void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }
}
