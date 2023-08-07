package com.example.mainproject;

import android.util.Log;
import android.view.View;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
public class DotView extends View {
    private float x = 0;
    private float y = 0;

    private Context sContext;

    public Canvas can;
    public DotView(Context context) {
        super(context);
        sContext = context;
        setWillNotDraw(false);
    }

    public DotView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
    }

    public DotView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //can = canvas;
        super.onDraw(canvas);
        can = canvas;
        setWillNotDraw(false);
        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        canvas.drawCircle(getWidth() / 2f + x, getHeight() / 2f + y, 25, paint);
        Log.d("Arduino", "Drawing");
    }

    @Override
    public float getX(){
        Log.d("Arduino", "x get " + x);
        return this.x;
    }

    @Override
    public float getY(){
        Log.d("Arduino", "y get" + y);
        return this.y;
    }

    public void draw(float x, float y){
        Log.d("Arduino", "draw");
        this.x = x;
        this.y = y;
        invalidate();
    }

}