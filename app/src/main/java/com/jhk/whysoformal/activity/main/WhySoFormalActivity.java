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

package com.jhk.whysoformal.activity.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import com.jhk.whysoformal.R;
import com.jhk.whysoformal.activity.moveMove.fragment.MoveMoveFragment;
import com.jhk.whysoformal.activity.musicentry.MusicPlayerActivity;
import com.jhk.whysoformal.activity.musiclist.fragment.MusicListFragment;
import com.jhk.whysoformal.context.AudioContext;
import com.jhk.whysoformal.service.MusicService;

/**
 * Created by Ji Kim on 12/3/2014.
 */
public class WhySoFormalActivity extends FragmentActivity
                                    implements MusicListFragment.Callbacks {

    private static final String TAG = "WhySoFormalActivity";
    private static final int MUSIC_LIST_FRAGMENT = 0;
    private static final int MOVE_MOVE_FRAGMENT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewPager viewPager = new ViewPager(this);
        viewPager.setId(R.id.mainViewPager);
        setContentView(viewPager);

        FragmentManager fragmentManager = getSupportFragmentManager();
        viewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {

            @Override
            public Fragment getItem(int position) {

                Fragment fragment;

                switch(position) {
                    case MUSIC_LIST_FRAGMENT: fragment = MusicListFragment.newInstance(); break;
                    //case MOVE_MOVE_FRAGMENT: fragment = MoveMoveFragment.newInstance(); break;
                    default: fragment = null;
                }

                return fragment;
            }

            @Override
            public int getCount() {
                return 1;
            }

        });

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int state) {
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int position) {
            }
        });
    }

    @Override
    public void onMusicSelected() {

        if(findViewById(R.id.detailFragmentContainer) == null) {
            //meaning not a tablet

            startActivity(new Intent(this, MusicPlayerActivity.class));
        }else {
            /*
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();

            Fragment oldInstance = manager.findFragmentById(R.id.detailFragmentContainer);
            Fragment newInstance = MusicPlayerFragment.newInstance(entry.getId());

            if(oldInstance != newInstance) {
                transaction.remove(oldInstance);
            }

            transaction.add(R.id.detailFragmentContainer, newInstance).commit();*/
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        stopService(new Intent(this, MusicService.class));
        AudioContext.getInstance().destroy();
    }
}
