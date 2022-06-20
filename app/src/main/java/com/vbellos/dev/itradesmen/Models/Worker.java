package com.vbellos.dev.itradesmen.Models;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.Marker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Worker extends User{


    Job job;
    List<Review> reviews = new ArrayList<Review>();

    Worker_Location worker_location;

    String description;

    Marker userLocMarker;

    public Marker getUserLocMarker() {
        return userLocMarker;
    }

    public void setUserLocMarker(Marker userLocMarker) {
        this.userLocMarker = userLocMarker;
    }

    public DataChangedEventListener getmListener() {
        return mListener;
    }

    public void setmListener(DataChangedEventListener mListener) {
        this.mListener = mListener;
    }

    double distance;

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public Worker_Location getWorker_location() {
        return worker_location;
    }

    public void setWorker_location(Worker_Location worker_location) {
        this.worker_location = worker_location;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        callListener();
    }

    public WorkerDbObj getDbObject()
    {
        return new WorkerDbObj(getId(), job.getId(), description);
    }


    public User getUser()
    {
        User user = new User();
        user.setId(this.getId());
        user.setName(this.getName());
        user.setEmail(this.getEmail());
        user.setPhone(this.getPhone());
        user.setPicture(this.getPicture());
        user.setWorker(true);
        user.setTimestamp(this.getTimestamp());
        return user;
    }

    public void InheritUser(User user)
    {
        this.setId(user.getId());
        this.setName(user.getName());
        this.setWorker(true);
        this.setEmail(user.getEmail());
        this.setPhone(user.getPhone());
        this.setPicture(user.getPicture());
        callListener();
    }


    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
        callListener();
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
        callListener();
    }

    public void InheritWorker(WorkerDbObj workerDbObj)
    {
        this.setDescription(workerDbObj.getDescription());

        callListener();

    }

    public void LoadReviews()
    {
        DatabaseReference ReviewsRef = FirebaseDatabase.getInstance().getReference("reviews").child(getId());
        ReviewsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reviews.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    reviews.add(ds.getValue(Review.class));
                }
                callListener();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private DataChangedEventListener mListener;
    public interface DataChangedEventListener
    {
        void onDataChanged();
    }
    public void setDataChangedEventListener(DataChangedEventListener eventListener)
    {
        this.mListener = eventListener;
    }
    public void callListener()
    {
        if(mListener!=null)
        {
            mListener.onDataChanged();
        }
    }


}
