package com.igalblech.school.graphicaljavascriptcompiler.utils.project;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.MenuInflater;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.igalblech.school.graphicaljavascriptcompiler.ActivityProject;
import com.igalblech.school.graphicaljavascriptcompiler.R;

import java.util.Calendar;

public class CreateProjectPopup extends Dialog {

    private ProjectSettings settings;

    public int selectedItem = 0;
    private final EditText etBits;
    private final EditText etRenderWidth;
    private final EditText etRenderHeight;
    private final TextView tvColorModel;
    private final CheckBox cbHasAlpha;
    private final CheckBox cbIsFloat;

    public CreateProjectPopup ( @NonNull Context context ) {
        super ( context );

        setContentView ( R.layout.popup_create_project );

        etBits = findViewById ( R.id.etPopupProjectBits );
        tvColorModel = findViewById ( R.id.tvPopupProjectColorModel );
        tvColorModel.setPaintFlags ( Paint.UNDERLINE_TEXT_FLAG );
        tvColorModel.setOnClickListener ( v -> {
            PopupMenu popup = new PopupMenu ( context, v );
            popup.setOnMenuItemClickListener ( item -> {
                tvColorModel.setText (String.format (context.getString ( R.string.color_model_text), item.getTitle ( )) );
                selectedItem = item.getOrder ( );
                return false;
            } );
            MenuInflater inflater = popup.getMenuInflater ( );
            inflater.inflate ( R.menu.project_color_model_menu, popup.getMenu ( ) );
            popup.show ( );
        } );

        etRenderWidth = findViewById ( R.id.etPopupProjectRenderWidth );
        etRenderHeight = findViewById ( R.id.etPopupProjectRenderHeight );

        cbHasAlpha = findViewById ( R.id.cbPopupProjectHasAlpha );
        cbIsFloat = findViewById ( R.id.cbPopupProjectIsFloat );

        Button tvCancel = findViewById ( R.id.tvPopupProjectCancel );
        tvCancel.setOnClickListener ( v -> dismiss ( ) );

        Button tvCreate = findViewById ( R.id.tvPopupProjectCreate );

        tvCreate.setOnClickListener ( v -> {
            try {
                int renderWidth = Integer.parseInt ( etRenderWidth.getText ( ).toString ( ) );
                int renderHeight = Integer.parseInt ( etRenderHeight.getText ( ).toString ( ) );
                int channelBits = Integer.parseInt ( etBits.getText ( ).toString ( ) );
                int colorModel = selectedItem;
                boolean isFloat = cbIsFloat.isChecked ( );
                boolean hasAlpha = cbHasAlpha.isChecked ( );

                ProjectSettings settings1;
                settings1 = new ProjectSettings ( );
                settings1.format = new RenderColorFormat ( colorModel, channelBits, isFloat, hasAlpha );
                settings1.width = renderWidth;
                settings1.height = renderHeight;
                settings1.dateCreated = Calendar.getInstance ( ).getTime ( );
                settings1.lastUpdated = Calendar.getInstance ( ).getTime ( );
                settings1.code = "";
                settings1.description = "";

                dismiss ( );

                Intent intent = new Intent ( context, ActivityProject.class );
                intent.putExtra ( "settings", settings1 );
                context.startActivity ( intent );
            } catch (IllegalArgumentException e) {
                Toast.makeText ( context, "Illegal argument in the Color Bits", Toast.LENGTH_SHORT ).show ( );
            }
        } );


        setCancelable ( true );
        setTitle ( "Create Project" );
    }

}
