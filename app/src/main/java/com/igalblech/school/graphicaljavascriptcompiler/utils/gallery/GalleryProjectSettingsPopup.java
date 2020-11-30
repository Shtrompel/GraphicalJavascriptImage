package com.igalblech.school.graphicaljavascriptcompiler.utils.gallery;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.igalblech.school.graphicaljavascriptcompiler.ActivityGallery;
import com.igalblech.school.graphicaljavascriptcompiler.ActivityProject;
import com.igalblech.school.graphicaljavascriptcompiler.R;
import com.igalblech.school.graphicaljavascriptcompiler.utils.project.ProjectSettings;

import java.util.Locale;

import static com.igalblech.school.graphicaljavascriptcompiler.ActivityGallery.dateToString;


public class GalleryProjectSettingsPopup extends Dialog {

    private ProjectSettings settings;

    private TextView tvPopupGallerySettingsTitle,
            tvPopupGallerySettingsDesc,
            tvPopupGallerySettingsInfo;
    private Button btnPopupGallerySettingsClose,
            btnProjectSettingsPublish,
            btnPopupGallerySettingsEdit;
    private RatingBar tvPopupRatingBar;
    private float rating = -1.0f;

    public GalleryProjectSettingsPopup ( @NonNull Context context, @NonNull final ProjectSettings settings) {
        this (context, settings, false, null);
    }

    public GalleryProjectSettingsPopup ( @NonNull Context context, @NonNull final ProjectSettings settings, boolean isPrivate, View.OnClickListener listener ) {
        super ( context );
        setContentView ( R.layout.popup_gallery_project_settings );
        getLayoutInflater ().from(context).inflate(R.layout.popup_gallery_project_settings, null);

        this.settings = settings;

        tvPopupGallerySettingsTitle = findViewById ( R.id.tvPopupGallerySettingsTitle );
        tvPopupGallerySettingsDesc = findViewById ( R.id.tvPopupGallerySettingsDesc );
        tvPopupGallerySettingsInfo = findViewById ( R.id.tvPopupGallerySettingsInfo );
        btnPopupGallerySettingsClose = findViewById ( R.id.btnPopupGallerySettingsClose );
        btnProjectSettingsPublish = findViewById ( R.id.btnProjectSettingsPublish );
        btnPopupGallerySettingsEdit = findViewById ( R.id.btnPopupGallerySettingsEdit );
        tvPopupRatingBar = findViewById ( R.id.tvPopupRatingBar );

        String title = String.format ( "%s - %s", settings.getUserData ().getUsername (), settings.getTitle () );
        tvPopupGallerySettingsTitle.setText ( title );
        tvPopupGallerySettingsDesc.setText ( settings.getDescription () );
        tvPopupGallerySettingsDesc.setMovementMethod(new ScrollingMovementMethod ());

        String infoFormat =
                "Views: %d\nWidth: %d\nHeight: %d\nColor Model: %s\n" +
                "Channel Bits: %d\n Is Float: %s\n Has Alpha: %s" +
                        "Date Created: %s\nDate Updated: %s";
        String info = String.format ( infoFormat,
                settings.getViews (),
                settings.getWidth (),
                settings.getHeight (),
                settings.getFormat ().colorModelToString (),
                settings.getFormat ().getChannelBit (),
                settings.getFormat ().isHasAlpha () ? "True" : "False",
                settings.getFormat ().isHasAlpha () ? "True" : "False",
                dateToString(settings.getDateCreated ()),
                dateToString(settings.getLastUpdated ())
                );

        btnPopupGallerySettingsClose.setOnClickListener ( v -> dismiss () );
        btnPopupGallerySettingsEdit.setOnClickListener ( v -> {
            Intent intent = new Intent ( context, ActivityProject.class );
            intent.putExtra ( "settings", settings );
            context.startActivity ( intent );
        } );

        if (isPrivate && listener != null) {
            btnProjectSettingsPublish.setOnClickListener (listener);
        }
        else {
            btnProjectSettingsPublish.setVisibility ( View.INVISIBLE );
        }

        tvPopupGallerySettingsInfo.setText ( info );

        if (isPrivate) {
            tvPopupRatingBar.setVisibility ( View.INVISIBLE );
        }
        else {
            tvPopupRatingBar.setRating ( settings.getRatings ( ) / 2.0f );
            tvPopupRatingBar.setOnRatingBarChangeListener ( new RatingBar.OnRatingBarChangeListener ( ) {
                @Override
                public void onRatingChanged ( RatingBar ratingBar, float r, boolean fromUser ) {
                    if (fromUser) {
                        rating = r;
                        ratingBar.setRating ( r );
                    }
                }
            } );
        }
    }

    @Override
    public void setOnDismissListener ( @Nullable OnDismissListener listener ) {
        if (rating != -1.0f)
            settings.addVote ( rating / 2.0f );
        super.setOnDismissListener ( listener );
    }
}