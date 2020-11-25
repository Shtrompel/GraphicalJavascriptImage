package com.igalblech.school.graphicaljavascriptcompiler;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.igalblech.school.graphicaljavascriptcompiler.utils.FakeDataGenerator;
import com.igalblech.school.graphicaljavascriptcompiler.utils.gallery.GalleryProjectSettingsPopup;
import com.igalblech.school.graphicaljavascriptcompiler.utils.gallery.ProjectListAdapter;
import com.igalblech.school.graphicaljavascriptcompiler.utils.gallery.ProjectSettingsDatabase;
import com.igalblech.school.graphicaljavascriptcompiler.utils.project.ProjectSettings;
import com.igalblech.school.graphicaljavascriptcompiler.utils.project.ProjectSettingsPopup;
import com.igalblech.school.graphicaljavascriptcompiler.utils.project.RenderColorFormat;
import com.igalblech.school.graphicaljavascriptcompiler.utils.userdata.UserData;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static com.igalblech.school.graphicaljavascriptcompiler.utils.FakeDataGenerator.debugRandomSettings;

public class ActivityGallery extends AppCompatActivity implements AbsListView.OnScrollListener {

    private UserData userData = null;
    private ProjectSettings settings = null;
    private boolean addNewProject = false;

    private ListView lsGalleryProjects;
    private Button btnGalleryMyProjects, btnGalleryNewProjects, btnGalleryTopProjects, btnGalleryExit;

    private ProjectSettingsDatabase database;
    private ProjectListAdapter listAdapter;

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_gallery );
        getLayoutInflater ().inflate ( R.layout.activity_gallery, null );

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            settings = (ProjectSettings) bundle.get ( "settings" );
            addNewProject = bundle.getBoolean ( "add_project", false );
        }

        database = new ProjectSettingsDatabase ( this, null );
        if (addNewProject) {
            if (settings.userData == null)
                Toast.makeText ( this, "There was a problem when adding the project!", Toast.LENGTH_SHORT ).show ( );
            else {
                long maxId = database.getMaxId ( ProjectSettingsDatabase.Constants.PRIVATE_TABLE_NAME );
                database.addProject ( settings, 1 + maxId, ProjectSettingsDatabase.Constants.PRIVATE_TABLE_NAME );
            }
        }

        btnGalleryMyProjects = findViewById(R.id.btnGalleryMyProjects);
        btnGalleryNewProjects = findViewById(R.id.btnGalleryNewProjects);
        btnGalleryTopProjects = findViewById(R.id.btnGalleryTopProjects);
        btnGalleryExit = findViewById(R.id.btnGalleryExit);
        lsGalleryProjects = findViewById(R.id.lsGalleryProjects);

        List<ProjectSettings> projects = new ArrayList<> ();

        listAdapter = new ProjectListAdapter(this, R.layout.list_gallery_project, projects);

        List<ProjectSettings> list;
        list = database.toList ( 0, 5, ProjectSettingsDatabase.Constants.PRIVATE_TABLE_NAME );
        if (list == null || list.size () == 0) {
            Toast.makeText ( this, "No Projects Found", Toast.LENGTH_SHORT ).show ( );
        }
        else {
            projects.addAll ( list );
        }

        btnGalleryExit.setOnClickListener ( v -> finish () );

        final Context context = this;
        lsGalleryProjects.setAdapter ( listAdapter );
        lsGalleryProjects.setOnItemClickListener ( ( parent, view, position, id ) -> {
            ProjectSettings settings = ((ProjectListAdapter) lsGalleryProjects.getAdapter ( )).getItem ( position );
            GalleryProjectSettingsPopup popup = new GalleryProjectSettingsPopup ( context, settings, true );
            popup.show ( );
        } );
        lsGalleryProjects.setOnScrollListener ( this );
    }

    public void applyList(String table, String sortBy) {
        List<ProjectSettings> projects = new ArrayList<> ();
        List<ProjectSettings> list;
        list = database.toList ( 0, 5, table, sortBy );
        if (list == null || list.size () == 0) {
            Toast.makeText ( this, "No Projects Found", Toast.LENGTH_SHORT ).show ( );
        }
        else {
            projects.addAll ( list );
        }
    }

    public static String dateToString( Date date) {
        DateTimeFormatter formatter;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ISO_LOCAL_DATE;
            return formatter.format(date.toInstant()
                    .atZone( ZoneId.systemDefault())
                    .toLocalDate());
        }
        else {
            return date.toString ();
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        int last = lsGalleryProjects.getLastVisiblePosition();
        int headerCount = lsGalleryProjects.getHeaderViewsCount();
        int footerCount = lsGalleryProjects.getFooterViewsCount();
        int adapterCount = listAdapter.getCount();
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE &&
                (last - headerCount - footerCount) >= (adapterCount - 1)) {

            int start = lsGalleryProjects.getCount ();
            List<ProjectSettings> list;
            list = database.toList ( start, start + 1, ProjectSettingsDatabase.Constants.PRIVATE_TABLE_NAME );
            if (list == null || list.size () == 0) {
                Toast.makeText ( this, "No More Projects Found", Toast.LENGTH_SHORT ).show ( );
            }
            else {
                listAdapter.addAll ( list );
                listAdapter.notifyDataSetChanged ( );
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }
}