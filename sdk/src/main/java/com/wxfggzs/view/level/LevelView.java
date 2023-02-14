package com.wxfggzs.view.level;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.view.View;


@SuppressWarnings("all")
public class LevelView extends View implements SensorEventListener {
    private float[] accValues;
    private Sensor acc_sensor;
    private Bitmap bitmap;
    private PointF bubblePoint;
    private PointF centerPnt;
    private int mBubbleColor;
    private Paint mBubblePaint;
    private float mBubbleRadius;
    private int mBubbleRuleColor;
    private Paint mBubbleRulePaint;
    private float mBubbleRuleRadius;
    private float mBubbleRuleWidth;
    private int mHorizontalColor;
    private float mLimitCircleWidth;
    private int mLimitColor;
    private Paint mLimitPaint;
    private float mLimitRadius;
    private float[] magValues;
    private Sensor mag_sensor;
    private double pitchAngle;
    private float[] r;
    private double rollAngle;
    private SensorManager sensorManager;
    private float[] values;

    @Override // android.hardware.SensorEventListener
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    public LevelView(Context context) {
        super(context);
        this.mLimitRadius = 0.0f;
        this.mLimitColor = -1;
        this.mBubbleRuleColor = -1;
        this.mHorizontalColor = -1;
        this.mBubbleColor = -1;
        this.centerPnt = new PointF();
        this.pitchAngle = -90.0d;
        this.rollAngle = -90.0d;
        this.accValues = new float[3];
        this.magValues = new float[3];
        this.r = new float[9];
        this.values = new float[3];
        init(null, 0);
    }

    public LevelView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mLimitRadius = 0.0f;
        this.mLimitColor = -1;
        this.mBubbleRuleColor = -1;
        this.mHorizontalColor = -1;
        this.mBubbleColor = -1;
        this.centerPnt = new PointF();
        this.pitchAngle = -90.0d;
        this.rollAngle = -90.0d;
        this.accValues = new float[3];
        this.magValues = new float[3];
        this.r = new float[9];
        this.values = new float[3];
        init(attributeSet, 0);
    }

    public LevelView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mLimitRadius = 0.0f;
        this.mLimitColor = -1;
        this.mBubbleRuleColor = -1;
        this.mHorizontalColor = -1;
        this.mBubbleColor = -1;
        this.centerPnt = new PointF();
        this.pitchAngle = -90.0d;
        this.rollAngle = -90.0d;
        this.accValues = new float[3];
        this.magValues = new float[3];
        this.r = new float[9];
        this.values = new float[3];
        init(attributeSet, i);
    }

    public static int dip2px(Context context, float f) {
        return (int) ((f * context.getResources().getDisplayMetrics().density) + 0.5f);
    }

    private void init(AttributeSet attributeSet, int i) {
        TypedArray obtainStyledAttributes = getContext().obtainStyledAttributes(attributeSet, R.styleable.LevelView, i, 0);
        this.mBubbleRuleColor = obtainStyledAttributes.getColor(R.styleable.LevelView_bubbleRuleColor, this.mBubbleRuleColor);
        this.mBubbleColor = obtainStyledAttributes.getColor(R.styleable.LevelView_bubbleColor, this.mBubbleColor);
        this.mLimitColor = obtainStyledAttributes.getColor(R.styleable.LevelView_limitColor, this.mLimitColor);
        this.mHorizontalColor = obtainStyledAttributes.getColor(R.styleable.LevelView_horizontalColor, this.mHorizontalColor);
        this.mLimitRadius = obtainStyledAttributes.getDimension(R.styleable.LevelView_limitRadius, dip2px(getContext(), 39.0f));
        this.mBubbleRadius = obtainStyledAttributes.getDimension(R.styleable.LevelView_bubbleRadius, dip2px(getContext(), 8.0f));
        this.mLimitCircleWidth = obtainStyledAttributes.getDimension(R.styleable.LevelView_limitCircleWidth, dip2px(getContext(), 1.0f));
        this.mBubbleRuleWidth = obtainStyledAttributes.getDimension(R.styleable.LevelView_bubbleRuleWidth, dip2px(getContext(), 1.0f));
        this.mBubbleRuleRadius = obtainStyledAttributes.getDimension(R.styleable.LevelView_bubbleRuleRadius, dip2px(getContext(), 8.0f));
        obtainStyledAttributes.recycle();
        this.mBubblePaint = new Paint();
        this.mBubblePaint.setColor(this.mBubbleColor);
        this.mBubblePaint.setStyle(Paint.Style.FILL);
        this.mBubblePaint.setAntiAlias(true);
        this.mLimitPaint = new Paint();
        this.mLimitPaint.setStyle(Paint.Style.STROKE);
        this.mLimitPaint.setColor(this.mLimitColor);
        this.mLimitPaint.setStrokeWidth(this.mLimitCircleWidth);
        this.mLimitPaint.setAntiAlias(true);
        this.mBubbleRulePaint = new Paint();
        this.mBubbleRulePaint.setColor(this.mBubbleRuleColor);
        this.mBubbleRulePaint.setStyle(Paint.Style.STROKE);
        this.mBubbleRulePaint.setStrokeWidth(this.mBubbleRuleWidth);
        this.mBubbleRulePaint.setAntiAlias(true);
        this.bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ic_level_bubble);
        this.sensorManager = (SensorManager) getContext().getSystemService("sensor");
    }

    @Override // android.view.View
    public void setVisibility(int i) {
        super.setVisibility(i);
        if (i == 8) {
            onStop();
        } else {
            onResume();
        }
    }

    public void onResume() {
        this.acc_sensor = this.sensorManager.getDefaultSensor(1);
        this.mag_sensor = this.sensorManager.getDefaultSensor(2);
        this.sensorManager.registerListener(this, this.acc_sensor, 1);
        this.sensorManager.registerListener(this, this.mag_sensor, 1);
    }

    public void onPause() {
        this.sensorManager.unregisterListener(this);
    }

    public void onStop() {
        this.sensorManager.unregisterListener(this);
    }

    @Override // android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mBubblePaint = null;
        this.mLimitPaint = null;
        this.mBubbleRulePaint = null;
        this.centerPnt = null;
        this.bubblePoint = null;
        SensorManager sensorManager = this.sensorManager;
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
            this.sensorManager = null;
        }
        this.acc_sensor = null;
        this.mag_sensor = null;
        this.accValues = null;
        this.magValues = null;
        Bitmap bitmap = this.bitmap;
        if (bitmap == null || bitmap.isRecycled()) {
            return;
        }
        this.bitmap.recycle();
        this.bitmap = null;
    }

    @Override // android.hardware.SensorEventListener
    public void onSensorChanged(SensorEvent sensorEvent) {
        switch (sensorEvent.sensor.getType()) {
            case 1:
                this.accValues = (float[]) sensorEvent.values.clone();
                break;
            case 2:
                this.magValues = (float[]) sensorEvent.values.clone();
                break;
        }
        SensorManager.getRotationMatrix(this.r, null, this.accValues, this.magValues);
        SensorManager.getOrientation(this.r, this.values);
        float[] fArr = this.values;
        float f = fArr[1] * 3.0f;
        onAngleChanged((-fArr[2]) * 3.0f, f, fArr[0] * 3.0f);
    }

    private void onAngleChanged(float f, float f2, float f3) {
        setAngle(f, f2);
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        calculateCenter(i, i2);
    }

    private void calculateCenter(int i, int i2) {
        float min = Math.min(MeasureSpec.makeMeasureSpec(i, 0), MeasureSpec.makeMeasureSpec(i2, 0)) / 2;
        this.centerPnt.set(min, min);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(this.centerPnt.x, this.centerPnt.y, this.mBubbleRuleRadius, this.mBubbleRulePaint);
        canvas.drawCircle(this.centerPnt.x, this.centerPnt.y, this.mLimitRadius, this.mLimitPaint);
        drawBubble(canvas);
    }

    private boolean isCenter(PointF pointF) {
        return pointF != null && Math.abs(pointF.x - this.centerPnt.x) < 1.0f && Math.abs(pointF.y - this.centerPnt.y) < 1.0f;
    }

    private void drawBubble(Canvas canvas) {
        if (this.bubblePoint != null) {
            if (Math.abs(this.centerPnt.x - this.bubblePoint.x) < 5.0f) {
                this.bubblePoint.x = this.centerPnt.x;
            }
            if (Math.abs(this.centerPnt.y - this.bubblePoint.y) < 5.0f) {
                this.bubblePoint.y = this.centerPnt.y;
            }
            canvas.drawBitmap(this.bitmap, (Rect) null, new RectF(this.bubblePoint.x - this.mBubbleRadius, this.bubblePoint.y - this.mBubbleRadius, this.bubblePoint.x + this.mBubbleRadius, this.bubblePoint.y + this.mBubbleRadius), this.mBubbleRulePaint);
        }
    }

    private PointF convertCoordinate(double d, double d2, double d3) {
        double radians = d3 / Math.toRadians(90.0d);
        return new PointF((float) (this.centerPnt.x - (-(d * radians))), (float) (this.centerPnt.y - (-(d2 * radians))));
    }

    public void setAngle(double d, double d2) {
        this.pitchAngle = d2;
        this.rollAngle = d;
        float f = this.mLimitRadius;
        float f2 = f - this.mBubbleRadius;
        this.bubblePoint = convertCoordinate(d, d2, f);
        outLimit(this.bubblePoint, f2);
        if (outLimit(this.bubblePoint, f2)) {
            onCirclePoint(this.bubblePoint, f2);
        }
        invalidate();
    }

    private boolean outLimit(PointF pointF, float f) {
        return (((pointF.x - this.centerPnt.x) * (pointF.x - this.centerPnt.x)) + ((this.centerPnt.y - pointF.y) * (this.centerPnt.y - pointF.y))) - (f * f) > 0.0f;
    }

    private PointF onCirclePoint(PointF pointF, double d) {
        double atan2 = Math.atan2(pointF.y - this.centerPnt.y, pointF.x - this.centerPnt.x);
        if (atan2 < 0.0d) {
            atan2 += 6.283185307179586d;
        }
        pointF.set((float) (this.centerPnt.x + (Math.cos(atan2) * d)), (float) (this.centerPnt.y + (d * Math.sin(atan2))));
        return pointF;
    }

    public double getPitchAngle() {
        return this.pitchAngle;
    }

    public double getRollAngle() {
        return this.rollAngle;
    }
}
