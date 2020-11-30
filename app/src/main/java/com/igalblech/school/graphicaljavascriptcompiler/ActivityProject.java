
package com.igalblech.school.graphicaljavascriptcompiler;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8ScriptException;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.igalblech.school.graphicaljavascriptcompiler.ui.ErrorFragment;
import com.igalblech.school.graphicaljavascriptcompiler.ui.RenderFragment;
import com.igalblech.school.graphicaljavascriptcompiler.ui.ScriptFragment;
import com.igalblech.school.graphicaljavascriptcompiler.utils.FakeDataGenerator;
import com.igalblech.school.graphicaljavascriptcompiler.utils.gallery.ProjectSettingsDatabase;
import com.igalblech.school.graphicaljavascriptcompiler.utils.project.ProjectActivityPagerAdapter;
import com.igalblech.school.graphicaljavascriptcompiler.utils.project.ProjectSettings;

import java.lang.ref.WeakReference;
import java.nio.Buffer;
import java.nio.ByteBuffer;

import lombok.Getter;

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
    public boolean onNavigationItemSelected ( @NonNull MenuItem menuItem ) {

        int itemId = menuItem.getItemId ( );
        if (itemId == R.id.navigation_project_script) {
            changeFragmentPage ( ProjectActivityPagerAdapter.PAGE_SCRIPT );
        } else if (itemId == R.id.navigation_project_render) {
            changeFragmentPage ( ProjectActivityPagerAdapter.PAGE_RENDER );
        } else if (itemId == R.id.navigation_project_error) {
            changeFragmentPage ( ProjectActivityPagerAdapter.PAGE_ERROR );
        } else {
            return false;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        exitProjectActivity();
    }

    public void exitProjectActivity() {
        AlertDialog alertDialog = new AlertDialog.Builder( this ).create();
        alertDialog.setTitle("Are you sure?");
        alertDialog.setMessage("Unsaved data will be lost");
        alertDialog.setButton("OK", ( dialog, which ) -> finish () );
        alertDialog.setButton ( DialogInterface.BUTTON_NEGATIVE, "Cancel", ( dialog, which ) -> alertDialog.dismiss () );
        alertDialog.show();
    }

}
