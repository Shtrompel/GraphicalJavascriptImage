
package com.igalblech.school.graphicaljavascriptcompiler;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.igalblech.school.graphicaljavascriptcompiler.ui.ErrorFragment;
import com.igalblech.school.graphicaljavascriptcompiler.ui.RenderFragment;
import com.igalblech.school.graphicaljavascriptcompiler.ui.ScriptFragment;
import com.igalblech.school.graphicaljavascriptcompiler.utils.project.ProjectActivityPagerAdapter;
import com.igalblech.school.graphicaljavascriptcompiler.utils.project.ProjectSettings;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.Buffer;
import java.util.Arrays;

import lombok.Getter;

/**
 * In this activity the user manages his currently open project
 */
public class ActivityProject extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private @Getter ProjectSettings settings;

    BottomNavigationView navProject;
    ViewPager vpProjectFragment;
    ProjectActivityPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            settings = (ProjectSettings) bundle.get ( "settings" );
        }

        vpProjectFragment = findViewById ( R.id.vpProjectFragment );
        navProject = findViewById(R.id.navProject);

        navProject.setOnNavigationItemSelectedListener ( this );

        adapter = new ProjectActivityPagerAdapter ( getSupportFragmentManager () );
        adapter.setFragment ( ProjectActivityPagerAdapter.PAGE_SCRIPT, new ScriptFragment (settings) );
        adapter.setFragment ( ProjectActivityPagerAdapter.PAGE_RENDER, new RenderFragment () );
        adapter.setFragment ( ProjectActivityPagerAdapter.PAGE_ERROR, new ErrorFragment () );
        vpProjectFragment.setAdapter ( adapter );

        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled ( false );
        }
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService( Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);


        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.show ();
    }

    public void updateImage( Buffer buffer, int w, int h ) {
        ((RenderFragment) (adapter.getItem ( ProjectActivityPagerAdapter.PAGE_RENDER ))).updateImage ( buffer, w, h );
    }

    public void updateError ( String error ) {
        ((ErrorFragment) (adapter.getItem ( ProjectActivityPagerAdapter.PAGE_ERROR ))).updateError ( error );
    }

    public void changeFragmentPage(int i) {
        vpProjectFragment.setCurrentItem ( i );
    }

    @Override
    public void onBackPressed() {
        saveProject();
        exitProjectActivity();
    }

    public void exitProjectActivity() {
        AlertDialog alertDialog = new AlertDialog.Builder( this ).create();
        alertDialog.setTitle("Are you sure?");
        alertDialog.setMessage("Unsaved data will be lost");
        alertDialog.setButton("OK", ( dialog, which ) -> {
            finish ();
            saveProject();
        } );
        alertDialog.setButton ( DialogInterface.BUTTON_NEGATIVE, "Cancel", ( dialog, which ) -> {
            alertDialog.dismiss ();
        } );
        alertDialog.show();

    }

    private void saveProject() {

        try {
            ObjectOutputStream out = new ObjectOutputStream(getApplicationContext ().openFileOutput("project.class", Context.MODE_PRIVATE));
            out.writeObject(settings);
            out.close();
        }
        catch (IOException e) {
            e.printStackTrace ();
            Log.e("Developer", "File write failed: " + Arrays.toString ( e.getStackTrace ( ) ) );
        }


    }

    @Override
    protected void onPause ( ) {
        saveProject();
        super.onPause ( );
    }

    @Override
    protected void onStop ( ) {
        saveProject();
        super.onStop ( );
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

        int itemId = menuItem.getItemId ( );
        if (itemId == R.id.navigation_project_script) {
            changeFragmentPage ( ProjectActivityPagerAdapter.PAGE_SCRIPT );
        } else if (itemId == R.id.navigation_project_render) {
            changeFragmentPage ( ProjectActivityPagerAdapter.PAGE_RENDER );
        } else if (itemId == R.id.navigation_project_error) {
            changeFragmentPage ( ProjectActivityPagerAdapter.PAGE_ERROR );
        } else {
            // Top menu
            Intent activityMain = new Intent ( this, ActivityMain.class );
            activityMain.putExtra ( "fragment_id", menuItem.getItemId() );
            startActivity ( activityMain );
        }

        return true;
    }


}
