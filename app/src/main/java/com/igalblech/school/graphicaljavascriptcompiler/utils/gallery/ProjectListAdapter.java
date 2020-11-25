package com.igalblech.school.graphicaljavascriptcompiler.utils.gallery;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.igalblech.school.graphicaljavascriptcompiler.R;
import com.igalblech.school.graphicaljavascriptcompiler.utils.project.ProjectSettings;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.igalblech.school.graphicaljavascriptcompiler.ActivityGallery.dateToString;

public class ProjectListAdapter extends ArrayAdapter<ProjectSettings> {

    private TextView tvProjectTitle, tvListGalleryWidth,
            tvListGalleryHeight, tvListGalleryCreated, tvListGalleryLast;

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

        tvProjectTitle = convertView.findViewById ( R.id.tvProjectTitle );
        tvListGalleryWidth =  convertView.findViewById ( R.id.tvListGalleryWidth );
        tvListGalleryHeight = convertView.findViewById ( R.id.tvListGalleryHeight );
        tvListGalleryCreated = convertView.findViewById ( R.id.tvListGalleryCreated );
        tvListGalleryLast = convertView.findViewById ( R.id.tvListGalleryLast );

        if (settings.userData != null) {
            tvProjectTitle.setText ( String.format ( "%s - %s", settings.userData.getUsername ( ), settings.title ) );
        }
        tvListGalleryWidth.setText ( String.format (Locale.ENGLISH, "Width - %d", settings.width ) );
        tvListGalleryHeight.setText ( String.format (Locale.ENGLISH, "Height - %d", settings.height ) );
        tvListGalleryCreated.setText ( String.format (Locale.ENGLISH, "Date Created - %s", dateToString ( settings.dateCreated )) );
        tvListGalleryLast.setText ( String.format (Locale.ENGLISH, "Date Updated - %s", dateToString ( settings.lastUpdated )) );

        return convertView;
    }
}