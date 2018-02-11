package com.finepointmobile.myapplication;

/**
 * Created by Роман on 20.01.2018.
 */

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.load.engine.Resource;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import at.grabner.circleprogress.AnimationState;
import at.grabner.circleprogress.AnimationStateChangedListener;
import at.grabner.circleprogress.CircleProgressView;
import at.grabner.circleprogress.ColorUtils;
import at.grabner.circleprogress.TextMode;

/**
 * Created by danielmalone on 10/27/17.
 */

class CircleAdapter extends RecyclerView.Adapter<CircleAdapter.ViewHolder> {

    public List<Circles> circles;
    Context context;
    private PackageManager packageManager = null;
    private List<ApplicationInfo> applist = null;
    public CircleAdapter(List<Circles> circles , Context context) {
        this.circles = circles;
        this.context = context;
    }

    @Override
    public CircleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.circle_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CircleAdapter.ViewHolder holder, int position) {
        holder.mCircleView.setShowTextWhileSpinning(false); // Show/hide text in spinning mode
        //TODO CALC TEXT COLOR()
        holder.mCircleView.setClickable(false);
        holder.mCircleView.setTextMode(TextMode.VALUE);
        holder.mCircleView.setUnitVisible(false);
        holder.mCircleView.setBarColor(Color.parseColor("#4fc3f7"), Color.parseColor("#0288D1"), Color.parseColor("#26C6DA"));
        //holder.mCircleView.setBarColor(Color.GREEN);
        holder.mCircleView.setSeekModeEnabled(false);
        holder.mCircleView.setRimWidth(10);
        holder.mCircleView.setBarWidth(10);
        int minutes = (int)(circles.get(position).getTime() / 60000);
        int limitMunutes = (int)(circles.get(position).getLimitTime()/60000);
        Log.d("myLog", "onBindViewHolder: " + minutes + ' ' + limitMunutes);
        holder.timeApp.setText("Oсталось " + Math.max(limitMunutes - minutes,0) + '/' + limitMunutes + " минут");
        double procent = Math.min(1, (double)minutes/(double)limitMunutes);
        Log.d("myLog", "onBindViewHolder: " + procent);
        holder.mCircleView.setValue((float) (100*(1 - procent)));

        packageManager = context.getPackageManager();
        applist = checkForLaunchIntent(packageManager.getInstalledApplications(PackageManager.GET_META_DATA));
        ApplicationInfo app = findApp(applist,circles.get(position).packageName);
        Log.d("myLog","OnBind:" + circles.get(position).packageName);
        Log.d("myLog", app.packageName);
        holder.imageApp.setImageDrawable(app.loadIcon(packageManager));
    }

    @Override
    public int getItemCount() {
        return circles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView timeApp;
        CircleProgressView mCircleView;
        ImageView imageApp;
        public ViewHolder(View itemView) {
            super(itemView);
            timeApp = itemView.findViewById(R.id.timeApp);
            mCircleView = itemView.findViewById(R.id.circleView);
            imageApp = itemView.findViewById(R.id.imageApp);
        }
    }
    private List<ApplicationInfo> checkForLaunchIntent(List<ApplicationInfo> list) {
        ArrayList<ApplicationInfo> applist = new ArrayList<ApplicationInfo>();
        for (ApplicationInfo info : list) {
            try {
                if (null != packageManager.getLaunchIntentForPackage(info.packageName)) {
                    applist.add(info);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return applist;
    }
    public ApplicationInfo findApp(List<ApplicationInfo> list, String name){
        for (ApplicationInfo applicationInfo : list) {
            if(null != packageManager.getLaunchIntentForPackage(applicationInfo.packageName)){
                Log.d("myLog", "findApp: " + applicationInfo.packageName);
                if(applicationInfo.packageName.equals(name)) {
                    Log.d("myLog", "I AMHEREEEEEEEEEEEEEEEEEEEEEEe ");
                    return applicationInfo;
                }
            }
        }
        return null;
    }
}