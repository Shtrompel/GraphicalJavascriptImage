package com.igalblech.school.graphicaljavascriptcompiler.ui.login;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
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

import com.igalblech.school.graphicaljavascriptcompiler.ActivityMain;
import com.igalblech.school.graphicaljavascriptcompiler.R;
import com.igalblech.school.graphicaljavascriptcompiler.interfaces.ActivityBase;
import com.igalblech.school.graphicaljavascriptcompiler.utils.CodeGenerator;
import com.igalblech.school.graphicaljavascriptcompiler.utils.GMailSender;
import com.igalblech.school.graphicaljavascriptcompiler.utils.UserData;
import com.igalblech.school.graphicaljavascriptcompiler.utils.UserDataValidator;
import com.igalblech.school.graphicaljavascriptcompiler.utils.UserDataDatabase;

import lombok.Getter;
import lombok.Setter;

public class LoginFragment extends Fragment implements ActivityBase {

    private com.igalblech.school.graphicaljavascriptcompiler.ui.login.LoginViewModel loginViewModel;
    private View root;

    private EditText etLoginUsername;
    private EditText etLoginPassword;
    private Button btnLoginApply;
    private ProgressBar pbLogin;
    private TextView tvForgotPassword;

    private EditText etLoginForgotPasswordEmail;
    private EditText etLoginForgotPasswordPhone;
    private Button btnLoginForgotPasswordSend;
    private TextView etLoginForgotPasswordCode;
    private Button btnLoginForgotPasswordApply;
    private Button btnLoginForgotPasswordCancel;

    private EditText etLoginNewPassword;
    private EditText etLoginNewPasswordConfirm;
    private Button btnLoginNewPasswordApply;

    private EditText etValidateUserCode;
    private Button btnValidateUserApply;
    private Button btnValidateUserCancel;
    private Button btnValidateUserResendSMS;
    private Button btnValidateUserResendEmail;

    private UserDataDatabase userDataManager;

    public View onCreateView ( @NonNull LayoutInflater inflater,
                               ViewGroup container, Bundle savedInstanceState ) {
        loginViewModel =
                ViewModelProviders.of ( this ).get ( com.igalblech.school.graphicaljavascriptcompiler.ui.login.LoginViewModel.class );
        root = inflater.inflate ( R.layout.fragment_login, container, false );

        userDataManager = new UserDataDatabase(getContext());

        initializeViews();
        addBehaviourToViews();

        return root;
    }


    @Override
    public void initializeViews() {

        etLoginUsername = root.findViewById(R.id.etLoginUsername);
        etLoginPassword = root.findViewById(R.id.etLoginPassword);
        btnLoginApply = root.findViewById(R.id.btnLoginApply);
        pbLogin = root.findViewById(R.id.pbLogin);
        tvForgotPassword = root.findViewById(R.id.tvForgotPassword);

        SpannableString content = new SpannableString(getResources().getString(R.string.forgot_password      ));
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        tvForgotPassword.setText(content);

    }

    @Override
    public void addBehaviourToViews() {

        btnLoginApply.setOnClickListener( view -> {

            if (((ActivityMain)getActivity ()).hasUser ()) {
                Toast.makeText ( getContext (), "You are already logged in! Log out from your current account to login as another one.", Toast.LENGTH_SHORT ).show ( );
                return;
            }

            String username = etLoginUsername.getText().toString();
            String password = etLoginPassword.getText().toString();
            UserData userData = userDataManager.loginUser (username, password);

            if (userData == null)
                Toast.makeText(getContext(), "Wrong username/password!", Toast.LENGTH_SHORT).show();
            else {
                Toast.makeText(getContext(), "Success!", Toast.LENGTH_SHORT).show();

                ((ActivityMain)getActivity ()).updateUser ( userData );
                /*
                TextView tvNavUsername = getActivity().findViewById(R.id.tvNavUsername);
                tvNavUsername.setText(userData.getUsername());
                TextView tvNavEmail = getActivity().findViewById(R.id.tvNavEmail);
                tvNavEmail.setText(userData.getEmail());
                */

                if (!userData.isVerified()) {

                    generateCode (userData);

                    sendCodeToUser (
                            userData,
                            formatTitle ( false ),
                            formatMessage ( userData, false ),
                            true,
                            true );
                    showValidatePopup(userData);
                }

            }
        } );

        tvForgotPassword.setOnClickListener( view -> showForgotPasswordEmailPopup() );
    }


    public void showForgotPasswordEmailPopup() {
        final AlertDialog.Builder screenDialog = new AlertDialog.Builder(getContext());
        screenDialog.setTitle("Write Your Email");
        View dialogView = getLayoutInflater().inflate(R.layout.popup_login_forgot_password, null);
        screenDialog.setView(dialogView);

        final AlertDialog alertDialog = screenDialog.show();
        alertDialog.setCanceledOnTouchOutside(false);

        etLoginForgotPasswordEmail = dialogView.findViewById(R.id.etLoginForgotPasswordEmail);
        etLoginForgotPasswordPhone = dialogView.findViewById(R.id.etLoginForgotPasswordPhone);
        btnLoginForgotPasswordSend = dialogView.findViewById(R.id.btnLoginForgotPasswordSend);
        etLoginForgotPasswordCode = dialogView.findViewById(R.id.etLoginForgotPasswordCode);
        btnLoginForgotPasswordApply = dialogView.findViewById(R.id.btnLoginForgotPasswordApply);
        btnLoginForgotPasswordCancel = dialogView.findViewById(R.id.btnLoginForgotPasswordCancel);

        btnLoginForgotPasswordSend.setOnClickListener( view -> {
            String phone   = etLoginForgotPasswordPhone.getText ().toString ();
            String email = etLoginForgotPasswordEmail.getText ().toString ();
            boolean isSms = !phone.equals ( "" );
            boolean isEmail = !email.equals ( "" );

            UserData userData = userDataManager.findUserViaEmailOrPhone ( email, phone );
            if (userData == null) {
                if (!isSms && !isEmail)
                    Toast.makeText ( getContext (), "Please fill in your email/sms", Toast.LENGTH_SHORT ).show ( );
                else if (!isSms && isEmail)
                    Toast.makeText ( getContext (), "No user with the inputted email!", Toast.LENGTH_SHORT ).show ( );
                else if (isSms && !isEmail)
                    Toast.makeText ( getContext (), "No user with the inputted phone!", Toast.LENGTH_SHORT ).show ( );
                else
                    Toast.makeText ( getContext (), "No user with the inputted phone and email!", Toast.LENGTH_SHORT ).show ( );

                return;
            }

            generateCode (userData);

            sendCodeToUser (
                    userData,
                    formatTitle ( true ),
                    formatMessage ( userData, true ),
                    isSms,
                    isEmail);

            if (isSms || isEmail)
                Toast.makeText ( getContext (), "Send success!", Toast.LENGTH_SHORT ).show ( );

        } );

        btnLoginForgotPasswordApply.setOnClickListener( view -> {

            UserData userData = userDataManager.findUserViaCode ( etLoginForgotPasswordCode.getText ( ).toString ( ) );

            if (userData == null){
                Toast.makeText ( getContext ( ), "Invalid Code", Toast.LENGTH_SHORT ).show ( );
                return;
            }

            alertDialog.dismiss();
            showNewPasswordPopup(userData);
        } );

        btnLoginForgotPasswordCancel.setOnClickListener( view -> alertDialog.dismiss() );
    }

    public void showNewPasswordPopup(final UserData userData) {
        final AlertDialog.Builder screenDialog = new AlertDialog.Builder(getContext());
        screenDialog.setTitle("Write Your Email");
        View dialogView = getLayoutInflater().inflate(R.layout.popup_login_new_password, null);
        screenDialog.setView(dialogView);

        final AlertDialog alertDialog = screenDialog.show();
        alertDialog.setCanceledOnTouchOutside(false);

        etLoginNewPassword = dialogView.findViewById(R.id.etLoginNewPassword);
        etLoginNewPasswordConfirm = dialogView.findViewById(R.id.etLoginNewPasswordConfirm);
        btnLoginNewPasswordApply = dialogView.findViewById(R.id.btnLoginNewPasswordApply);

        btnLoginNewPasswordApply.setOnClickListener( view -> {

            String newPassword = etLoginNewPassword.getText ().toString ();
            String passwordConfirm = etLoginNewPasswordConfirm.getText ().toString ();

            int err;
            if ((err = UserDataValidator.validatePassword ( newPassword ) ) != UserDataValidator.ERROR_NONE ) {
                Toast.makeText ( getContext (), UserDataValidator.errorToString ( err,"password" ), Toast.LENGTH_SHORT ).show ( );
                return;
            }

            if (!newPassword.equals ( passwordConfirm )) {
                Toast.makeText ( getContext (), "Passwords do not match!", Toast.LENGTH_SHORT ).show ( );
                return;
            }

            userDataManager.changePassword ( userData, newPassword );
            alertDialog.dismiss ( );
            Toast.makeText ( getContext (), "Done!", Toast.LENGTH_SHORT ).show ( );

        } );
    }

    public void showValidatePopup(final UserData userData) {
        final AlertDialog.Builder screenDialog = new AlertDialog.Builder(getContext());
        screenDialog.setTitle("Print validation code here.");
        View dialogView = getLayoutInflater().inflate(R.layout.popup_validate_user, null);
        screenDialog.setView(dialogView);

        final AlertDialog alertDialog = screenDialog.show();
        alertDialog.setCanceledOnTouchOutside(false);

        etValidateUserCode = dialogView.findViewById(R.id.etValidateUserCode);
        btnValidateUserApply = dialogView.findViewById(R.id.btnValidateUserApply);
        btnValidateUserCancel = dialogView.findViewById(R.id.btnValidateUserCancel);
        btnValidateUserResendSMS = dialogView.findViewById(R.id.btnValidateUserResendSMS);
        btnValidateUserResendEmail = dialogView.findViewById(R.id.btnValidateUserResendEmail);

        btnValidateUserApply.setOnClickListener( view -> {
            if (userData.getVerificationCode ( ).equals ( etValidateUserCode.getText ( ).toString ( ) )) {
                userDataManager.validateUser ( userData );
                Toast.makeText ( getContext ( ), "User validated!", Toast.LENGTH_SHORT ).show ( );
                alertDialog.dismiss ( );
            } else {
                Toast.makeText ( getContext ( ), "Wrong code!", Toast.LENGTH_SHORT ).show ( );
            }
        } );

        btnValidateUserResendSMS.setOnClickListener( view ->
                sendCodeToUserSms(
                        userData,
                        formatTitle ( false ),
                        formatMessage ( userData, false ))
        );

        btnValidateUserResendEmail.setOnClickListener( view ->
                sendCodeToUserEmail (
                        userData,
                        formatTitle ( false ),
                        formatMessage ( userData, false ))
        );

        btnValidateUserCancel.setOnClickListener( view -> alertDialog.dismiss() );
    }

    private void generateCode(UserData userData) {
        String newCode = new CodeGenerator (  ).generate ( 6 );
        userDataManager.changeCode ( userData, newCode );
        userData.setVerificationCode ( newCode );
    }

    private String formatMessage(UserData userData, boolean password) {
        return String.format (
                "Hello %s, your %s code for %s is %s.",
                userData.getUsername(),
                password?"verification":"password change",
                getString(R.string.title_activity_main),

                userData.getVerificationCode() );
    }

    private String formatTitle(boolean password) {
        return String.format ( "%s %s.",
                getString(R.string.title_activity_main),
                password?"verification":"password change");

    }

    public void sendCodeToUserSms(UserData userData, String title, String message) {
        sendCodeToUser(userData, title, message, true, false);
    }

    public void sendCodeToUserEmail(UserData userData, String title, String message) {
        sendCodeToUser(userData, title, message, false, true);
    }

    public void sendCodeToUser(UserData userData, String title, String message, boolean sms, boolean email) {
        if (userData == null)
            return;

        if (sms) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(userData.getPhone(), null,
                    title + "\n" + message, null, null);
        }
        if (email) {
            new SendVerificationMailTask ().execute(
                    new SendVerificationMailParams(userData,
                            message,
                            title) );
        }
    }
/*
    public void sendVerificationCodeWithSMS(UserData userData) {
        userData.generateVerificationCode();
        userData.getVerificationCode();
        String message = formatMessage(userData, false);
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(userData.getPhone(), null,
                message, null, null);
    }

    public void sendVerificationCodeWithEmail(UserData userData) {
        new SendVerificationMailTask ().execute(
                new SendVerificationMailParams(userData,
                        formatMessage ( userData, false ),
                        formatTitle ( false ))
        );
    }
*/
    private static class SendVerificationMailParams {
        @Getter @Setter UserData userData;
        @Getter @Setter String message;
        @Getter @Setter String title;

        public SendVerificationMailParams ( UserData userData, String message, String title) {
            this.userData = userData;
            this.message = message;
            this.title = title;
        }
    }

    private static class SendVerificationMailTask extends AsyncTask<SendVerificationMailParams, Void, Void> {

        @Override
        protected Void doInBackground ( SendVerificationMailParams... params ) {

            if (params[0] == null)
                return null;

            GMailSender sender = new GMailSender (
                    "GraphicCubeApp@gmail.com",
                    "0n93y7g5h3" );
            try
            {
                sender.sendMail ( params[0].title,
                        params[0].message,
                        "GraphicCubeApp@gmail.com",
                        params[0].getUserData ().getEmail () );
            } catch(
                    Exception e)

            {
                Log.d ( "developer", e.toString ( ) );
                e.printStackTrace ( );
            }

            return null;
        }
    }

}