package com.igalblech.school.graphicaljavascriptcompiler.ui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.igalblech.school.graphicaljavascriptcompiler.ActivityProject;
import com.igalblech.school.graphicaljavascriptcompiler.R;
import com.igalblech.school.graphicaljavascriptcompiler.views.BitmapView;

import java.nio.Buffer;

/**
 * In this fragment the user can see his rendered image.
 * This fragment is part of the project activity.
 * @see ActivityProject
 */
public class RenderFragment extends Fragment {

    private BitmapView bitmapView;

    @Nullable
    @Override
    public View onCreateView ( @NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState ) {
        View view = inflater.inflate ( R.layout.fragment_render , container, false);
        bitmapView = view.findViewById ( R.id.bitmapView );
        return view;
    }

    public void updateImage( Buffer buffer, int w, int h ) {
        bitmapView.passByteBuffer ( buffer, w, h );
        Activity activity = getActivity ( );
        assert activity != null;
        SharedPreferences preferences = activity.getSharedPreferences (getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        bitmapView.getPaint ().setFilterBitmap ( preferences.getBoolean ( "enable_filter", false ) );
        bitmapView.getPaint ().setAntiAlias ( preferences.getBoolean ( "enable_antialiasing", false ) );
        bitmapView.getPaint ().setDither ( preferences.getBoolean ( "enable_dithering", false ) );

    }
}
