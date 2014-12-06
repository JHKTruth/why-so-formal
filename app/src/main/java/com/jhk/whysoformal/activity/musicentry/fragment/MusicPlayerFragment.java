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

package com.jhk.whysoformal.activity.musicentry.fragment;

import java.util.HashMap;
import java.util.Map;

import com.jhk.whysoformal.R;
import com.jhk.whysoformal.context.AudioContext;
import com.jhk.whysoformal.service.MusicService;
import com.jhk.whysoformal.service.MusicService.MEDIA_PLAYER_STATUS_UPDATE;
import com.jhk.whysoformal.util.Constants;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;

/**
 * Try to use touch action for making the song a favorite [save real estate]
 * Also make it s.t. you can swipe it in and out from main
 *
 * Created by Ji Kim on 12/3/2014.
 */
public class MusicPlayerFragment extends Fragment {
    
    private static final String TAG = "MusicPlayerFragment";
    private static final int SEEK_THRESHOLD = 98;

    private ImageView mPlay;
    private SeekBar mSeekBar;

    private SeekBarThread mSeekBarThread;

    private boolean mIsPlaying;
    private boolean mIsDragging;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setHasOptionsMenu(true);
        
        mIsPlaying = true;

        mSeekBarThread = new SeekBarThread();
        mSeekBarThread.start();

        getActivity().registerReceiver(mMediaPlayerStatusUpdate, new IntentFilter(MusicService.MEDIA_PLAYER_STATUS_UPDATE_BROADCAST), Constants.PRIVATE_PERMISSION, null);
    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().getActionBar().setIcon(R.drawable.icon_music_player);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        
        View view = inflater.inflate(R.layout.fragment_music_player, container, false);
        
        updateTitle();

        ImageView mPrevious = (ImageView) view.findViewById(R.id.previous);
        mPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUpdateMediaPlayerBroadCast(MusicService.UPDATE_MEDIA_PLAYER.PREV, null);
            }
        });

        mPlay = (ImageView) view.findViewById(R.id.play);
        mPlay.setImageResource(android.R.drawable.ic_media_pause);
        mPlay.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View view) {
                sendUpdateMediaPlayerBroadCast(MusicService.UPDATE_MEDIA_PLAYER.PLAY_PAUSE, null);
                
                //can be out of sync, but don't want to couple strongly due to tablet
                if(mIsPlaying) {
                    mIsPlaying = false;
                    mPlay.setImageResource(android.R.drawable.ic_media_play);
                    mSeekBarThread.mHandler.sendEmptyMessage(SeekBarThread.PAUSE);
                }else {
                    mIsPlaying = true;
                    mPlay.setImageResource(android.R.drawable.ic_media_pause);
                    mSeekBarThread.mHandler.sendEmptyMessage(SeekBarThread.SEEK_CHANGED);
                }
            }
        });
        
        ImageView mNext = (ImageView) view.findViewById(R.id.next);
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUpdateMediaPlayerBroadCast(MusicService.UPDATE_MEDIA_PLAYER.NEXT, null);
            }
        });
        
        mSeekBar = (SeekBar) view.findViewById(R.id.seekBar);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            
            int progressChanged = 0;
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mIsDragging = true;
            }
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                float percentage = progressChanged / 100.0f;

                if((percentage * 100) > SEEK_THRESHOLD) {
                    sendUpdateMediaPlayerBroadCast(MusicService.UPDATE_MEDIA_PLAYER.NEXT, null);
                }else {
                    Map<String, Float> additional = new HashMap<String, Float>();
                    additional.put(MusicService.UPDATE_MEDIA_PLAYER_SEEK_POSITION_KEY, percentage);
                    sendUpdateMediaPlayerBroadCast(MusicService.UPDATE_MEDIA_PLAYER.SEEK, additional);
                }

                mIsDragging = false;
            }
            
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                    boolean fromUser) {
                progressChanged = progress;
            }
            
        });

        ImageView mLoop = (ImageView) view.findViewById(R.id.loopImage);
        mLoop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AudioContext.getInstance().toggleLooping();
            }
        });

        ImageView mShuffle = (ImageView) view.findViewById(R.id.shuffleImage);
        mShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AudioContext.getInstance().toggleShuffling();
            }
        });
        
        if(NavUtils.getParentActivityName(getActivity()) != null) {
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
        case android.R.id.home:
            //move back to list
            if (NavUtils.getParentActivityName(getActivity()) != null) {
                NavUtils.navigateUpFromSameTask(getActivity());
            }
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    private void updateTitle() {
        AudioContext context = AudioContext.getInstance();
        Cursor cursor = context.getCursor();
        cursor.moveToPosition(context.peekPlayEntry().getPosition());
        getActivity().setTitle(cursor.getString(cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.DISPLAY_NAME)));
    }
    
    public static MusicPlayerFragment newInstance() {
        return new MusicPlayerFragment();
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();

        mSeekBarThread.quit();
        getActivity().unregisterReceiver(mMediaPlayerStatusUpdate);
    }
    
    private void sendUpdateMediaPlayerBroadCast(MusicService.UPDATE_MEDIA_PLAYER event, Map<String, ?> additionalArgs) {
        Intent intent = new Intent(getActivity(), MusicService.class);
        intent.putExtra(MusicService.UPDATE_MEDIA_PLAYER_KEY, event);
        
        if(additionalArgs != null) {
            for(String key : additionalArgs.keySet()) {
                Object value = additionalArgs.get(key);
                
                if(value instanceof Float) {
                    intent.putExtra(key, (Float) value);
                }else if(value instanceof Boolean) {
                    intent.putExtra(key, (Boolean) value);
                }else if(value instanceof Integer) {
                    intent.putExtra(key, (Integer) value);
                }else {
                    intent.putExtra(key, value.toString());
                }
            }
        }
        
        getActivity().startService(intent);
    }

    private BroadcastReceiver mMediaPlayerStatusUpdate = new BroadcastReceiver() {
        
        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent != null && intent.getExtras() != null) {
                
                Bundle bundle = intent.getExtras();
                MEDIA_PLAYER_STATUS_UPDATE event = (MEDIA_PLAYER_STATUS_UPDATE) bundle.get(MusicService.MEDIA_PLAYER_STATUS_UPDATE_KEY);
                
                switch(event) {
                    case AUDIO_POSITION_UPDATED:
                        updateTitle();
                        break;
                }
            }
            
        }
        
    };

    private class SeekBarThread extends HandlerThread {

        private static final String TAG = "SeekBarThread";

        private static final int SEEK_CHANGED = 0;
        private static final int PAUSE = 1;

        private Handler mHandler;

        public SeekBarThread() {
            super(TAG);
        }

        @Override
        protected void onLooperPrepared() {
            super.onLooperPrepared();

            mHandler = new Handler() {

                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);

                    if(msg.what == PAUSE) {
                        return;
                    }

                    if(msg.what == SEEK_CHANGED) {
                        MediaPlayer mp = AudioContext.getInstance().getMediaPlayer();
                        int position = 10;

                        try {
                            if (mSeekBar != null && mp != null && mp.isPlaying() && !mIsDragging) {
                                position = mp.getCurrentPosition();
                                int percentageIn100 = (int) ((position * 1.0f / mp.getDuration()) * 100);
                                mSeekBar.setProgress(percentageIn100);

                                if (percentageIn100 > SEEK_THRESHOLD) {
                                    sendUpdateMediaPlayerBroadCast(MusicService.UPDATE_MEDIA_PLAYER.NEXT, null);
                                }
                            }
                        }catch(IllegalStateException e) {
                            //caused when mp has yet to be initialized or has been released
                        }

                        sendMessageDelayed(obtainMessage(SEEK_CHANGED), 1000 - (position % 1000));
                    }

                }
            };

            mHandler.sendEmptyMessage(SEEK_CHANGED);
        }

    }

}
