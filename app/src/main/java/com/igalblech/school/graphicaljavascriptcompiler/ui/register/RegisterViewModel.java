package com.igalblech.school.graphicaljavascriptcompiler.ui.register;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RegisterViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public RegisterViewModel( ) {
        mText = new MutableLiveData<> ( );
        mText.setValue ( "This is tutorial fragment" );
    }

    public LiveData<String> getText ( ) {
        return mText;
    }
}