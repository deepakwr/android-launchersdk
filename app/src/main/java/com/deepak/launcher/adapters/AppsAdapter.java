package com.deepak.launcher.adapters;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.deepak.launcher.R;
import com.deepak.launchersdk.models.AppInfo;

import java.util.ArrayList;
import java.util.Collection;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AppsAdapter extends RecyclerView.Adapter<AppsAdapter.AppViewHolder> {

    Collection<AppInfo> appDetails = new ArrayList<>();
    ArrayList<AppInfo> filteredAppDetails = new ArrayList<>();
    ListClickListener listClickListener;
    Context context = null;

    public AppsAdapter(Collection<AppInfo> appDetails, ListClickListener listClickListener){
        this.appDetails = appDetails;
        this.filteredAppDetails.addAll(this.appDetails);
        this.listClickListener = listClickListener;

        this.context = (Context) listClickListener;
    }

    public void removeApp(AppInfo appInfo){
        this.appDetails.remove(appInfo);
        filter("");
    }

    public void addApp(AppInfo appInfo){
        this.appDetails.add(appInfo);
        filter("");
    }

    public void updateList(Collection<AppInfo> appDetails){
        this.appDetails = appDetails;
        this.filteredAppDetails.clear();
        this.filteredAppDetails.addAll(this.appDetails);
        refreshList();
    }

    public void refreshList(){
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    public void filter(final String text) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                filteredAppDetails.clear();

                if (TextUtils.isEmpty(text)) {

                    filteredAppDetails.addAll(appDetails);

                } else {
                    for (AppInfo item : appDetails) {
                        if (item.appName.toLowerCase().contains(text.toLowerCase())) {
                            filteredAppDetails.add(item);
                        }
                    }
                }
                refreshList();
            }
        }).start();

    }


    @NonNull
    @Override
    public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_item,parent,false);
        return new AppViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull AppViewHolder holder, int position) {
        AppInfo app = filteredAppDetails.get(position);
        holder.nameTxt.setText(app.appName);
        holder.packageNameTxt.setText(app.packageName);
        holder.activityNameTxt.setText(app.defaultActivity);
        holder.versionNameTxt.setText(app.versionName);
        holder.versionCodeTxt.setText(""+app.versionCode);
        holder.appImg.setImageDrawable(app.drawable);
        holder.containerView.setTag(app.packageName);
        holder.containerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String packageName = (String) v.getTag();
                listClickListener.launchApp(packageName);
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredAppDetails.size();
    }

    public class AppViewHolder extends RecyclerView.ViewHolder{
        View containerView;
        ImageView appImg;
        TextView nameTxt,packageNameTxt,activityNameTxt,versionNameTxt,versionCodeTxt;

        public AppViewHolder(@NonNull View itemView) {
            super(itemView);
            containerView = itemView;
            appImg = itemView.findViewById(R.id.appImg);
            nameTxt = itemView.findViewById(R.id.nameTxt);
            packageNameTxt = itemView.findViewById(R.id.packageNameTxt);
            activityNameTxt = itemView.findViewById(R.id.activityNameTxt);
            versionNameTxt = itemView.findViewById(R.id.versionNameTxt);
            versionCodeTxt = itemView.findViewById(R.id.versionCodeTxt);
        }
    }
}

