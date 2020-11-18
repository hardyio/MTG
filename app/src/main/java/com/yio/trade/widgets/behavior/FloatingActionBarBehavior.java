package com.yio.trade.widgets.behavior;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class FloatingActionBarBehavior extends FloatingActionButton.Behavior {

    private boolean isVisible = true;

    public FloatingActionBarBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout,
                                       @NonNull FloatingActionButton child,
                                       @NonNull View directTargetChild, @NonNull View target,
                                       int axes, int type) {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL ||
                super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, axes, type);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onNestedPreScroll(@NonNull CoordinatorLayout coordinatorLayout,
                                  @NonNull FloatingActionButton child, @NonNull View target, int dx,
                                  int dy, @NonNull int[] consumed, int type) {
        if (Math.abs(dy) < 10) {
            return;
        }
        if (dy > 0 && isVisible) {
            child.setVisibility(View.INVISIBLE);
            isVisible = false;
        } else if (dy < 0 && !isVisible) {
            child.show();
            isVisible = true;
        }
    }
}
