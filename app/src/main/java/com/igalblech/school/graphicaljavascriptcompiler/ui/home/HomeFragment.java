package com.igalblech.school.graphicaljavascriptcompiler.ui.home;

import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.igalblech.school.graphicaljavascriptcompiler.ActivityGallery;
import com.igalblech.school.graphicaljavascriptcompiler.ActivityMain;
import com.igalblech.school.graphicaljavascriptcompiler.ActivityProject;
import com.igalblech.school.graphicaljavascriptcompiler.R;
import com.igalblech.school.graphicaljavascriptcompiler.utils.project.CreateProjectPopup;
import com.igalblech.school.graphicaljavascriptcompiler.utils.project.ProjectSettings;
import com.igalblech.school.graphicaljavascriptcompiler.utils.userdata.UserData;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;

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

/**
 * The default main screen, here the user can create a new project or
 * load a project from the gallery.
 * This fragment is part of the main activity.
 * @see com.igalblech.school.graphicaljavascriptcompiler.ActivityMain
 */
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

    public ProjectSettings loadProject() {
        ProjectSettings s = null;
        try {
            Activity activity = getActivity ( );
            assert activity != null;
            ObjectInputStream in = new ObjectInputStream( activity.openFileInput ("project.class"));
            s = (ProjectSettings)in.readObject ();
            in.close();
        }
        catch (IOException | ClassNotFoundException e) {
            Log.e("Developer", "File input failed: " + Arrays.toString ( e.getStackTrace ( ) ) );
        }
        return s;
    }

    public void initializeViews() {
        btnMainResume = root.findViewById ( R.id.btnMainResume );
        btnMainCreateNew = root.findViewById(R.id.btnMainCreateNew);
        btnOpenGallery = root.findViewById(R.id.btnOpenGallery);
        btnMainExit = root.findViewById(R.id.btnMainExit);
    }

    public void addBehaviourToViews() {

        ProjectSettings settings = loadProject();
        if (settings == null) {
            btnMainResume.setOnClickListener ( v -> Toast.makeText ( getContext ( ), "No Project Found!", Toast.LENGTH_SHORT ).show ( ) );
        }
        else {
            btnMainResume.setTextColor ( getResources ( ).getColor ( R.color.palette_a_oxford_blue ) );
            btnMainResume.setOnClickListener ( v -> {
                Activity activity = getActivity ();
                if (activity == null)
                    return;
                UserData userData = ((ActivityMain)activity).getUserData ( );
                if (userData == null) {
                    showMissingUserPopup();
                }
                else if (!settings.getUserData ().getUsername ().equals ( userData.getUsername () )) {
                    showDifferentUserPopup(settings.getUserData ().getUsername ());
                }
                else {
                    Intent intent = new Intent ( getContext ( ), ActivityProject.class );
                    intent.putExtra ( "settings", loadProject() );
                    Context context = getContext ();
                    assert context != null;
                    context.startActivity ( intent );
                }
            } );
        }

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
            }
        } );

        btnOpenGallery.setOnClickListener ( v -> {
            Activity activity = getActivity ();
            if (activity == null)
                return;
            UserData userData = ((ActivityMain)activity).getUserData ( );
            if (userData == null) {
                showMissingUserPopup ( );
            }
            else {
                Intent intent = new Intent ( getActivity ( ), ActivityGallery.class );
                intent.putExtra ( "user_data", (Parcelable) userData );
                startActivity ( intent );
            }
        } );

        //imgMainLogo.set
    }

    @Override
    public void onResume ( ) {
        loadProject();
        super.onResume ( );
    }

    private void showMissingUserPopup() {
        Activity activity = getActivity ( );
        assert activity != null;
        AlertDialog.Builder builder = new AlertDialog.Builder ( activity, R.style.Theme_AppCompat_Dialog_Alert);
        builder.setMessage("You must log in to your account in order to make a new project!");
        builder.setPositiveButton("OK", ( dialog, id ) -> { } );
        AlertDialog alert = builder.create();
        alert.show();//Toast.makeText ( getContext (), "You must log in to your account in order to make a new project!", Toast.LENGTH_LONG ).show ( );
    }

    private void showDifferentUserPopup(String original) {
        Activity activity = getActivity ( );
        assert activity != null;
        AlertDialog.Builder builder = new AlertDialog.Builder ( activity, R.style.Theme_AppCompat_Dialog_Alert);
        builder.setMessage("This project was made by another account! Please use " + original + "'s account instead." );
        builder.setPositiveButton("OK", ( dialog, id ) -> { } );
        AlertDialog alert = builder.create();
        alert.show();
    }

}