package com.igalblech.school.graphicaljavascriptcompiler;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.igalblech.school.graphicaljavascriptcompiler.utils.FakeDataGenerator;
import com.igalblech.school.graphicaljavascriptcompiler.utils.gallery.GalleryProjectSettingsPopup;
import com.igalblech.school.graphicaljavascriptcompiler.utils.gallery.ProjectListAdapter;
import com.igalblech.school.graphicaljavascriptcompiler.utils.gallery.ProjectSettingsDatabase;
import com.igalblech.school.graphicaljavascriptcompiler.utils.project.ProjectSettings;
import com.igalblech.school.graphicaljavascriptcompiler.utils.userdata.UserData;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.igalblech.school.graphicaljavascriptcompiler.utils.gallery.ProjectSettingsDatabase.Constants.COLUMN_CREATED_DATE;
import static com.igalblech.school.graphicaljavascriptcompiler.utils.gallery.ProjectSettingsDatabase.Constants.COLUMN_PUBLIC_RATING;
import static com.igalblech.school.graphicaljavascriptcompiler.utils.gallery.ProjectSettingsDatabase.Constants.COLUMN_PUBLIC_VIEWS;
import static com.igalblech.school.graphicaljavascriptcompiler.utils.gallery.ProjectSettingsDatabase.Constants.PRIVATE_TABLE_NAME;
import static com.igalblech.school.graphicaljavascriptcompiler.utils.gallery.ProjectSettingsDatabase.Constants.PUBLIC_TABLE_NAME;



public class ActivityGallery extends AppCompatActivity implements AbsListView.OnScrollListener {

    public static final int GALLERY_MODE_PRIVATE = 0;
    public static final int GALLERY_MODE_PUBLIC_NEW = 1;
    public static final int GALLERY_MODE_PUBLIC_TOP = 2;
    private static final int GALLERY_MODE_PUBLIC_HOT = 3;

    private UserData userData = null;
    private ProjectSettings settings = null;
    private boolean addNewProject = false;

    private ListView lsGalleryProjects;
    private Button btnGalleryMyProjects, btnGalleryNewProjects, btnGalleryTopProjects, btnGalleryHotProjects, btnGalleryExit;

    private ProjectSettingsDatabase database;
    private ProjectListAdapter listAdapter;
    private GalleryProjectSettingsPopup settingsPopup;

    private int galleryMode = 0;

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

        /*FakeDataGenerator.rand.setSeed ( 1239 );
        for (int i = 0; i < 12; i++){
            database.addProject ( FakeDataGenerator.debugRandomSettings (), database.getMaxId ( PUBLIC_TABLE_NAME ) + 1, PUBLIC_TABLE_NAME );
            database.addProject ( FakeDataGenerator.debugRandomSettings (), database.getMaxId ( PRIVATE_TABLE_NAME ) + 1, PRIVATE_TABLE_NAME );
        }*/

        if (addNewProject) {
            if (settings.getUserData () == null)
                Toast.makeText ( this, "There was a problem when adding the project!", Toast.LENGTH_SHORT ).show ( );
            else {
                long maxId = database.getMaxId ( PRIVATE_TABLE_NAME );
                database.addProject ( settings, 1 + maxId, PRIVATE_TABLE_NAME );
            }
        }

        btnGalleryMyProjects = findViewById(R.id.btnGalleryMyProjects);
        btnGalleryNewProjects = findViewById(R.id.btnGalleryNewProjects);
        btnGalleryTopProjects = findViewById(R.id.btnGalleryTopProjects);
        btnGalleryHotProjects = findViewById(R.id.btnGalleryHotProjects);

        btnGalleryExit = findViewById(R.id.btnGalleryExit);
        lsGalleryProjects = findViewById(R.id.lsGalleryProjects);

        listAdapter = new ProjectListAdapter(this, R.layout.list_gallery_project, new ArrayList<> (  ));
        applyList(PRIVATE_TABLE_NAME, COLUMN_CREATED_DATE);

        btnGalleryMyProjects.setOnClickListener ( v -> {
            galleryMode = GALLERY_MODE_PRIVATE;
            applyList(PRIVATE_TABLE_NAME, COLUMN_CREATED_DATE);
        } );

        btnGalleryNewProjects.setOnClickListener ( v -> {
            galleryMode = GALLERY_MODE_PUBLIC_NEW;
            applyList(PUBLIC_TABLE_NAME, COLUMN_CREATED_DATE);
        } );

        btnGalleryTopProjects.setOnClickListener ( v -> {
            galleryMode = GALLERY_MODE_PUBLIC_TOP;
            applyList(PUBLIC_TABLE_NAME, COLUMN_PUBLIC_RATING);
        } );

        btnGalleryHotProjects.setOnClickListener ( v -> {
            galleryMode = GALLERY_MODE_PUBLIC_HOT;
            applyList(PUBLIC_TABLE_NAME, COLUMN_PUBLIC_VIEWS);
        } );

        btnGalleryExit.setOnClickListener ( v -> finish () );

        final Context context = this;
        lsGalleryProjects.setAdapter ( listAdapter );
        lsGalleryProjects.setOnItemClickListener ( ( parent, view, position, id ) -> {
            ProjectSettings settings = ((ProjectListAdapter) lsGalleryProjects.getAdapter ( )).getItem ( position );
            if (galleryMode == GALLERY_MODE_PRIVATE) {
                settingsPopup = new GalleryProjectSettingsPopup ( context, settings, true, v -> {
                    if (database.hasOccurrence ( settings, PUBLIC_TABLE_NAME )) {
                        Toast.makeText ( context, "Projects with the same name was already shared!", Toast.LENGTH_SHORT ).show ( );
                    } else {
                        database.addProject ( settings, database.getMaxId ( PUBLIC_TABLE_NAME ) + 1, PUBLIC_TABLE_NAME );
                        applyList ( PUBLIC_TABLE_NAME, COLUMN_CREATED_DATE );
                        if (settingsPopup != null)
                            settingsPopup.dismiss ( );
                    }
                } );
            }
            else {
                settingsPopup = new GalleryProjectSettingsPopup ( context, settings );
            }
            settingsPopup.show ( );
        } );
        lsGalleryProjects.setOnScrollListener ( this );
    }

    public void applyList(String table, String sortBy) {
        listAdapter.clear ();
        List<ProjectSettings> list;
        list = database.toList ( 0, 200, table, sortBy );
        if (list == null || list.size () == 0) {
            Toast.makeText ( this, "No Projects Found", Toast.LENGTH_SHORT ).show ( );
        }
        else {
            listAdapter.addAll ( list );
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
            list = database.toList ( start, start + 1, PRIVATE_TABLE_NAME );
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