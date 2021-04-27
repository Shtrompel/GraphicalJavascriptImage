package com.igalblech.school.graphicaljavascriptcompiler.utils.gallery;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.igalblech.school.graphicaljavascriptcompiler.R;
import com.igalblech.school.graphicaljavascriptcompiler.utils.project.ProjectSettings;

import java.util.List;
import java.util.Locale;

import static com.igalblech.school.graphicaljavascriptcompiler.ActivityGallery.dateToString;

/**
 * Adapter that used for displaying projects in a list.
 * @see com.igalblech.school.graphicaljavascriptcompiler.ActivityGallery
 */
public class ProjectListAdapter extends ArrayAdapter<ProjectSettings> {

    public ProjectListAdapter ( Context context, int resource, List<ProjectSettings> users ) {
        super ( context, resource, users );
    }

    public ProjectListAdapter ( @NonNull Context context, int resource, int textViewResourceId, @NonNull List<ProjectSettings> objects ) {
        super ( context, resource, textViewResourceId, objects );
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View getView ( int position, View convertView, ViewGroup parent ) {

        ProjectSettings settings = getItem ( position );

        if (convertView == null) {
            convertView = LayoutInflater.from ( getContext ( ) ).inflate ( R.layout.list_gallery_project, null );
        }

        TextView tvProjectTitle = convertView.findViewById ( R.id.tvProjectTitle );
        TextView tvListGalleryWidth = convertView.findViewById ( R.id.tvListGalleryWidth );
        TextView tvListGalleryHeight = convertView.findViewById ( R.id.tvListGalleryHeight );
        TextView tvListGalleryCreated = convertView.findViewById ( R.id.tvListGalleryCreated );
        TextView tvListGalleryLast = convertView.findViewById ( R.id.tvListGalleryLast );

        if (settings.getUserData () != null) {
            tvProjectTitle.setText ( String.format ( "%s - %s", settings.getUserData ().getUsername ( ), settings.getTitle () ) );
        }
        tvListGalleryWidth.setText ( String.format (Locale.ENGLISH, "Width - %d", settings.getWidth () ) );
        tvListGalleryHeight.setText ( String.format (Locale.ENGLISH, "Height - %d", settings.getHeight () ) );
        tvListGalleryCreated.setText ( String.format (Locale.ENGLISH, "Date Created - %s", dateToString ( settings.getDateCreated () )) );
        tvListGalleryLast.setText ( String.format (Locale.ENGLISH, "Date Updated - %s", dateToString ( settings.getLastUpdated () )) );

        return convertView;
    }
}