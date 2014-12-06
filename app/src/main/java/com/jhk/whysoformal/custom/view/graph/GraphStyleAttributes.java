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

import android.graphics.Color;
import android.graphics.Paint;

import com.jhk.whysoformal.util.Constants;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ji Kim on 10/6/2014.
 */
public class GraphStyleAttributes {

    public static final GraphStyleAttributes sDEFAULT_AXIS_LINE_STYLE_ATTRIBUTES;
    public static final GraphStyleAttributes sDEFAULT_X_AXIS_LABEL_STYLE_ATTRIBUTES;
    public static final GraphStyleAttributes sDEFAULT_Y_AXIS_LABEL_STYLE_ATTRIBUTES;

    static {
        GraphStyleAttributes.BorderPaddingMargin bpm5 = new GraphStyleAttributes.BorderPaddingMargin(5f, 5f);

        sDEFAULT_AXIS_LINE_STYLE_ATTRIBUTES = new DefaultGraphStyleAttributes();
        sDEFAULT_AXIS_LINE_STYLE_ATTRIBUTES.mColor = Color.RED;

        sDEFAULT_X_AXIS_LABEL_STYLE_ATTRIBUTES = new DefaultGraphStyleAttributes();
        sDEFAULT_X_AXIS_LABEL_STYLE_ATTRIBUTES.mColor = Color.MAGENTA;
        sDEFAULT_X_AXIS_LABEL_STYLE_ATTRIBUTES.mStrokeWidth = 2;
        sDEFAULT_X_AXIS_LABEL_STYLE_ATTRIBUTES.mBpm.put(Constants.POSITION.BOTTOM, bpm5);
        sDEFAULT_X_AXIS_LABEL_STYLE_ATTRIBUTES.mBpm.put(Constants.POSITION.TOP, bpm5);

        sDEFAULT_Y_AXIS_LABEL_STYLE_ATTRIBUTES = new DefaultGraphStyleAttributes();
        sDEFAULT_Y_AXIS_LABEL_STYLE_ATTRIBUTES.mColor = Color.MAGENTA;
        sDEFAULT_X_AXIS_LABEL_STYLE_ATTRIBUTES.mStrokeWidth = 2;
        sDEFAULT_Y_AXIS_LABEL_STYLE_ATTRIBUTES.mBpm.put(Constants.POSITION.LEFT, bpm5);
        sDEFAULT_Y_AXIS_LABEL_STYLE_ATTRIBUTES.mBpm.put(Constants.POSITION.RIGHT, bpm5);
    }

    public enum ATTRIBUTES {
        ALL, COLOR, TEXT_SIZE, STROKE_WIDTH
    }

    private int mColor;
    private float mTextSize;
    private float mStrokeWidth;
    private Map<Constants.POSITION, BorderPaddingMargin> mBpm;

    public GraphStyleAttributes() {
        super();

        mColor = Color.WHITE;
        mTextSize = 20f;
        mStrokeWidth = 2f;

        mBpm = new HashMap<Constants.POSITION, BorderPaddingMargin>();
    }

    public GraphStyleAttributes(int color, float textSize, float strokeWidth) {
        super();

        mColor = color;
        mTextSize = textSize;
        mStrokeWidth = strokeWidth;
    }

    public void updatePaintInstance(Paint paint) {
        final EnumSet<ATTRIBUTES> sALL_ATTRIBUTES = EnumSet.of(ATTRIBUTES.ALL);

        updatePaintInstance(paint, sALL_ATTRIBUTES);
    }

    public void updatePaintInstance(Paint paint, EnumSet<ATTRIBUTES> attributes) {

        for(ATTRIBUTES attr : attributes) {
            switch(attr) {
                case COLOR: paint.setColor(mColor); break;
                case TEXT_SIZE: paint.setTextSize(mTextSize);break;
                case STROKE_WIDTH: paint.setStrokeWidth(mStrokeWidth); break;
                case ALL: paint.setColor(mColor); paint.setTextSize(mTextSize);
                    paint.setStrokeWidth(mStrokeWidth); break;
            }
        }

    }

    public GraphStyleAttributes setColor(int color) {
        mColor = color;
        return this;
    }

    public GraphStyleAttributes setTextSize(float textSize) {
        mTextSize = textSize;
        return this;
    }

    public GraphStyleAttributes setStrokeWidth(float strokeWidth) {
        mStrokeWidth = strokeWidth;
        return this;
    }

    public BorderPaddingMargin getBorderPaddingMargin(Constants.POSITION position) {
        return mBpm == null ? null : mBpm.get(position);
    }
    public GraphStyleAttributes putBorderPaddingMargin(Constants.POSITION position, BorderPaddingMargin bpm) {
        synchronized(this) {
            if (mBpm == null) {
                mBpm = new HashMap<Constants.POSITION, BorderPaddingMargin>();
            }
        }
        mBpm.put(position, bpm);
        return this;
    }

    public float getPaddingMarginWidth(EnumSet<Constants.POSITION> positions) {

        float computed = 0f;

        for(Constants.POSITION position : positions) {
            BorderPaddingMargin bpm = getBorderPaddingMargin(position);
            computed += bpm == null ? 0f : bpm.getPaddingWidth() + bpm.getMarginWidth() + 0f;
        }

        return computed;
    }

    public static class BorderPaddingMargin {

        private int mBorderColor;
        private float mBorderWidth;
        private float mPaddingWidth;
        private float mMarginWidth;

        public BorderPaddingMargin() {
            super();

            mMarginWidth = 1f;
        }

        public BorderPaddingMargin(float paddingWidth, float marginWidth) {
            super();

            mPaddingWidth = paddingWidth;
            mMarginWidth = marginWidth;
        }

        public BorderPaddingMargin(float paddingWidth, float marginWidth, float borderWidth, int borderColor) {
            this(paddingWidth, marginWidth);

            mBorderWidth = borderWidth;
            mBorderColor = borderColor;
        }

        public int getBorderColor() {
            return mBorderColor;
        }
        public void setBorderColor(int borderColor) {
            mBorderColor = borderColor;
        }

        public float getBorderWidth() {
            return mBorderWidth;
        }
        public void setBorderWidth(float borderWidth) {
            mBorderWidth = borderWidth;
        }

        public float getPaddingWidth() {
            return mPaddingWidth;
        }
        public void setPaddingWidth(float paddingWidth) {
            mPaddingWidth = paddingWidth;
        }

        public float getMarginWidth() {
            return mMarginWidth;
        }
        public void setMarginWidth(float marginWidth) {
            mMarginWidth = marginWidth;
        }
    }

    public static class DefaultGraphStyleAttributes extends GraphStyleAttributes {

        @Override
        public GraphStyleAttributes setStrokeWidth(float strokeWidth) {
            throw new UnsupportedOperationException("Cannot set on a DefaultGraphStyleAttributes instance");
        }

        @Override
        public GraphStyleAttributes setColor(int color) {
            throw new UnsupportedOperationException("Cannot set on a DefaultGraphStyleAttributes instance");
        }

        @Override
        public GraphStyleAttributes setTextSize(float textSize) {
            throw new UnsupportedOperationException("Cannot set on a DefaultGraphStyleAttributes instance");
        }

        @Override
        public GraphStyleAttributes putBorderPaddingMargin(Constants.POSITION position, BorderPaddingMargin bpm) {
            throw new UnsupportedOperationException("Cannot set on a DefaultGraphStyleAttributes instance");
        }
    }
}
