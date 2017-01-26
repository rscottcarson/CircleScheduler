package com.rscottcarson.circleschedulertest;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.PointFEvaluator;
import android.animation.ValueAnimator;
import android.graphics.PointF;
import android.graphics.RectF;
import android.transition.Transition;
import android.transition.TransitionValues;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;


import java.util.ArrayList;
import java.util.List;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;

/**
 * Created by scottcarson on 1/12/17.
 */

public class CircleSchedulerTransition extends Transition {

    private static final String TAG = CircleSchedulerTransition.class.getSimpleName();

    // Defined property value keys
    private static final String PROPNAME_OFFSET_POINT =
            "com.rscottcarson.circleschedulertest.CircleSchedulerTransition:offsetx";
    private static final String PROPNAME_CIRCLE_RADIUS =
            "com.rscottcarson.circleschedulertest.CircleSchedulerTransition:circleradius";
    private static final String PROPNAME_CIRCLE_RECTF =
            "com.rscottcarson.circleschedulertest.CircleSchedulerTransition:circlerectf";

    private static final boolean USE_INTERPOLATOR = true;

    @Override
    public void captureStartValues(TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    @Override
    public void captureEndValues(TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

   @Override
    public Animator createAnimator(ViewGroup sceneRoot,
                                   TransitionValues startValues,
                                   TransitionValues endValues) {

       Log.e(TAG, "Creating animator!");
        AnimatorSet animatorSet = new AnimatorSet();
        if(startValues == null || endValues == null){
            return null;
        }

        // reference to target
        final CircleSchedulerView view = (CircleSchedulerView) endValues.view;

        // Create animator objects
        ValueAnimator circleRadiusAnimator = ValueAnimator.ofInt(
                (int) startValues.values.get(PROPNAME_CIRCLE_RADIUS),
                (int) endValues.values.get(PROPNAME_CIRCLE_RADIUS));

        circleRadiusAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Object value = valueAnimator.getAnimatedValue();

                Log.e(TAG, "changing Radius");

                if(value != null){
                    view.setCircleRadius((int) value);
                    Log.e(TAG, "changing Radius");
                }
            }
        });


        ValueAnimator offsetPointAnimator = ValueAnimator.ofObject(
                new PointFEvaluator(),
                startValues.values.get(PROPNAME_OFFSET_POINT),
                endValues.values.get(PROPNAME_OFFSET_POINT));
       offsetPointAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Object value = valueAnimator.getAnimatedValue();

                Log.e(TAG, "changing PointF");

                if(value != null){
                    view.setOffsetPoint((PointF) value);
                    Log.e(TAG, "changing PointF");
                }
            }
        });


        ValueAnimator circleRectfAnimator = ValueAnimator.ofObject(
                        new RectFEvaluator(),
                        startValues.values.get(PROPNAME_CIRCLE_RECTF),
                        endValues.values.get(PROPNAME_CIRCLE_RECTF));
        circleRectfAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Object value = valueAnimator.getAnimatedValue();

                Log.e(TAG, "changing RectF");

                if(value != null){
                    view.setCircleRectangle((RectF) value);
                    Log.e(TAG, "changing RectF");
                }
            }
        });

       // Add all of the animations to our animator set
       animatorSet.playTogether(circleRadiusAnimator, offsetPointAnimator, circleRectfAnimator);


        return animatorSet;
    }

    // For the view in transitionValues.view, get the values you
    // want and put them in transitionValues.values
    private void captureValues(TransitionValues transitionValues) {

        // Get a reference to the CircleSchedulerView
        CircleSchedulerView view = (CircleSchedulerView) transitionValues.view;

        // Store its background property in the values map
        transitionValues.values.put(PROPNAME_OFFSET_POINT, view.getOffsetPoint());
        transitionValues.values.put(PROPNAME_CIRCLE_RADIUS, view.getCircleRadius());
        transitionValues.values.put(PROPNAME_CIRCLE_RECTF, view.getCircleRectangle());
    }
}
