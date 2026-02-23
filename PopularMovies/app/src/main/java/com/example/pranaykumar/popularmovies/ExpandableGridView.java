package com.example.pranaykumar.popularmovies;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * A GridView that expands to show all its items when placed inside a ScrollView.
 * Replaces the deprecated ExpandableHeightGridView library.
 */
public class ExpandableGridView extends GridView {

    public ExpandableGridView(Context context) {
        super(context);
    }

    public ExpandableGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExpandableGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
