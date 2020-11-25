package com.igalblech.school.graphicaljavascriptcompiler.ui.home;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.igalblech.school.graphicaljavascriptcompiler.ActivityGallery;
import com.igalblech.school.graphicaljavascriptcompiler.ActivityMain;
import com.igalblech.school.graphicaljavascriptcompiler.R;
import com.igalblech.school.graphicaljavascriptcompiler.utils.project.CreateProjectPopup;
import com.igalblech.school.graphicaljavascriptcompiler.utils.userdata.UserData;

public class HomeFragment extends Fragment {

    private Button btnMainCreateNew;
    private Button btnOpenGallery;
    private Button btnMainExit;
    private Button btnMainResume;
    private View root;

    public View onCreateView ( @NonNull LayoutInflater inflater,
                               ViewGroup container, Bundle savedInstanceState ) {
        //HomeViewModel homeViewModel =
        ViewModelProviders.of ( this ).get ( HomeViewModel.class );
        root = inflater.inflate ( R.layout.fragment_home, container, false );

        initializeViews();
        addBehaviourToViews();

        return root;
    }

    public void initializeViews() {
        btnMainResume = root.findViewById ( R.id.btnMainResume );
        btnMainCreateNew = root.findViewById(R.id.btnMainCreateNew);
        btnOpenGallery = root.findViewById(R.id.btnOpenGallery);
        btnMainExit = root.findViewById(R.id.btnMainExit);
    }

    public void addBehaviourToViews() {
        btnMainResume.setOnClickListener ( v -> Toast.makeText ( getContext (), "Todo", Toast.LENGTH_SHORT ).show ( ) );

        btnMainExit.setOnClickListener ( view -> {
            Activity activity = getActivity ();
            if (activity != null)
                activity.finish ();
            getActivity().moveTaskToBack(true);
            System.exit(0);
        } );

        btnMainCreateNew.setOnClickListener ( v -> {
            Activity activity = getActivity ();
            if (activity == null)
                return;
            UserData userData = ((ActivityMain)activity).getUserData ( );
            if (userData == null) {
                showMissingUserPopup();
            }
            else {
                Context context = getContext ();
                if (context != null) {
                    CreateProjectPopup popup = new CreateProjectPopup ( getContext ( ), userData );
                    popup.show ( );
                }
/*
                int renderWidth = 256;
                int renderHeight = 256;
                int channelBits = 8;
                int colorModel = RenderColorFormat.COLOR_MODEL_RGB;
                ProjectSettings settings;
                settings = new ProjectSettings ( );
                settings.format = new RenderColorFormat ( colorModel, channelBits, false, false );
                settings.width = renderWidth;
                settings.height = renderHeight;
                settings.dateCreated = Calendar.getInstance ( ).getTime ( );
                settings.lastUpdated = Calendar.getInstance ( ).getTime ( );
                settings.description = "";
                settings.userData = ((ActivityMain)getActivity ()).getUserData ( );
                settings.userData = new UserData (  );
                settings.userData.setUsername ( "Sex man" );
                settings.userData.setEmail ( "Epic win" );

                Intent intent = new Intent ( getActivity ( ), ActivityProject.class );
                intent.putExtra ( "settings", settings );
                startActivity ( intent );
 */
            }
        } );

        btnOpenGallery.setOnClickListener ( v -> {
            Activity activity = getActivity ();
            if (activity == null)
                return;
            if (((ActivityMain)activity).getUserData ( ) == null) {
                showMissingUserPopup();
            }
            else {
                Intent intent = new Intent ( getActivity ( ), ActivityGallery.class );
                UserData userData = ((ActivityMain)getActivity ()).getUserData ( );
                intent.putExtra ( "user_data", (Parcelable) userData );
                startActivity ( intent );
            }
        } );
    }

    private void showMissingUserPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext ());
        builder.setMessage("You must log in to your account in order to make a new project!")
                .setCancelable(false)
                .setPositiveButton("OK", ( dialog, id ) -> {

                } );
        AlertDialog alert = builder.create();
        alert.show();//Toast.makeText ( getContext (), "You must log in to your account in order to make a new project!", Toast.LENGTH_LONG ).show ( );
    }

}