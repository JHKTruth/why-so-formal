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

package com.jhk.whysoformal.custom.view.graph.axis;

import com.jhk.whysoformal.custom.view.graph.GraphStyleAttributes;

import java.util.List;

/**
 * Created by Ji Kim on 9/24/2014.
 */
public class BaseAxis {

    private int mMin;
    private int mMax;
    private int mStep;
    private int mTickSize;

    private GraphStyleAttributes mAxisLineStyleAttributes;
    private GraphStyleAttributes mAxisLabelStyleAttributes;
    private List<Float> mSeriesPositions;                   //will be populated by View during calculation

    public BaseAxis(int min, int max, int step, int tickSize, GraphStyleAttributes axisLabelStyleAttributes) {
        super();

        mMin = min;
        mMax = max;
        mStep = step;
        mTickSize = tickSize;

        mAxisLineStyleAttributes = GraphStyleAttributes.sDEFAULT_AXIS_LINE_STYLE_ATTRIBUTES;
        mAxisLabelStyleAttributes = axisLabelStyleAttributes;
    }

    public BaseAxis(int min, int max, int step, int tickSize, GraphStyleAttributes axisLineStyleAttributes,
                    GraphStyleAttributes axisLabelStyleAttributes) {
        this(min, max, step, tickSize, axisLabelStyleAttributes);

        mAxisLineStyleAttributes = axisLineStyleAttributes;
    }

    public int getMin() {
        return mMin;
    }
    public void setMin(int min) {
        mMin = min;
    }

    public int getMax() {
        return mMax;
    }
    public void setMax(int max) {
        mMax = max;
    }

    public int getStep() {
        return mStep;
    }
    public void setStep(int step) {
        mStep = step;
    }

    public int getTickSize() {
        return mTickSize;
    }
    public void setTickSize(int tickSize) {
        mTickSize = tickSize;
    }

    public GraphStyleAttributes getAxisLineStyleAttributes() {
        return mAxisLineStyleAttributes;
    }
    public void setAxisLineAttributes(GraphStyleAttributes axisLineStyleAttributes) {
        mAxisLineStyleAttributes = axisLineStyleAttributes;
    }

    public GraphStyleAttributes getAxisLabelStyleAttributes() {
        return mAxisLabelStyleAttributes;
    }
    public void setAxisLabelAttributes(GraphStyleAttributes axisLabelStyleAttributes) {
        mAxisLabelStyleAttributes = axisLabelStyleAttributes;
    }

    public List<Float> getSeriesPositions() {
        return mSeriesPositions;
    }
    public void setSeriesPositions(List<Float> seriesPositions) {
        mSeriesPositions = seriesPositions;
    }

    public String getLabelAtPosition(int position) {
        return position + "";
    }

}
