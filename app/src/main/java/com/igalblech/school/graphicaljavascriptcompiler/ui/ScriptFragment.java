package com.igalblech.school.graphicaljavascriptcompiler.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.igalblech.school.graphicaljavascriptcompiler.ActivityGallery;
import com.igalblech.school.graphicaljavascriptcompiler.ActivityProject;
import com.igalblech.school.graphicaljavascriptcompiler.R;
import com.igalblech.school.graphicaljavascriptcompiler.utils.project.ProjectSettings;
import com.igalblech.school.graphicaljavascriptcompiler.utils.project.ProjectSettingsPopup;
import com.igalblech.school.graphicaljavascriptcompiler.utils.project.V8ScriptExecutionThread;
import com.igalblech.school.graphicaljavascriptcompiler.views.CodeEditText;
import com.igalblech.school.graphicaljavascriptcompiler.views.CodeLineText;

import java.io.Serializable;

public class ScriptFragment extends Fragment {

    private CodeEditText cetScript;
    private final ProjectSettings settings;
    private ProgressBar pnRenderingProgress;

    public static final String JS_CONST_DEFAULT = "" +
            "function set(x,y) {\n" +
            "\tlet a = x / 10.0 + y / 10.0;\n" +
            "\ta *= 100;\n" +
            "\treturn [0, a, 255];\n" +
            "}\n" +
            "\n";

    public static final String JS_CONST_EXECUTE_ARR = "\n" +
            "function executeArray() {\n" +
            "   let a = [];\n" +
            "   var i, j, x, y, c;\n" +
            "   for (i = 0; i < width * height; i++) {\n" +
            "       x = i % width;" +
            "       y = i / width;\n" +
            "       c = set(x, y);" +
            "       for (j = 0; j < c.length; j++)\n" +
            "           a.push(c[j]);\n" +
            "   }\n" +
            "   return a;\n" +
            "}\n"
            ;

    public ScriptFragment( ProjectSettings settings) {
        this.settings = settings;
        if (settings.getUserData () == null)
            Log.d("Developer", "ScriptFragment 1 settings.userData is null");
        if (settings.getCode ().equals ( "" ))
            this.settings.setCode ( JS_CONST_DEFAULT );
    }

    @Nullable
    @Override
    public View onCreateView ( @NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState ) {
        View view = inflater.inflate ( R.layout.fragment_script , container, false);

        Button btnExecute = view.findViewById ( R.id.btnExecute );
        Button btnSettings = view.findViewById ( R.id.btnSettings );
        Button btnScriptShare = view.findViewById ( R.id.btnScriptShare );
        Button btnScriptShareOnGallery = view.findViewById ( R.id.btnScriptShareOnGallery );
        Button btnScriptBack = view.findViewById ( R.id.btnScriptBack );

        CodeLineText cltScript = view.findViewById ( R.id.cltScript );
        cetScript = view.findViewById(R.id.cetScript);
        pnRenderingProgress = view.findViewById ( R.id.pnRenderingProgress );

        cetScript.setText (this.settings.getCode ());
        cetScript.setCltScript ( cltScript );
        cetScript.setLines(15);
        btnExecute.setOnClickListener ( v -> {
                    Editable code = cetScript.getText ( );
                    if (code != null)
                        settings.setCode ( code.toString ( ) );
                    Activity activity = getActivity ( );
                    if (activity != null) {
                        new V8ScriptExecutionThread ( (ActivityProject) getActivity ( ), pnRenderingProgress ).execute ( settings );
                    }
                }
        );
        btnExecute.setPressed(false);
        pnRenderingProgress.setMax ( this.settings.getWidth () * this.settings.getHeight () );
        pnRenderingProgress.setVisibility(View.GONE);

        btnSettings.setOnClickListener ( v -> {
            Context context = getContext ();
            if (context != null) {
                ProjectSettingsPopup settingsPopup = new ProjectSettingsPopup ( context, settings );
                settingsPopup.show ( );
            }
        } );

        btnScriptShare.setOnClickListener ( v -> {
            if (settings.getTitle ().equals ( "" )) {
                Toast.makeText ( getContext (), getString( R.string.project_title_request), Toast.LENGTH_SHORT ).show ( );
                Context context = getContext ();
                if (context != null) {
                    ProjectSettingsPopup settingsPopup = new ProjectSettingsPopup ( context, settings );
                    settingsPopup.show ( );
                }
            }
            else {
                Intent sendIntent = new Intent ( );
                sendIntent.setAction ( Intent.ACTION_SEND );

                if (settings.getUserData () == null) {
                    Toast.makeText ( getContext ( ), "You aren't logged in!", Toast.LENGTH_SHORT ).show ( );
                    return;
                }

                StringBuilder ss = new StringBuilder (  );
                ss.append ("This is a shared code from the app \"Graphical Javascript Images\"\n");
                ss.append ( "Username : " );
                ss.append ( settings.getUserData ().getUsername ( ) );
                ss.append ( "\n" );

                String email = settings.getUserData ().getEmail ( );
                if (!email.equals ( "" )) {
                    ss.append ( "Email : " );
                    ss.append ( email );
                    ss.append ( "\n" );
                }

                ss.append ( "Title : " );
                ss.append ( settings.getTitle () );
                ss.append ( "\n" );

                String description = settings.getDescription ();
                if (!description.equals ( "" )) {
                    ss.append ( "Description : " );
                    ss.append ( description );
                    ss.append ( "\n" );
                }

                ss.append ( "Code : \n" );
                ss.append ( settings.getCode () );
                ss.append ( "\n" );

                sendIntent.setType ( "text/plain" );
                sendIntent.putExtra ( Intent.EXTRA_TEXT, ss.toString () );
                Intent shareIntent = Intent.createChooser ( sendIntent, null );
                startActivity ( shareIntent );
            }
        } );

        btnScriptShareOnGallery.setOnClickListener ( v -> {
            if (settings.getTitle ().equals ( "" )) {
                Toast.makeText ( getContext ( ), R.string.project_title_request, Toast.LENGTH_SHORT ).show ( );
                Context context = getContext ( );
                if (context != null) {
                    ProjectSettingsPopup settingsPopup = new ProjectSettingsPopup ( context, settings );
                    settingsPopup.show ( );
                }
            } else {
                if (settings.getUserData () == null)
                    Log.d("Developer", "ScriptFragment settings.userData is null");

                Intent intent = new Intent ( getActivity (), ActivityGallery.class );
                intent.putExtra ( "settings", settings );
                intent.putExtra ( "add_project", true );
                startActivity ( intent );
            }
        } );

        btnScriptBack.setOnClickListener ( v -> {
            Activity activity = getActivity ();
            if (activity != null) {
                ((ActivityProject)activity).exitProjectActivity ();
            }
        } );

        return view;
    }
}