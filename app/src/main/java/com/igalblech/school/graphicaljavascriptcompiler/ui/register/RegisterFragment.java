package com.igalblech.school.graphicaljavascriptcompiler.ui.register;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.igalblech.school.graphicaljavascriptcompiler.R;
import com.igalblech.school.graphicaljavascriptcompiler.interfaces.ActivityBase;
import com.igalblech.school.graphicaljavascriptcompiler.utils.UserData;
import com.igalblech.school.graphicaljavascriptcompiler.utils.UserDataValidator;
import com.igalblech.school.graphicaljavascriptcompiler.utils.UserDataDatabase;

public class RegisterFragment extends Fragment implements ActivityBase {

    private RegisterViewModel loginViewModel;
    private View root;

    private EditText etRegisterUsername;
    private EditText etRegisterEmail;
    private EditText etRegisterPassword;
    private EditText etRegisterPasswordRepeat;
    private EditText etRegisterPhone;
    private Button btnRegisterApply;
    private ProgressBar pbRegister;

    private UserDataDatabase userDataManager;

    private static String etToStr(TextView textView) {
        return textView.getText().toString();
    }

    public View onCreateView ( @NonNull LayoutInflater inflater,
                               ViewGroup container, Bundle savedInstanceState ) {
        loginViewModel =
                ViewModelProviders.of ( this ).get ( RegisterViewModel.class );
        root = inflater.inflate ( R.layout.fragment_register, container, false );

        userDataManager = new UserDataDatabase (getContext());
        initializeViews();
        addBehaviourToViews();

        return root;
    }


    @Override
    public void initializeViews() {
        etRegisterUsername = root.findViewById(R.id.etRegisterUsername);
        etRegisterEmail = root.findViewById(R.id.etRegisterEmail);
        etRegisterPhone  = root.findViewById(R.id.etRegisterPhone);
        etRegisterPassword = root.findViewById(R.id.etRegisterPassword);
        etRegisterPasswordRepeat = root.findViewById(R.id.etRegisterPasswordRepeat);
        btnRegisterApply = root.findViewById(R.id.btnRegisterApply);
        pbRegister = root.findViewById(R.id.pbRegister);
    }

    @Override
    public void addBehaviourToViews() {
        etRegisterUsername.setText("blechigal");
        etRegisterEmail.setText("blechigal@gmail.com");
        etRegisterPhone.setText("058-430-2061");
        etRegisterPassword.setText("password0");
        etRegisterPasswordRepeat.setText("password0");

        btnRegisterApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final UserData userData = new UserData(
                        etToStr(etRegisterUsername),
                        etToStr(etRegisterPassword),
                        etToStr(etRegisterEmail).toLowerCase (),
                        etToStr(etRegisterPhone)
                );

                int err;
                if ((err = userDataManager.registerUser (userData, etToStr(etRegisterPasswordRepeat))) != UserDataValidator.ERROR_NONE)
                    Toast.makeText(getContext(), UserDataValidator.errorToString ( err ), Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getContext(), "Done!", Toast.LENGTH_SHORT).show();
            }
        });

    }

}