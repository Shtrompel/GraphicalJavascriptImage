package com.igalblech.school.graphicaljavascriptcompiler;

import android.Manifest;
import android.app.AlertDialog;
import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import com.igalblech.school.graphicaljavascriptcompiler.interfaces.ActivityBase;
import com.igalblech.school.graphicaljavascriptcompiler.ui.about.AboutFragment;
import com.igalblech.school.graphicaljavascriptcompiler.ui.home.HomeFragment;
import com.igalblech.school.graphicaljavascriptcompiler.ui.login.LoginFragment;
import com.igalblech.school.graphicaljavascriptcompiler.ui.register.RegisterFragment;
import com.igalblech.school.graphicaljavascriptcompiler.ui.tutorial.TutorialFragment;
import com.igalblech.school.graphicaljavascriptcompiler.utils.userdata.UserData;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ActivityMain extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        ActivityBase {

    private AppBarConfiguration mAppBarConfiguration;

    private TextView tvNavUsername;
    private TextView tvNavEmail;

    private UserData userData = null;

    // --Commented out by Inspection (28/10/2020 14:07):public static ActivityMain activityMain;

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {

        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_main );

        Toolbar toolbar = findViewById ( R.id.toolbar );
        setSupportActionBar ( toolbar );

        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null)
            actionbar.setDisplayHomeAsUpEnabled(true);

        DrawerLayout drawer = findViewById ( R.id.drawer_layout );
        NavigationView navigationView = findViewById ( R.id.nav_view );
        navigationView.setNavigationItemSelectedListener ( this );
        navigationView.bringToFront ();

        mAppBarConfiguration = new AppBarConfiguration.Builder (
                R.id.nav_home, R.id.nav_login)
                .setDrawerLayout ( drawer )
                .build ( );


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,
                drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(actionBarDrawerToggle);
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled ( true );
            actionbar.setHomeButtonEnabled ( true );
        }

        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.SEND_SMS,
                        Manifest.permission.INTERNET
                },
                1);


        initializeViews();
    }

    public void initializeViews() {
        NavigationView nvMainNavigation = findViewById ( R.id.nav_view );

        View headerView = nvMainNavigation.getHeaderView (0);

        tvNavUsername = headerView.findViewById(R.id.tvNavUsername);
        tvNavEmail = headerView.findViewById(R.id.tvNavEmail);
    }


    @Override
    public boolean onSupportNavigateUp ( ) {
        NavController navController = Navigation.findNavController ( this, R.id.nav_host_fragment );
        return NavigationUI.navigateUp ( navController, mAppBarConfiguration )
                || super.onSupportNavigateUp ( );
    }

    @Override
    public boolean onNavigationItemSelected ( @NonNull MenuItem menuItem ) {

        final int id = menuItem.getItemId();
        Fragment fragment = null;

        if (id == R.id.navigation_main_home) {
            fragment = new HomeFragment ( );
        } else if (id == R.id.navigation_main_about) {
            fragment = new AboutFragment ( );
        } else if (id == R.id.navigation_main_tutorial) {
            fragment = new TutorialFragment ( );
        } else if (id == R.id.navigation_main_login) {
            fragment = new LoginFragment ( );
        } else if (id == R.id.navigation_main_logout) {
            showLogoutConfirmation ( );
        } else if (id == R.id.navigation_main_register) {
            fragment = new RegisterFragment ( );
        } else {
            return false;
        }

        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.nav_host_fragment, fragment);
            ft.commit();
        }
        else
            return false;

        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        drawer.closeDrawer( GravityCompat.START);

        return true;
    }

    public void logOutUser() {
        tvNavUsername.setText ( getString ( R.string.nav_header_title ) );
        tvNavEmail.setText ( getString ( R.string.nav_header_subtitle ) );
        this.userData = null;
    }

    public void updateUser(UserData userData) {
        tvNavUsername.setText(userData.getUsername());
        tvNavEmail.setText(userData.getEmail());
        this.userData = userData;
    }

    public boolean hasUser(){
        return userData != null;
    }

    public void showLogoutConfirmation() {
        if (userData == null)
            return;
        AlertDialog.Builder dialog=new AlertDialog.Builder(this);
        dialog.setMessage("Are you sure that you want to log out from your account?");
        dialog.setTitle("Log Out");
        dialog.setPositiveButton("Ok",
                ( dialog1, which ) -> {
                    logOutUser();
                    Toast.makeText ( getApplicationContext ( ), "You have successfully logged out!", Toast.LENGTH_LONG ).show ( );
                } );
        dialog.setNegativeButton("Cancel", ( dialog12, which ) ->{});
        AlertDialog alertDialog=dialog.create();
        alertDialog.show();
    }

    public UserData getUserData() {
        return userData;
    }

}
