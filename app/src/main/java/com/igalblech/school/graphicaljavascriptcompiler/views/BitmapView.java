package com.igalblech.school.graphicaljavascriptcompiler.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import java.nio.Buffer;

import lombok.Getter;

/**
 * Shows bitmap image, used for showing rendered image.
 * @see com.igalblech.school.graphicaljavascriptcompiler.ui.RenderFragment
 */
public class BitmapView extends View {

    private final Rect boxSrc, boxDest, box;

    private Bitmap bitmap;
    private final @Getter Paint paint;

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    public BitmapView( Context context){
        this(context, null);
    }

    //XML inflation/instantiation
    public BitmapView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public BitmapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);

        boxSrc = new Rect ( 0, 0, 0, 0 );
        box = new Rect ( 0, 0, 0, 0 );
        boxDest = new Rect ( 0, 0, 0, 0 );
        paint = new Paint (  );
        //paint.setFilterBitmap(false);
        //paint.setAntiAlias ( false );
        //paint.setDither ( false );
    }

    public void passByteBuffer( Buffer byteBuffer, int w, int h ) {
        if (byteBuffer != null) {
            bitmap = Bitmap.createBitmap (w, h, Bitmap.Config.ARGB_8888);
            bitmap.copyPixelsFromBuffer ( byteBuffer );
            invalidate();
            boxSrc.set ( 0, 0, bitmap.getWidth (), bitmap.getHeight () );
            resize();
        }
    }

    @Override
    protected void onDraw ( Canvas canvas ) {
        super.onDraw ( canvas );

        if (bitmap != null)
            canvas.drawBitmap(bitmap, boxSrc, box, paint);
        //canvas.drawBitmap(bitmap, new Rect (0,0,box.right,box.bottom), box, paint);
    }

    private void resize() {

        if (boxDest.right == 0.0f || boxDest.bottom == 0.0f)
            return;


        float rx = (float) boxSrc.right / boxDest.right;
        float ry = (float)boxSrc.bottom / boxDest.bottom;

        float w = (rx >= ry) ? boxDest.right :  (boxSrc.right / ry);
        float h = (ry >= rx) ? boxDest.bottom :  (boxSrc.bottom / rx);

        box.set(0, 0, (int)w, (int)h);
    }

    @Override
    public void onWindowFocusChanged ( boolean hasFocus ) {
        super.onWindowFocusChanged ( hasFocus );

        boxDest.right = getWidth ();
        boxDest.bottom = getHeight ();
        resize ();
    }

}
