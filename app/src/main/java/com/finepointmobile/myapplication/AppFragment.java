package com.finepointmobile.myapplication;

import android.app.ActivityManager;
import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKList;

import java.util.ArrayList;
import java.util.List;

import at.grabner.circleprogress.AnimationState;
import at.grabner.circleprogress.AnimationStateChangedListener;
import at.grabner.circleprogress.CircleProgressView;
import at.grabner.circleprogress.TextMode;
import at.grabner.circleprogress.UnitPosition;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AppFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AppFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AppFragment extends Fragment {
    private static final String TAG = "myLog" ;
    CircleProgressView mCircleView;
    ApplicationInfo app;
    private PackageManager packageManager = null;
    private List<ApplicationInfo> applist = null;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private OnFragmentInteractionListener mListener;
    TextView timeApp;
    AppDatabase db;
    CircleAdapter adapter;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    SharedPreferences sharedPreferences;
    TextView textView;

    public static AppFragment newInstance(String param1, String param2) {
        AppFragment fragment = new AppFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_app, container, false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        TextView name = view.findViewById(R.id.userName);

        VKApi.users().get().executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                VKApiUser user = ((VKList<VKApiUser>)response.parsedModel).get(0);
                Log.d(TAG, user.first_name + " " + user.last_name);
                name.setText(user.first_name + " " + user.last_name);
            }
        });
        //Log.d(TAG, "NAME : " + name.toString());
//        new LongOperation().execute();
        FloatingActionButton btn_access = view.findViewById(R.id.buttonAccess);
        btn_access.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){

                if(!isMyServiceRunning(MyAccessibilityService.class)){
                    Intent intent = new Intent(android.provider.Settings.
                            ACTION_ACCESSIBILITY_SETTINGS);
                    startActivity(intent);
                }
                else{
                    Intent intent1 = new Intent(getActivity(),AllAppsActivity.class);
                    startActivity(intent1);
                }
            }
        });
        recyclerView = view.findViewById(R.id.recycler_circle);
        db = Room.databaseBuilder(getContext(), AppDatabase.class, "production")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
        Log.d(TAG, "Data base: " + db.circlesDao().getAll().size());
        if (db.circlesDao().getAll().size() != 0) {
            adapter = new CircleAdapter(db.circlesDao().getAll(), getActivity());
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(adapter);
        }
        int points = sharedPreferences.getInt("TP",0);
        if(points > 100){
            SavePreferences("TP",points%100);
            SavePreferences("level",points/100);
        }
        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setProgress(sharedPreferences.getInt("TP",0));
        textView = view.findViewById(R.id.userLevel);
        textView.setText("Level:" + sharedPreferences.getInt("level",1));
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            Toast.makeText(context, "App", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    public void SavePreferences(String key, int value) {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putInt(key, value);
        edit.commit();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    @Override
    public void onStart() {
        super.onStart();
    }

    private class LongOperation extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mCircleView.setValue(0);
                    mCircleView.spin();
                }
            });

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mCircleView.setValueAnimated(42);
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        //TODO Change Value
        if(db.circlesDao().getAll().size() > 0 && !db.circlesDao().getAll().get(0).packageName.equals("")) {
            Log.d("myLog","OnResume");
            for (Circles circles : db.circlesDao().getAll()) {
                Log.d(TAG, circles.getPackageName() + circles.getTime());
            }
            adapter = new CircleAdapter(db.circlesDao().getAll(), getActivity());
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(adapter);
        }
        int points = sharedPreferences.getInt("TP",0);
        if(points > 100){
            SavePreferences("TP",points%100);
            SavePreferences("level",points/100);
        }
        textView.setText("Level:" + sharedPreferences.getInt("level",1));
        progressBar.setProgress(sharedPreferences.getInt("TP",0));

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getContext().
                getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.
                getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
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
}
