package com.igalblech.school.graphicaljavascriptcompiler.ui.home;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    public HomeViewModel ( ) {
        MutableLiveData<String> mText = new MutableLiveData<> ( );
        mText.setValue ( "This is home fragment" );
    }

// --Commented out by Inspection START (28/10/2020 14:07):
//    public LiveData<String> getText ( ) {
//        return mText;
//    }
// --Commented out by Inspection STOP (28/10/2020 14:07)
}