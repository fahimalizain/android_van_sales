package com.casualmill.vansales.animation;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Copied from gist: https://gist.github.com/rafali/5146957
 * Added height animation to it
 */
public class ResizeAnimation extends Animation {
    private final int start;
    private final int target;
    private View view;
    private Boolean resizeHeight;

    public ResizeAnimation(View view, int target, Boolean resizeHeight) {
        this.view = view;
        this.target = target;
        this.resizeHeight = resizeHeight;
        start = resizeHeight? view.getHeight() : view.getWidth();
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        int newValue = (int) (start + (target - start) * interpolatedTime);
        if (resizeHeight)
            view.getLayoutParams().height = newValue;
        else
            view.getLayoutParams().width = newValue;
        view.requestLayout();
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}
