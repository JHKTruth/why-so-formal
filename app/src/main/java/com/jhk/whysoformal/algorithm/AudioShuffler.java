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

package com.jhk.whysoformal.algorithm;

import android.util.Log;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Random;

/**
 * So going to use a simple algorithm to randomly pick by weight
 *
 * For instance let's say there are 1000 songs and the partition is 250
 * w/ the distribution of 2:1:1:1 in terms of last songs added
 *
 * Then each partition will have a BitSet corresponding to the amount
 * [i.e. for instance the total set is 5 w/ the random being part of the first 2 w/ 2/5 and others
 * w/ 1/5 chance]
 *
 * Which ever partition is picked. it will use the BitSet of 250 to pick the song. It will randomly try
 * for first 5 and afterwards pick the next bit not chosen. Will have a count of the chosen to avoid
 * coming in if full in that partition.
 *
 * Created by Ji Kim on 12/3/2014.
 */
public class AudioShuffler {

    public static final String WEIGHT_DISTRIBUTION = "WEIGHT_DISTRIBUTION";
    public static final float[] DEFAULT_WEIGHT_DISTRIBUTION = new float[]{2.0f, 1.0f, 1.0f, 1.0f};

    private static final String TAG = "AudioShuffler";

    private class AudioShufflerPartition {

        private static final int RANDOM_PICK_ATTEMPT = 5;

        private BitSet mBitSet;
        private int mMarked;
        private float mWeight;
        private int mBitSize;

        private AudioShufflerPartition(int size, float weight) {
            super();

            mBitSet = new BitSet(size);
            mBitSize = size;
            mWeight = weight;
        }

        private boolean isAllMarked() {
            return mMarked == mBitSize;
        }

        private void resetUsage() {
            mBitSet.clear();
            mMarked = 0;
        }

        private int nextPosition() {

            int nextRandomPosition = -1;

            for(int tries = 0; tries < RANDOM_PICK_ATTEMPT; tries++) {
                int attemptPosition = (int) (mBitSize * mRandom.nextFloat());

                if(!mBitSet.get(attemptPosition)) {
                    mBitSet.set(attemptPosition);
                    nextRandomPosition = attemptPosition;
                    break;
                }
            }

            if(nextRandomPosition == -1) {
                Log.i(TAG, "NextCursor is -1 still");
                nextRandomPosition = mBitSet.nextClearBit(0);
                mBitSet.set(nextRandomPosition);
            }

            mMarked++;
            return nextRandomPosition;
        }
    }

    private Random mRandom;
    private List<AudioShufflerPartition> mPartitions;
    private float mCompleteWeight;

    public AudioShuffler() {
        super();

        mRandom = new Random(); //note that Random has internal lock, so best to create an instance
                                // rather than static; just note since single app anyway
    }

    public void reset(int dataSize, float[] weightDistribution) {

        int partitionSize = (int) Math.ceil(dataSize / weightDistribution.length);
        int remaining = dataSize % weightDistribution.length;

        mCompleteWeight = 0f;
        for(float curr : weightDistribution) {
            mCompleteWeight += curr;
        }

        mPartitions = new ArrayList<AudioShufflerPartition>(weightDistribution.length);

        for(int i=0; i < weightDistribution.length; i++) {
            mPartitions.add(new AudioShufflerPartition(i == weightDistribution.length-1 ? (partitionSize+remaining) : partitionSize, weightDistribution[i]));
        }

    }

    private AudioShufflerPartition getNextRandomAudioShufflerPartition() {
        AudioShufflerPartition aspInstance = null;

        float randomDistributionPick = mRandom.nextFloat()*mCompleteWeight;
        float accumulatedWeight = 0f;

        int position = 0;
        for(AudioShufflerPartition traverse : mPartitions) {
            accumulatedWeight += traverse.mWeight;
            if(randomDistributionPick < accumulatedWeight && !traverse.isAllMarked()) {
                Log.i(TAG, "WITH " + position);
                aspInstance = traverse;
                break;
            }
            position++;
        }

        return aspInstance;
    }


    public int getNextRandomPosition() {

        //first decide which AudioShufflerPartition one should use by using the weightDistribution
        AudioShufflerPartition aspInstance = getNextRandomAudioShufflerPartition();

        if(aspInstance == null) {
            Log.i(TAG, "All used so reset");
            for(AudioShufflerPartition traverse : mPartitions) {
                traverse.resetUsage();
            }

            aspInstance = getNextRandomAudioShufflerPartition();
        }

        return aspInstance.nextPosition();
    }
}
