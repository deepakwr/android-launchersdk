package com.deepak.launchersdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.deepak.launchersdk.listeners.AppUpdateListener;
import com.deepak.launchersdk.models.AppInfo;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class LauncherSDK extends BroadcastReceiver {

    public static final String TAG = "LauncherSDK";

    private static LauncherSDK instance;

    private PackageManager pm = null;

    private AppUpdateListener appUpdateListener =null;

    private HashMap<String, AppInfo> apps = new LinkedHashMap<>();

    public Collection<AppInfo> getApplications() {
        return apps.values();
    }


    private LauncherSDK(Context context){
        pm = context.getPackageManager();
        refreshApplicationList();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_FULLY_REMOVED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_INSTALL);
        intentFilter.addDataScheme("package");
        context.registerReceiver(this, intentFilter);

        refreshApplicationList();
    }

    public static LauncherSDK getInstance(Context context) {
        if(instance==null)
            instance = new LauncherSDK(context);

        return instance;
    }


    public void refreshApplicationList(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
                mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

                List<ResolveInfo> appList = pm.queryIntentActivities(mainIntent, 0);
                Collections.sort(appList, new ResolveInfo.DisplayNameComparator(pm));
                apps.clear();
                for (ResolveInfo app : appList) {
                    AppInfo appDetails = getApplicationDetails(app);
                    apps.put(appDetails.packageName,appDetails);
                }
                if(appUpdateListener !=null)
                    appUpdateListener.appListRefreshed();
            }
        }).start();
    }

    private AppInfo getApplicationDetails(ResolveInfo app){
        AppInfo appDetails = new AppInfo();
        appDetails.packageName = app.activityInfo.packageName;
        appDetails.appName = app.loadLabel(pm).toString();
        appDetails.defaultActivity = app.activityInfo.name;
        appDetails.drawable = app.loadIcon(pm);
        try {
            appDetails.versionName = pm.getPackageInfo(appDetails.packageName, 0).versionName;
            appDetails.versionCode = pm.getPackageInfo(appDetails.packageName, 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appDetails;
    }

    public void setAppUpdateListener(AppUpdateListener appUpdateListener) {
        this.appUpdateListener = appUpdateListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        // when package removed
        if (action.equals(Intent.ACTION_PACKAGE_REMOVED) || action.equals(Intent.ACTION_PACKAGE_FULLY_REMOVED)) {
            String packageName = intent.getDataString().replace("package:","");
            if(appUpdateListener !=null){
                if(apps.get(packageName)!=null)
                    appUpdateListener.appUninstalled(apps.get(packageName));
                apps.remove(packageName);
            }
        }
        // when package installed
        else if (action.equals(Intent.ACTION_PACKAGE_INSTALL) ||action.equals(Intent.ACTION_PACKAGE_ADDED)) {

            String packageName = intent.getDataString().replace("package:","");
            if(appUpdateListener !=null){
                refreshApplicationList();
                if(apps.get(packageName)!=null)
                    appUpdateListener.appInstalled(apps.get(packageName));
            }

        }
    }
}

