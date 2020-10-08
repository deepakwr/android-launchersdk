package com.deepak.launcher;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.deepak.launcher.adapters.AppsAdapter;
import com.deepak.launcher.adapters.ListClickListener;
import com.deepak.launcher.viewmodels.AppViewModel;
import com.deepak.launchersdk.LauncherSDK;
import com.deepak.launchersdk.listeners.AppUpdateListener;
import com.deepak.launchersdk.models.AppInfo;

import java.util.Collection;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity implements ListClickListener, AppUpdateListener,SearchView.OnQueryTextListener {

    public final static String TAG="MainActivity";

    RecyclerView recyclerView;
    AppsAdapter recyclerViewAdapter;
    SearchView searchView;

    AppViewModel appViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        LauncherSDK.getInstance(this).setAppUpdateListener(this);

        searchView = (SearchView) findViewById(R.id.searchView);
        setupSearchView();

        recyclerView = findViewById(R.id.appsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        appViewModel = new ViewModelProvider(getViewModelStore(),new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(AppViewModel.class);
        appViewModel.getAppLiveDataList().observe(this, new Observer<Collection<AppInfo>>() {
            @Override
            public void onChanged(Collection<AppInfo> appDetails) {
                if(recyclerViewAdapter!=null)
                    recyclerViewAdapter.updateList(appDetails);
                else{
                    Collection<AppInfo> apps = LauncherSDK.getInstance(MainActivity.this).getApplications();
                    recyclerViewAdapter = new AppsAdapter(apps,MainActivity.this);
                    recyclerView.setAdapter(recyclerViewAdapter);
                }
            }
        });
    }

    @Override
    public void launchApp(String packageName) {
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage(packageName);
        if (launchIntent != null) {
            startActivity(launchIntent);
        } else {
            Toast.makeText(MainActivity.this, "There is no package available in android", Toast.LENGTH_LONG).show();
        }
    }



    private void setupSearchView() {
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(this);
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryHint("Search Here");
    }

    public boolean onQueryTextChange(String newText) {
        if(recyclerViewAdapter!=null)
            recyclerViewAdapter.filter(newText);
        return true;
    }

    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public void appUninstalled(AppInfo appInfo) {

        if(appInfo==null)
            return;

        if(recyclerViewAdapter!=null)
            recyclerViewAdapter.removeApp(appInfo);

        String message = appInfo.appName + " uninstalled";
        sendNotification(message);
    }

    @Override
    public void appInstalled(AppInfo appInfo) {
        if(appInfo==null)
            return;
//          TODO::recheck
//        if(recyclerViewAdapter!=null)
//            recyclerViewAdapter.addApp(appInfo);

        String message = appInfo.appName + " installation successful";
        sendNotification(message);
    }


    @Override
    public void appListRefreshed() {
        Collection<AppInfo> apps = LauncherSDK.getInstance(this).getApplications();
        appViewModel.getAppLiveDataList().postValue(apps);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.loading).setVisibility(View.GONE);
                ((SearchView)findViewById(R.id.searchView)).setQuery("",false);
            }
        });
//        sendNotification("Applications list refreshed");
    }

    public void sendNotification(String message){

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        NotificationChannel mChannel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel("Sample", "Sample",
                    NotificationManager.IMPORTANCE_DEFAULT);
            mChannel.setDescription("Sample");
            mChannel.setLightColor(Color.CYAN);
            mChannel.canShowBadge();
            mChannel.setShowBadge(true);
            notificationManager.createNotificationChannel(mChannel);
        }

        Notification n  = new NotificationCompat.Builder(this,"Sample")
                .setContentTitle("Launcher Update")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentText(message)
                .setAutoCancel(true).build();

        notificationManager.notify(0, n);
    }




    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}
