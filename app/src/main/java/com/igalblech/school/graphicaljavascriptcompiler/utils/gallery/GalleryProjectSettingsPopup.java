package com.igalblech.school.graphicaljavascriptcompiler.utils.gallery;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.igalblech.school.graphicaljavascriptcompiler.ActivityGallery;
import com.igalblech.school.graphicaljavascriptcompiler.ActivityProject;
import com.igalblech.school.graphicaljavascriptcompiler.R;
import com.igalblech.school.graphicaljavascriptcompiler.utils.project.ProjectSettings;

import java.util.Locale;

import static com.igalblech.school.graphicaljavascriptcompiler.ActivityGallery.dateToString;


public class GalleryProjectSettingsPopup extends Dialog {

    private TextView tvPopupGallerySettingsTitle,
            tvPopupGallerySettingsDesc,
            tvPopupGallerySettingsInfo;
    private Button btnPopupGallerySettingsClose,
            btnProjectSettingsPublish,
            btnPopupGallerySettingsEdit;

    public GalleryProjectSettingsPopup ( @NonNull Context context, @NonNull final ProjectSettings settings, boolean isPrivate ) {
        super ( context );
        setContentView ( R.layout.popup_gallery_project_settings );
        final ViewGroup nullParent = null;
        getLayoutInflater ().from(context).inflate(R.layout.popup_gallery_project_settings, nullParent);

        tvPopupGallerySettingsTitle = findViewById ( R.id.tvPopupGallerySettingsTitle );
        tvPopupGallerySettingsDesc = findViewById ( R.id.tvPopupGallerySettingsDesc );
        tvPopupGallerySettingsInfo = findViewById ( R.id.tvPopupGallerySettingsInfo );
        btnPopupGallerySettingsClose = findViewById ( R.id.btnPopupGallerySettingsClose );
        btnProjectSettingsPublish = findViewById ( R.id.btnProjectSettingsPublish );
        btnPopupGallerySettingsEdit = findViewById ( R.id.btnPopupGallerySettingsEdit );


        String title = String.format ( "%s - %s", settings.userData.getUsername (), settings.title );
        tvPopupGallerySettingsTitle.setText ( title );
        tvPopupGallerySettingsDesc.setText ( settings.description );
        tvPopupGallerySettingsDesc.setMovementMethod(new ScrollingMovementMethod ());

        String infoFormat =
                "Width: %d\nHeight: %d\nColor Model: %s\n" +
                "Channel Bits: %d\n Is Float: %s\n Has Alpha: %s" +
                        "Date Created: %s\nDate Updated: %s";
        String info = String.format ( Locale.ENGLISH, infoFormat,
                settings.width,
                settings.height,
                settings.format.colorModelToString (),
                settings.format.channelBit,
                settings.format.hasAlpha ? "True" : "False",
                settings.format.hasAlpha ? "True" : "False",
                dateToString(settings.dateCreated),
                dateToString(settings.lastUpdated)
                );
        tvPopupGallerySettingsInfo.setText ( info );

        btnPopupGallerySettingsClose.setOnClickListener ( v -> dismiss () );
        btnPopupGallerySettingsEdit.setOnClickListener ( v -> {
            Intent intent = new Intent ( context, ActivityProject.class );
            intent.putExtra ( "settings", settings );
            context.startActivity ( intent );
        } );

        btnProjectSettingsPublish.setOnClickListener ( v -> {

        } );
    }
}