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

package com.jhk.whysoformal.custom.view.graph;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.jhk.whysoformal.custom.view.graph.axis.BaseAxis;
import com.jhk.whysoformal.custom.view.graph.graphViewTypes.LineGraphContentView;
import com.jhk.whysoformal.custom.view.graph.graphViewTypes.labelViews.LLabelViews;
import com.jhk.whysoformal.custom.view.graph.series.BaseSeries;
import com.jhk.whysoformal.util.Constants;

import java.util.List;

/**
 * Created by Ji Kim on 9/24/2014.
 */
public class GraphView<E extends BaseSeries> extends RelativeLayout {

    private static final String TAG = "GraphView";

    private Constants.POSITION mTitlePosition;
    private String mTitle;
    private boolean mTicksEnabled;
    private Paint mPaint;
    private List<E> mSeries;
    private View mContentView;
    private View mLabelsView;
    private BaseAxis mXAxis;
    private BaseAxis mYAxis;

    public GraphView(Context context, AttributeSet attrs, List<E> series, String title, BaseAxis xAxis, BaseAxis yAxis, boolean ticksEnabled) {
        this(context, series, title, xAxis, yAxis);

        mTicksEnabled = ticksEnabled;

        setLayoutParams(new LayoutParams(attrs.getAttributeIntValue("android", "layout_width", LayoutParams.MATCH_PARENT),
        attrs.getAttributeIntValue("android", "layout_height", LinearLayout.LayoutParams.MATCH_PARENT)));
    }

    public GraphView(Context context, List<E> series, String title, BaseAxis xAxis, BaseAxis yAxis) {
        super(context);

        setWillNotDraw(false); //so stupid, why is this needed?
        mSeries = series;
        mTitle = title;

        mPaint = new Paint();

        mXAxis = xAxis;
        mYAxis = yAxis;

        mLabelsView = new LLabelViews(context, this);
        addView(mLabelsView);

        mContentView = new LineGraphContentView(context, this); //make it customizable later for a different view
        addView(mContentView, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    public BaseAxis getXAxis() {
        return mXAxis;
    }
    public BaseAxis getYAxis() {
        return mYAxis;
    }

    public Paint getPaint() {
        return mPaint;
    }

    public String getTitle() {
        return mTitle;
    }
    public GraphView setTitle(String title) {
        mTitle = title;
        return this;
    }

    public boolean isTicksEnabled() {
        return mTicksEnabled;
    }
    public GraphView setTicksEnabled(boolean ticksEnabled) {
        mTicksEnabled = ticksEnabled;
        invalidate(); //cause a redraw since setTicksEnabled will have to redraw the content
        return this;
    }

    public Constants.POSITION getTitlePosition() {
        return mTitlePosition;
    }
    public GraphView setTitlePosition(Constants.POSITION titlePosition) {
        mTitlePosition = titlePosition;
        return this;
    }

    public List<E> getSeries() {
        return mSeries;
    }
    public GraphView<E> setSeries(List<E> series) {
        mSeries = series;
        return this;
    }

    public View getContentView() {
        return mContentView;
    }

    public View getLabelsView() {
        return mLabelsView;
    }
}
