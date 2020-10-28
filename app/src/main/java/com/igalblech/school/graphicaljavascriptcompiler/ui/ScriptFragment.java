package com.igalblech.school.graphicaljavascriptcompiler.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import com.igalblech.school.graphicaljavascriptcompiler.ActivityProject;
import com.igalblech.school.graphicaljavascriptcompiler.R;
import com.igalblech.school.graphicaljavascriptcompiler.utils.project.ProjectSettings;
import com.igalblech.school.graphicaljavascriptcompiler.utils.project.ProjectSettingsPopup;
import com.igalblech.school.graphicaljavascriptcompiler.views.CodeEditText;
import com.igalblech.school.graphicaljavascriptcompiler.views.CodeLineText;

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
        if (this.settings.code.equals ( "" ))
            this.settings.code = JS_CONST_DEFAULT;
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

        cetScript.setText (this.settings.code);
        cetScript.setCltScript ( cltScript );
        cetScript.setLines(15);
        btnExecute.setOnClickListener ( v -> {
                    Editable code = cetScript.getText ( );
                    try {
                        settings.code = code.toString ( );
                    } catch (NullPointerException e) {
                        e.printStackTrace ( );
                        Log.d ( "developer", e.toString ( ) );
                    }
                    Activity activity = getActivity ( );
                    if (activity != null) {
                        new ActivityProject.V8ScriptExecutionThread ( (ActivityProject) getActivity ( ), pnRenderingProgress ).execute ( settings );
                    }
                }
        );
        btnExecute.setPressed(false);
        pnRenderingProgress.setMax ( this.settings.width * this.settings.height );
        pnRenderingProgress.setVisibility(View.GONE);

        btnSettings.setOnClickListener ( v -> {
            Context context = getContext ();
            if (context != null) {
                ProjectSettingsPopup settingsPopup = new ProjectSettingsPopup ( context, settings );
                settingsPopup.show ( );
            }
        } );

        btnScriptShare.setOnClickListener ( v -> {
            if (settings.title.equals ( "" )) {
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

                if (settings.userData == null) {
                    Toast.makeText ( getContext ( ), "You aren't logged in!", Toast.LENGTH_SHORT ).show ( );
                    return;
                }

                StringBuilder ss = new StringBuilder (  );
                ss.append ("This is a shared code from the app \"Graphical Javascript Images\"\n");
                ss.append ( "Username : " );
                ss.append ( settings.userData.getUsername ( ) );
                ss.append ( "\n" );

                String email = settings.userData.getEmail ( );
                if (!email.equals ( "" )) {
                    ss.append ( "Email : " );
                    ss.append ( email );
                    ss.append ( "\n" );
                }

                ss.append ( "Title : " );
                ss.append ( settings.title );
                ss.append ( "\n" );

                String description = settings.description;
                if (!description.equals ( "" )) {
                    ss.append ( "Description : " );
                    ss.append ( description );
                    ss.append ( "\n" );
                }

                ss.append ( "Code : \n" );
                ss.append ( settings.code );
                ss.append ( "\n" );

                sendIntent.setType ( "text/plain" );
                sendIntent.putExtra ( Intent.EXTRA_TEXT, ss.toString () );
                Intent shareIntent = Intent.createChooser ( sendIntent, null );
                startActivity ( shareIntent );
            }
        } );

        btnScriptShareOnGallery.setOnClickListener ( v -> {
            if (settings.title.equals ( "" )) {
                Toast.makeText ( getContext (), R.string.project_title_request, Toast.LENGTH_SHORT ).show ( );
                Context context = getContext ();
                if (context != null) {
                    ProjectSettingsPopup settingsPopup = new ProjectSettingsPopup ( context, settings );
                    settingsPopup.show ( );
                }
            }
        } );

        btnScriptBack.setOnClickListener ( v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext ());
            Activity activity = getActivity ();
            if (activity != null) {
                builder.setMessage ( "Unsaved project will be lost" )
                        .setCancelable ( false )
                        .setPositiveButton ( "OK", ( dialog, id ) -> activity.finish ( ) )
                        .setNegativeButton ( "Cancel", ( dialog, which ) -> {

                        } );
                AlertDialog alert = builder.create ( );
                alert.show ( );//Toast.makeText ( getContext (), "You must log in to your account in order to make a new project!", Toast.LENGTH_LONG ).show ( );
            }
        } );

        return view;
    }
}