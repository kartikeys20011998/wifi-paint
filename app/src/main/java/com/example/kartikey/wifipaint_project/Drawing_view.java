package com.example.kartikey.wifipaint_project;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.TypedValue;

public class Drawing_view extends View {
//drawing path
    private Path drawPath;
//drawing and canvas paint
    private Paint drawPaint,canvasPaint;
//initial color
    private int paintColor=0xFF660000;
//canvas
    private Canvas drawCanvas,drawCanvas2;
//canvas Bitmap
    private Bitmap canvasBitmap,canvasBitmap2;
    //TOUCH-EVENT ACTION
    int event_action;
//    Extracting the screen resolution
    int w,h;

    //brushsize and previousbrushSize
    protected float brushSize,lastBrushSize,rbrushSize;
    //X AND Y COORDINATES OF USER's TOUCH
    float touchX,touchY;
//flag for checking whethere user is erasing or not


    private Path drawPath2;
    //drawing and canvas paint
    private Paint drawPaint2,canvasPaint2;


    protected boolean erase=false;
    boolean other_erase=false;

    public Drawing_view(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setupDrawing();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        //view given size
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap=Bitmap.createBitmap(w,h, Bitmap.Config.ARGB_8888);
        drawCanvas=new Canvas(canvasBitmap);
        canvasBitmap2=Bitmap.createBitmap(w,h, Bitmap.Config.ARGB_8888);
        drawCanvas2=new Canvas(canvasBitmap2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(canvasBitmap,0,0,canvasPaint);
        canvas.drawPath(drawPath,drawPaint);
        System.out.println("onDraw CALLED");
        canvas.drawBitmap(canvasBitmap2,0,0,canvasPaint2);
        canvas.drawPath(drawPath2,drawPaint2);
    }


    private void setupDrawing()
    {
        System.out.println("setupDrawing CALLED");
        brushSize= getResources().getInteger(R.integer.medium_size);
        lastBrushSize=brushSize;
        //get drawing area setup for interaction
        drawPath2 = new Path();
        drawPaint2 = new Paint();
        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(brushSize);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        System.out.println("MotionEvent CALLED");
        touchX=event.getX();
        touchY=event.getY();
        event_action=event.getAction();
        switch(event_action)
        {
            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(touchX,touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(touchX,touchY);
                break;
            case MotionEvent.ACTION_UP:
                drawCanvas.drawPath(drawPath,drawPaint);
                drawPath.reset();
                break;
        }
        invalidate();
        return true;
    }


    public void setupDrawing2(int paintcolor,float brushSize)
    {
        System.out.println("setupDrawing2 CALLED");
        rbrushSize = brushSize;
        // lastBrushSize=brushSize;
        // get drawing area setup for interaction
        drawPaint2.setColor(paintcolor);
        drawPaint2.setAntiAlias(true);
        drawPaint2.setStrokeWidth(brushSize);
        drawPaint2.setStyle(Paint.Style.STROKE);
        drawPaint2.setStrokeJoin(Paint.Join.ROUND);
        drawPaint2.setStrokeCap(Paint.Cap.ROUND);
        canvasPaint2 = new Paint(Paint.DITHER_FLAG);
    }


    public void  onOtherTouchEvent(int eventAction,float touchX2,float touchY2,int width,int height)
    {
//detect other user's touch
        touchX2=(w*touchX2)/width;
        touchY2=(h*touchY2)/height;
        System.out.println("touchX2 : "+touchX2);
        System.out.println("touchY2 : "+touchY2);
        System.out.println("TouchEvent CALLED");
        switch (eventAction)
        {
            case MotionEvent.ACTION_DOWN:
                drawPath2.moveTo(touchX2,touchY2);
                break;
            case MotionEvent.ACTION_MOVE:
                drawPath2.lineTo(touchX2, touchY2);
                break;
            case MotionEvent.ACTION_UP:
                drawCanvas.drawPath(drawPath2,drawPaint2);
                drawPath2.reset();
                break;
        }
        invalidate();
    }


    public void setColor(String newColor)
    {
        invalidate();
        paintColor= Color.parseColor(newColor);
        drawPaint.setColor(paintColor);
    }

    public void setBrushSize(float newSize)
   {
    float pixelAmount = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,newSize,getResources().getDisplayMetrics());
    brushSize = pixelAmount;
    drawPaint.setStrokeWidth(brushSize);
   }

 public void setLastBrushSize(float lastSize)
 {
     lastBrushSize = lastSize;
 }

 public float getLastBrushSize()
 {
     return lastBrushSize;
 }

 public void setErase(boolean isErase)
 {
     erase=isErase;
     if(erase) {
         drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

     }
     else {
         drawPaint.setXfermode(null);

     }
 }
 public void setErase2(boolean erase2)
 {
     other_erase=erase2;
     if(other_erase)
     {
         drawPaint2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
     }
     else {
         drawPaint2.setXfermode(null);
     }
 }

 public void startNew()
 {
     drawCanvas.drawColor(0,PorterDuff.Mode.CLEAR);
     invalidate();
 }

    public int getPaintColor() {
        return paintColor;
    }

}