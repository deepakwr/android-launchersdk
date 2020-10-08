package com.deepak.launcher.viewmodels;

import android.app.Application;

import com.deepak.launchersdk.models.AppInfo;

import java.util.Collection;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class AppViewModel extends AndroidViewModel {

    public MutableLiveData<Collection<AppInfo>> getAppLiveDataList() {
        return appLiveDataList;
    }


    MutableLiveData<Collection<AppInfo>> appLiveDataList = new MutableLiveData<>();


    public AppViewModel(@NonNull Application application) {
        super(application);
    }

}
