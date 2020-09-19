
package com.igalblech.school.graphicaljavascriptcompiler;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8ScriptException;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.igalblech.school.graphicaljavascriptcompiler.ui.ErrorFragment;
import com.igalblech.school.graphicaljavascriptcompiler.ui.RenderFragment;
import com.igalblech.school.graphicaljavascriptcompiler.ui.ScriptFragment;
import com.igalblech.school.graphicaljavascriptcompiler.utils.front.ProjectActivityPagerAdapter;
import com.igalblech.school.graphicaljavascriptcompiler.utils.front.RenderColorFormat;

import java.nio.Buffer;
import java.nio.ByteBuffer;

public class ActivityProject extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private RenderColorFormat format;

    public static class V8ScriptExecutionThread extends AsyncTask<V8ScriptExecutionThread.Arguments, Void, V8ScriptExecutionThread.Output> {

        public static class Arguments {
            public String script;
            // Screen width, screen height
            public int width, height;
            public RenderColorFormat format;
        }

        public static class Output {
            public String error;
            public ByteBuffer buffer;
            public int width, height;
            public boolean isError;
        }

        private V8 runtime = null;
        private ActivityProject activityProject;

        public V8ScriptExecutionThread(ActivityProject activityProject) {
            this.activityProject = activityProject;
            //runtime.registerJavaMethod (  );
        }

        @Override
        protected void onPreExecute ( ) {
            super.onPreExecute ( );
        }

        @Override
        protected Output doInBackground ( Arguments... args ) {

            Output output = new Output();
            output.isError = false;
            output.width = args[0].width;
            output.height = args[0].height;

            byte[] bytesArray = new byte[args[0].width * args[0].height * 4];

            runtime = V8.createV8Runtime();
            String script = args[0].script;

            boolean success = true;
            try {
                runtime.executeVoidScript ( script );
            }
            catch (V8ScriptException e) {
                output.error = e.getJSStackTrace ();
                if (output.error == null)
                    output.error = e.getJSMessage ();
                output.isError = true;
                success = false;
            }

            if (success) {
                for (int i = 0; i < args[0].width * args[0].height; i++) {
                    int x = i % args[0].width;
                    int y = i / args[0].height;
                    V8Array params = new V8Array ( runtime ).push ( x ).push ( y );
                    Object result;
                    try {
                        result = runtime.executeFunction ( "set", params );
                    } catch (V8ScriptException e) {
                        output.error = e.getJSStackTrace ();
                        if (output.error == null)
                            output.error = e.getJSMessage ();
                        output.isError = true;
                        break;
                    } finally {
                        params.release ();
                    }

                    byte[] values = new byte[4];
                    if (result instanceof Double) {
                        Double v = (Double) result;
                        values = args[0].format.createColor ( v );
                    }
                    else if (result instanceof Integer) {
                        Integer v = (Integer) result;
                        values = args[0].format.createColor ( v );
                    }
                    else if (result instanceof V8Array) {
                        V8Array arr = (V8Array) result;
                        double[] doubles = arr.getDoubles ( 0, arr.length () );

                        if (doubles.length != args[0].format.channelCount) {
                            String err = "";
                            err += "Returned array has a wrong amount of channels! Array should has ";
                            err += args[0].format.channelCount;
                            err += " channels.";
                            output.error = err;
                            output.isError = true;
                        }
                        else {
                            values = args[0].format.createColor ( doubles );
                        }
                        arr.release ();
                    }
                    else {
                        output.error = "Wrong return type! Return type should be an array or a number.";
                        output.isError = true;
                        break;
                    }

                    if (output.isError)
                        break;


                    bytesArray[i * 4] = values[0];
                    bytesArray[i * 4 + 1] = values[1];
                    bytesArray[i * 4 + 2] = values[2];
                    bytesArray[i * 4 + 3] = values[3];
                }

                output.buffer = ByteBuffer.wrap ( bytesArray );
            }

            runtime.release ();
            return output;
        }

        @Override
        protected void onPostExecute ( Output s ) {
            super.onPostExecute ( s );

            activityProject.hideKeyboard ();

            if (s.isError) {
                activityProject.changeFragmentPage ( ProjectActivityPagerAdapter.PAGE_ERROR );
                activityProject.updateError ( s.error );
            }
            else if (s.buffer != null) {
                activityProject.changeFragmentPage ( ProjectActivityPagerAdapter.PAGE_RENDER );
                activityProject.updateImage ( s.buffer, s.width, s.height );
                activityProject.updateError ( "Program compiled successfully" );
            }


        }
    }


    BottomNavigationView navProject;
    ViewPager vpProjectFragment;
    ProjectActivityPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        Bundle bundle = getIntent().getExtras();

        String code = "";
        if (bundle != null) {
            if (bundle.getString ( "script_code" ).equals ( null )) {
                code = bundle.getString ( "script_code" );
            }
            if (bundle.get ( "format" ) != null) {
                format = (RenderColorFormat) bundle.get ( "format" );
            }
        }
        if (format == null)
            format = new RenderColorFormat ( format.COLOR_MODEL_G, 8, false, true );

        vpProjectFragment = findViewById ( R.id.vpProjectFragment );
        navProject = findViewById(R.id.navProject);

        navProject.setOnNavigationItemSelectedListener ( this );

        adapter = new ProjectActivityPagerAdapter ( getSupportFragmentManager () );
        adapter.setFragment ( ProjectActivityPagerAdapter.PAGE_SCRIPT, new ScriptFragment (format) );
        adapter.setFragment ( ProjectActivityPagerAdapter.PAGE_RENDER, new RenderFragment () );
        adapter.setFragment ( ProjectActivityPagerAdapter.PAGE_ERROR, new ErrorFragment () );
        vpProjectFragment.setAdapter ( adapter );

        //if (code != null)
        //    ((ScriptFragment) adapter.getItem ( ProjectStatePagerAdapter.PAGE_SCRIPT )).setCode(code);
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService( Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void updateImage( Buffer buffer, int w, int h ) {
        ((RenderFragment) (adapter.getItem ( ProjectActivityPagerAdapter.PAGE_RENDER ))).updateImage ( buffer, w, h );
    }

    private void updateError ( String error ) {
        ((ErrorFragment) (adapter.getItem ( ProjectActivityPagerAdapter.PAGE_ERROR ))).updateError ( error );
    }

    public void changeFragmentPage(int i) {
        vpProjectFragment.setCurrentItem ( i );
    }

    @Override
    public boolean onNavigationItemSelected ( @NonNull MenuItem menuItem ) {

        switch (menuItem.getItemId ( )) {
            case R.id.navigation_project_script:
                changeFragmentPage ( ProjectActivityPagerAdapter.PAGE_SCRIPT );
                break;
            case R.id.navigation_project_render:
                changeFragmentPage ( ProjectActivityPagerAdapter.PAGE_RENDER );
                break;
            case R.id.navigation_project_error:
                changeFragmentPage ( ProjectActivityPagerAdapter.PAGE_ERROR );
                break;
            default:
                return false;
        }

        return true;
    }

}
