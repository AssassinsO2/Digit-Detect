package com.app.digitdetect;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

public class DrawingView extends View {

    private Paint paint;
    private Path path;
    private float lastX, lastY;

    public DrawingView(Context context) {
        super(context);
        init();
    }

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        path = new Path();

        // Configure paint for drawing
        paint.setAntiAlias(true);
        paint.setColor(0xFF000000); // Black color
        paint.setStyle(Paint.Style.STROKE); // Stroke style
        paint.setStrokeWidth(60f); // Stroke width
        paint.setStrokeCap(Paint.Cap.ROUND); // Rounded line ends
        paint.setStrokeJoin(Paint.Join.ROUND); // Rounded joins
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        // Draw the path (drawing)
        canvas.drawPath(path, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        // Request the parent (e.g., ViewPager2) to not intercept touch events
        getParent().requestDisallowInterceptTouchEvent(true);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(x, y); // Start the path
                lastX = x;
                lastY = y;
                invalidate(); // Redraw the view
                return true; // Return true to consume the touch event
            case MotionEvent.ACTION_MOVE:
                // Use quadTo for smoother curves
                float midX = (lastX + x) / 2;
                float midY = (lastY + y) / 2;
                path.quadTo(lastX, lastY, midX, midY);
                lastX = x;
                lastY = y;
                invalidate(); // Redraw the view
                return true;
            case MotionEvent.ACTION_UP:
                // Ensure the path is finished with a smooth curve
                path.lineTo(x, y);
                performClick(); // Call performClick() for accessibility
                invalidate();
                return true;
        }

        return super.onTouchEvent(event);
    }

    @Override
    public boolean performClick() {
        // Call the super implementation for accessibility events
        super.performClick();
        return true;
    }

    // Method to clear the drawing
    public void clearCanvas() {
        path.reset();
        invalidate();
    }

    // Method to capture the drawing as a Bitmap
    public Bitmap getDrawingBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        draw(canvas); // Draw the current view to the canvas (including the path)
        return bitmap;
    }
}
