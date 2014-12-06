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

package com.jhk.whysoformal.context;

import java.util.Stack;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;

import com.jhk.whysoformal.algorithm.AudioShuffler;

/**
 * Created by Ji Kim on 12/3/2014.
 */
public final class AudioContext {

    public static final String SEARCH_QUERY = "SEARCH_QUERY";
    public static final int LOOPING_INDEX = -1;
    
    private static AudioContext sInstance;

    private Context mApplicationContext;
    private Cursor mCursor;
    private MediaPlayer mMediaPlayer;
    private Stack<PlayEntry> mPlayEntries;
    private boolean mShuffling;
    private boolean mLooping;
    private AudioShuffler mAShuffler;

    private AudioContext() {
        super();

        mPlayEntries = new Stack<PlayEntry>();
        mShuffling = true;
        mAShuffler = new AudioShuffler();
    }
    
    public static synchronized AudioContext getInstance() {
        
        if(sInstance == null) {
            sInstance = new AudioContext();
        }
        
        return sInstance; 
    }

    public void destroy() {
        if(mCursor != null) {
            mCursor.close();
        }
        if(mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public int getNextCursorPosition() {
        if(mCursor == null) {
            return 0;
        }

        int nextCursorPos = -1;

        if(mLooping) {
            nextCursorPos = LOOPING_INDEX;
        }else {

            if(mShuffling) {
                nextCursorPos = mAShuffler.getNextRandomPosition();
            }else {
                nextCursorPos = (mCursor.getPosition() + 1) % mCursor.getCount();
            }
        }
        return nextCursorPos;
    }

    public boolean isLooping() {
        return mLooping;
    }
    public void toggleLooping() {
        mLooping = !mLooping;
    }

    public boolean isShuffling() {
        return mShuffling;
    }
    public void toggleShuffling() {
        mShuffling = !mShuffling;
    }

    public Context getApplicationContext() {
        return mApplicationContext;
    }
    public void setApplicationContext(Context context) {
        mApplicationContext = context;
    }

    public MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }
    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        mMediaPlayer = mediaPlayer;
    }

    public boolean isPlayEntryEmpty() {
        return mPlayEntries.isEmpty();
    }
    public void pushPlayEntry(PlayEntry entry) {
        mPlayEntries.push(entry);
    }
    public PlayEntry popPlayEntry() {
        if(mPlayEntries.isEmpty()) {
            return null;
        }
        return mPlayEntries.pop();
    }
    public PlayEntry peekPlayEntry() {
        if(mPlayEntries.isEmpty()) {
            return null;
        }
        return mPlayEntries.peek();
    }
    public void clearPlayEntry() {
        mPlayEntries.clear();
    }
    
    public Cursor getCursor() {
        return mCursor;
    }
    public void setCursor(Cursor cursor, float[] weightDistribution) {
        mCursor = cursor;
        mAShuffler.reset(cursor.getCount(), weightDistribution);
    }

    public static class PlayEntry {
        private int mPosition;
        private long mId;

        public PlayEntry(int position, long id) {
            super();

            mPosition = position;
            mId = id;
        }

        public long getId() {
            return mId;
        }
        public int getPosition() {
            return mPosition;
        }
    }
    
}
