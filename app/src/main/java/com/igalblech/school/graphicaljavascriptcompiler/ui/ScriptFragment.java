package com.igalblech.school.graphicaljavascriptcompiler.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.igalblech.school.graphicaljavascriptcompiler.ActivityProject;
import com.igalblech.school.graphicaljavascriptcompiler.R;
import com.igalblech.school.graphicaljavascriptcompiler.utils.front.RenderColorFormat;
import com.igalblech.school.graphicaljavascriptcompiler.views.CodeEditText;
import com.igalblech.school.graphicaljavascriptcompiler.views.CodeLineText;

public class ScriptFragment extends Fragment {

    private Button btnExecute;
    private CodeEditText cetScript;
    private CodeLineText cltScript;
    private RenderColorFormat renderColorFormat;

    private String strCode = "function set(x,y) {\n" +
            "   let a = x / 10.0 + y / 10.0;\n" +
            "   a *= 100;" +
            "   return [0, a, 255];\n" +
            "}\n";

    public ScriptFragment(RenderColorFormat renderColorFormat) {
        this.renderColorFormat = renderColorFormat;
    }

    @Nullable
    @Override
    public View onCreateView ( @NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState ) {
        View view = inflater.inflate ( R.layout.fragment_script , container, false);

        btnExecute = view.findViewById(R.id.btnExecute);

        cltScript = view.findViewById(R.id.cltScript);
        cetScript = view.findViewById(R.id.cetScript);
        cetScript.setText (strCode);
        cetScript.setCodeLineText (cltScript);

        btnExecute.setOnClickListener ( v -> {

            ActivityProject.V8ScriptExecutionThread.Arguments args = new
                    ActivityProject.V8ScriptExecutionThread.Arguments();
            args.script = cetScript.getText ( ).toString ( );
            args.width = 25;
            args.height = 25;
            args.format = renderColorFormat;
            new ActivityProject.V8ScriptExecutionThread ( (ActivityProject) getActivity () ).execute ( args );

        } );

        btnExecute.setPressed(false);

        return view;
    }

    public void setCode ( String code ) {
        this.strCode = code;
        cetScript.setText ( code );
    }
}