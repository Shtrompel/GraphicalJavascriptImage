package com.igalblech.school.graphicaljavascriptcompiler.utils.project;


import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8ScriptException;
import com.igalblech.school.graphicaljavascriptcompiler.ActivityProject;
import com.igalblech.school.graphicaljavascriptcompiler.ui.ScriptFragment;

import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;

public class V8ScriptExecutionThread extends AsyncTask<ProjectSettings, Integer, V8ScriptExecutionThread.Output> {

    public static class Output {
        public String error;
        public ByteBuffer buffer;
        public int width, height;
        public boolean isError;
    }
    
    private final WeakReference<ActivityProject> activityProject;
    private final WeakReference<ProgressBar> progressBar;

    public V8ScriptExecutionThread( @NonNull ActivityProject activityProject, @NonNull ProgressBar progressBar ) {
        this.activityProject = new WeakReference<> ( activityProject );
        this.progressBar = new WeakReference<> ( progressBar );
    }

    @Override
    protected void onPreExecute ( ) {
        super.onPreExecute ( );
        progressBar.get ().setVisibility( View.VISIBLE);
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
                            startTime = System.currentTimeMillis ( );
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
