package com.igalblech.school.graphicaljavascriptcompiler.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.igalblech.school.graphicaljavascriptcompiler.ActivityMain;
import com.igalblech.school.graphicaljavascriptcompiler.ActivityProject;
import com.igalblech.school.graphicaljavascriptcompiler.R;
import com.igalblech.school.graphicaljavascriptcompiler.interfaces.ActivityBase;

public class HomeFragment extends Fragment implements ActivityBase {

    private HomeViewModel homeViewModel;
    private Button btnMainResume, btnMainCreateNew, btnOpenGallery, btnMainExit;
    private View root;

    public View onCreateView ( @NonNull LayoutInflater inflater,
                               ViewGroup container, Bundle savedInstanceState ) {
        homeViewModel =
                ViewModelProviders.of ( this ).get ( HomeViewModel.class );
        root = inflater.inflate ( R.layout.fragment_home, container, false );

        initializeViews();
        addBehaviourToViews();

        return root;
    }

    @Override
    public void initializeViews() {
        btnMainResume = root.findViewById(R.id.btnMainResume);
        btnMainCreateNew = root.findViewById(R.id.btnMainCreateNew);
        btnOpenGallery = root.findViewById(R.id.btnOpenGallery);
        btnMainExit = root.findViewById(R.id.btnMainExit);
    }

    @Override
    public void addBehaviourToViews() {

        btnMainExit.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick ( View view ) {
                getActivity ().finish();
                getActivity().moveTaskToBack(true);
                System.exit(0);
            }
        } );

        btnMainCreateNew.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick ( View v ) {
                Intent intent = new Intent ( getActivity (), ActivityProject.class );
                startActivity ( intent );
            }
        } );
    }

}