package com.igalblech.school.graphicaljavascriptcompiler.ui.tutorial;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.barteksc.pdfviewer.util.FitPolicy;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.igalblech.school.graphicaljavascriptcompiler.R;

/**
 * In this fragment the user can find help on how to use the app.
 * This fragment is part of the main activity.
 * @see com.igalblech.school.graphicaljavascriptcompiler.ActivityMain
 */
public class TutorialFragment extends Fragment implements BottomNavigationView.OnNavigationItemSelectedListener {

    private PDFView pdfView;
    private BottomNavigationView bottomNavigationView;
    private View root;

    public static final int PDF_LAYOUT_ID = R.raw.layout;
    public static final int PDF_LOGIN_ID = R.raw.login;
    public static final int PDF_PROJECT_ID = R.raw.project;
    public static final int PDF_SCRIPTING_ID = R.raw.scripting;

    public View onCreateView ( @NonNull LayoutInflater inflater,
                               ViewGroup container, Bundle savedInstanceState ) {
        //TutorialViewModel tutorialViewModel =
        ViewModelProviders.of ( this ).get ( TutorialViewModel.class );
        root = inflater.inflate ( R.layout.fragment_tutorial, container, false );

        Activity activity = getActivity ( );
        assert activity != null;
        activity.getWindow().addFlags( WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS );

        initializeViews();
        addBehaviourToViews();

        hideSystemUI();

        return root;
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getActivity ( ).getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    // Shows the system bars by removing all the flags
// except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
        View decorView = getActivity ( ).getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    public void initializeViews ( ) {
        pdfView = root.findViewById ( R.id.pdfViewTutorial );
        bottomNavigationView = root.findViewById ( R.id.navTutorial );
    }

    public void addBehaviourToViews ( ) {



        pdfView.setMaxZoom ( 1.0f );
        pdfView.setMinZoom ( 1.0f );
        pdfView.enableAntialiasing ( false );
        openPDFFromRawResources(PDF_LAYOUT_ID);

        /*.onPageChange ( new OnPageChangeListener ( ) {
                @Override
                public void onPageChanged ( int page, int pageCount ) {
                Log.d("Developer", String.format ( "Page changed: %d %d", page, pageCount ));
                }
                } )*/
                /*.onLoad ( new OnLoadCompleteListener ( ) {
                    @Override
                    public void loadComplete ( int nbPages ) {
                        Log.d("Developer", String.format ( "Load complete: %d", nbPages ));
                    }
                } )*/
                /*.onPageError ( new OnPageErrorListener ( ) {
                    @Override
                    public void onPageError ( int page, Throwable t ) {
                        Log.d("Developer", String.format ( "Load complete: %d %s", page, t.toString () ));
                    }
                } )*/

        bottomNavigationView.setOnNavigationItemSelectedListener ( this );
        bottomNavigationView.bringToFront ();

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        /*if (requestCode == PERMISSION_CODE && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

        }*/
    }



    public void openPDFFromRawResources (int id) {

        pdfView.fromStream (  getResources().openRawResource ( id ) )
                .defaultPage ( 0 )
                .enableAnnotationRendering ( true )
                .scrollHandle ( new DefaultScrollHandle ( getActivity () ) )
                .spacing ( 10 ) // in dp
                .pageFitPolicy ( FitPolicy.BOTH )
                .load ( );
    }

    @Override
    public boolean onNavigationItemSelected ( @NonNull MenuItem menuItem ) {

        int itemId = menuItem.getItemId ( );
        if (itemId == R.id.navigation_tutorial_layout) {
            openPDFFromRawResources ( PDF_LAYOUT_ID );
        } else if (itemId == R.id.navigation_tutorial_login) {
            openPDFFromRawResources ( PDF_LOGIN_ID );
        } else if (itemId == R.id.navigation_tutorial_project) {
            openPDFFromRawResources ( PDF_PROJECT_ID );
        } else if (itemId == R.id.navigation_tutorial_javascript) {
            openPDFFromRawResources ( PDF_SCRIPTING_ID );
        } else {
            return false;
        }
        pdfView.loadPages ();

        return true;
    }
}