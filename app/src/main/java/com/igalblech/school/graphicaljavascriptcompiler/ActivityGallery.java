package com.igalblech.school.graphicaljavascriptcompiler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.igalblech.school.graphicaljavascriptcompiler.utils.FakeDataGenerator;
import com.igalblech.school.graphicaljavascriptcompiler.utils.gallery.GalleryProjectSettingsPopup;
import com.igalblech.school.graphicaljavascriptcompiler.utils.gallery.ProjectListAdapter;
import com.igalblech.school.graphicaljavascriptcompiler.utils.gallery.ProjectSettingsDatabase;
import com.igalblech.school.graphicaljavascriptcompiler.utils.project.ProjectSettings;
import com.igalblech.school.graphicaljavascriptcompiler.utils.userdata.UserData;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.igalblech.school.graphicaljavascriptcompiler.utils.gallery.ProjectSettingsDatabase.Constants.COLUMN_UPDATED_DATE;
import static com.igalblech.school.graphicaljavascriptcompiler.utils.gallery.ProjectSettingsDatabase.Constants.COLUMN_PUBLIC_RATING;
import static com.igalblech.school.graphicaljavascriptcompiler.utils.gallery.ProjectSettingsDatabase.Constants.COLUMN_PUBLIC_VIEWS;
import static com.igalblech.school.graphicaljavascriptcompiler.utils.gallery.ProjectSettingsDatabase.Constants.PRIVATE_TABLE_NAME;
import static com.igalblech.school.graphicaljavascriptcompiler.utils.gallery.ProjectSettingsDatabase.Constants.PUBLIC_TABLE_NAME;


/**
 * Gallery activity, here the user can see his projects and other's people projects.
 */
public class ActivityGallery extends AppCompatActivity implements AbsListView.OnScrollListener, BottomNavigationView.OnNavigationItemSelectedListener {

    static class AsyncTaskApplyList extends AsyncTask<Void, Void, List<ProjectSettings>> {

        private final WeakReference<Context> context;
        private final String table;
        private final String sortBy;
        private final ProjectSettingsDatabase database;
        private final ProjectListAdapter listAdapter;

        public AsyncTaskApplyList ( Context context, String table, String sortBy, ProjectSettingsDatabase database, ProjectListAdapter listAdapter ) {
            this.context = new WeakReference<>(context);
            this.table = table;
            this.sortBy = sortBy;
            this.database = database;
            this.listAdapter = listAdapter;
        }

        @Override
        protected List<ProjectSettings> doInBackground ( Void... voids ) {
            return database.toList ( 0, 200, table, sortBy );
        }

        @Override
        protected void onPostExecute(List<ProjectSettings> list) {
            if (list == null || list.size () == 0) {
                Toast.makeText ( context.get () , "No Projects Found", Toast.LENGTH_SHORT ).show ( );
            }
            else {
                listAdapter.addAll ( list );
            }
        }
    }

    public static final int GALLERY_MODE_PRIVATE = 0;
    public static final int GALLERY_MODE_PUBLIC_NEW = 1;
    public static final int GALLERY_MODE_PUBLIC_TOP = 2;
    private static final int GALLERY_MODE_PUBLIC_HOT = 3;

    private ProjectSettings settings = null;
    private boolean addNewProject = false;
    private UserData userData = null;

    private ListView lsGalleryProjects;

    private ProjectSettingsDatabase database;
    private ProjectListAdapter listAdapter;
    private GalleryProjectSettingsPopup settingsPopup;

    private int galleryMode = 0;



    @Override
    protected void onCreate ( Bundle savedInstanceState ) {

        Button
                btnGalleryMyProjects,
                btnGalleryNewProjects,
                btnGalleryTopProjects,
                btnGalleryHotProjects,
                btnGalleryExit;

        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_gallery );
        getLayoutInflater ().inflate ( R.layout.activity_gallery, null );

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            settings = (ProjectSettings) bundle.get ( "settings" );
            addNewProject = bundle.getBoolean ( "add_project", false );
            userData = (UserData) bundle.get ( "user_data" );
        }

        database = new ProjectSettingsDatabase ( this, null );

        /*FakeDataGenerator.rand.setSeed ( 1239 );
        for (int i = 0; i < 6; i++){
            ProjectSettings p = FakeDataGenerator.debugRandomSettings ();
            Field privateField;
            try {
                privateField = ProjectSettings.class.
                        getDeclaredField("userData");
                privateField.setAccessible(true);
                privateField.set ( p, userData );
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace ( );
            }
        }
        */
        if (addNewProject) {
            if (settings.getUserData () == null)
                Toast.makeText ( this, "There was a problem when adding the project!", Toast.LENGTH_SHORT ).show ( );
            else if (database.hasOccurrence ( settings, PRIVATE_TABLE_NAME )) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Project with the same name already exists. Replace it or go back?");
                builder.setPositiveButton ( "Replace", ( dialog, which ) -> {
                    database.deleteProject ( PRIVATE_TABLE_NAME, settings );
                    database.addProject ( settings, database.getMaxId ( PRIVATE_TABLE_NAME ) + 1, PRIVATE_TABLE_NAME );
                } );
                builder.setNegativeButton ( "No", ( dialog, which ) -> super.onBackPressed () ).show();
            }
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
        applyList(PRIVATE_TABLE_NAME, COLUMN_UPDATED_DATE);

        btnGalleryMyProjects.setOnClickListener ( v -> {
            galleryMode = GALLERY_MODE_PRIVATE;
            applyList(PRIVATE_TABLE_NAME, COLUMN_UPDATED_DATE);
        } );

        btnGalleryNewProjects.setOnClickListener ( v -> {
            if (!checkInternetConnectivity())
                return;
            galleryMode = GALLERY_MODE_PUBLIC_NEW;
            applyList(PUBLIC_TABLE_NAME, COLUMN_UPDATED_DATE);
        } );

        btnGalleryTopProjects.setOnClickListener ( v -> {
            if (!checkInternetConnectivity())
                return;
            galleryMode = GALLERY_MODE_PUBLIC_TOP;
            applyList(PUBLIC_TABLE_NAME, COLUMN_PUBLIC_RATING);
        } );

        btnGalleryHotProjects.setOnClickListener ( v -> {
            if (!checkInternetConnectivity())
                return;
            galleryMode = GALLERY_MODE_PUBLIC_HOT;
            applyList(PUBLIC_TABLE_NAME, COLUMN_PUBLIC_VIEWS);
        } );

        btnGalleryExit.setOnClickListener ( v -> finish () );

        final Context context = this;
        lsGalleryProjects.setAdapter ( listAdapter );
        lsGalleryProjects.setOnItemClickListener ( ( parent, view, position, id ) -> {
            ProjectSettings settings = ((ProjectListAdapter) lsGalleryProjects.getAdapter ( )).getItem ( position );
            if (galleryMode == GALLERY_MODE_PRIVATE) {
                settingsPopup = new GalleryProjectSettingsPopup ( context, settings, database, true);
                settingsPopup.setOnPublishClick (v -> {
                    if (database.hasOccurrence ( settings, PUBLIC_TABLE_NAME )) {
                        Toast.makeText ( context, "Projects with the same name was already shared!", Toast.LENGTH_SHORT ).show ( );
                    } else {
                        database.addProject ( settings, database.getMaxId ( PUBLIC_TABLE_NAME ) + 1, PUBLIC_TABLE_NAME );
                        applyList ( PUBLIC_TABLE_NAME, COLUMN_UPDATED_DATE );
                        if (settingsPopup != null)
                            settingsPopup.dismiss ( );
                    }
                } );

                settingsPopup.setOnDeleteClick ( v -> {
                    database.deleteProject ( PUBLIC_TABLE_NAME, settings );
                    listAdapter.remove ( settings );
                    settingsPopup.dismiss ();
                } );
            }
            else {
                settingsPopup = new GalleryProjectSettingsPopup ( context, settings, database );
            }
            settingsPopup.show ( );
        } );
        lsGalleryProjects.setOnScrollListener ( this );

        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null)
            actionbar.setDisplayHomeAsUpEnabled(false);
    }

    public boolean checkInternetConnectivity() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean connected = Objects.requireNonNull ( connectivityManager.getNetworkInfo ( ConnectivityManager.TYPE_MOBILE ) )
                .getState() == NetworkInfo.State.CONNECTED;
        connected |= Objects.requireNonNull ( connectivityManager.getNetworkInfo ( ConnectivityManager.TYPE_WIFI ) )
                .getState() == NetworkInfo.State.CONNECTED;

        if (!connected) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("There's no internet connection! Public gallery is not accessible ")
                    .setCancelable(false)
                    .setPositiveButton ( "OK", new DialogInterface.OnClickListener ( ) {
                        @Override
                        public void onClick ( DialogInterface dialog, int which ) {
                            dialog.cancel ();
                        }
                    } )
                    .setTitle ( "Error" );
            AlertDialog alert = builder.create();
            alert.show();
        }

        return connected;
    }

    public void applyList(String table, String sortBy) {
        listAdapter.clear ();
        AsyncTaskApplyList asyncTaskApplyList = new AsyncTaskApplyList(this, table, sortBy, database, listAdapter);
        asyncTaskApplyList.execute (  );
    }

    public static String dateToString( Instant instant) {
        //LocalDateTime ldt = LocalDateTime.fromDateFields (instant.toDate ());
        //return ldt.toString ();
        DateTime dateTime = instant.toDateTime ( DateTimeZone.getDefault () );
        String dow = dateTime.dayOfWeek ().getAsShortText ();
        return dateTime.toString ( "HH:mm:ss" ) + " " + dow + " " + dateTime.toString ( "dd/MM/yyyy" );
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


    @Override
    public boolean onCreateOptionsMenu( Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected ( @NonNull MenuItem item ) {
        return onNavigationItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected ( @NonNull MenuItem menuItem ) {

        Intent activityMain = new Intent ( this, ActivityMain.class );
        activityMain.putExtra ( "fragment_id", menuItem.getItemId() );
        startActivity ( activityMain );

        return true;
    }


}