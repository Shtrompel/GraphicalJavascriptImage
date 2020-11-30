package com.igalblech.school.graphicaljavascriptcompiler.utils.project;

import android.app.Dialog;
import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.igalblech.school.graphicaljavascriptcompiler.R;


public class ProjectSettingsPopup extends Dialog {

    private final EditText etProjectSettingsTitle;
    private final EditText etProjectSettingsDescription;

    public ProjectSettingsPopup ( @NonNull Context context, @NonNull ProjectSettings settings ) {
        super ( context );

        setContentView ( R.layout.popup_project_settings );

        etProjectSettingsTitle = findViewById ( R.id.etProjectSettingsTitle );
        etProjectSettingsDescription = findViewById ( R.id.etProjectSettingsDescription );

        etProjectSettingsTitle.setText ( settings.getTitle () );
        etProjectSettingsDescription.setText ( settings.getDescription () );
        etProjectSettingsDescription.setMovementMethod(new ScrollingMovementMethod ());

        Button btnProjectSettingsCancel = findViewById ( R.id.btnProjectSettingsCancel );
        Button btnProjectSettingsSave = findViewById ( R.id.btnProjectSettingsSave );

        btnProjectSettingsCancel.setOnClickListener ( v -> dismiss () );

        btnProjectSettingsSave.setOnClickListener ( v -> {
            String title = etProjectSettingsTitle.getText ().toString ();
            String description = etProjectSettingsDescription.getText ().toString ();
            settings.setProjectInfo ( title, description );
            dismiss ();
        } );
    }
}
