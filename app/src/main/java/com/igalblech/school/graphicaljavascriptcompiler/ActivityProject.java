
package com.igalblech.school.graphicaljavascriptcompiler;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;

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
import com.igalblech.school.graphicaljavascriptcompiler.utils.project.ProjectActivityPagerAdapter;
import com.igalblech.school.graphicaljavascriptcompiler.utils.project.ProjectSettings;

import java.lang.ref.WeakReference;
import java.nio.Buffer;
import java.nio.ByteBuffer;

import lombok.Getter;

public class ActivityProject extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private @Getter ProjectSettings settings;

    public static class V8ScriptExecutionThread extends AsyncTask<ProjectSettings, Integer, V8ScriptExecutionThread.Output> {

        // --Commented out by Inspection (28/10/2020 14:07):private Thread threadGetDoubles;

        public static class Output {
            public String error;
            public ByteBuffer buffer;
            public int width, height;
            public boolean isError;
        }

        private final WeakReference<ActivityProject> activityProject;
        private final WeakReference<ProgressBar> progressBar;

        public V8ScriptExecutionThread(@NonNull ActivityProject activityProject, @NonNull ProgressBar progressBar ) {
            this.activityProject = new WeakReference<> ( activityProject );
            this.progressBar = new WeakReference<> ( progressBar );
        }

        @Override
        protected void onPreExecute ( ) {
            super.onPreExecute ( );
            progressBar.get ().setVisibility(View.VISIBLE);
        }

        @Override
        protected Output doInBackground ( ProjectSettings... args ) {

            Output output = new Output();
            output.isError = false;
            output.width = args[0].width;
            output.height = args[0].height;

            byte[] bytesArray = new byte[args[0].width * args[0].height * 4];

            V8 runtime = V8.createV8Runtime ( );
            String script = args[0].code + ScriptFragment.JS_CONST_EXECUTE_ARR;

            try {
                runtime.executeVoidScript ( script );
            }
            catch (V8ScriptException e) {
                output.error = e.getJSStackTrace ();
                if (output.error == null)
                    output.error = e.getJSMessage ();
                output.isError = true;
            }

            int width = args[0].width;
            int height = args[0].height;
            int count = args[0].format.channelCount;
            int pixelCount = width * height;

            runtime.add ( "width", width );
            runtime.add ( "height", height );

            long startTime = System.currentTimeMillis ( );
            if (!output.isError) {
                V8Array params = new V8Array ( runtime );
                Object result = null;
                try {
                    result = runtime.executeFunction ( "executeArray", params );
                } catch (V8ScriptException e) {
                    output.error = e.getJSStackTrace ( );
                    if (output.error == null)
                        output.error = e.getJSMessage ( );
                    output.isError = true;
                } finally {
                    params.release ( );
                }

                if (result instanceof V8Array) {
                    V8Array arr = (V8Array) result;
                    double[] doubles = arr.getDoubles ( 0, arr.length ( ) );

                    int doublesPixel = 0;
                    double[] values = new double[count];
                    for (int i = 0; i < pixelCount; i++) {

                        long timerTime = System.currentTimeMillis ( );
                        if (timerTime - startTime > 1000) {
                            startTime = System.currentTimeMillis ();
                            publishProgress ( i );
                        }

                        for (int j = 0; j < count; j++)
                            values[j] = doubles[doublesPixel++];

                        byte[] bytes = args[0].format.createColor ( values );

                        bytesArray[i * 4] = bytes[0];
                        bytesArray[i * 4 + 1] = bytes[1];
                        bytesArray[i * 4 + 2] = bytes[2];
                        bytesArray[i * 4 + 3] = bytes[3];
                    }
                    arr.release ( );
                }

            }
            output.buffer = ByteBuffer.wrap ( bytesArray );

            runtime.release (true);
            return output;
        }

        @Override
        protected void onPostExecute ( Output s ) {

            progressBar.get ().setVisibility(View.GONE);
            super.onPostExecute ( s );

            activityProject.get ().hideKeyboard ();

            if (s.isError) {
                activityProject.get ().changeFragmentPage ( ProjectActivityPagerAdapter.PAGE_ERROR );
                activityProject.get ().updateError ( s.error );
            }
            else if (s.buffer != null) {
                activityProject.get ().changeFragmentPage ( ProjectActivityPagerAdapter.PAGE_RENDER );
                activityProject.get ().updateImage ( s.buffer, s.width, s.height );
                activityProject.get ().updateError ( "Program compiled successfully" );
            }


        }

        @Override
        protected void onProgressUpdate ( Integer... values ) {
            super.onProgressUpdate ( values );
            progressBar.get ().setProgress ( values[0] );
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

        if (bundle != null) {
            settings = (ProjectSettings) bundle.get ( "settings" );
        }

        vpProjectFragment = findViewById ( R.id.vpProjectFragment );
        navProject = findViewById(R.id.navProject);

        navProject.setOnNavigationItemSelectedListener ( this );

        adapter = new ProjectActivityPagerAdapter ( getSupportFragmentManager () );
        adapter.setFragment ( ProjectActivityPagerAdapter.PAGE_SCRIPT, new ScriptFragment (settings) );
        adapter.setFragment ( ProjectActivityPagerAdapter.PAGE_RENDER, new RenderFragment () );
        adapter.setFragment ( ProjectActivityPagerAdapter.PAGE_ERROR, new ErrorFragment () );
        vpProjectFragment.setAdapter ( adapter );

        //format = new RenderColorFormat ( format.COLOR_MODEL_G, 8, false, true );
        //if (code != null)
        //  ((ScriptFragment) adapter.getItem ( ProjectActivityPagerAdapter.PAGE_SCRIPT )).setSettings(settings);
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

        int itemId = menuItem.getItemId ( );
        if (itemId == R.id.navigation_project_script) {
            changeFragmentPage ( ProjectActivityPagerAdapter.PAGE_SCRIPT );
        } else if (itemId == R.id.navigation_project_render) {
            changeFragmentPage ( ProjectActivityPagerAdapter.PAGE_RENDER );
        } else if (itemId == R.id.navigation_project_error) {
            changeFragmentPage ( ProjectActivityPagerAdapter.PAGE_ERROR );
        } else {
            return false;
        }

        return true;
    }

}
