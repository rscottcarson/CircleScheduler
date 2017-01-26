package com.rscottcarson.circleschedulertest;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by scottcarson on 1/11/17.
 */

public class CircleSchedulerView extends View {


    private static final String TAG = CircleSchedulerView.class.getSimpleName();
    /*
     * Constants used to save/restore the instance state.
     */
    private static final String STATE_PARENT = "parent";
    private static final String STATE_ANGLE = "angle";
    private static final String STATE_OLD_COLOR = "color";
    private static final String STATE_SHOW_OLD_COLOR = "showColor";

    /**
     * Colors to construct the color wheel using {@link android.graphics.SweepGradient}.
     */
    private static final int[] COLORS = new int[] {
            0xFFFF0000,     // RED
            0xFFFF00FF,     // FUSCHIA
            0xFF0000FF,     // BLUE
            0xFF00FFFF,     // CYAN  (light blue)
            //0xFFFFFFFF,     // WHITE
            0xFF00FF00,     // GREEN
            0xFFFFFF00,     // YELLOW
            0xFFFF0000 };   // RED

    /**
     * {@code Paint} instance used to draw the color wheel.
     */
    private Paint mColorWheelPaint;

    private Paint arcPaint;

    private Paint mBackgroundPaint;

    /**
     * {@code Paint} instance used to draw the pointer's "halo".
     */
    private Paint mPointerHaloPaint;

    /**
     * {@code Paint} instance used to draw the pointer (the selected color).
     */
    private Paint mPointerColor;

    /**
     * The width of the color wheel thickness.
     */
    private int mColorWheelThickness;

    /**
     * The radius of the color wheel.
     */
    private int mColorWheelRadius;
    private int mPreferredColorWheelRadius;

    /**
     * The radius of the center circle inside the color wheel.
     */
    private int mColorCenterRadius;
    private int mPreferredColorCenterRadius;

    /**
     * The radius of the halo of the center circle inside the color wheel.
     */
    private int mColorCenterHaloRadius;
    private int mPreferredColorCenterHaloRadius;

    private int mSelectedSegmentThickness;

    /**
     * The radius of the pointer.
     */
    private int mColorPointerRadius;

    /**
     * For the color circles surrounding the color wheel
     */
    private int mColorCircleHaloRadius, mColorCircleDistance;

    /**
     * The radius of the halo of the pointer.
     */
    private int mColorPointerHaloRadius;

    /**
     * The rectangle enclosing the color wheel.
     */
    private RectF mColorWheelRectangle = new RectF();

    /**
     * The rectangle enclosing the center inside the color wheel.
     */
    private RectF mCenterRectangle = new RectF();

    /**
     * {@code true} if the user clicked on the pointer to start the move mode. <br>
     * {@code false} once the user stops touching the screen.
     *
     * @see #onTouchEvent(android.view.MotionEvent)
     */
    private boolean mUserIsMovingPointer = false;

    /**
     * The ARGB value of the currently selected color.
     */
    private int mColor;


    /**
     * Number of pixels the origin of this view is moved in X- and Y-direction.
     *
     * <p>
     * We use the center of this (quadratic) View as origin of our internal
     * coordinate system. Android uses the upper left corner as origin for the
     * View-specific coordinate system. So this is the value we use to translate
     * from one coordinate system to the other.
     * </p>
     *
     * <p>
     * Note: (Re)calculated in {@link #onMeasure(int, int)}.
     * </p>
     *
     * @see #onDraw(android.graphics.Canvas)
     */
    private float mTranslationOffset;

    private PointF mOffsetPoint;

    /**
     * Distance between pointer and user touch in X-direction.
     */
    private float mSlopX;

    /**
     * Distance between pointer and user touch in Y-direction.
     */
    private float mSlopY;

    /**
     * The pointer's position expressed as angle (in rad).
     */
    private float mAngle;


    /**
     * {@code Paint} instance used to draw the center with the new selected
     * color.
     */
    private Paint mCenterNewPaint;

    /**
     * {@code Paint} instance used to draw the halo of the center selected
     * colors.
     */
    private Paint mCenterHaloPaint;



    public CircleSchedulerView(Context context) {
        super(context);
        init(null, 0);
    }

    public CircleSchedulerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public CircleSchedulerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }


    private void init(AttributeSet attrs, int defStyle) {

        final TypedArray a = getContext().obtainStyledAttributes(attrs,
                R.styleable.ColorPicker, defStyle, 0);
        final Resources b = getContext().getResources();

        mColorWheelThickness = a.getDimensionPixelSize(
                R.styleable.ColorPicker_color_wheel_thickness,
                b.getDimensionPixelSize(R.dimen.color_wheel_thickness));
        mSelectedSegmentThickness = a.getDimensionPixelSize(
                R.styleable.ColorPicker_selected_segment_thickness,
                b.getDimensionPixelSize(R.dimen.selected_segment_thickness));
        mColorWheelRadius = a.getDimensionPixelSize(
                R.styleable.ColorPicker_color_wheel_radius,
                b.getDimensionPixelSize(R.dimen.color_wheel_radius));
        mPreferredColorWheelRadius = mColorWheelRadius;
        mColorCenterRadius = a.getDimensionPixelSize(
                R.styleable.ColorPicker_color_center_radius,
                b.getDimensionPixelSize(R.dimen.color_center_radius));
        mPreferredColorCenterRadius = mColorCenterRadius;
        mColorCenterHaloRadius = a.getDimensionPixelSize(
                R.styleable.ColorPicker_color_center_halo_radius,
                b.getDimensionPixelSize(R.dimen.color_center_halo_radius));
        mPreferredColorCenterHaloRadius = mColorCenterHaloRadius;
        mColorPointerRadius = a.getDimensionPixelSize(
                R.styleable.ColorPicker_color_pointer_radius,
                b.getDimensionPixelSize(R.dimen.color_pointer_radius));
        mColorPointerHaloRadius = a.getDimensionPixelSize(
                R.styleable.ColorPicker_color_pointer_halo_radius,
                b.getDimensionPixelSize(R.dimen.color_pointer_halo_radius));

        mColorCircleHaloRadius = a.getDimensionPixelSize(
                R.styleable.ColorPicker_color_circle_halo_radius,
                b.getDimensionPixelSize(R.dimen.color_circle_halo_radius));

        mColorCircleDistance = a.getDimensionPixelSize(
                R.styleable.ColorPicker_color_circle_distance,
                b.getDimensionPixelSize(R.dimen.color_circle_distance));

        mOffsetPoint = new PointF(0.0f, 0.0f);

        a.recycle();

        mAngle = (float) (0);

        mColorWheelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mColorWheelPaint.setStyle(Paint.Style.STROKE);
        mColorWheelPaint.setStrokeWidth(mColorWheelThickness);

        mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundPaint.setColor(Color.WHITE);
        mBackgroundPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mBackgroundPaint.setStrokeWidth(mColorWheelThickness);
        mBackgroundPaint.setShadowLayer(20f, 0.0f, 0.0f, Color.BLACK);

        mPointerHaloPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPointerHaloPaint.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        mPointerHaloPaint.setAlpha(0xFF);

        mPointerColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPointerColor.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        mPointerColor.setShadowLayer(20f, 0.0f, 0.0f, Color.BLACK);

        arcPaint = new Paint(Paint.FILTER_BITMAP_FLAG |
                Paint.DITHER_FLAG |
                Paint.ANTI_ALIAS_FLAG);
        arcPaint.setColor(Color.YELLOW);
        arcPaint.setStrokeWidth(mColorWheelThickness+1);



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(LAYER_TYPE_SOFTWARE, mPointerColor);
            setLayerType(LAYER_TYPE_SOFTWARE, mBackgroundPaint);
        }

    }

/*    @Override
    protected void onDraw(Canvas canvas) {
        // All of our positions are using our internal coordinate system.
        // Instead of translating
        // them we let Canvas do the work for us.
        canvas.translate(mTranslationOffset, mTranslationOffset);

        // Draw the halo of the center colors.
        //canvas.drawCircle(0, 0, mColorCenterHaloRadius, mCenterHaloPaint);

        int inner_radius = mColorWheelRadius - mSelectedSegmentThickness/2;
        int outer_radius = mColorWheelRadius + mSelectedSegmentThickness/2;

                // Draw the color wheel.

        canvas.drawOval(mColorWheelRectangle, mColorWheelPaint);

        float[] pointerPosition1 = calculatePointerPosition(mAngle);
        float[] pointerPosition2 = calculatePointerPosition(mAngle + (float) (Math.PI / 2f));

        //canvas.translate(-mTranslationOffset, -mTranslationOffset);

        int arc_sweep = 90;
        int arc_offset = 0;

        RectF outer_rect = new RectF(-outer_radius, -outer_radius, outer_radius, outer_radius);
        RectF inner_rect = new RectF(-inner_radius, -inner_radius, inner_radius, inner_radius);

        Path path = new Path();
        path.arcTo(outer_rect, arc_offset, arc_sweep);
        path.arcTo(inner_rect, arc_offset + arc_sweep, -arc_sweep);
        path.close();

        Paint fill = new Paint(Paint.ANTI_ALIAS_FLAG);
        fill.setDither(true);
        fill.setStyle(Paint.Style.FILL_AND_STROKE);
        fill.setColor(Color.BLUE);
        fill.setStrokeCap(Paint.Cap.ROUND);
        fill.setDither(true);                    // set the dither to true
        fill.setStrokeJoin(Paint.Join.ROUND);    // set the join to round you want
        canvas.drawPath(path, fill);

        // Draw the pointer's "halo"
        canvas.drawCircle(pointerPosition1[0], pointerPosition1[1],
                mColorPointerHaloRadius, mPointerHaloPaint);

        // Draw the pointer (the currently selected color) slightly smaller on
        // top.
        canvas.drawCircle(pointerPosition1[0], pointerPosition1[1],
                mColorPointerRadius, mPointerColor);

        // Draw the pointer's "halo"
        canvas.drawCircle(pointerPosition2[0], pointerPosition2[1],
                mColorPointerHaloRadius, mPointerHaloPaint);

        // Draw the pointer (the currently selected color) slightly smaller on
        // top.
        canvas.drawCircle(pointerPosition2[0], pointerPosition2[1],
                mColorPointerRadius, mPointerColor);

    }*/



    @Override
    protected void onDraw(Canvas canvas) {
        // All of our positions are using our internal coordinate system.
        // Instead of translating
        // them we let Canvas do the work for us.
        //canvas.translate(mTranslationOffset, mTranslationOffset);

        // Draw the halo of the center colors.
        //canvas.drawCircle(0, 0, mColorCenterHaloRadius, mCenterHaloPaint);

        int inner_radius = mColorWheelRadius - mSelectedSegmentThickness/2;
        int outer_radius = mColorWheelRadius + mSelectedSegmentThickness/2;

        // Draw the color wheel.

        canvas.drawOval(mColorWheelRectangle, mColorWheelPaint);

        float[] pointerPosition1 = calculatePointerPosition(mAngle);
        float[] pointerPosition2 = calculatePointerPosition(mAngle + (float) (Math.PI / 2f));

        //canvas.translate(-mTranslationOffset, -mTranslationOffset);

        int arc_sweep = 90;
        int arc_offset = 0;

        RectF outer_rect = new RectF(-outer_radius + mOffsetPoint.x, -outer_radius + mOffsetPoint.y, outer_radius + mOffsetPoint.x, outer_radius + mOffsetPoint.y);
        RectF inner_rect = new RectF(-inner_radius + mOffsetPoint.x, -inner_radius + mOffsetPoint.y, inner_radius + mOffsetPoint.x, inner_radius + mOffsetPoint.y);

        Path path = new Path();
        path.arcTo(outer_rect, arc_offset, arc_sweep);
        path.arcTo(inner_rect, arc_offset + arc_sweep, -arc_sweep);
        path.close();

        Paint fill = new Paint(Paint.ANTI_ALIAS_FLAG);
        fill.setDither(true);
        fill.setStyle(Paint.Style.FILL_AND_STROKE);
        fill.setColor(Color.BLUE);
        fill.setStrokeCap(Paint.Cap.ROUND);
        fill.setDither(true);                    // set the dither to true
        fill.setStrokeJoin(Paint.Join.ROUND);    // set the join to round you want
        canvas.drawPath(path, fill);

        // Draw the pointer's "halo"
        canvas.drawCircle(pointerPosition1[0] + mOffsetPoint.x, pointerPosition1[1] + mOffsetPoint.y,
                mColorPointerHaloRadius, mPointerHaloPaint);

        // Draw the pointer (the currently selected color) slightly smaller on
        // top.
        canvas.drawCircle(pointerPosition1[0] + mOffsetPoint.x, pointerPosition1[1] + mOffsetPoint.y,
                mColorPointerRadius, mPointerColor);

        // Draw the pointer's "halo"
        canvas.drawCircle(pointerPosition2[0] + mOffsetPoint.x, pointerPosition2[1] + mOffsetPoint.y,
                mColorPointerHaloRadius, mPointerHaloPaint);

        // Draw the pointer (the currently selected color) slightly smaller on
        // top.
        canvas.drawCircle(pointerPosition2[0] + mOffsetPoint.x, pointerPosition2[1] + mOffsetPoint.y,
                mColorPointerRadius, mPointerColor);

    }


    /**
     * Calculate the pointer's coordinates on the color wheel using the supplied
     * angle.
     *
     * @param angle The position of the pointer expressed as angle (in rad).
     *
     * @return The coordinates of the pointer's center in our internal
     *         coordinate system.
     */
    private float[] calculatePointerPosition(float angle) {
        float x = (float) (mColorWheelRadius * Math.cos(angle));
        float y = (float) (mColorWheelRadius * Math.sin(angle));

        return new float[] { x, y };
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int intrinsicSize = 2 * (mColorCircleDistance + mColorCircleHaloRadius);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(intrinsicSize, widthSize);
        } else {
            width = intrinsicSize;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(intrinsicSize, heightSize);
        } else {
            height = intrinsicSize;
        }

        int min = Math.min(width, height);
        setMeasuredDimension(min, min);
        mTranslationOffset = min * 0.5f;
        mOffsetPoint.set(mTranslationOffset, mTranslationOffset);

        // fill the rectangle instances.
        mColorWheelRadius = min / 2 - mColorWheelThickness - mColorPointerHaloRadius;
        mColorWheelRectangle.set(-mColorWheelRadius + mTranslationOffset, -mColorWheelRadius + mTranslationOffset,
                mColorWheelRadius + mTranslationOffset, mColorWheelRadius + mTranslationOffset);


    }

    public int getCircleRadius() {
        return mColorWheelRadius;
    }

    public void setCircleRadius(int _ColorWheelRadius) {
        mColorWheelRadius = _ColorWheelRadius;
    }

    public PointF getOffsetPoint(){
        return mOffsetPoint;
    }

    public void setOffsetPoint(PointF _OffsetPoint){
        mOffsetPoint.set(_OffsetPoint.x, _OffsetPoint.y);
    }

    public RectF getCircleRectangle(){
        return mColorWheelRectangle;
    }

    public void setCircleRectangle(RectF _CircleRectangle){
        mColorWheelRectangle = _CircleRectangle;
    }
}
