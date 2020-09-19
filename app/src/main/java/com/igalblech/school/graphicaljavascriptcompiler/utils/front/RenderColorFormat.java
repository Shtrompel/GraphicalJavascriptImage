package com.igalblech.school.graphicaljavascriptcompiler.utils.front;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class RenderColorFormat implements Serializable {

    public static final int COLOR_MODEL_G = 0;
    public static final int COLOR_MODEL_RGB = 1;
    public static final int COLOR_MODEL_HSV = 2;

    public final int colorModel;
    public final int channelBit;
    public final boolean isFloat;
    public final boolean hasAlpha;
    public int channelCount;
    public int channelBits;

    public static double[] HSVtoRGB(double h, double s, double v)
    {
        // H is given on [0->6] or -1. S and V are given on [0->1].
        // RGB are each returned on [0->1].
        double m, n, f;
        int i;

        double[] hsv = new double[3];
        double[] rgb = new double[3];

        hsv[0] = h;
        hsv[1] = s;
        hsv[2] = v;

        if (hsv[0] == -1)
        {
            rgb[0] = rgb[1] = rgb[2] = hsv[2];
            return rgb;
        }
        i = (int) (Math.floor(hsv[0]));
        f = hsv[0] - i;
        if (i % 2 == 0)
        {
            f = 1 - f; // if i is even
        }
        m = hsv[2] * (1 - hsv[1]);
        n = hsv[2] * (1 - hsv[1] * f);
        switch (i)
        {
            case 6:
            case 0:
                rgb[0] = hsv[2];
                rgb[1] = n;
                rgb[2] = m;
                break;
            case 1:
                rgb[0] = n;
                rgb[1] = hsv[2];
                rgb[2] = m;
                break;
            case 2:
                rgb[0] = m;
                rgb[1] = hsv[2];
                rgb[2] = n;
                break;
            case 3:
                rgb[0] = m;
                rgb[1] = n;
                rgb[2] = hsv[2];
                break;
            case 4:
                rgb[0] = n;
                rgb[1] = m;
                rgb[2] = hsv[2];
                break;
            case 5:
                rgb[0] = hsv[2];
                rgb[1] = m;
                rgb[2] = n;
                break;
        }

        return rgb;

    }

    public RenderColorFormat(int colorModel, int channelBit, boolean isFloat, boolean hasAlpha) {
        this.colorModel = colorModel;
        this.channelBit = channelBit;
        this.isFloat = isFloat;
        this.hasAlpha = hasAlpha;
        channelCount = new int[]{1, 3, 3}[colorModel];
        if (hasAlpha)
            channelCount++;
        channelBits = channelBit * channelCount;
    }

    public byte quantizeValue(double x) {
        int i;

        if (isFloat)
            i = (int)(x * (Math.pow(2, channelBit) - 1));
        else
            i = (int)x;

        switch (channelBit) {
            case 1:
                return i == 0 ? (byte)0 : (byte)255;
            case 8:
                return (byte)i;
            default:
                return (byte) ((x / (Math.pow ( 2, channelBit ))) * 256.0);
        }
    }

    /*
    public byte[] createColor(double v1, double v2, double v3) {
        double a;
        if (hasAlpha)
            a = 0;
        else {
            if (isFloat) a = 1;
            else a = 255;
        }
        return createColor(v1, v2, v3, a);
    }

    public byte[] createColor(double v1, double v2, double v3, double a) {
        double g;

        switch (colorModel) {
            case COLOR_MODEL_G:
                if (v1 != v2 || v1 != v3) {
                    g = (v1 + v2 + v3) / 3;
                    v1 = g;
                    v2 = g;
                    v3 = g;
                }
                break;
            case COLOR_MODEL_RGB:
                break;
            case COLOR_MODEL_HSV:
                double[] arr = HSVtoRGB(v1, v2, v3);
                v1 = (int)arr[0];
                v2 = (int)arr[1];
                v3 = (int)arr[2];
                break;
        }


        byte[] ret = new byte[4];
        ret[0] = (byte)quantizeValue ( v1 );
        ret[1] = (byte)quantizeValue ( v2 );
        ret[2] = (byte)quantizeValue ( v3 );
        ret[3] = (byte)quantizeValue ( a );
        return ret;
    }

    public int[] createColor(double v1, double v2, double v3) {
        return createColor(v1, v2, v3, hasAlpha ? 0 : 255);
    }

    public int[] createColor(double v1, double v2, double v3, double a) {

        double g;
        switch (colorModel) {
            case COLOR_MODEL_G:
                if (v1 != v2 || v1 != v3) {
                    g = (v1 + v2 + v3) / 3;
                    v1 = g;
                    v2 = g;
                    v3 = g;
                }
                break;
            case COLOR_MODEL_RGB:
                break;
            case COLOR_MODEL_HSV:
                double[] arr = HSVtoRGB(v1, v2, v3);
                v1 = arr[0];
                v2 = arr[1];
                v3 = arr[2];
                break;
        }

        int[] ret = new int[4];
        ret[0] = quantizeValue ( v1 );
        ret[1] = quantizeValue ( v2 );
        ret[2] = quantizeValue ( v3 );
        ret[3] = quantizeValue ( a );
        return ret;
    }
    */

    public byte[] createColor ( double v ) {
        return createColor(new double[]{v});
    }

    public byte[] createColor ( double[] doubles ) {
        byte r = 0, g = 0, b = 0, a;
        int i = 0;
        switch (colorModel) {
            case (COLOR_MODEL_G):
                i = 0;
                r = quantizeValue ( doubles[0] );
                g = quantizeValue ( doubles[0] );
                b = quantizeValue ( doubles[0] );
                break;
            case COLOR_MODEL_RGB:
                i = 2;
                r = quantizeValue ( doubles[0] );
                g = quantizeValue ( doubles[1] );
                b = quantizeValue ( doubles[2] );
                break;
            case COLOR_MODEL_HSV:
                i = 2;
                double h = doubles[0];
                double s = doubles[1];
                double v = doubles[2];
                if (!isFloat) {
                    h /= 256.0;
                    s /= 256.0;
                    v /= 256.0;
                }
                h *= 6.0;
                doubles = HSVtoRGB(h % 6.0, s, v);
                r = (byte)(doubles[0] * 255.0);
                g = (byte)(doubles[1] * 255.0);
                b = (byte)(doubles[2] * 255.0);
                break;
        }

        if (hasAlpha) {
            a = quantizeValue ( doubles[i + 1] );
        }
        else
            a = (byte)255;

        return new byte[]{r, g, b, a};
    }

    @Override
    public String toString() {
        String ret = "{";
        ret += "Color Model = " + colorModel + ", ";
        ret += "Color Bits = " + channelBit + ", ";
        ret += "Has Alpha = " + (hasAlpha ? "true" : "false") + ", ";
        ret += "Is Float = " + (isFloat ? "true" : "false");
        ret += "}";
        return ret;
    }

    @NonNull
    @Override
    protected Object clone ( ) throws CloneNotSupportedException {
        RenderColorFormat ret = new RenderColorFormat (
                this.colorModel,
                this.channelBit,
                this.isFloat,
                this.hasAlpha
        );
        return ret;
    }
}
