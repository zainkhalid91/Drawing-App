package com.raed.drawingview;


public class DrawingEvent {

    public float[] mPoints = new float[20000];
    private int mAction;//similar to MotionEvent actions
    private int mSize;

    void add(float x, float y) {
        mPoints[mSize] = x;
        mPoints[mSize + 1] = y;
        mSize += 2;
    }

    void clear() {
        mSize = 0;
    }

    public int size() {
        return mSize;
    }

    public int getAction() {
        return mAction;
    }

    void setAction(int action) {
        mAction = action;
    }
}
