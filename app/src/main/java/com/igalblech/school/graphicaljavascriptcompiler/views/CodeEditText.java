package com.igalblech.school.graphicaljavascriptcompiler.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatEditText;

import lombok.Setter;

/**
 * Author S Mahbub Uz Zaman on 5/9/15.
 * Lisence Under GPL2
 * The source code has been changed.
 * This view in mainly for highlighting text for easier coding.
 * Used in coding fragment.
 * @see com.igalblech.school.graphicaljavascriptcompiler.ui.ScriptFragment
 */
public class CodeEditText extends AppCompatEditText {

    private final Paint paintSelected;

    private int selectedLine = -1;
    private final int lastLineCount = 0;
    private @Setter
    CodeLineText cltScript;

    public CodeEditText ( Context context, AttributeSet attrs) {
        super(context, attrs);
        Paint paint = new Paint ( );
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        paint.setTextSize(20);
        paint.setTextAlign(Paint.Align.CENTER);

        paintSelected = new Paint();
        paintSelected.setStyle(Paint.Style.FILL);
        paintSelected.setColor(0xFFDDDDDD);

        //float textHeight = paint.descent ( ) - paint.ascent ( );
        //float textOffset = (textHeight / 2) - paint.descent ( );

        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            public void colorWord(Editable s, String word, int color, boolean sb, boolean sa) {
                int index;
                int startIndex = 0;
                do {
                    index = s.toString ( ).indexOf ( word, startIndex );
                    startIndex = index + word.length ();
                    if (index >= 0) {

                        int end = index + word.length ( );
                        boolean spaceBefore = true, spaceAfter = true;
                        if (sb)
                            spaceBefore = index == 0 || s.charAt ( index - 1 ) == ' ' || s.charAt ( index - 1 ) == '\n';
                        if (sa)
                            spaceAfter = end - 1 == s.length ( ) - 1 || s.charAt ( end ) == ' ' || s.charAt ( end ) == '\n';

                        if (spaceBefore && spaceAfter) {
                            s.setSpan (
                                    new ForegroundColorSpan ( color ),
                                    index,
                                    index + word.length ( ),
                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );
                        }
                    }
                } while (index >= 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
                s.setSpan ( new ForegroundColorSpan (Color.BLACK), 0, s.length (), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );
                colorWord(s,"function",0xFF008080, true, true);
                colorWord(s,"set", 0xFF008B8B, true , false);
                colorWord(s,"return", 0xFF5F9EA0, true , true );
                colorWord(s,"Math", 0xFF20B2AA, true , false );
            }
        });
    }

    public void updateSelectedLine() {

        int selected = Selection.getSelectionStart(getEditableText ());
        Editable editable = getEditableText ();
        int count = 0;

        for (int i = 0; i < selected; i++) {
            if (editable.charAt ( i ) == '\n')
                count++;
        }
        //Log.d("Developer", "" + count);
        selectedLine = count;
    }

    @Override
    public boolean onTouchEvent ( MotionEvent event ) {
        boolean ret = super.onTouchEvent ( event );
        updateSelectedLine();
        performClick();
        return ret;
    }

    @Override
    protected void onTextChanged ( CharSequence text, int start, int lengthBefore, int lengthAfter ) {
        super.onTextChanged ( text, start, lengthBefore, lengthAfter );
        updateSelectedLine();
    }

    @Override
    public boolean onKeyDown ( int keyCode, KeyEvent event ) {
        boolean ret = super.onKeyDown ( keyCode, event );
        if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            if (selectedLine > 0) selectedLine--;
        }
        else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            if (getLineCount () > selectedLine + 1) selectedLine++;
        }
        else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            if (getEditableText ().charAt ( getSelectionStart() ) == '\n')
                selectedLine--;
        }
        else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            try {
                if (getEditableText ( ).charAt ( getSelectionStart ( ) - 1 ) == '\n' &&
                        getLineCount () > selectedLine + 1) {
                    selectedLine++;
                }
            }
            catch (IndexOutOfBoundsException e) {
                e.printStackTrace ();
            }
        }

        return ret;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (cltScript != null) {
            cltScript.scrollY = getScrollY ( );
            cltScript.lineHeight = getLineHeight ( );
            cltScript.baseline = getBaseline ( );
            cltScript.lineCount = getLineCount ( );

            cltScript.invalidate ( );
        }

        {
            float w = getWidth ( );
            float h = getLineHeight ( );
            float x = 0.0f;
            float y = selectedLine * getLineHeight ( ) + getBaseline ( ) / 2.0f + 10;
            canvas.drawRect ( x, y - h / 2, x + w, y + h / 2, paintSelected );
        }

        super.onDraw(canvas);
    }
}