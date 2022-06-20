package com.vbellos.dev.itradesmen.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.vbellos.dev.itradesmen.Utilities.GlideApp;
import com.vbellos.dev.itradesmen.Models.Job;
import com.vbellos.dev.itradesmen.R;

import java.util.List;

public class JobSpinnerAdapter extends BaseAdapter {

    List<Job> jobList;
    LayoutInflater inflater;
    StorageReference imgRef = FirebaseStorage.getInstance().getReference().child("job_icons");
    Context context;
    public JobSpinnerAdapter(Context applicationContext, List<Job> jobList)
    {
        this.jobList = jobList;
        this.context = applicationContext;
        inflater=(LayoutInflater.from(applicationContext));
    }
    @Override
    public int getCount() {
        return jobList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    public Job getJob(int i)
    {
        return jobList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.job_view_item_medium,null);
        ImageView icon = view.findViewById(R.id.job_view_item_medium_Icon);
        TextView title = view.findViewById(R.id.job_view_item_medium_Title);
        title.setText(jobList.get(i).getTitle());

        GlideApp.with(this.context /* context */)
                .load(imgRef.child(getJob(i).getIcon()))
                .error(R.mipmap.ic_launcher)
                .placeholder(R.drawable.default_avatar)
                .into(icon);

        return view;
    }
}
