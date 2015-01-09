/*
 * Copyright 2014 Ji Kim
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jhk.whysoformal.service;

import java.io.IOException;

import com.jhk.whysoformal.context.audio.AudioContext;
import com.jhk.whysoformal.util.Constants;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.util.Log;

/**
 * Note that IntentService has a single thread, meaning though it is ran in background if 5 tasks are queued to 
 * IntentService; they will work sequentially.
 * 
 * Created by Ji Kim on 12/3/2014.
 */
public class MusicService extends IntentService {

    public static final String MEDIA_PLAYER_STATUS_UPDATE_BROADCAST = "com.jhk.whysoformal.service.MEDIA_PLAYER_STATUS_UPDATE";
    public static final String MEDIA_PLAYER_STATUS_UPDATE_KEY = "MEDIA_PLAYER_STATUS_UPDATE";
    
    public static final String UPDATE_MEDIA_PLAYER_KEY = "UPDATE_MEDIA_PLAYER";
    public static final String UPDATE_MEDIA_PLAYER_SEEK_POSITION_KEY = "SEEK_POSITION";

    private static final String TAG = "MusicService";
    
    public enum MEDIA_PLAYER_STATUS_UPDATE {
        AUDIO_POSITION_UPDATED
    }
    
    public enum UPDATE_MEDIA_PLAYER {
        RESET {
            
            @Override
            public void handleEvent(Context context, MediaPlayer mediaPlayer, Intent intent) {
                if(mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.release();
                }
                
                AudioContext aContext = AudioContext.getInstance();
                Cursor cursor = getDataCursor(context, aContext.peekPlayEntry().getId());

                MediaPlayer newMediaPlayer = getMediaPlayer(cursor, aContext.getApplicationContext());
                aContext.setMediaPlayer(newMediaPlayer);

                try {
                    newMediaPlayer.prepare();
                    newMediaPlayer.start();
                }catch(IOException e) {
                    
                }
            }
        }, 
        
        PLAY_PAUSE {
            
            @Override
            public void handleEvent(Context context, MediaPlayer mediaPlayer, Intent intent) {
                if(mediaPlayer != null) {
                    if(mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                    }else {
                        mediaPlayer.start();
                    }
                }
                
            }
        }, 
        
        PREV {
            
            @Override
            public void handleEvent(Context context, MediaPlayer mediaPlayer, Intent intent) {
                if(mediaPlayer.getCurrentPosition() > 10) {
                    mediaPlayer.seekTo(0);
                }else {
                    AudioContext aContext = AudioContext.getInstance();
                    AudioContext.PlayEntry entry = aContext.popPlayEntry(); //currently playing

                    if(entry == null || aContext.isPlayEntryEmpty()) {
                        aContext.pushPlayEntry(new AudioContext.PlayEntry(0, getSetPlayId(0)));
                    }

                    RESET.handleEvent(context, mediaPlayer, intent);
                    sendPlayEntryUpdated(context);
                }
            }
        },

        NEXT {

            @Override
            public void handleEvent(Context context, MediaPlayer mediaPlayer, Intent intent) {
                AudioContext aContext = AudioContext.getInstance();
                int nextPosition = aContext.getNextCursorPosition();

                if(nextPosition == AudioContext.LOOPING_INDEX) {
                    aContext.getMediaPlayer().seekTo(0);
                }else {

                    aContext.pushPlayEntry(new AudioContext.PlayEntry(nextPosition, getSetPlayId(nextPosition)));
                    RESET.handleEvent(context, mediaPlayer, intent);
                    sendPlayEntryUpdated(context);
                }
            }
        },

        SEEK {

            @Override
            public void handleEvent(Context context, MediaPlayer mediaPlayer, Intent intent) {

                float percentage = intent.getFloatExtra(UPDATE_MEDIA_PLAYER_SEEK_POSITION_KEY, 0.0f);
                mediaPlayer.seekTo((int) (percentage * mediaPlayer.getDuration()));
            }
        };

        private static void sendPlayEntryUpdated(Context context) {
            Intent intent = new Intent(MEDIA_PLAYER_STATUS_UPDATE_BROADCAST);
            intent.putExtra(MEDIA_PLAYER_STATUS_UPDATE_KEY, MEDIA_PLAYER_STATUS_UPDATE.AUDIO_POSITION_UPDATED);

            context.sendBroadcast(intent, Constants.PRIVATE_PERMISSION);
        }

        public static Cursor getDataCursor(Context context, long id) {
            final String[] projection = new String[]{ MediaStore.MediaColumns.DATA };

            Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, MediaStore.MediaColumns._ID + " = ?", new String[]{ ""+id }, null);
            cursor.moveToFirst();

            return cursor;
        }

        public static long getSetPlayId(int position) {
            Cursor cursor = AudioContext.getInstance().getCursor();
            cursor.moveToPosition(position);
            return cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
        }

        private static MediaPlayer getMediaPlayer(Cursor cursor, Context context) {

            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            try {

                mediaPlayer.setDataSource(cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA)));
                //below is so that Android will allow mediaPlayer to continue plying even when it goes idle
                mediaPlayer.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);
            }catch(IOException e) {
                mediaPlayer = null;
            }

            return mediaPlayer;
        }

        public abstract void handleEvent(Context context, MediaPlayer mediaPlayer, Intent intent);
    }

    public MusicService() {
        super("MusicService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        
        Log.i(TAG, "IN SERVICE CREATE");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        
        if(intent != null && intent.getExtras() != null) {
            
            Bundle bundle = intent.getExtras();
            UPDATE_MEDIA_PLAYER event = (UPDATE_MEDIA_PLAYER) bundle.get(UPDATE_MEDIA_PLAYER_KEY);
            AudioContext aContext = AudioContext.getInstance();

            Context context = aContext.getApplicationContext();
            MediaPlayer mediaPlayer = aContext.getMediaPlayer();

            switch(event) {
                case RESET: UPDATE_MEDIA_PLAYER.RESET.handleEvent(context, mediaPlayer, intent); break;
                case PLAY_PAUSE: UPDATE_MEDIA_PLAYER.PLAY_PAUSE.handleEvent(context, mediaPlayer, intent); break;
                case PREV: UPDATE_MEDIA_PLAYER.PREV.handleEvent(context, mediaPlayer, intent); break;
                case NEXT: UPDATE_MEDIA_PLAYER.NEXT.handleEvent(context, mediaPlayer, intent); break;
                case SEEK: UPDATE_MEDIA_PLAYER.SEEK.handleEvent(context, mediaPlayer, intent); break;
            }
            
        }
        
    }

}
