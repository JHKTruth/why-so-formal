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

package com.jhk.whysoformal.custom.view.graph.graphViewTypes.labelViews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.widget.RelativeLayout;

import com.jhk.whysoformal.custom.view.graph.GraphStyleAttributes;
import com.jhk.whysoformal.custom.view.graph.GraphView;
import com.jhk.whysoformal.custom.view.graph.axis.BaseAxis;
import com.jhk.whysoformal.util.Constants;

import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Ji Kim on 10/6/2014.
 */
public class LLabelViews extends RelativeLayout {

    private GraphView mGraphView;
    private float mVLineXOffSet = 0f;
    private float mHLineYOffSet = 0f;

    private Rect mTextCheck = new Rect();

    public LLabelViews(Context context, GraphView graphView) {
        super(context);
        setWillNotDraw(false);

        mGraphView = graphView;
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint paint = mGraphView.getPaint();

        BaseAxis xAxis = mGraphView.getXAxis();
        BaseAxis yAxis = mGraphView.getYAxis();

        float horizontalLabelYOffSetStart = getHorizontalLabelYOffSetStart(xAxis, paint);
        float verticalLabelXOffSetStart = getVerticalLabelXOffSetStart(yAxis, paint);

        float hLineYOffset = horizontalLabelYOffSetStart-xAxis.getTickSize();
        float vLineXOffset = verticalLabelXOffSetStart+yAxis.getTickSize();

        mHLineYOffSet = hLineYOffset;
        mVLineXOffSet = vLineXOffset;

        drawHorizontalLine(vLineXOffset, getWidth(), hLineYOffset, canvas, paint, xAxis.getAxisLineStyleAttributes());
        drawVerticalLine(0, hLineYOffset, vLineXOffset, canvas, paint, yAxis.getAxisLineStyleAttributes());

        List<Float> xLabelPositions = getXLabelPositions(xAxis, yAxis.getAxisLabelStyleAttributes());
        List<Float> yLabelPositions = getYLabelPositions(yAxis, xAxis.getAxisLabelStyleAttributes());

        xAxis.setSeriesPositions(getXSeriesPositions(xLabelPositions));
        yAxis.setSeriesPositions(getYSeriesPositions(yLabelPositions));

        drawHorizontalLabels(xAxis, canvas, horizontalLabelYOffSetStart, hLineYOffset, paint, xLabelPositions);
        drawVerticalLabels(yAxis, canvas, verticalLabelXOffSetStart, vLineXOffset, paint, yLabelPositions);

    }

    private float getHorizontalLabelYOffSetStart(BaseAxis xAxis, Paint paint) {
        GraphStyleAttributes xAxisLabelStyleAttributes = xAxis.getAxisLabelStyleAttributes();
        float hLStart = xAxisLabelStyleAttributes.getPaddingMarginWidth(EnumSet.of(Constants.POSITION.BOTTOM, Constants.POSITION.TOP));

        hLStart += getTextHeight(paint, xAxis.getLabelAtPosition(0), xAxisLabelStyleAttributes);

        return getHeight() - hLStart;
    }

    private float getVerticalLabelXOffSetStart(BaseAxis yAxis, Paint paint) {
        GraphStyleAttributes yAxisLabelStyleAttributes = yAxis.getAxisLabelStyleAttributes();
        float vLStart = yAxisLabelStyleAttributes.getPaddingMarginWidth(EnumSet.of(Constants.POSITION.LEFT, Constants.POSITION.RIGHT));

        vLStart += getTextWidth(paint, yAxis.getLabelAtPosition(0), yAxisLabelStyleAttributes);

        return vLStart;
    }

    private float getTextWidth(Paint paint, String text, GraphStyleAttributes styleAttributes) {

        styleAttributes.updatePaintInstance(paint);
        paint.getTextBounds(text, 0, text.length(), mTextCheck);

        return mTextCheck.width();
    }

    private float getTextHeight(Paint paint, String text, GraphStyleAttributes styleAttributes) {

        styleAttributes.updatePaintInstance(paint);
        paint.getTextBounds(text, 0, text.length(), mTextCheck);

        return mTextCheck.height();
    }

    protected void drawHorizontalLine(float startX, float stopX, float yPosition,
                                      Canvas canvas, Paint paint, GraphStyleAttributes xAxisLineStyleAttributes) {

        xAxisLineStyleAttributes.updatePaintInstance(paint);
        canvas.drawLine(startX, yPosition, stopX, yPosition, paint);
    }

    protected void drawVerticalLine(float startY, float stopY, float xPosition,
                                    Canvas canvas, Paint paint, GraphStyleAttributes yAxisLineStyleAttributes) {

        yAxisLineStyleAttributes.updatePaintInstance(paint);
        canvas.drawLine(xPosition, startY, xPosition, stopY, paint);
    }

    protected void drawHorizontalLabels(BaseAxis xAxis, Canvas canvas, float startY, float stopY, Paint paint,
                                        List<Float> xLabelPositions) {
        if(xAxis == null) {
            return;
        }

        GraphStyleAttributes xAxisLineStyleAttributes = xAxis.getAxisLineStyleAttributes();
        GraphStyleAttributes xAxisLabelStyleAttributes = xAxis.getAxisLabelStyleAttributes();

        //include margin and padding
        float textYPosition = getHeight() - xAxisLabelStyleAttributes.getPaddingMarginWidth(EnumSet.of(Constants.POSITION.BOTTOM));

        //will take care of 0 position
        for(int i=0, start=xAxis.getMin(); start <= xAxis.getMax(); i++, start = start + xAxis.getStep()) {
            float position = xLabelPositions.get(i);

            xAxisLineStyleAttributes.updatePaintInstance(paint);
            canvas.drawLine(position, startY, position, stopY, paint);

            //need to position the label better
            String label = xAxis.getLabelAtPosition(start);
            canvas.drawText(label, position-(getTextWidth(paint, label, xAxisLabelStyleAttributes)*0.5f), textYPosition, paint);
        }

    }

    protected List<Float> getXSeriesPositions(List<Float> xLabelPositions) {
        if(xLabelPositions.size() == 0) {
            return Collections.EMPTY_LIST;
        }

        List<Float> xSeriesPositions = new LinkedList<Float>(xLabelPositions);
        xSeriesPositions.set(0, mVLineXOffSet);

        return xSeriesPositions;
    }

    protected List<Float> getXLabelPositions(BaseAxis xAxis, GraphStyleAttributes yAxisLabelStyleAttributes) {

        List<Float> xPositions = new LinkedList<Float>();

        float delta = (getWidth()-mVLineXOffSet)/(xAxis.getMax()-xAxis.getMin()+1); //note that it is inclusive so need to add 1
        float position = mVLineXOffSet;

        for(int start=xAxis.getMin(); start <= xAxis.getMax(); start = start + xAxis.getStep(), position += delta) {
            xPositions.add(position);
        }

        return xPositions;
    }

    protected void drawVerticalLabels(BaseAxis yAxis, Canvas canvas, float startX, float stopX, Paint paint,
                                      List<Float> yLabelPositions) {
        if(yAxis == null) {
            return;
        }

        GraphStyleAttributes yAxisLineStyleAttributes = yAxis.getAxisLineStyleAttributes();
        GraphStyleAttributes yAxisLabelStyleAttributes = yAxis.getAxisLabelStyleAttributes();
        float paddingMarginLeft = yAxisLabelStyleAttributes.getPaddingMarginWidth(EnumSet.of(Constants.POSITION.LEFT));

        //intentionally 1 to avoid clashing w/ horizontal + vertical label
        for(int i=1, start=yAxis.getMin()+ yAxis.getStep(); start <= yAxis.getMax(); i++, start = start + yAxis.getStep()) {
            float position = yLabelPositions.get(i);

            yAxisLineStyleAttributes.updatePaintInstance(paint);
            canvas.drawLine(startX, position, stopX, position, paint);

            //need to position the label better
            String label = yAxis.getLabelAtPosition(start);
            canvas.drawText(label, paddingMarginLeft, position+(getTextHeight(paint, label, yAxisLabelStyleAttributes)*0.5f), paint);
        }

    }

    protected List<Float> getYSeriesPositions(List<Float> yLabelPositions) {
        if(yLabelPositions.size() == 0) {
            return Collections.EMPTY_LIST;
        }

        List<Float> ySeriesPositions = new LinkedList<Float>(yLabelPositions);
        yLabelPositions.set(0, mHLineYOffSet);

        return ySeriesPositions;
    }

    protected List<Float> getYLabelPositions(BaseAxis yAxis, GraphStyleAttributes xAxisLabelStyleAttributes) {

        List<Float> yPositions = new LinkedList<Float>();

        //include margin and padding
        float delta = (getHeight()-xAxisLabelStyleAttributes.getPaddingMarginWidth(EnumSet.of(Constants.POSITION.BOTTOM, Constants.POSITION.TOP)))/(yAxis.getMax()-yAxis.getMin()+1); //note that one has to include, so add 1
        float position = delta;

        for(int end=yAxis.getMax(); end >= yAxis.getMin(); end = end - yAxis.getStep(), position += delta) {
            yPositions.add(position);
        }

        Collections.reverse(yPositions);

        return yPositions;
    }
}
