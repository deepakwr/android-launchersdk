package com.deepak.launchersdk.models;

import android.graphics.drawable.Drawable;

import com.google.gson.Gson;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AppInfo {
    public String appName;
    public String packageName;
    public String versionName;
    public int versionCode;
    public String defaultActivity;
    public Drawable drawable;



    @Override
    public int hashCode() {
        return packageName.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof AppInfo && hashCode() == obj.hashCode();
    }

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

}
