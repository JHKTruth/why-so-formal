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

package com.jhk.whysoformal.custom.view.graph.series;

import com.jhk.whysoformal.custom.view.graph.GraphStyleAttributes;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Ji Kim on 9/24/2014.
 */
public class BaseSeries {

    private String mLabel;
    private List<float[]> mEntries;
    private GraphStyleAttributes mStyles;
    private int mPointRadius;

    public BaseSeries(String label, GraphStyleAttributes styles) {
        super();

        mLabel = label;
        mStyles = styles;
        mEntries = new LinkedList<float[]>();
        mPointRadius = -1;
    }

    public BaseSeries(String label, List<float[]> entries, int pointRadius, GraphStyleAttributes styles) {
        this(label, styles);

        mEntries = entries; //do not copy intentionally
        mPointRadius = pointRadius;
    }

    public BaseSeries(String label, float[][] entries, int pointRadius, GraphStyleAttributes styles) {
        this(label, styles);

        mPointRadius = pointRadius;

        for(float[] entry : entries) {
            if(entry.length < 2) {
                throw new IllegalArgumentException("Each float[][] entry must be at least length of 2");
            }
            mEntries.add(new float[]{entry[0], entry[1]});
        }
    }

    public GraphStyleAttributes getGraphStyleAttributes() {
        return mStyles;
    }
    public BaseSeries setGraphStyleAttributes(GraphStyleAttributes styles) {
        mStyles = styles;
        return this;
    }

    public int getPointRadius() {
        return mPointRadius;
    }
    public BaseSeries setPointRadius(int pointRadius) {
        mPointRadius = pointRadius;
        return this;
    }

    public BaseSeries addSeriesPoint(float x, float y) {
        mEntries.add(new float[]{x, y});
        return this;
    }
    public BaseSeries addSeriesPoint(float y) {
        mEntries.add(new float[]{mEntries.size(), y});
        return this;
    }

    public List<float[]> getSeriesPoints() {
        return mEntries;
    }
    public BaseSeries setSeriesPoints(List<float[]> seriesPoints) {
        mEntries = seriesPoints;
        return this;
    }

    public String getLabel() {
        return mLabel;
    }
    public BaseSeries setLabel(String label) {
        mLabel = label;
        return this;
    }

}
