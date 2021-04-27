package com.igalblech.school.graphicaljavascriptcompiler.ui.about;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.igalblech.school.graphicaljavascriptcompiler.R;

/**
 * The about screen, here the user can find information about
 * the developer of the app.
 * This fragment is part of the main activity.
 * @see com.igalblech.school.graphicaljavascriptcompiler.ActivityMain
 */
public class AboutFragment extends Fragment {

    public View onCreateView ( @NonNull LayoutInflater inflater,
                               ViewGroup container, Bundle savedInstanceState ) {
        //AboutViewModel homeViewModel =
        ViewModelProviders.of ( this ).get ( AboutViewModel.class );

        return inflater.inflate ( R.layout.fragment_about, container, false );
    }
}