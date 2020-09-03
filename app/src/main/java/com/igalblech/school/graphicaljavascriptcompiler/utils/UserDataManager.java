package com.igalblech.school.graphicaljavascriptcompiler.utils;

import android.content.Context;
import android.util.Log;

import java.io.IOException;

import lombok.Getter;

@Deprecated
public class UserDataManager {

    private String errorStr = "";


    @Getter private UserTextFile userTextFile;

    public UserDataManager(Context context) {
        try {
            userTextFile = new UserTextFile(context, "passwords.txt");
        } catch (IOException e) {
           // Log.d("Developer", e.toString());
            e.printStackTrace();
        }
    }

    public boolean addUser(UserData userData, String validationPassword) {
        Log.d("developer", "addUser called");

        int err;

        //Log.d("Developer", userData.toString());

        if ((err = UserDataValidator.validateUsername(userData.getUsername())) != UserDataValidator.ERROR_NONE) {
            errorStr = UserDataValidator.errorToString(err, UserDataValidator.STRING_USERNAME);
            return false;
        }

        if ((err = UserDataValidator.validateEmail(userData.getEmail())) != UserDataValidator.ERROR_NONE) {
            errorStr = UserDataValidator.errorToString(err, UserDataValidator.STRING_EMAIL);
            return false;
        }

        if ((err = UserDataValidator.validatePhone(userData.getPhone())) != UserDataValidator.ERROR_NONE) {
                errorStr = UserDataValidator.errorToString(err, UserDataValidator.STRING_PHONE);
            return false;
        }
        if ((err = UserDataValidator.validatePassword(userData.getPassword())) != UserDataValidator.ERROR_NONE) {
            errorStr = UserDataValidator.errorToString(err, UserDataValidator.STRING_PASSWORD);
            return false;
        }

        if (!validationPassword.equals(userData.getPassword())) {
            errorStr =UserDataValidator.errorToString(
                    UserDataValidator.ERROR_DIF_PASSWORDS, "");
            return false;
        }

        if (userData.getUsername().equals(userData.getPassword())) {
            errorStr =UserDataValidator.errorToString(
                    UserDataValidator.ERROR_USERNAME_DIF_PASSWORD, "");
            return false;
        }

        if ((err = checkForClashes(userData)) != UserDataValidator.ERROR_NONE) {
            errorStr = UserDataValidator.errorToString(err, "");
            return false;
        }

        addUserToDatabase(userData);

        return true;
    }

    public int checkForClashes(UserData userData) {
        Log.d("developer", "checkForClashes called");
        String[] lines = null;
        try {
            lines = userTextFile.getLines();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (lines == null)
            return -1;

        for (int i = 0; i < lines.length; i++) {
            int v = userData.checkForClashes ( new UserData(lines[i]) );
            if (v != UserDataValidator.ERROR_NONE) {
                return v;
            }
        }

        return UserDataValidator.ERROR_NONE;
    }

    public void addUserToDatabase(UserData userData) {
        Log.d("developer", "addUserToDatabase called");
        try {
           //Log.d("Developer", java.util.Arrays.deepToString(userTextFile.getLines()));
            userTextFile.appendLine(userData.getStringArray());
            //Log.d("Developer", java.util.Arrays.deepToString(userTextFile.getLines()));
        } catch (IOException e) {
           // Log.d("Developer", e.toString());
            e.printStackTrace();
        }
    }

    public String[] getRawUserData() {
        String[] lines = null;
        try {
            lines = userTextFile.getLines();
        } catch (IOException e) {
            Log.d("Developer", e.toString());
            e.printStackTrace();
        }
        return lines;
    }

    public UserData loginUser(String username, String password) {
        Log.d("developer", "loginUser called");

        String[] lines = getRawUserData();
        if (lines == null) return null;

        for (int i = 0; i < lines.length; i++) {
            UserData userData = new UserData(lines[i]);
            if (userData.loginValidation(username, password))
                return userData;
        }
        return null;
    }

    public UserData findUserViaEmailOrPhone(String email, String phone) {
        Log.d("developer", "findUserViaEmailOrPhone called");
        String[] lines = getRawUserData();
        if (lines == null) return null;
        String emailF = email.toLowerCase ();
        String phoneF = phone
                .replace ( "-", "" )
                .replace ( " ", "" );
        boolean isEmail = !email.equals ( "" );
        boolean isPhone = !phone.equals ( "" );

        for (int i = 0; i < lines.length; i++) {
            UserData userData = new UserData(lines[i]);
            String uEmailF = userData.getEmail ().toLowerCase ();
            String uPhoneF = userData.getPhone ()
                    .replace ( "-", "" )
                    .replace ( " ", "" );

            boolean emailEquals = emailF.equals ( uEmailF );
            boolean phoneEquals = phoneF.equals ( uPhoneF );

            if (isEmail && !isPhone && emailEquals )
                return userData;
            else if (isPhone && !isEmail && phoneEquals )
                return userData;
            else if (isEmail && isPhone && emailEquals && phoneEquals)
                return userData;
        }
        return null;
    }

    public UserData findUserViaCode(String code) {
        Log.d("developer", "findUserViaCode called");
        String[] lines = getRawUserData();
        if (lines == null) return null;

        Log.d("developer", "A = " + code);

        for (int i = 0; i < lines.length; i++) {
            UserData userData = new UserData(lines[i]);
            Log.d("developer", "B = " + userData.getVerificationCode ());
            if (userData.getVerificationCode ().equals ( code )) {
                Log.d("developer", "C = " + " Yay!");
                return userData;
            }
        }
        return null;
    }

    public boolean validateUser(UserData userDataParam) {
        Log.d("developer", "validateUser called");
        String[] lines = getRawUserData();
        if (lines == null) return false;
        for (int i = 0; i < lines.length; i++) {
            UserData userData = new UserData(lines[i]);
            if (userData.equals(userDataParam)) {
                try {
                    userDataParam.setVerified(true);
                    userTextFile.replaceLine(i, userDataParam.getStringArray());
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return false;
    }

    public boolean updateUser(UserData userDataParam) {
        Log.d("developer", "updateUser called");
        String[] lines = getRawUserData();
        if (lines == null) return false;
        for (int i = 0; i < lines.length; i++) {
            UserData userData = new UserData(lines[i]);
            if (userData.equals(userDataParam)) {
                try {
                    userTextFile.replaceLine(i, userDataParam.getStringArray());
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return false;
    }

    public boolean changePassword(UserData userDataParam, String password) {
        Log.d("developer", "Change Password called");
        String[] lines = getRawUserData();
        if (lines == null) return false;
        for (int i = 0; i < lines.length; i++) {
            UserData userData = new UserData(lines[i]);
            if (userData.equals(userDataParam)) {
                try {
                    userDataParam.setPassword (password);
                    userTextFile.replaceLine(i, userDataParam.getStringArray());
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return false;
    }

    public String getErrorStr() {
        return errorStr;
    }
}
