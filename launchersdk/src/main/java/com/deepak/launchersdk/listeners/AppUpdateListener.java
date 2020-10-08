package com.deepak.launchersdk.listeners;

import com.deepak.launchersdk.models.AppInfo;

public interface AppUpdateListener {
    void appUninstalled(AppInfo appInfo);

    void appInstalled(AppInfo appInfo);

    void appListRefreshed();
}
