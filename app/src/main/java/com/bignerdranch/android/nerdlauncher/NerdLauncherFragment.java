package com.bignerdranch.android.nerdlauncher;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by olo35 on 23.03.2016.
 */
public class NerdLauncherFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private static final String TAG = "NerdLauncherFragment";

    public static NerdLauncherFragment newInstance()
    {
        return new NerdLauncherFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstance)
    {
        View v =inflater.inflate(R.layout.fragment_nerd_launcher,container,false);
        mRecyclerView=(RecyclerView) v.findViewById(R.id.fragment_nerd_launcher_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        setupAdapter();
        return v;
    }
    private void setupAdapter()
    {
        Intent startupIntent = new Intent(Intent.ACTION_MAIN);
        startupIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        PackageManager pm = getActivity().getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(startupIntent,0);


        Collections.sort(activities, new Comparator<ResolveInfo>() {
            @Override
            public int compare(ResolveInfo lhs, ResolveInfo rhs) {
                PackageManager pm = getActivity().getPackageManager();
                return String.CASE_INSENSITIVE_ORDER.compare(lhs.loadLabel(pm).toString(),rhs.loadLabel(pm).toString());
            }
        });

        Log.i(TAG, "Found" + activities.size() + "activities");
        mRecyclerView.setAdapter(new ActivityAdapter(activities));
    }
    private class ActivityHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private ResolveInfo mResolveInfo;
        private TextView mNameTextView;
        private ImageView mImageView;

        public ActivityHolder(View itemView)
        {
            super(itemView);
            mNameTextView = (TextView) itemView.findViewById(R.id.list_app_text);
            mImageView = (ImageView) itemView.findViewById(R.id.list_app_icon);

            mNameTextView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            ActivityInfo activityInfo = mResolveInfo.activityInfo;

            Intent i=new Intent(Intent.ACTION_MAIN).setClassName(activityInfo.applicationInfo.packageName,activityInfo.name)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }

        public void bindActivity(ResolveInfo resolveInfo)
        {
            mResolveInfo = resolveInfo;
            PackageManager pm = getActivity().getPackageManager();
            String appName = mResolveInfo.loadLabel(pm).toString();

            mImageView.setImageDrawable(mResolveInfo.loadIcon(pm));
            mNameTextView.setText(appName);
        }
    }
    private class ActivityAdapter extends RecyclerView.Adapter<ActivityHolder>
    {
        private final List<ResolveInfo> mActivities;
        public ActivityAdapter(List<ResolveInfo> activities)
        {
            mActivities = activities;
        }

        @Override
        public ActivityHolder onCreateViewHolder(ViewGroup parent,int viewType)
        {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view=layoutInflater.inflate(R.layout.app_list_item,parent,false);
            return new ActivityHolder(view);
        }
        @Override
        public void onBindViewHolder(ActivityHolder activityHolder,int position)
        {
            ResolveInfo resolveInfo = mActivities.get(position);
            activityHolder.bindActivity(resolveInfo);
        }
        @Override
        public int getItemCount()
        {
            return mActivities.size();
        }
    }
}
