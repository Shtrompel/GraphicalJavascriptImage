package com.igalblech.school.graphicaljavascriptcompiler.ui.login;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LoginViewModel extends ViewModel {

    public LoginViewModel( ) {
        MutableLiveData<String> mText = new MutableLiveData<> ( );
        mText.setValue ( "This is tutorial fragment" );
    }

// --Commented out by Inspection START (28/10/2020 14:07):
//    public LiveData<String> getText ( ) {
//        return mText;
//    }
// --Commented out by Inspection STOP (28/10/2020 14:07)
}