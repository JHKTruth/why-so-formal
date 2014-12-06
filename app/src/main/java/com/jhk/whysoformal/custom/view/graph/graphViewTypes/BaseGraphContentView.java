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

package com.jhk.whysoformal.custom.view.graph.graphViewTypes;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.widget.RelativeLayout;

import com.jhk.whysoformal.custom.view.graph.GraphView;
import com.jhk.whysoformal.custom.view.graph.axis.BaseAxis;
import com.jhk.whysoformal.custom.view.graph.series.BaseSeries;

import java.util.List;

/**
 * Created by Ji Kim on 9/25/2014.
 */
public abstract class BaseGraphContentView extends RelativeLayout {

    private static final String TAG = "BaseGraphContentView";

    protected GraphView mGraphView;

    protected boolean mTouchStarted;
    protected float[] mTouchLocations;

    public BaseGraphContentView(Context context, GraphView graphView) {
        super(context);
        setWillNotDraw(false);

        mGraphView = graphView;
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawSeries(mGraphView.getSeries(), mGraphView.getXAxis(), mGraphView.getYAxis(), canvas, mGraphView.getPaint());

    }

    protected void normalizePositions(List<float[]> original, BaseAxis xAxis, BaseAxis yAxis, List<Float> xSeriesPositions, List<Float> ySeriesPositions) {
        if(original.size() == 0) {
            return;
        }

        int xIndex = 0;
        int xValue = xAxis.getMin();
        int yIndex = 0;
        int yValue = yAxis.getMin();

        for(int i=0; i < original.size(); i++) {

            float[] current = original.get(i);

            while(xIndex < xSeriesPositions.size() && xValue < current[0]) {
                xIndex++;
                xValue += xAxis.getStep();
            }

            if(xIndex > xAxis.getMax()) {
                throw new IllegalStateException("DUDE you went over in the xAxis");
            }

            yIndex = 0;
            yValue = yAxis.getMin();
            while(yIndex < ySeriesPositions.size() && yValue < current[1]) {
                yIndex++;
                yValue += yAxis.getStep();
            }

            if(yIndex > yAxis.getMax()) {
                throw new IllegalStateException("DUDE you went over in the yAxis");
            }

            if(xIndex == xAxis.getMin()) {
                current[0] = xSeriesPositions.get(0);
            }else {
                float xIndexValue = xSeriesPositions.get(xIndex);
                float xIndexMinusOneValue = xSeriesPositions.get(xIndex-1);
                int compare = Float.compare(xIndex, current[0]);

                current[0] = compare == 0 ? xIndexValue : ((xIndex - current[0]) * (xIndexValue - xIndexMinusOneValue)) + xIndexMinusOneValue;
            }

            if(yIndex == yAxis.getMin()) {
                current[1] = ySeriesPositions.get(0);
            }else {
                float yIndexValue = ySeriesPositions.get(yIndex);
                float yIndexMinusOneValue = ySeriesPositions.get(yIndex-1);
                int compare = Float.compare(yIndex, current[1]);

                current[1] = compare == 0 ? yIndexValue : ((yIndex - current[1]) * (yIndexValue - yIndexMinusOneValue)) + yIndexValue;
            }
        }

    }

    public abstract <E extends BaseSeries, T extends BaseAxis> void drawSeries(List<E> series, T xAxis, T yAxis, Canvas canvas, Paint paint);

}
