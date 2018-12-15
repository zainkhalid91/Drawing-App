package smartboard.fyp.com.smartapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class Multitouch extends View {
    private static final float TOUCH_TOLERANCE = 4;


    //   private Path            m_Path;
    private static final int INVALID_POINTER_ID = -1;
    int current_path_count = -1;
    ArrayList<Path> m_Path_list = new ArrayList<Path>();
    ArrayList<Float> mX_list = new ArrayList<Float>();
    ArrayList<Float> mY_list = new ArrayList<Float>();
    ArrayList<Integer> mActivePointerId_list = new ArrayList<Integer>();
    ArrayList<Pair<Path, Paint>> arrayListPaths = new ArrayList<Pair<Path, Paint>>();

    //ArrayList<Pair<Path, Paint>> undonePaths = new ArrayList<Pair<Path, Paint>>();
    private Canvas m_Canvas;
    private Paint m_Paint;
    private float mX, mY;
    private Bitmap bitmapToCanvas;
    // The ‘active pointer’ is the one currently moving our object.
    private int mActivePointerId = INVALID_POINTER_ID;

    public Multitouch(Context context) {
        super(context);
        setFocusable(true);
        setFocusableInTouchMode(true);

        onCanvasInitialization();
    }

    public Multitouch(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setFocusable(true);
        setFocusableInTouchMode(true);

        onCanvasInitialization();
    }

    public void onCanvasInitialization() {
        m_Paint = new Paint();
        m_Paint.setAntiAlias(true);
        m_Paint.setDither(true);
        m_Paint.setColor(Color.parseColor("#37A1D1"));
        m_Paint.setStyle(Paint.Style.STROKE);
        m_Paint.setStrokeJoin(Paint.Join.ROUND);
        m_Paint.setStrokeCap(Paint.Cap.ROUND);
        m_Paint.setStrokeWidth(2);

        //   m_Path = new Path();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        bitmapToCanvas = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        m_Canvas = new Canvas(bitmapToCanvas);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        try {
            canvas.drawBitmap(bitmapToCanvas, 0f, 0f, null);
            for (int i = 0; i <= current_path_count; i++) {
                canvas.drawPath(m_Path_list.get(i), m_Paint);
            }
        } catch (Exception e) {

        }
    }

    public void onDrawCanvas() {
        for (Pair<Path, Paint> p : arrayListPaths) {
            m_Canvas.drawPath(p.first, p.second);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        final int action = event.getAction();
        try {
            switch (action & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN: {
                    float x = event.getX();
                    float y = event.getY();


                    current_path_count = 0;
                    mActivePointerId_list.add(event.getPointerId(0), current_path_count);
                    touch_start((x), (y), current_path_count);
                }
                break;

                case MotionEvent.ACTION_POINTER_DOWN: {

                    if (event.getPointerCount() > current_path_count) {

                        current_path_count++;
                        float x = event.getX(current_path_count);
                        float y = event.getY(current_path_count);


                        mActivePointerId_list.add(event.getPointerId(current_path_count), current_path_count);
                        touch_start((x), (y), current_path_count);
                    }
                }
                break;

                case MotionEvent.ACTION_MOVE: {
                    for (int i = 0; i <= current_path_count; i++) {
                        try {
                            int pointerIndex = event
                                    .findPointerIndex(mActivePointerId_list.get(i));

                            float x = event.getX(pointerIndex);
                            float y = event.getY(pointerIndex);

                            touch_move((x), (y), i);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }


                }
                break;

                case MotionEvent.ACTION_UP: {
                    //current_path_count = -1;
                    for (int i = 0; i <= current_path_count; i++) {

                        touch_up(i);
                    }
                    mActivePointerId_list = new ArrayList<Integer>();


                }
                break;

                case MotionEvent.ACTION_CANCEL: {
                    mActivePointerId = INVALID_POINTER_ID;
                    current_path_count = -1;
                }
                break;

                case MotionEvent.ACTION_POINTER_UP: {
                    final int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                    final int pointerId = event.getPointerId(pointerIndex);
                    for (int i = 0; i <= current_path_count; i++) {
                        if (pointerId == mActivePointerId_list.get(i)) {
                            // This was our active pointer going up. Choose a new
                            // active pointer and adjust accordingly.

                            mActivePointerId_list.remove(i);
                            touch_up(i);
                            break;
                        }
                    }
                }
                break;

                case MotionEvent.ACTION_OUTSIDE:
                    break;
            }
        } catch (Exception e) {

        }

        invalidate();
        return true;
    }

    private void touch_start(float x, float y, int count) {
        Path m_Path = new Path();

        m_Path_list.add(count, m_Path);

        m_Path_list.get(count).reset();


        m_Path_list.get(count).moveTo(x, y);

        mX_list.add(count, x);
        mY_list.add(count, y);

    }

    private void touch_move(float x, float y, int count) {
        float dx = Math.abs(x - mX_list.get(count));
        float dy = Math.abs(y - mY_list.get(count));
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            m_Path_list.get(count).quadTo(mX_list.get(count), mY_list.get(count), (x + mX_list.get(count)) / 2, (y + mY_list.get(count)) / 2);
            try {

                mX_list.remove(count);
                mY_list.remove(count);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mX_list.add(count, x);
            mY_list.add(count, y);
        }
    }

    private void touch_up(int count) {
        m_Path_list.get(count).lineTo(mX_list.get(count), mY_list.get(count));

        // commit the path to our offscreen
        m_Canvas.drawPath(m_Path_list.get(count), m_Paint);

        // kill this so we don't double draw
        Paint newPaint = new Paint(m_Paint); // Clones the mPaint object
        arrayListPaths.add(new Pair<Path, Paint>(m_Path_list.get(count), newPaint));
        m_Path_list.remove(count);
        mX_list.remove(count);
        mY_list.remove(count);
    }
}
