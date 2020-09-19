package com.igalblech.school.graphicaljavascriptcompiler.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.igalblech.school.graphicaljavascriptcompiler.R;
import com.igalblech.school.graphicaljavascriptcompiler.views.BitmapView;

import java.nio.Buffer;

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
    }
}
