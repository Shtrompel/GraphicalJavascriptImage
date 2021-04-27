package com.igalblech.school.graphicaljavascriptcompiler.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

/**
 * Located next to CodeEditText view, this view numbers each row in the text view
 * Used in coding fragment.
 * @see CodeEditText
 * @see com.igalblech.school.graphicaljavascriptcompiler.ui.ScriptFragment
 */
public class CodeLineText extends AppCompatTextView {

    private final Paint paint;

    public int scrollY;
    public int lineHeight;
    public int baseline;
    public int lineCount;

    public CodeLineText ( Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        paint.setTextSize(20);
        paint.setTextAlign(Paint.Align.CENTER);

        Paint paintSelected = new Paint ( );
        paintSelected.setStyle(Paint.Style.FILL);
        paintSelected.setColor(0xFFDDDDDD);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (lineHeight == 0)
            return;

        int startLine = ( scrollY / lineHeight );
        int startBaseline = baseline;// + lineHeight * startLine;

        int line;
        for (int i = 0; i < 15; i++) {
            line = i+startLine+1;
            if (line <= lineCount)
                paint.setColor ( Color.BLACK );
            else
                paint.setColor ( 0xFFC0C0C0 );
            float y = (paint.descent() + paint.ascent()) / 2 + startBaseline;
            canvas.drawText(String.valueOf (line), 25.0f, y, paint);
            startBaseline += lineHeight;
        }

        super.onDraw(canvas);
    }
}
