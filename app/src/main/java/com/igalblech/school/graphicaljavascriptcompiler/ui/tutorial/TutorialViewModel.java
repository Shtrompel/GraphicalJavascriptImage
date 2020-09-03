package com.igalblech.school.graphicaljavascriptcompiler.ui.tutorial;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TutorialViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public TutorialViewModel ( ) {
        mText = new MutableLiveData<> ( );
        mText.setValue ( "This is tutorial fragment" );
    }

    public LiveData<String> getText ( ) {
        return mText;
    }
}