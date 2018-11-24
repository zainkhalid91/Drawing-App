package com.raed.drawingview;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.raed.drawingview.brushes.BrushSettings;
import com.raed.drawingview.brushes.Brushes;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import ja.burhanrashid52.photoeditor.MultiTouchListener;
import ja.burhanrashid52.photoeditor.OnPhotoEditorListener;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.ViewType;


public class DrawingView extends View {


    private static final String TAG = "PhotoEditor";
    private LayoutInflater mLayoutInflater;
    private Context context;
    private PhotoEditorView parentView;
    private ImageView imageView;
    private View deleteView;

    private List<View> addedViews;
    private List<View> redoViews;
    private OnPhotoEditorListener mOnPhotoEditorListener;
    private boolean isTextPinchZoomable;
    private Typeface mDefaultTextTypeface;
    private Typeface mDefaultEmojiTypeface;

    private static final float MAX_SCALE = 5f;
    private static final float MIN_SCALE = 0.1f;

    private Canvas mCanvas;
    private Bitmap mDrawingBitmap;
    private Bitmap mBGBitmap;
    private int mBGColor;//BackGroundColor

    //if true, do not draw anything. Just zoom and translate thr drawing in onTouchEvent()
    private boolean mZoomMode = false;

    private float mDrawingTranslationX = 0f;
    private float mDrawingTranslationY = 0f;
    private float mScaleFactor = 1f;

    private float mLastX[] = new float[2];
    private float mLastY[] = new float[2];

    private ActionStack mActionStack;//This is used for undo/redo, if null this means the undo and redo are disabled

    private DrawingPerformer mDrawingPerformer;//

    private OnDrawListener mOnDrawListener;

    private Brushes mBrushes;

    private boolean mCleared = true;

    private Paint mSrcPaint = new Paint() {{
        setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
    }};

    private ScaleGestureDetector mScaleGestureDetector = new ScaleGestureDetector(
            getContext(),
            new ScaleGestureDetector.SimpleOnScaleGestureListener() {
                @Override
                public boolean onScale(ScaleGestureDetector detector) {
                    float xCenter = (mLastX[0] + mLastX[1]) / 2;
                    float yCenter = (mLastY[0] + mLastY[1]) / 2;
                    float xd = (xCenter - mDrawingTranslationX);
                    float yd = (yCenter - mDrawingTranslationY);
                    mScaleFactor *= detector.getScaleFactor();
                    if (mScaleFactor == MAX_SCALE || mScaleFactor == MIN_SCALE)
                        return true;
                    mDrawingTranslationX = xCenter - xd * detector.getScaleFactor();
                    mDrawingTranslationY = yCenter - yd * detector.getScaleFactor();

                    checkBounds();
                    invalidate();
                    return true;
                }
            }
    );
    private int mPointerId;
    private boolean translateAction = true;

    public DrawingView(Builder builder) {
        super(builder.context);
        this.context = builder.context;
        this.parentView = builder.parentView;
        this.imageView = builder.imageView;
        this.deleteView = builder.deleteView;
        this.isTextPinchZoomable = builder.isTextPinchZoomable;
        this.mDefaultTextTypeface = builder.textTypeface;
        this.mDefaultEmojiTypeface = builder.emojiTypeface;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        addedViews = new ArrayList<>();
        redoViews = new ArrayList<>();
    }

    public DrawingView(Context context) {
        this(context, null);
    }

    public DrawingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mBrushes = new Brushes(context.getResources());
        if (attrs != null)
            initializeAttributes(attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w == 0 || h == 0 || mDrawingBitmap != null)
            return;
        if (mBGBitmap == null) {
            initializeDrawingBitmap(
                    (int) getWidthWithoutPadding(),
                    (int) getHeightWithoutPadding());
        } else {//in most cases this means the setBackgroundImage has been called before the view gets its dimensions
            //call this method so mBGBitmap gets scaled and aligned in the center
            //this method should also call initializeDrawingBitmap
            setBackgroundImage(mBGBitmap);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //prevent drawing in the padding
        canvas.clipRect(
                getPaddingStart(),
                getPaddingTop(),
                canvas.getWidth() - getPaddingRight(),
                canvas.getHeight() - getPaddingBottom()
        );

        //drawFromTo the background and the bitmap in the middle with scale and translation
        canvas.translate(getPaddingStart() + mDrawingTranslationX, getPaddingTop() + mDrawingTranslationY);
        canvas.scale(mScaleFactor, mScaleFactor);
        canvas.clipRect(//prevent drawing paths outside the bounds
                0,
                0,
                mDrawingBitmap.getWidth(),
                mDrawingBitmap.getHeight()
        );
        canvas.drawColor(mBGColor);
        if (mBGBitmap != null)
            canvas.drawBitmap(mBGBitmap, 0, 0, null);
        if (mDrawingPerformer.isDrawing())//true if the user is touching the screen
            mDrawingPerformer.draw(canvas, mDrawingBitmap);
        else
            canvas.drawBitmap(mDrawingBitmap, 0, 0, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int minDimension = (int) (250 * getResources().getDisplayMetrics().density);//150dp
        int contentWidth = minDimension + getPaddingStart() + getPaddingEnd();
        int contentHeight = minDimension + getPaddingTop() + getPaddingBottom();

        int measuredWidth = resolveSize(contentWidth, widthMeasureSpec);
        int measuredHeight = resolveSize(contentHeight, heightMeasureSpec);
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        if (mZoomMode)
            return handleZoomAndTransEvent(event);
        if (event.getPointerCount() > 1)
            return false;
        float scaledX = (event.getX() - mDrawingTranslationX) / mScaleFactor;
        float scaledY = (event.getY() - mDrawingTranslationY) / mScaleFactor;
        event.setLocation(scaledX, scaledY);
        mDrawingPerformer.onTouch(event);
        invalidate();
        return true;
    }

    public boolean handleZoomAndTransEvent(MotionEvent event) {
        super.onTouchEvent(event);
        if (event.getActionMasked() == MotionEvent.ACTION_POINTER_UP && event.getPointerCount() == 1)
            return false;
        if (event.getPointerCount() > 1) {
            translateAction = false;
            mScaleGestureDetector.onTouchEvent(event);
        } else if (translateAction)
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    mPointerId = event.getPointerId(0);
                    break;
                case MotionEvent.ACTION_MOVE:
                case MotionEvent.ACTION_CANCEL:
                    int pointerIndex = event.findPointerIndex(mPointerId);
                    if (pointerIndex != -1) {
                        mDrawingTranslationX += event.getX(pointerIndex) - mLastX[0];
                        mDrawingTranslationY += event.getY(pointerIndex) - mLastY[0];
                    }
                    break;
            }
        if (event.getActionMasked() == MotionEvent.ACTION_UP)
            translateAction = true; // reset

        mLastX[0] = event.getX(0);
        mLastY[0] = event.getY(0);
        if (event.getPointerCount() > 1) {
            mLastX[1] = event.getX(1);
            mLastY[1] = event.getY(1);
        }

        checkBounds();
        invalidate();
        return true;
    }

    public Bitmap exportDrawing() {
        Bitmap bitmap = Bitmap.createBitmap(
                mDrawingBitmap.getWidth(),
                mDrawingBitmap.getHeight(),
                Bitmap.Config.ARGB_8888
        );
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(mBGColor);
        if (mBGBitmap != null)
            canvas.drawBitmap(mBGBitmap, 0, 0, null);
        canvas.drawBitmap(mDrawingBitmap, 0, 0, null);
        return bitmap;
    }

    public Bitmap exportDrawingWithoutBackground() {
        return mDrawingBitmap;
    }

    public void setUndoAndRedoEnable(boolean enabled) {
        if (enabled)
            mActionStack = new ActionStack();
        else
            mActionStack = null;
    }

    /**
     * Set an image as a background so you can draw on top of it. NOTE that calling this method is
     * going to clear anything drawn previously and you will not be able to restore anything with undo().
     *
     * @param bitmap to be used as a background image.
     */
    public void setBackgroundImage(Bitmap bitmap) {
        mBGBitmap = bitmap;
        if (getWidth() == 0 || getHeight() == 0)
            return;//mBGBitmap will be scaled when the view gets its dimensions
        if (mBGBitmap == null) {
            mScaleFactor = 1f;
            mDrawingTranslationX = mDrawingTranslationY = 0;
            initializeDrawingBitmap(((int) getWidthWithoutPadding()), (int) getHeightWithoutPadding());
        } else {
            scaleBGBitmapIfNeeded();
            alignDrawingInTheCenter();
            initializeDrawingBitmap(mBGBitmap.getWidth(), mBGBitmap.getHeight());
        }
        if (mActionStack != null) //if undo and redo is enabled, remove the old actions by creating a new instance.
            mActionStack = new ActionStack();
        invalidate();
    }

    public int getDrawingBackground() {
        return mBGColor;
    }

    public void setDrawingBackground(int color) {
        mBGColor = color;
        invalidate();
    }

    public void resetZoom() {
        //if the bitmap is smaller than the view zoom in to make the bitmap fit the view
        float targetSF = calcAppropriateScaleFactor(mDrawingBitmap.getWidth(), mDrawingBitmap.getHeight());

        //align the bitmap in the center
        float targetX = (getWidth() - mDrawingBitmap.getWidth() * targetSF) / 2;
        float targetY = (getHeight() - mDrawingBitmap.getHeight() * targetSF) / 2;

        ObjectAnimator scaleAnimator = ObjectAnimator.ofFloat(this, "scaleFactor", mScaleFactor, targetSF);
        ObjectAnimator xTranslationAnimator
                = ObjectAnimator.ofFloat(this, "drawingTranslationX", mDrawingTranslationX, targetX);
        ObjectAnimator yTranslationAnimator
                = ObjectAnimator.ofFloat(this, "drawingTranslationY", mDrawingTranslationY, targetY);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleAnimator, xTranslationAnimator, yTranslationAnimator);
        animatorSet.start();
    }

    /**
     * This method clears the drawing bitmap. If this method is called consecutively only the first
     * call will take effect.
     *
     * @return true if the canvas cleared successfully.
     */
    public boolean clear() {
        if (mCleared)
            return false;
        Rect rect = new Rect(
                0,
                0,
                mDrawingBitmap.getWidth(),
                mDrawingBitmap.getHeight()
        );
        if (mActionStack != null) {
            DrawingAction drawingAction = new DrawingAction(
                    Bitmap.createBitmap(mDrawingBitmap),
                    rect
            );
            mActionStack.addAction(drawingAction);
        }
        mCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
        mCleared = true;
        return true;
    }

    public boolean isCleared() {
        return mCleared;
    }

    Brushes getBrushes() {
        return mBrushes;
    }

    public boolean undo() {
        if (mActionStack == null)
            throw new IllegalStateException("Undo functionality is disable you can enable it by calling setUndoAndRedoEnable(true)");
        if (mActionStack.isUndoStackEmpty() || mDrawingPerformer.isDrawing())
            return false;
        DrawingAction previousAction = mActionStack.previous();
        DrawingAction oppositeAction = getOppositeAction(previousAction);
        mActionStack.addActionToRedoStack(oppositeAction);
        performAction(previousAction);
        return true;
    }

    public boolean redo() {
        if (mActionStack == null)
            throw new IllegalStateException("Redo functionality is disable you can enable it by calling setUndoAndRedoEnable(true)");
        if (mActionStack.isRedoStackEmpty() || mDrawingPerformer.isDrawing())
            return false;
        DrawingAction nextAction = mActionStack.next();
        DrawingAction oppositeAction = getOppositeAction(nextAction);
        mActionStack.addActionToUndoStack(oppositeAction);
        performAction(nextAction);
        return true;
    }

    public boolean isUndoStackEmpty() {
        if (mActionStack == null)
            throw new IllegalStateException("Undo functionality is disable you can enable it by calling setUndoAndRedoEnable(true)");
        return mActionStack.isUndoStackEmpty();
    }

    public boolean isRedoStackEmpty() {
        if (mActionStack == null)
            throw new IllegalStateException("Undo functionality is disable you can enable it by calling setUndoAndRedoEnable(true)");
        return mActionStack.isRedoStackEmpty();
    }

    /**
     * Return an instance of BrushSetting, you can use it to change the selected brush. And change
     * the size of the selected brush and the color.
     *
     * @return an instance of BrushSetting associated with this DrawingView.
     */
    public BrushSettings getBrushSettings() {
        return mBrushes.getBrushSettings();
    }

    /**
     * Enter the zoom mode to be able to zoom and move the drawing. Note that you cannot enter
     * the zoom mode if the the user is drawing.
     *
     * @return true if enter successfully, false otherwise.
     */
    public boolean enterZoomMode() {
        if (mDrawingPerformer.isDrawing())
            return false;
        mZoomMode = true;
        return true;
    }

    /**
     * Exit the zoom mode to be able to draw.
     */
    public void exitZoomMode() {
        mZoomMode = false;
    }

    public boolean isInZoomMode() {
        return mZoomMode;
    }

    /**
     * Set a listener to be notified whenever a new stroke or a point is drawn.
     */
    public void setOnDrawListener(OnDrawListener onDrawListener) {
        mOnDrawListener = onDrawListener;
    }

    public float getDrawingTranslationX() {
        return mDrawingTranslationX;
    }

    public void setDrawingTranslationX(float drawingTranslationX) {
        mDrawingTranslationX = drawingTranslationX;
        invalidate();
    }

    public float getDrawingTranslationY() {
        return mDrawingTranslationY;
    }

    public void setDrawingTranslationY(float drawingTranslationY) {
        mDrawingTranslationY = drawingTranslationY;
        invalidate();
    }

    public float getScaleFactor() {
        return mScaleFactor;
    }

    public void setScaleFactor(float scaleFactor) {
        mScaleFactor = scaleFactor;
        invalidate();
    }

    private void performAction(DrawingAction action) {
        mCleared = false;
        mCanvas.drawBitmap(
                action.mBitmap,
                action.mRect.left,
                action.mRect.top,
                mSrcPaint
        );
        invalidate();
    }

    private DrawingAction getOppositeAction(DrawingAction action) {
        Rect rect = action.mRect;
        Bitmap bitmap = Bitmap.createBitmap(
                mDrawingBitmap,
                rect.left,
                rect.top,
                rect.right - rect.left,
                rect.bottom - rect.top
        );
        return new DrawingAction(bitmap, rect);
    }

    protected void checkBounds() {
        int width = mDrawingBitmap.getWidth();
        int height = mDrawingBitmap.getHeight();

        int contentWidth = (int) (width * mScaleFactor);
        int contentHeight = (int) (height * mScaleFactor);

        float widthBound = getWidth() / 6;
        float heightBound = getHeight() / 6;

        if (contentWidth < widthBound) {
            if (mDrawingTranslationX < -contentWidth / 2)
                mDrawingTranslationX = -contentWidth / 2f;
            else if (mDrawingTranslationX > getWidth() - contentWidth / 2)
                mDrawingTranslationX = getWidth() - contentWidth / 2f;
        } else if (mDrawingTranslationX > getWidth() - widthBound)
            mDrawingTranslationX = getWidth() - widthBound;
        else if (mDrawingTranslationX + contentWidth < widthBound)
            mDrawingTranslationX = widthBound - contentWidth;

        if (contentHeight < heightBound) {
            if (mDrawingTranslationY < -contentHeight / 2)
                mDrawingTranslationY = -contentHeight / 2f;
            else if (mDrawingTranslationY > getHeight() - contentHeight / 2)
                mDrawingTranslationY = getHeight() - contentHeight / 2f;
        } else if (mDrawingTranslationY > getHeight() - heightBound)
            mDrawingTranslationY = getHeight() - heightBound;
        else if (mDrawingTranslationY + contentHeight < heightBound)
            mDrawingTranslationY = heightBound - contentHeight;
    }

    private void initializeDrawingBitmap(int w, int h) {
        mDrawingBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mDrawingBitmap);
        if (mDrawingPerformer == null) {
            mDrawingPerformer = new DrawingPerformer(mBrushes);
            mDrawingPerformer.setPaintPerformListener(new MyDrawingPerformerListener());
        }
        mDrawingPerformer.setWidthAndHeight(w, h);
    }

    private void initializeAttributes(AttributeSet attrs) {
        TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.DrawingView,
                0, 0);
        try {
            BrushSettings settings = mBrushes.getBrushSettings();
            int brushColor = typedArray.getColor(R.styleable.DrawingView_brush_color, 0xFF000000);
            settings.setColor(brushColor);
            int selectedBrush = typedArray.getInteger(R.styleable.DrawingView_brush, Brushes.PENCIL);
            settings.setSelectedBrush(selectedBrush);
            float size = typedArray.getFloat(R.styleable.DrawingView_brush_size, 0.5f);
            if (size < 0 || size > 1)
                throw new IllegalArgumentException("DrawingView brush_size attribute should have a value between 0 and 1 in your xml file");
            settings.setSelectedBrushSize(size);

            mBGColor = typedArray.getColor(R.styleable.DrawingView_drawing_background_color, -1);//default to white

        } finally {
            typedArray.recycle();
        }
    }

    private void scaleBGBitmapIfNeeded() {
        float canvasWidth = getWidthWithoutPadding();
        float canvasHeight = getHeightWithoutPadding();
        if (canvasWidth <= 0 || canvasHeight <= 0)
            return;
        float bitmapWidth = mBGBitmap.getWidth();
        float bitmapHeight = mBGBitmap.getHeight();
        float scaleFactor = 1;
        //if the bitmap is smaller than the view -> find a scale factor to scale it down
        if (bitmapWidth > canvasWidth && bitmapHeight > canvasHeight) {
            scaleFactor = Math.min(canvasHeight / bitmapHeight, canvasWidth / bitmapWidth);
        } else if (bitmapWidth > canvasWidth && bitmapHeight < canvasHeight)
            scaleFactor = canvasWidth / bitmapWidth;
        else if (bitmapWidth < canvasWidth && bitmapHeight > canvasHeight)
            scaleFactor = canvasHeight / bitmapHeight;

        if (scaleFactor != 1)//if the bitmap is larger than the view scale it down
            mBGBitmap = Utilities.resizeBitmap(mBGBitmap, ((int) (mBGBitmap.getWidth() * scaleFactor)), (int) (mBGBitmap.getHeight() * scaleFactor));
    }

    private void alignDrawingInTheCenter() {
        float canvasWidth = getWidthWithoutPadding();
        float canvasHeight = getHeightWithoutPadding();
        if (canvasWidth <= 0 || canvasHeight <= 0)
            return;
        mScaleFactor = calcAppropriateScaleFactor(mBGBitmap.getWidth(), mBGBitmap.getHeight());
        //align the bitmap in the center
        mDrawingTranslationX = (canvasWidth - mBGBitmap.getWidth() * mScaleFactor) / 2;
        mDrawingTranslationY = (canvasHeight - mBGBitmap.getHeight() * mScaleFactor) / 2;
    }

    private float calcAppropriateScaleFactor(int bitmapWidth, int bitmapHeight) {
        float canvasWidth = getWidthWithoutPadding();
        float canvasHeight = getHeightWithoutPadding();
        if (bitmapWidth < canvasWidth && bitmapHeight < canvasHeight) {
            return Math.min(canvasHeight / bitmapHeight, canvasWidth / bitmapWidth);
        } else { //otherwise just make the scale factor is 1
            return 1f;
        }
    }

    private float getWidthWithoutPadding() {
        return getWidth() - getPaddingStart() - getPaddingEnd();
    }

    private float getHeightWithoutPadding() {
        return getHeight() - getPaddingTop() - getPaddingBottom();
    }

    public interface OnDrawListener {
        void onDraw();
    }

    private class MyDrawingPerformerListener implements DrawingPerformer.DrawingPerformerListener {

        @Override
        public void onDrawingPerformed(Bitmap bitmap, Rect rect) {
            mCleared = false;
            if (mActionStack != null)
                storeAction(rect);//for undo and redo
            mCanvas.drawBitmap(bitmap, rect.left, rect.top, null);
            invalidate();
            if (mOnDrawListener != null)
                mOnDrawListener.onDraw();
        }

        @Override
        public void onDrawingPerformed(Path path, Paint paint, Rect rect) {
            mCleared = false;
            if (mActionStack != null)
                storeAction(rect);//for undo and redo
            mCanvas.drawPath(path, paint);
            invalidate();
            if (mOnDrawListener != null)
                mOnDrawListener.onDraw();
        }

        private void storeAction(Rect rect) {
            Bitmap bitmap = Bitmap.createBitmap(
                    mDrawingBitmap,
                    rect.left,
                    rect.top,
                    rect.right - rect.left,
                    rect.bottom - rect.top
            );
            DrawingAction action = new DrawingAction(bitmap, rect);
            mActionStack.addAction(action);
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    public void addText(String text, final int colorCodeTextView) {
        addText(null, text, colorCodeTextView);
    }

    /**
     * This add the text on the {@link PhotoEditorView} with provided parameters
     * by default {@link TextView#setText(int)} will be 18sp
     *
     * @param textTypeface      typeface for custom font in the text
     * @param text              text to display
     * @param colorCodeTextView text color to be displayed
     */
    @SuppressLint("ClickableViewAccessibility")
    public void addText(@Nullable Typeface textTypeface, String text, final int colorCodeTextView) {
        final View textRootView = getLayout(ViewType.TEXT);
        final TextView textInputTv = textRootView.findViewById(R.id.tvPhotoEditorText);
        final ImageView imgClose = textRootView.findViewById(R.id.imgPhotoEditorClose);
        final FrameLayout frmBorder = textRootView.findViewById(R.id.frmBorder);

        textInputTv.setText(text);
        textInputTv.setTextColor(colorCodeTextView);
        if (textTypeface != null) {
            textInputTv.setTypeface(textTypeface);
        }
        MultiTouchListener multiTouchListener = getMultiTouchListener();
        multiTouchListener.setOnGestureControl(new MultiTouchListener.OnGestureControl() {
            @Override
            public void onClick() {
                boolean isBackgroundVisible = frmBorder.getTag() != null && (boolean) frmBorder.getTag();
                frmBorder.setBackgroundResource(isBackgroundVisible ? 0 : ja.burhanrashid52.photoeditor.R.drawable.rounded_border_tv);
                imgClose.setVisibility(isBackgroundVisible ? View.GONE : View.VISIBLE);
                frmBorder.setTag(!isBackgroundVisible);
            }

            @Override
            public void onLongClick() {
                String textInput = textInputTv.getText().toString();
                int currentTextColor = textInputTv.getCurrentTextColor();
                if (mOnPhotoEditorListener != null) {
                    mOnPhotoEditorListener.onEditTextChangeListener(textRootView, textInput, currentTextColor);
                }
            }
        });

        textRootView.setOnTouchListener(multiTouchListener);
        addViewToParent(textRootView, ViewType.TEXT);
    }

    private void addViewToParent(View rootView, ViewType viewType) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        parentView.addView(rootView, params);
        addedViews.add(rootView);
        if (mOnPhotoEditorListener != null)
            mOnPhotoEditorListener.onAddViewListener(viewType, addedViews.size());
    }

    private View getLayout(final ViewType viewType) {
        View rootView = null;
        switch (viewType) {
            case TEXT:
                rootView = mLayoutInflater.inflate(R.layout.view_photo_editor_text, null);
                TextView txtText = rootView.findViewById(R.id.tvPhotoEditorText);
                if (txtText != null && mDefaultTextTypeface != null) {
                    txtText.setGravity(Gravity.CENTER);
                    if (mDefaultEmojiTypeface != null) {
                        txtText.setTypeface(mDefaultTextTypeface);
                    }
                }
                break;
            case IMAGE:
                rootView = mLayoutInflater.inflate(R.layout.view_photo_editor_image, null);
                break;
            case EMOJI:
                rootView = mLayoutInflater.inflate(R.layout.view_photo_editor_text, null);
                TextView txtTextEmoji = rootView.findViewById(R.id.tvPhotoEditorText);
                if (txtTextEmoji != null) {
                    if (mDefaultEmojiTypeface != null) {
                        txtTextEmoji.setTypeface(mDefaultEmojiTypeface);
                    }
                    txtTextEmoji.setGravity(Gravity.CENTER);
                    txtTextEmoji.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                }
                break;
        }

        if (rootView != null) {
            //We are setting tag as ViewType to identify what type of the view it is
            //when we remove the view from stack i.e onRemoveViewListener(ViewType viewType, int numberOfAddedViews);
            rootView.setTag(viewType);
            final ImageView imgClose = rootView.findViewById(R.id.imgPhotoEditorClose);
            final View finalRootView = rootView;
            if (imgClose != null) {
                imgClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewUndo(finalRootView, viewType);
                    }
                });
            }
        }
        return rootView;
    }


    private void viewUndo(View removedView, ViewType viewType) {
        if (addedViews.size() > 0) {
            if (addedViews.contains(removedView)) {
                parentView.removeView(removedView);
                addedViews.remove(removedView);
                redoViews.add(removedView);
                if (mOnPhotoEditorListener != null) {
                    mOnPhotoEditorListener.onRemoveViewListener(addedViews.size());
                    mOnPhotoEditorListener.onRemoveViewListener(viewType, addedViews.size());
                }
            }
        }
    }


    private MultiTouchListener getMultiTouchListener() {
        MultiTouchListener multiTouchListener = new MultiTouchListener(
                deleteView,
                parentView,
                this.imageView,
                isTextPinchZoomable,
                mOnPhotoEditorListener);

        //multiTouchListener.setOnMultiTouchListener(this);

        return multiTouchListener;
    }


    /**
     * This will update text and color on provided view
     *
     * @param view      view on which you want update
     * @param inputText text to update {@link TextView}
     * @param colorCode color to update on {@link TextView}
     */
    public void editText(View view, String inputText, int colorCode) {
        editText(view, null, inputText, colorCode);
    }

    /**
     * This will update the text and color on provided view
     *
     * @param view         root view where text view is a child
     * @param textTypeface update typeface for custom font in the text
     * @param inputText    text to update {@link TextView}
     * @param colorCode    color to update on {@link TextView}
     */
    public void editText(View view, Typeface textTypeface, String inputText, int colorCode) {
        TextView inputTextView = view.findViewById(ja.burhanrashid52.photoeditor.R.id.tvPhotoEditorText);
        if (inputTextView != null && addedViews.contains(view) && !TextUtils.isEmpty(inputText)) {
            inputTextView.setText(inputText);
            if (textTypeface != null) {
                inputTextView.setTypeface(textTypeface);
            }
            inputTextView.setTextColor(colorCode);
            parentView.updateViewLayout(view, view.getLayoutParams());
            int i = addedViews.indexOf(view);
            if (i > -1) addedViews.set(i, view);
        }
    }

    public static class Builder {

        private Context context;
        private PhotoEditorView parentView;
        private ImageView imageView;
        private View deleteView;
        private Typeface textTypeface;
        private Typeface emojiTypeface;
        //By Default pinch zoom on text is enabled
        private boolean isTextPinchZoomable = true;

        /**
         * Building a PhotoEditor which requires a Context and PhotoEditorView
         * which we have setup in our xml layout
         *
         * @param context         context
         * @param photoEditorView {@link PhotoEditorView}
         */
        public Builder(Context context, PhotoEditorView photoEditorView) {
            this.context = context;
            parentView = photoEditorView;
            imageView = photoEditorView.getSource();

        }

        Builder setDeleteView(View deleteView) {
            this.deleteView = deleteView;
            return this;
        }

        /**
         * set default text font to be added on image
         *
         * @param textTypeface typeface for custom font
         * @return {@link Builder} instant to build {@link DrawingView}
         */
        public Builder setDefaultTextTypeface(Typeface textTypeface) {
            this.textTypeface = textTypeface;
            return this;
        }

        /**
         * set default font specific to add emojis
         *
         * @param emojiTypeface typeface for custom font
         * @return {@link Builder} instant to build {@link DrawingView}
         */
        public Builder setDefaultEmojiTypeface(Typeface emojiTypeface) {
            this.emojiTypeface = emojiTypeface;
            return this;
        }

        /**
         * set false to disable pinch to zoom on text insertion.By deafult its true
         *
         * @param isTextPinchZoomable flag to make pinch to zoom
         * @return {@link Builder} instant to build {@link DrawingView}
         */
        public Builder setPinchTextScalable(boolean isTextPinchZoomable) {
            this.isTextPinchZoomable = isTextPinchZoomable;
            return this;
        }

        /**
         * @return build PhotoEditor instance
         */
        public DrawingView build() {
            return new DrawingView(this);
        }
    }
}
