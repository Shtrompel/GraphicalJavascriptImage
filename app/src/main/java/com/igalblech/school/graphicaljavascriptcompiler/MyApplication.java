package com.igalblech.school.graphicaljavascriptcompiler;

import android.app.Application;
import android.widget.Toast;

/**
 * Overrides Application class
 */
public class MyApplication extends Application {

    public MyApplication() {
        super();
    }

    /*public void setColorTheme(String s) {
        int id = -1;
        switch (s.toLowerCase ()) {
            case "day":
                id = R.style.AppTheme;
                break;
            case "night":
                id = -1;
                break;
            default:
                Toast.makeText ( this, "What", Toast.LENGTH_SHORT ).show ( );
        }
        if (id != -1)
            setTheme ( id );
    }*/

}
