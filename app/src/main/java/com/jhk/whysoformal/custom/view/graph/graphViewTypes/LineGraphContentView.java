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
import android.util.Log;
import android.view.MotionEvent;

import com.jhk.whysoformal.custom.view.graph.GraphStyleAttributes;
import com.jhk.whysoformal.custom.view.graph.GraphView;
import com.jhk.whysoformal.custom.view.graph.axis.BaseAxis;
import com.jhk.whysoformal.custom.view.graph.series.BaseSeries;

import java.util.List;

/**
 * Created by Ji Kim on 9/25/2014.
 */
public class LineGraphContentView extends BaseGraphContentView {

    private static String TAG = "LineGraphContentView";

    public LineGraphContentView(Context context, GraphView graphView) {
        super(context, graphView);
    }

    @Override
    public <E extends BaseSeries, T extends BaseAxis> void drawSeries(List<E> series, T xAxis, T yAxis, Canvas canvas, Paint paint) {

        List<Float> xSeriesPositions = xAxis.getSeriesPositions();
        List<Float> ySeriesPositions = yAxis.getSeriesPositions();

        for(E entry : series) {

            List<float[]> seriesPoints = entry.getSeriesPoints();
            int pointRadius = entry.getPointRadius();

            GraphStyleAttributes styles = entry.getGraphStyleAttributes();

            if(styles != null) {
                styles.updatePaintInstance(paint);
            }
            normalizePositions(seriesPoints, xAxis, yAxis, xSeriesPositions, ySeriesPositions);

            if(seriesPoints.size() == 1) {
                float[] stop = seriesPoints.get(0);
                canvas.drawLine(stop[0], stop[1], stop[0], stop[1], paint);
            }else {
                for(int i=1; i < seriesPoints.size(); i++) {
                    float[] start = seriesPoints.get(i-1);
                    float[] stop = seriesPoints.get(i);

                    if(i == 1) {
                        canvas.drawCircle(start[0], start[1], pointRadius, paint);
                        canvas.drawCircle(stop[0], stop[1], pointRadius, paint);
                    }else {
                        canvas.drawCircle(stop[0], stop[1], pointRadius, paint);
                    }

                    canvas.drawCircle(stop[0], stop[1], 2, paint);
                    canvas.drawLine(start[0], start[1], stop[0], stop[1], paint);
                }
            }

        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        Log.i(TAG, "IN TOUCH EVENT " + event.getAction() + ": " + event.getX() + "-" + event.getY());

        boolean handled = false;

        if(((event.getAction() & MotionEvent.ACTION_DOWN) == MotionEvent.ACTION_DOWN) && ((event.getAction() & MotionEvent.ACTION_MOVE) == 0)) {
            mTouchStarted = true;
            mTouchLocations = new float[]{event.getX(), event.getY()};
            handled = true;
        }

        if((event.getAction() & MotionEvent.ACTION_MOVE) == MotionEvent.ACTION_MOVE) {
            if(mTouchStarted && mTouchLocations != null) {
                onTouchUpdate(mTouchLocations, new float[]{event.getX(), event.getY()});
                invalidate();
                handled = true;
            }
        }

        if((event.getAction() & MotionEvent.ACTION_UP) == MotionEvent.ACTION_UP) {
            mTouchStarted = false;
            mTouchLocations = null;
            handled = true;
        }

        return handled || super.onTouchEvent(event);
    }

    protected void onTouchUpdate(float[] previous, float[] current) {

    }
}
