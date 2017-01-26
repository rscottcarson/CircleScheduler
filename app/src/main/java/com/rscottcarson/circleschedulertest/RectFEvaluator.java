package com.rscottcarson.circleschedulertest;

import android.animation.TypeEvaluator;
import android.graphics.RectF;

import static android.R.attr.fraction;
import static android.R.attr.x;

/**
 * Created by scottcarson on 1/12/17.
 */

public class RectFEvaluator implements TypeEvaluator<RectF>{
    @Override
    public RectF evaluate(float v, RectF rectF, RectF t1) {
        RectF newRectF = new RectF();

        newRectF.left = rectF.left + v * (t1.left - rectF.left);
        newRectF.top = rectF.top + v * (t1.top - rectF.top);
        newRectF.right = rectF.right + v * (t1.right - rectF.right);
        newRectF.bottom = rectF.bottom + v * (t1.bottom - rectF.bottom);

        return newRectF;
    }
}
