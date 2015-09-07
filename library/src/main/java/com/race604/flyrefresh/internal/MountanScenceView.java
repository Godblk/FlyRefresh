package com.race604.flyrefresh.internal;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.*;
import android.os.Build;
import android.support.v4.view.animation.PathInterpolatorCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Interpolator;

import com.race604.flyrefresh.IPullHeader;
import com.race604.flyrefresh.PullHeaderLayout;
import com.race604.utils.UIUtils;

/**
 * Created by jing on 15-5-28.
 */
public class MountanScenceView extends View implements IPullHeader {

    private static final int COLOR_BACKGROUND = Color.parseColor("#7ECEC9");
    private static final int COLOR_MOUNTAIN_1 = Color.parseColor("#86DAD7");
    private static final int COLOR_MOUNTAIN_2 = Color.parseColor("#3C929C");
    private static final int COLOR_MOUNTAIN_3 = Color.parseColor("#3E5F73");
    private static final int COLOR_TREE_1_BRANCH = Color.parseColor("#1F7177");
    private static final int COLOR_TREE_1_BTRUNK = Color.parseColor("#0C3E48");
    private static final int COLOR_TREE_2_BRANCH = Color.parseColor("#34888F");
    private static final int COLOR_TREE_2_BTRUNK = Color.parseColor("#1B6169");
    private static final int COLOR_TREE_3_BRANCH = Color.parseColor("#57B1AE");
    private static final int COLOR_TREE_3_BTRUNK = Color.parseColor("#62A4AD");

    private static final int TRANSLATE_X_SPEED_ONE = 7;
    // 第二条水波移动速度
    private static final int TRANSLATE_X_SPEED_TWO = 5;

    private static final int WIDTH = 240;
    private static final int HEIGHT = 180;

    private static final int TREE_WIDTH = 100;
    private static final int TREE_HEIGHT = 200;

    private static final float STRETCH_FACTOR_A = 20;
    private static final int OFFSET_Y = 0;

    private Paint mMountPaint = new Paint();
    private Paint mTrunkPaint = new Paint();
    private Paint mBranchPaint = new Paint();
    private Paint mBoarderPaint = new Paint();

    private Path mMount1 = new Path();
    private Path mMount2 = new Path();
    private Path mMount3 = new Path();
    private Path mTrunk = new Path();
    private Path mBranch = new Path();
    private float[] mYPositions;
    private float[] mTesetYPositions;
    private int mTotalWidth, mTotalHeight;
    private float mCycleFactorW;

    private float mScaleX = 5f;
    private float mScaleY = 5f;
    private float mMoveFactor = 0;
    private float mBounceMax = 1;
    private float mTreeBendFactor = Float.MAX_VALUE;
    private Matrix mTransMatrix = new Matrix();

    private int mXOffsetSpeedOne;
    private int mXOffsetSpeedTwo;
    private int mXOffset;
    private DrawFilter mDrawFilter;

    public MountanScenceView(Context context) {
        super(context);
        init();
    }

    public MountanScenceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MountanScenceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MountanScenceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final float width = getMeasuredWidth();
        final float height = getMeasuredHeight();
        mScaleX = width / WIDTH;
        mScaleY = height / HEIGHT;

        updateMountainPath(mMoveFactor);
        updateTreePath(mMoveFactor, true);
    }

    private void init() {

        mXOffsetSpeedOne = UIUtils.dpToPx(TRANSLATE_X_SPEED_ONE);
        mXOffsetSpeedTwo = UIUtils.dpToPx(TRANSLATE_X_SPEED_TWO);

        mDrawFilter = new PaintFlagsDrawFilter(0,Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);

        mMountPaint.setAntiAlias(true);
        mMountPaint.setStyle(Paint.Style.FILL);

        mTrunkPaint.setAntiAlias(true);
        mBranchPaint.setAntiAlias(true);
        mBoarderPaint.setAntiAlias(true);
        mBoarderPaint.setStyle(Paint.Style.STROKE);
        mBoarderPaint.setStrokeWidth(2);
        mBoarderPaint.setStrokeJoin(Paint.Join.ROUND);

        updateMountainPath(mMoveFactor);
        updateTreePath(mMoveFactor, true);
    }

    private void updateMountainPath(float factor) {

        mTransMatrix.reset();
        mTransMatrix.setScale(mScaleX, mScaleY);

        int offset1 = (int) (10 * factor);
        mMount1.reset();
        mMount1.moveTo(0, 95 + offset1);
        mMount1.lineTo(55, 74 + offset1);
        mMount1.lineTo(146, 104 + offset1);
        mMount1.lineTo(227, 72 + offset1);
        mMount1.lineTo(WIDTH, 80 + offset1);
        mMount1.lineTo(WIDTH, HEIGHT);
        mMount1.lineTo(0, HEIGHT);
        mMount1.close();
        mMount1.transform(mTransMatrix);

        int offset2 = (int) (20 * factor);
        int offset22 = (int) (60 * factor);
        mMount2.reset();
        mMount2.moveTo(0, 40 + offset2);

//        mMount2.cubicTo(0 , 40 + offset2,WIDTH / 2, 40 + offset22,WIDTH, 40 + offset2);


        mMount2.lineTo(165, 115 + offset2);
        mMount2.lineTo(221, 87 + offset2);
        mMount2.lineTo(WIDTH, 100 + offset2);
        mMount2.lineTo(WIDTH, HEIGHT);
        mMount2.lineTo(0, HEIGHT);
        mMount2.close();
        mMount2.transform(mTransMatrix);

        int offset3 = (int) (30 * factor);
        mMount3.reset();
        mMount3.moveTo(0, 114 + offset3);
        mMount3.cubicTo(30, 106 + offset3, 196, 97 + offset3, WIDTH, 104 + offset3);
        mMount3.lineTo(WIDTH, HEIGHT);
        mMount3.lineTo(0, HEIGHT);
        mMount3.close();
        mMount3.transform(mTransMatrix);
    }

    private void updateTreePath(float factor, boolean force) {
        if (factor == mTreeBendFactor && !force) {
            return;
        }

        final Interpolator interpolator = PathInterpolatorCompat.create(0.8f, -0.5f * factor);

        final float width = TREE_WIDTH;
        final float height = TREE_HEIGHT;

        final float maxMove = width * 0.3f * factor;
        final float trunkSize = width * 0.05f;
        final float branchSize = width * 0.2f;
        final float x0 = width / 2;
        final float y0 = height;

        final int N = 25;
        final float dp = 1f / N;
        final float dy = -dp * height;
        float y = y0;
        float p = 0;
        float[] xx = new float[N + 1];
        float[] yy = new float[N + 1];
        for (int i = 0; i <= N; i++) {
            xx[i] = interpolator.getInterpolation(p) * maxMove + x0;
            yy[i] = y;

            y += dy;
            p += dp;
        }

        mTrunk.reset();
        mTrunk.moveTo(x0 - trunkSize, y0);
        int max = (int) (N * 0.7f);
        int max1 = (int) (max * 0.5f);
        float diff = max - max1;
        for (int i = 0; i < max; i++) {
            if (i < max1) {
                mTrunk.lineTo(xx[i] - trunkSize, yy[i]);
            } else {
                mTrunk.lineTo(xx[i] - trunkSize * (max - i) / diff, yy[i]);
            }
        }

        for (int i = max - 1; i >= 0; i--) {
            if (i < max1) {
                mTrunk.lineTo(xx[i] + trunkSize, yy[i]);
            } else {
                mTrunk.lineTo(xx[i] + trunkSize * (max - i) / diff, yy[i]);
            }
        }
        mTrunk.close();

        mBranch.reset();
        int min = (int) (N * 0.4f);
        diff = N - min;

        mBranch.moveTo(xx[min] - branchSize, yy[min]);
        mBranch.addArc(new RectF(xx[min] - branchSize, yy[min] - branchSize, xx[min] + branchSize, yy[min] + branchSize), 0f, 180f);
        for (int i = min; i <= N; i++) {
            float f = (i - min) / diff;
            mBranch.lineTo(xx[i] - branchSize + f * f * branchSize, yy[i]);
        }
        for (int i = N; i >= min; i--) {
            float f = (i - min) / diff;
            mBranch.lineTo(xx[i] + branchSize - f * f * branchSize, yy[i]);
        }

    }

    @Override
    public void onPullProgress(PullHeaderLayout parent, int state, float factor) {

        float bendFactor;
        if (state == PullHeaderLayout.STATE_BOUNCE) {
            if (factor < mBounceMax) {
                mBounceMax = factor;
            }
            bendFactor = factor;
        } else {
            mBounceMax = factor;
            bendFactor = Math.max(0, factor);
        }

        mMoveFactor = Math.max(0, mBounceMax);
        updateMountainPath(mMoveFactor);
        updateTreePath(bendFactor, false);

        postInvalidate();
    }

    private void drawTree(Canvas canvas, float scale, float baseX, float baseY,
                          int colorTrunk, int colorBranch) {
        canvas.save();

        final float dx = baseX - TREE_WIDTH * scale ;
        final float dy = baseY - TREE_HEIGHT * scale;
        canvas.translate(dx, dy);
        canvas.scale(scale, scale);

        mBranchPaint.setColor(colorBranch);
        canvas.drawPath(mBranch, mBranchPaint);
        mTrunkPaint.setColor(colorTrunk);
        canvas.drawPath(mTrunk, mTrunkPaint);
        mBoarderPaint.setColor(colorTrunk);
        canvas.drawPath(mBranch, mBoarderPaint);

        canvas.restore();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(COLOR_BACKGROUND);
        canvas.setDrawFilter(mDrawFilter);
        resetPositonY();

        mMountPaint.setColor(COLOR_MOUNTAIN_1);
        canvas.drawPath(mMount1, mMountPaint);
//        for (int i = 0; i < mTotalWidth; i++) {
//            canvas.drawLine(i,mTotalHeight-mTesetYPositions[i]-300,i,mTotalHeight,mMountPaint);
//        }

        //在新的图层绘制
        canvas.save();
        canvas.scale(-1, 1, getWidth() / 2, 0);
//        drawTree(canvas, 0.1f * mScaleX, 200 * mScaleX, (96 + 20 * mMoveFactor) * mScaleY,
//                COLOR_TREE_3_BTRUNK, COLOR_TREE_3_BRANCH);
        canvas.restore();
        mMountPaint.setColor(COLOR_MOUNTAIN_2);
        canvas.drawPath(mMount2, mMountPaint);
//        for (int i = 0; i < mTotalWidth; i++) {
//            canvas.drawLine(i,mTotalHeight-mTesetYPositions[i]-200,i,mTotalHeight,mMountPaint);
//        }

//        drawTree(canvas, 0.5f * mScaleX, 180 * mScaleX, (93 + 20 * mMoveFactor) * mScaleY,
//                COLOR_TREE_3_BTRUNK, COLOR_TREE_3_BRANCH);
        drawTree(canvas, 0.2f * mScaleX, 160 * mScaleX, (105 + 30 * mMoveFactor) * mScaleY,
                COLOR_TREE_1_BTRUNK, COLOR_TREE_1_BRANCH);

        drawTree(canvas, 0.14f * mScaleX, 180 * mScaleX, (105 + 30 * mMoveFactor) * mScaleY,
                COLOR_TREE_2_BTRUNK, COLOR_TREE_2_BRANCH);

        drawTree(canvas, 0.16f * mScaleX, 140 * mScaleX, (105 + 30 * mMoveFactor) * mScaleY,
                COLOR_TREE_2_BTRUNK, COLOR_TREE_2_BRANCH);

        mMountPaint.setColor(COLOR_MOUNTAIN_3);
        canvas.drawPath(mMount3, mMountPaint);

//        mXOffset+= mXOffsetSpeedOne;
//
//        if (mXOffset>=mTotalWidth){
//            mXOffset = 0;
//        }


        postInvalidate();
    }

    private void resetPositonY() {
        // mXOneOffset代表当前第一条水波纹要移动的距离
        int yOneInterval = mYPositions.length - mXOffset;
        // 使用System.arraycopy方式重新填充第一条波纹的数据
        System.arraycopy(mYPositions, mXOffset, mTesetYPositions, 0, yOneInterval);
        System.arraycopy(mYPositions, 0, mTesetYPositions, yOneInterval, mXOffset);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mTotalWidth = w;
        mTotalHeight = h;

        mYPositions = new float[mTotalWidth];
        mTesetYPositions = new float[mTotalWidth];

        mCycleFactorW = (float) (2 * Math.PI / mTotalWidth);

        // 根据view总宽度得出所有对应的y值
        //根据sin函数画出正玄曲线
        for (int i = 0; i < mTotalWidth; i++) {
            mYPositions[i] = (float) (STRETCH_FACTOR_A * Math.sin(mCycleFactorW * i) + OFFSET_Y);
        }
    }
}
