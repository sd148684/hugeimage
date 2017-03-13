package me.liu.hugeimage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

public class GestureImageView extends View {

    private ScaleGestureDetector mScaleGestureDetector;

    private PointF mSlidePoint = new PointF(0.0f, 0.0f);

    private GestureDetectorCompat mDetectorCompat;

    @SuppressWarnings("FieldCanBeLocal")
    private final GestureDetector.SimpleOnGestureListener mGestureListener
            = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            mSlidePoint.x -= distanceX / mScaleFactor;
            mSlidePoint.y -= distanceY / mScaleFactor;
            invalidate();
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            mScaleFactor = 1.f;
            mSlidePoint.x = 0;
            mSlidePoint.y = 0;
            invalidate();
            return true;
        }
    };

    private float mScaleFactor = 1.f;

    private Bitmap mBitmap;

    public GestureImageView(Context context) {
        this(context, null);
    }

    public GestureImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GestureImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());
        mDetectorCompat = new GestureDetectorCompat(context, mGestureListener);
    }

    public void setImageBitmap(Bitmap bitmap) {
        this.mBitmap = bitmap;
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // Let the ScaleGestureDetector inspect all events.
        boolean retVal = mScaleGestureDetector.onTouchEvent(ev);
        retVal = mDetectorCompat.onTouchEvent(ev) || retVal;
        return retVal || super.onTouchEvent(ev);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(mBitmap != null) {
            canvas.save();
            canvas.scale(mScaleFactor, mScaleFactor);
            canvas.drawBitmap(mBitmap, mSlidePoint.x, mSlidePoint.y, null);
            canvas.restore();
        }
    }

    private class ScaleListener
            extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));
            invalidate();
            return true;
        }
    }
}
