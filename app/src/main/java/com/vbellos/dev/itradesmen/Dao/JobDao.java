package com.vbellos.dev.itradesmen.Dao;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vbellos.dev.itradesmen.Models.Job;

import java.util.ArrayList;
import java.util.List;

public class JobDao {

    private  static DatabaseReference jRef= FirebaseDatabase.getInstance().getReference("jobs");


    public void addJob(Job job)
    {
        jRef.child(job.getId()).setValue(job);
    }


    private ArrayList<JobsChangedEventListener> mListener =new ArrayList<>();
    public interface JobsChangedEventListener
    {
        void JobsChanged(List<Job> jobList);
    }
    public void addJobsChangedEventListener(JobsChangedEventListener eventListener)
    {
        this.mListener.add(eventListener);
        List<Job> jobs = new ArrayList<Job>();
        jRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                jobs.clear();
                for (DataSnapshot ds: snapshot.getChildren()) {
                    jobs.add(ds.getValue(Job.class));
                }
                for (JobsChangedEventListener l: mListener) {
                    l.JobsChanged(jobs);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static class JobReference {
        String id;
         public JobReference(String id)
         {this.id = id;}

        private ArrayList<GetJobEventListener> JobListeners = new ArrayList<>();

        public interface GetJobEventListener {
            void JobExists(Job job);

            void JobDoesntExist();
        }

        public void addGetJobEventListener(GetJobEventListener eventListener) {
            this.JobListeners.add(eventListener);
            jRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChild(id)) {
                        JobListeners.get(JobListeners.indexOf(eventListener)).JobExists(snapshot.child(id).getValue(Job.class));
                    } else {
                        JobListeners.get(JobListeners.indexOf(eventListener)).JobDoesntExist();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }


}
