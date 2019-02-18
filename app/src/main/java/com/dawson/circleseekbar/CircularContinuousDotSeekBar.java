package com.dawson.circleseekbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


public class CircularContinuousDotSeekBar extends View {
    private static final String TAG = "CircularContinuousDotSeekBar";
    private static final int STATUS_DEFAULT = 0;
    protected static final int STATUS_START = 1;
    protected static final int STATUS_UNKNOWN = 2;
    protected int status = STATUS_DEFAULT;
    protected int maxProgress = 100;
    protected int startAngle = 90;
    private int ancRotateRadius = 0;
    protected int centerX = 0;
    protected int centerY = 0;
    protected int circleRadius = 0;
    protected int thumbRadius;
    protected float thumbX = 0;
    protected float thumbY = 0;
    private float pressX = -1;
    private float pressY = -1;
    protected double progress = 0;
    private boolean controllable = true;
    private boolean drawThumb = true;
    private boolean drawSecondProgress = false;
    private boolean thumbInside = true;
    private boolean drawAncRotate = false;
    private boolean enable = true;

    protected Paint progressPaint;
    protected Paint thumbPaint;
    private Paint secondProgressPaint;
    protected Bitmap ancRotate;
    protected Context context = null;
    private RectF progressRect = new RectF();
    private OnProgressControlListener progressControlListener;

    public CircularContinuousDotSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public CircularContinuousDotSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CircularContinuousDotSeekBar(Context context) {
        super(context);
        init(context);
    }

    protected void init(Context context) {
        this.context = context;
        ancRotate = BitmapFactory.decodeResource(this.context.getResources(), R.mipmap.anc_rotate);

        thumbRadius = UI.dp2px(5.0f);

        thumbPaint = new Paint();
        thumbPaint.setColor(Color.WHITE);
        thumbPaint.setAntiAlias(true);

        final float barWidth = this.context.getResources().getDimension(R.dimen.now_playing_ring_width);
        progressPaint = new Paint();
        progressPaint.setColor(getResources().getColor(R.color.now_playing_progress_bar_primary_color));
        progressPaint.setAntiAlias(true);
        progressPaint.setStrokeWidth(barWidth);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);

        secondProgressPaint = new Paint();
        secondProgressPaint.setColor(getResources().getColor(R.color.black));
        secondProgressPaint.setAntiAlias(true);
        secondProgressPaint.setStrokeWidth(barWidth);
        secondProgressPaint.setStyle(Paint.Style.STROKE);
        secondProgressPaint.setAlpha(60);
    }

    /**
     * Gets the progress.
     *
     * @return the progress
     */
    public int getProgress() {
        return (int) progress;
    }

    /**
     * Sets the progress.
     *
     * @param progress the new progress
     */
    public void setProgress(int progress) {
        if (progress <= maxProgress) {
            if (this.progress != progress) {
                this.progress = (double) progress;
                GTLog.v(TAG, " Invalidate progress=" + progress);
                invalidate();
            }
        }
    }

    public void setColor(int progressColor, int backgroundDotsColor) {
        progressPaint.setColor(progressColor);
        thumbPaint.setColor(backgroundDotsColor);
    }

    public void setThumbInside(boolean thumbInside) {
        this.thumbInside = thumbInside;
    }

    public void setAlpha(int progressAlpha) {
        progressPaint.setAlpha(progressAlpha);
    }

    public void setMarkerColor(int color) {
        thumbPaint.setColor(color);
        invalidate();
    }

    public void setMarkerAlpha(int alpha) {
        thumbPaint.setAlpha(alpha);
        invalidate();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
//        GTLog.d(TAG, "onLayout - changed : " + changed + ", left : " + left + ", top : " + top + ", right : " + right + ", bottom : " + bottom);
        ancRotateRadius = ((ancRotate.getWidth() > ancRotate.getHeight() ? ancRotate.getWidth() : ancRotate.getHeight())) / 2;
        if (changed) {
            int width = right - left;
            int height = bottom - top;
            int size = (width > height ? height : width) / 2;
            centerX = width / 2;
            centerY = height / 2;
            circleRadius = size - UI.dp2px(6);
//            GTLog.d(TAG, "onLayout - centerX : width=" + width + " height=  " + height);
        }
//        GTLog.d(TAG, "onLayout - centerX : " + centerX + ", centerY : " + centerY + ", circleRadius : " + circleRadius + ", thumbRadius : " + thumbRadius);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.view.View#onDraw(android.graphics.Canvas)
     */
    @Override
    protected void onDraw(Canvas canvas) {
        /* It's easier to handle the drawing process in a polar coordination.
           DO NOT forget to handle the coordination transformation below because
           the canvas is translated to the center of the circle.
         */
        GTLog.d("transparent_zone", "onDraw()");
        canvas.translate(centerX, centerY);
//        float count = 100;
//        for (int i = 0; i < count; i++) {
//            canvas.drawCircle(0, circleRadius, context.getResources().getDimension(R.dimen.now_playing_ring_dot_width), thumbPaint);
//            canvas.rotate(360 / count, 0f, 0f);
//        }

        progressRect.set(-circleRadius, -circleRadius, circleRadius, circleRadius);
        float sweepAngle = (float) progress * 360f / maxProgress;
        if (controllable) {
            canvas.drawArc(progressRect, startAngle, sweepAngle, false, progressPaint);
        }
        // Draw the thumb
        if (drawThumb && controllable) {
            float xEnd = (float) (circleRadius * Math.sin(Math.toRadians(-sweepAngle)));
            float yEnd = (float) (circleRadius * Math.cos(Math.toRadians(-sweepAngle)));
            float cx;
            float cy;
            if (thumbInside) {
                cx = (float) (xEnd * 0.92);
                cy = (float) (yEnd * 0.92);
            } else {
                cx = xEnd;
                cy = yEnd;
            }
            canvas.drawCircle(cx, cy, thumbRadius, thumbPaint);
            thumbX = centerX + cx;
            thumbY = centerY + cy;
        }

        if (drawSecondProgress) {
            float sweepAngle_line = (float) (maxProgress - progress) * 360f / maxProgress;
            canvas.drawArc(progressRect, startAngle + sweepAngle, sweepAngle_line, false, secondProgressPaint);
        }
        if (drawAncRotate) {
            canvas.rotate(sweepAngle);
            canvas.drawBitmap(ancRotate, -ancRotateRadius, -ancRotateRadius, null);
        }
        super.onDraw(canvas);
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!enable) return false;

//        GTLog.d(TAG, "onTouchTest - progress - x : " + event.getX() + ", y : " + event.getY() + ", action : " + event.getActionMasked());
        if (!controllable) return false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                GTLog.d(TAG, "onTouchEvent - ACTION_DOWN");
                // if (isInMarkerPressed(event.getX(), event.getY())) {
                //if (isInAnglePressed(event.getX(), event.getY())) {
                if (isInCirclePressed(event.getX(), event.getY(), false)) {
                    pressX = event.getX();
                    pressY = event.getY();
                    status = STATUS_DEFAULT;

                    if (null != progressControlListener) {
                        status = STATUS_START;
                        GTLog.d(TAG, "progressListener - onProgressControlStart");
                        progressControlListener.onProgressControlStart(this);
                    }
                    return true;
                }
                return false;
            case MotionEvent.ACTION_MOVE: {
//                GTLog.d(TAG, "onTouchEvent - ACTION_MOVE");
                if (STATUS_UNKNOWN == status) {
                    return true;
                }
                if ((-1 == pressX) && (-1 == pressY)) {
//                    GTLog.w(TAG, "ACTION_MOVE - touch move not down in annulus");
                    if ((STATUS_START == status) && (null != progressControlListener)) {
                        status = STATUS_UNKNOWN;
//                        GTLog.d(TAG, "progressListener - onProgressControlEnd");
                        progressControlListener.onProgressControlEnd(this);
                    }
                    return true;
                }
                handleProgressChange(event.getX(), event.getY());
                return true;
            }
            case MotionEvent.ACTION_CANCEL:
                GTLog.d(TAG, "onTouchEvent - ACTION_CANCEL");
                return true;
            case MotionEvent.ACTION_UP:
                GTLog.d(TAG, "onTouchEvent - ACTION_UP");
                pressX = -1;
                pressY = -1;
                if (STATUS_UNKNOWN == status) {
                    return true;
                }
                if ((STATUS_START == status) && (null != progressControlListener)) {
                    status = STATUS_DEFAULT;
                    GTLog.d(TAG, "progressListener - onProgressControlEnd");
                    progressControlListener.onProgressControlEnd(this);
                }
                return true;
            default:
                GTLog.d(TAG, "onTouchEvent - action : " + event.getAction());
                return false;
        }
    }

    private void handleProgressChange(float x, float y) {
//        GTLog.d(TAG, "handleProgressChange - x : " + x + ", y : " + y + ", pressX : " + pressX + ", pressY : " + pressY);
        if ((-1 == pressX) && (-1 == pressY)) {
//            GTLog.w(TAG, "ACTION_MOVE - touch move not down in annulus");
            return;
        }
        // if (!isInMarkerPressed(x, y)) {
        // if (!isInAnglePressed(x, y)) {
        if (!isInCirclePressed(x, y, true)) {
            pressX = -1;
            pressY = -1;
//            GTLog.w(TAG, "handleProgressChange - not in big circle");
            return;
        }

        double alphaDown = Math.atan2((centerY - pressY), (pressX - centerX));
        double alphaT = Math.atan2((centerY - y), (x - centerX));
        //Avoid from -PI to PI
        if (alphaT - alphaDown > Math.PI) {
            alphaT = alphaT - 2 * Math.PI;
        }

        //Avoid from PI to -PI
        if (alphaDown - alphaT > Math.PI) {
            alphaT = 2 * Math.PI + alphaT;
        }

        double progress = -(float) (((alphaT - alphaDown) / (Math.PI * 2)) * maxProgress);
//        GTLog.d(TAG, "handleProgressChange - progress : " + progress + ", alphaDown : " + Math.toDegrees(alphaDown) + ", alphaT : " + Math.toDegrees(alphaT) + ", (Math.abs(alphaT - alphaDown) : " + (Math.abs(alphaT - alphaDown)));

        if (Math.abs(progress) > 0) {
            pressX = x;
            pressY = y;

//            GTLog.d(TAG, "handleProgressChange setProgress - progress : " + progress + ", progress : " + progress);
            this.progress += progress;
            if (0 > this.progress) {
                this.progress = 0;
            }

            if (maxProgress < this.progress) {
                this.progress = maxProgress;
            }

            if ((STATUS_START == status) && (null != progressControlListener)) {
//                GTLog.d(TAG, "progressListener - onProgressChange - progress : " + progress);
                progressControlListener.onProgressChange(this, (int) this.progress);
            }
            invalidate();
        }
    }

    protected boolean isInCirclePressed(float x, float y, boolean isMoving) {
        if (isMoving) {
            return true;
        }
        float distanceToCircleCenter = (float) Math.sqrt(Math.abs(centerX - x) * Math.abs(centerX - x) + Math.abs(centerY - y) * Math.abs(centerY - y));
        return distanceToCircleCenter > circleRadius - UI.dp2px(23.0f) && distanceToCircleCenter < circleRadius + UI.dp2px(20.0f);
    }

    protected boolean isInAnglePressed(float x, float y) {

        float line = (float) Math.sqrt(Math.abs(centerX - x) * Math.abs(centerX - x) + Math.abs(centerY - y) * Math.abs(centerY - y));
        if (line > circleRadius + UI.dp2px(20.0f)) {
            return false;
        }
        //  0-90-180  -180 -  -90  -  0     逆时针 0-100
        double makeAlpha = alphaToProcess(Math.toDegrees(Math.atan2((centerY - thumbY), (thumbX - centerX))));
        double currentAlpha = alphaToProcess(Math.toDegrees(Math.atan2((centerY - y), (x - centerX))));

        GTLog.d(TAG, "isInAnglePressed :        " + "       makeAlpha:  " + makeAlpha + "       currentAlpha:  " + currentAlpha + "       makeAlpha - 12.5:  " + (makeAlpha - 12.5) + "       makeAlpha + 12.5:  " + (makeAlpha + 12.5));

        if (makeAlpha + 12.5 > 100 || makeAlpha - 12.5 < 0) {
            return true;
        }
        if (currentAlpha >= makeAlpha - 12.5 && currentAlpha <= makeAlpha + 12.5) {
            return true;
        }
        return false;
    }

    protected double alphaToProcess(double alpha) {
        double progress = 0;
        if (alpha > 0) {
            progress = alpha / 360 * 100;
        } else if (alpha < 0) {
            progress = ((180 - Math.abs(alpha)) + 180) / 360 * 100;
        } else {
            progress = 100;
        }
        return progress;
    }

    protected boolean isInMarkerPressed(float x, float y) {
        int mRange = 6;
        float left, right, top, bottom;
        if (status == STATUS_START) {
            left = thumbX - circleRadius;
            right = thumbX + circleRadius;
            top = thumbY - circleRadius;
            bottom = thumbY + circleRadius;
        } else {
            left = thumbX - thumbRadius * mRange;
            right = thumbX + thumbRadius * mRange;
            top = thumbY - thumbRadius * mRange;
            bottom = thumbY + thumbRadius * mRange;
        }
        RectF rect = new RectF(left, top, right, bottom);
//        GTLog.i(TAG,"isInMarkerPressed thumbX="+thumbX+" thumbY="+thumbY+" thumbRadius="+thumbRadius);
//        GTLog.i(TAG, "isInMarkerPressed left=" + left + " right=" + right + " top=" + top + " bottom=" + bottom + " x=" + x + " y=" + y);
        return rect.contains(x, y);
    }

    public void setProgressChangeListener(OnProgressControlListener listener) {
        progressControlListener = listener;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public interface OnProgressControlListener {
        void onProgressChange(View view, int progress);

        void onProgressControlStart(View view);

        void onProgressControlEnd(View view);
    }

    public void setDrawSecondProgress(boolean drawSecond) {
        this.drawSecondProgress = drawSecond;
    }

    public void setProgressPaintSquare() {
        progressPaint.setStrokeCap(Paint.Cap.BUTT);
    }

    public void setDrawAncRotate(boolean drawRotate) {
        drawAncRotate = drawRotate;
    }

    public void setControllable(boolean flag) {
        controllable = flag;
        invalidate();
    }

    public void setDrawThumb(boolean drawThumbSrc) {
        drawThumb = drawThumbSrc;
    }
}
