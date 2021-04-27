package com.igalblech.school.graphicaljavascriptcompiler.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import java.util.Locale;

/**
 * In this fragment the user have access for the most important parts of
 * his project.
 * This fragment is part of the project activity.
 * @see ActivityProject
 */
public class ScriptFragment extends Fragment {

    private CodeEditText cetScript;
    private final ProjectSettings settings;
    private ProgressBar pnRenderingProgress;

    public static final String JS_CONST_DEFAULT = "" +
            "function set(x,y) {\n"                  +
            "\tlet a = x / %f + y / %f;\n"       +
            "\ta *= %f;\n"                          +
            "\treturn %s;\n"                +
            "}\n"                                    +
            "\n";

    public static final String JS_CONST_EXECUTE_ARR = "\n" +
            "function executeArray() {\n"                  +
            "   let a = [];\n"                             +
            "   var i, j, x, y, c;\n"                      +
            "   for (i = 0; i < width * height; i++) {\n"  +
            "       x = i % width;"                        +
            "       y = i / width;\n"                      +
            "       c = set(x, y);"                        +
            "       for (j = 0; j < c.length; j++)\n"      +
            "           a.push(c[j]);\n"                   +
            "   }\n"                                       +
            "   return a;\n"                               +
            "}\n"
            ;

    public ScriptFragment( ProjectSettings settings) {
        this.settings = settings;
        if (settings.getUserData () == null)
            Log.d("Developer", "ScriptFragment 1 settings.userData is null");
        if (settings.getCode ().equals ( "" )) {
            int cc = settings.getFormat ().getChannelCount ();
            int byteCount = (int)Math.pow(2, settings.getFormat ().getChannelBit ());
            double argA = Math.sqrt(byteCount);
            double argB = Math.pow(byteCount, 5.0 / 6.0);

            String returnArr = "[";
            String[] returnArgs = new String[cc];
            String[] returnArgs2 = {"0", ""+(cc-1), "a"};
            for (int i = 0; i < cc; i++) {
                returnArgs[i] = returnArgs2[i%3];
            }
            if (settings.getFormat ().isHasAlpha ())
                returnArgs[returnArgs.length - 1] = "" + (byteCount - 1);

            for (int i = 0; i < cc; i++) {
                returnArr += returnArgs[i] + ((i==cc-1)?"":", ");
            }
            returnArr += "]";

            String code = String.format (
                    Locale.ENGLISH,
                    JS_CONST_DEFAULT,
                    argA,
                    argA,
                    argB,
                    returnArr
                    );

            this.settings.setCode ( code );
        }
    }

    @Nullable
    @Override
    public View onCreateView (
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState ) {

        View view = inflater.inflate ( R.layout.fragment_script, container, false );
        //View view = inflater.inflate ( R.layout.fragment_script , container, false);

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
        cetScript.addTextChangedListener ( new TextWatcher ( ) {
            @Override
            public void beforeTextChanged ( CharSequence s, int start, int count, int after ) {

            }

            @Override
            public void onTextChanged ( CharSequence s, int start, int before, int count ) {

            }

            @Override
            public void afterTextChanged ( Editable s ) {
                settings.setCode ( s.toString () );
            }
        } );
        btnExecute.setOnClickListener ( v -> {
                    Editable code = cetScript.getText ( );
                    if (code != null)
                        settings.setCode ( code.toString ( ) );
                    Activity activity = getActivity ( );
                    if (activity != null) {
                        V8ScriptExecutionThread thread;
                        thread = new V8ScriptExecutionThread ( (ActivityProject) activity, pnRenderingProgress );
                        thread.execute ( settings );
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

                settings.updateDate ();

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