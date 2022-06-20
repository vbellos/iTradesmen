package com.vbellos.dev.itradesmen.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.vbellos.dev.itradesmen.Utilities.GlideApp;
import com.vbellos.dev.itradesmen.Models.Job;
import com.vbellos.dev.itradesmen.R;
import com.vbellos.dev.itradesmen.User.ViewUserProfileActivity;
import com.vbellos.dev.itradesmen.Models.Worker;
import com.vbellos.dev.itradesmen.Utilities.TinyDB;

import java.util.ArrayList;

public class WorkersRecyclerViewAdapter extends RecyclerView.Adapter<WorkersRecyclerViewAdapter.ViewHolder> {
    private Context context;
    //private WorkerListHelper workerListHelper;
    //private List<Job> jobList;
    private ArrayList<Worker> workers =new ArrayList<>();



    public WorkersRecyclerViewAdapter(Context context) {
       this.context = context;
    }





    public void AddWorker(Worker worker)
    {
            this.workers.add(worker);
            notifyDataSetChanged();
    }

    public void AddAll(ArrayList<Worker> arrayList)
    {
        workers.addAll(arrayList);
        notifyDataSetChanged();
    }

    public void RemoveAll(ArrayList<Worker> workerArrayList)
    {
        workers.removeAll(workerArrayList);
        notifyDataSetChanged();
    }
    public void RemoveWorker(Worker worker)
    {
        workers.remove(worker);
    }

    public void LoadList(ArrayList<Worker> workerArrayList)
    {
        workers.clear();
        workers.addAll(workerArrayList);
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name, jobtxt,distancetxt;
        private ImageView prof_pic,job_img;
        private CardView WokerCardView;


        public ViewHolder(final View view){
            super(view);
            name =(TextView) view.findViewById(R.id.workerNametxt);
            jobtxt =(TextView) view.findViewById(R.id.worker_job_txt);
            job_img = (ImageView) view.findViewById(R.id.worker_job_imageView);
            prof_pic = (ImageView) view.findViewById(R.id.worker_prof_pic);
            WokerCardView = (CardView) view.findViewById(R.id.WorkerCardView);
            distancetxt = (TextView) view.findViewById(R.id.worker_distance_txt);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item= LayoutInflater.from(parent.getContext()).inflate(R.layout.worker_recycler_item,parent,false);
        return new ViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Worker worker = workers.get(position);
        Job job = worker.getJob();
        if(job!= null)
        {
            holder.jobtxt.setText(job.getTitle());
            if(job.getIcon()!= null && job.getIcon()!=""){
                StorageReference imgRef = FirebaseStorage.getInstance().getReference().child("job_icons").child(job.getIcon());
                GlideApp.with(context /* context */)
                        .load(imgRef)
                        .error(R.mipmap.ic_launcher)
                        .placeholder(R.mipmap.ic_launcher)
                        .into(holder.job_img);
            }
        }

        holder.WokerCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TinyDB tinyDB = new TinyDB(context);
                String search_key= "users_searches_"+ FirebaseAuth.getInstance().getCurrentUser().getUid();
                ArrayList<String> searches = tinyDB.getListString(search_key);
                if(searches == null){searches = new ArrayList<String>();}
                if(searches.contains(worker.getId())){ searches.remove(worker.getId());}
                searches.add(worker.getId());
                tinyDB.putListString(search_key,searches);


                Intent i = new Intent(context , ViewUserProfileActivity.class);
                i.putExtra("id",worker.getId());
                context.startActivity(i);
            }
        });


                holder.distancetxt.setText(worker.getDistance() + " Km");
                holder.name.setText(worker.getName());
                if(worker.getPicture() != null && worker.getPicture()!="") {
                    StorageReference imgRef = FirebaseStorage.getInstance().getReference().child("profile_pics").child(worker.getPicture());
                    GlideApp.with(context /* context */)
                            .load(imgRef)
                            .error(R.mipmap.ic_launcher)
                            .placeholder(R.drawable.default_avatar)
                            .into(holder.prof_pic);


                }
    }

    @Override
    public int getItemCount() {
        return workers.size();
    }
}
