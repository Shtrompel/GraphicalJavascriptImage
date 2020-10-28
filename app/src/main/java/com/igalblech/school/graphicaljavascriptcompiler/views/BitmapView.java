package com.igalblech.school.graphicaljavascriptcompiler.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import java.nio.Buffer;

import lombok.Setter;

public class BitmapView extends View {

    private final Rect box;
    @Setter private Bitmap bitmap;
    private final Paint paint;

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

        box = new Rect ( 0, 0, 0, 0 );
        paint = new Paint (  );
        paint.setFilterBitmap(false);
    }

// --Commented out by Inspection START (28/10/2020 14:07):
//    public void setSize(int w, int h) {
//
//        LayoutParams params = getLayoutParams();
//        params.width = w;
//        params.height = h;
//        setLayoutParams(params);
//
//        setMinimumWidth(w);
//        setMinimumHeight (h);
//
//        box.right = w;
//        box.bottom = h;
//    }
// --Commented out by Inspection STOP (28/10/2020 14:07)

    public void passByteBuffer( Buffer byteBuffer, int w, int h ) {
        if (byteBuffer != null) {
            bitmap = Bitmap.createBitmap (w, h, Bitmap.Config.ARGB_8888);
            bitmap.copyPixelsFromBuffer ( byteBuffer );
            invalidate();
        }
    }

    @Override
    protected void onDraw ( Canvas canvas ) {
        super.onDraw ( canvas );

        if (bitmap != null)
            canvas.drawBitmap(bitmap, box, box, paint);
        //canvas.drawBitmap(bitmap, new Rect (0,0,box.right,box.bottom), box, paint);
    }

    @Override
    public void onWindowFocusChanged ( boolean hasFocus ) {
        super.onWindowFocusChanged ( hasFocus );

        box.right = getWidth ();
        box.bottom = getHeight ();
    }
}
