package com.example.progressbar;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.lang.ref.WeakReference;

/**
 * Created by 龙泉 on 2016/12/15.
 */
public class VideoProgressBar extends View {


    private Paint mPaint;
    private Paint mBackGroundPaint;
    private Paint mThumbPaint;

    private WeakReference<VideoController> weakReference;


    private ProgressListener mProgressListener;
    private final String colorBlue = "#3488EB";

    private float mTouchX;
    private float mTounchY;

    private boolean mHitThumb;

    private final int DEFAULT_PROGRESS_HEIGHT = 4;
    private final int DEFAULT_THUMB_SIZE = 10;

    private int mProgressBarHeight;
    private int mThumbRadius;

    private SeekListener mSeekListener;

    private Point mThumbPoint;
    private int mLastProgress;

    public VideoProgressBar(Context context) {
        this(context, null);
    }

    public VideoProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Resources res = getResources();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackGroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mThumbPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mPaint.setColor(Color.parseColor(colorBlue));
        mBackGroundPaint.setColor(Color.parseColor("#565656"));
        mThumbPaint.setColor(Color.parseColor(colorBlue));

        mThumbPoint = new Point();
        mThumbPoint.x = 0;
        mThumbPoint.y = getHeight() / 2;

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.VideoProgressBar);
        mProgressBarHeight = array.getDimensionPixelSize(R.styleable.VideoProgressBar_progress_height, DEFAULT_PROGRESS_HEIGHT);
        mThumbRadius = array.getDimensionPixelSize(R.styleable.VideoProgressBar_thumb_radius, DEFAULT_THUMB_SIZE);
        array.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int currentProgress = getProgress();
        int currentPositionX = (int) (currentProgress * 1.0 * getWidth() / 100);


        int progressBarTop = getHeight() / 2 - mProgressBarHeight / 2;


        float fixedX = getFixedThumbPosition(currentPositionX);

        canvas.drawRect(0, progressBarTop, getWidth(), progressBarTop + mProgressBarHeight, mBackGroundPaint);
        canvas.drawRect(0, progressBarTop, fixedX, progressBarTop + mProgressBarHeight, mPaint);

        canvas.drawCircle(fixedX, getHeight() / 2, mThumbRadius, mThumbPaint);

        if (mProgressListener != null) {
            mProgressListener.updateProgress(currentProgress);
        }

        if (currentProgress <= 100) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float newX = event.getX();
        float newY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isHintThumb(newX, newY)) {
                    mTouchX = newX;
                    mTounchY = newY;
                    mHitThumb = true;
                } else {
                    mHitThumb = false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mHitThumb) {
                    mThumbPoint.x = getFixedX(newX);
                    if (mSeekListener != null) {
                        int progress = (int) (mThumbPoint.x * 100 * 1.0f / getWidth());
                        if(progress != mLastProgress){
                            mSeekListener.onSeek(progress);
                            mLastProgress = progress;
                        }
                    }
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                mHitThumb = false;
                break;
        }
        return mHitThumb;
    }

    private float getFixedThumbPosition(float thumbPosition) {
        int maxThumbPosition = getWidth() - mThumbRadius;
        int minThumbPosition = mThumbRadius;
        if (thumbPosition > maxThumbPosition) return maxThumbPosition;
        if (thumbPosition < minThumbPosition) return minThumbPosition;
        return thumbPosition;
    }

    private int getFixedX(float newX) {
        if (newX < 0) return 0;
        if (newX > getWidth()) return getWidth();
        return (int) newX;
    }

    private boolean isHintThumb(float touchX, float touchY) {
        boolean matchedY = touchY >= 0 && touchY <= getHeight();
        boolean matchedX = touchX >= 0 && touchX <= getWidth();
        return matchedX && matchedY;
    }


    private int getProgress() {
//        if (weakReference == null || weakReference.get() == null) return 0;
//        long duration = weakReference.get().getDuration();
//        if (duration == 0) return 0;
//        return (int) (100.f * weakReference.get().getCurrentPosition() / duration);
        if (weakReference != null && weakReference.get() != null) {
           return  weakReference.get().getProgress();
        }else{
            return 0;
        }
    }

    public void setController(WeakReference<VideoController> playerControlReference) {
        weakReference = playerControlReference;
        ViewCompat.postInvalidateOnAnimation(this);
    }

    public void releseController() {
        if (weakReference != null && weakReference.get() != null) {
            weakReference.clear();
        }
    }

    public void setSeekListener(SeekListener seekListener){
        this.mSeekListener = seekListener;
    }

    public void setProgressListener(ProgressListener progressListener) {
        this.mProgressListener = progressListener;
    }



    public interface ProgressListener {
        void updateProgress(int progress);
    }

    public interface SeekListener {
        void onSeek(int progress);
    }
}
