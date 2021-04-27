package com.igalblech.school.graphicaljavascriptcompiler.ui;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.igalblech.school.graphicaljavascriptcompiler.ActivityProject;
import com.igalblech.school.graphicaljavascriptcompiler.R;

/**
 * If the user runs his code and the code contains an error, the user
 * can see the error in this fragment.
 * This fragment is part of the project activity.
 * @see ActivityProject
 */
public class ErrorFragment extends Fragment {

    TextView tvProjectError;

    @Nullable
    @Override
    public View onCreateView ( @NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState ) {
        View view = inflater.inflate ( R.layout.fragment_error , container, false);
        tvProjectError = view.findViewById ( R.id.tvProjectError );
        return view;
    }

    public void updateError ( String error ) {
        tvProjectError.setText ( error );
    }
}