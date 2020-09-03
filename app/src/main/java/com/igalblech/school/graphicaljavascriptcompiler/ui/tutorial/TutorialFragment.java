package com.igalblech.school.graphicaljavascriptcompiler.ui.tutorial;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.barteksc.pdfviewer.util.FitPolicy;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.igalblech.school.graphicaljavascriptcompiler.R;
import com.igalblech.school.graphicaljavascriptcompiler.interfaces.ActivityBase;

public class TutorialFragment extends Fragment implements ActivityBase, BottomNavigationView.OnNavigationItemSelectedListener {

    public static final int PERMISSION_CODE = 42042;

    private com.igalblech.school.graphicaljavascriptcompiler.ui.tutorial.TutorialViewModel tutorialViewModel;
    private PDFView pdfView;
    private BottomNavigationView bottomNavigationView;
    private View root;

    public static final int PDF_LAYOUT_ID = R.raw.layout;
    public static final int PDF_LOGIN_ID = R.raw.login;
    public static final int PDF_PROJECT_ID = R.raw.project;
    public static final int PDF_SCRIPTING_ID = R.raw.scripting;

    public View onCreateView ( @NonNull LayoutInflater inflater,
                               ViewGroup container, Bundle savedInstanceState ) {
        tutorialViewModel =
                ViewModelProviders.of ( this ).get ( com.igalblech.school.graphicaljavascriptcompiler.ui.tutorial.TutorialViewModel.class );
        root = inflater.inflate ( R.layout.fragment_tutorial, container, false );

        initializeViews();
        addBehaviourToViews();

        return root;
    }

    @Override
    public void initializeViews ( ) {
        pdfView = root.findViewById ( R.id.pdfViewTutorial );
        bottomNavigationView = root.findViewById ( R.id.navTutorial );
    }

    @Override
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            }
        }
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


        switch (menuItem.getItemId ( )) {
            case R.id.navigation_tutorial_layout:
                openPDFFromRawResources( PDF_LAYOUT_ID );
                break;
            case R.id.navigation_tutorial_login:
                openPDFFromRawResources( PDF_LOGIN_ID );
                break;
            case R.id.navigation_tutorial_project:
                openPDFFromRawResources( PDF_PROJECT_ID );
                break;
            case R.id.navigation_tutorial_javascript:
                openPDFFromRawResources( PDF_SCRIPTING_ID );
                break;
            default:
                return false;
        }
        pdfView.loadPages ();

        return true;
    }
}