package com.igalblech.school.graphicaljavascriptcompiler.utils.gallery;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.igalblech.school.graphicaljavascriptcompiler.ActivityProject;
import com.igalblech.school.graphicaljavascriptcompiler.R;
import com.igalblech.school.graphicaljavascriptcompiler.utils.project.ProjectSettings;

import java.util.Locale;

import static com.igalblech.school.graphicaljavascriptcompiler.ActivityGallery.dateToString;

/**
 * Shows information about a selected project from the gallery.
 * @see com.igalblech.school.graphicaljavascriptcompiler.ActivityGallery
 */
public class GalleryProjectSettingsPopup extends Dialog {

    private final ProjectSettings settings;
    private final ProjectSettingsDatabase database;
    private float rating = -1.0f;
    final Button btnPopupGallerySettingsClose;
    final Button btnProjectSettingsPublish;
    final Button btnPopupGallerySettingsEdit;
    final Button btnPopupGallerySettingsDelete;

    public GalleryProjectSettingsPopup ( @NonNull Context context,
                                         @NonNull final ProjectSettings settings,
                                         @NonNull ProjectSettingsDatabase database) {
        this (context, settings, database, false);
    }

    public GalleryProjectSettingsPopup ( @NonNull Context context,
                                         @NonNull final ProjectSettings settings,
                                         @NonNull ProjectSettingsDatabase database,
                                         boolean isPrivate) {
        super ( context );
        setContentView ( R.layout.popup_gallery_project_settings );
        LayoutInflater.from (context).inflate(R.layout.popup_gallery_project_settings, null);

        TextView tvPopupGallerySettingsTitle,
                tvPopupGallerySettingsDesc,
                tvPopupGallerySettingsInfo;
        RatingBar tvPopupRatingBar;

        this.settings = settings;
        this.database = database;

        settings.addView ();
        database.updateProject ( ProjectSettingsDatabase.Constants.PUBLIC_TABLE_NAME, settings );

        tvPopupGallerySettingsTitle = findViewById ( R.id.tvPopupGallerySettingsTitle );
        tvPopupGallerySettingsDesc = findViewById ( R.id.tvPopupGallerySettingsDesc );
        tvPopupGallerySettingsInfo = findViewById ( R.id.tvPopupGallerySettingsInfo );
        btnPopupGallerySettingsClose = findViewById ( R.id.btnPopupGallerySettingsClose );
        btnProjectSettingsPublish = findViewById ( R.id.btnProjectSettingsPublish );
        btnPopupGallerySettingsEdit = findViewById ( R.id.btnPopupGallerySettingsEdit );
        btnPopupGallerySettingsDelete = findViewById ( R.id.btnPopupGallerySettingsDelete );
        tvPopupRatingBar = findViewById ( R.id.tvPopupRatingBar );

        String title = String.format ( "%s - %s", settings.getUserData ().getUsername (), settings.getTitle () );
        tvPopupGallerySettingsTitle.setText ( title );
        tvPopupGallerySettingsDesc.setText ( settings.getDescription () );
        tvPopupGallerySettingsDesc.setMovementMethod(new ScrollingMovementMethod ());

        String infoFormat =
                "Views: %d\nWidth: %d\nHeight: %d\nColor Model: %s\n" +
                "Channel Bits: %d\n Is Float: %s\n Has Alpha: %s\n" +
                        "Date Created: %s\nDate Updated: %s";
        String info = String.format ( Locale.ENGLISH,
                infoFormat,
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

        tvPopupGallerySettingsInfo.setText ( info );

        if (isPrivate) {
            tvPopupRatingBar.setVisibility ( View.INVISIBLE );
            //btnPopupGallerySettingsDelete.setOnClickListener ( v -> { database.deleteProject(ProjectSettingsDatabase.Constants.PRIVATE_TABLE_NAME, settings); } );
        }
        else {
            btnProjectSettingsPublish.setVisibility ( View.INVISIBLE );
            btnPopupGallerySettingsDelete.setVisibility ( View.INVISIBLE );

            tvPopupRatingBar.setRating ( settings.getRatings ( ) / 2.0f );
            tvPopupRatingBar.setOnRatingBarChangeListener ( ( ratingBar, r, fromUser ) -> {
                rating = 2.0f * r;
                ratingBar.setRating ( r );
            } );
        }

        setOnDismissListener ( dialog -> finalize() );
        setOnCancelListener ( dialog -> finalize() );
    }

    public void finalize() {
        if (rating != -1.0f) {
            settings.addVote ( rating );
            database.updateProject ( ProjectSettingsDatabase.Constants.PUBLIC_TABLE_NAME, settings );
        }
    }

    public void setOnPublishClick(Button.OnClickListener listener) {
        btnProjectSettingsPublish.setOnClickListener (listener);
    }

    public void setOnDeleteClick(Button.OnClickListener listener) {
        btnPopupGallerySettingsDelete.setOnClickListener (listener);
    }
}