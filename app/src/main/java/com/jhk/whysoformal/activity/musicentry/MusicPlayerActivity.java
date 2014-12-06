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

package com.jhk.whysoformal.activity.musicentry;

import com.jhk.whysoformal.activity.SimpleFragmentActivity;
import com.jhk.whysoformal.activity.musicentry.fragment.MusicPlayerFragment;
import com.jhk.whysoformal.service.MusicService;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by Ji Kim on 12/3/2014.
 */
public class MusicPlayerActivity extends SimpleFragmentActivity {
    
    private static final String TAG = "MusicPagerActivity";

    private Fragment mMPlayerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, MusicService.class);
        intent.putExtra(MusicService.UPDATE_MEDIA_PLAYER_KEY, MusicService.UPDATE_MEDIA_PLAYER.RESET);
        startService(intent);
    }

    @Override
    protected Fragment getMainFragment() {
        if(mMPlayerFragment == null) {
            mMPlayerFragment = MusicPlayerFragment.newInstance();
        }

        return mMPlayerFragment;
    }

    @Override
    protected void onResume() {
        super.onResume();
        
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        
    }
}
