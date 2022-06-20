package com.vbellos.dev.itradesmen.Dao;

import android.location.Location;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vbellos.dev.itradesmen.Models.Review;
import com.vbellos.dev.itradesmen.Models.Worker;
import com.vbellos.dev.itradesmen.Models.WorkerDbObj;
import com.vbellos.dev.itradesmen.Models.Worker_Location;
import com.vbellos.dev.itradesmen.Dao.UserDao;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class WorkerDao {
    private static DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("workers");
    private static DatabaseReference locDatabase = FirebaseDatabase.getInstance().getReference().child("worker_location");
    private  static UserDao userDao = new UserDao();;


    public WorkerDao()
    {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("workers");
        userDao = new UserDao();
    }

    public void addWorker(WorkerDbObj workerDbObj)
    {
        mDatabase.child(workerDbObj.getId()).setValue(workerDbObj);
    }

    public void updateWorker(Worker worker)
    {
        userDao.updateuser(worker.getUser());
        mDatabase.child(worker.getId()).setValue(worker.getDbObject());
    }
    public void updateWorker(WorkerDbObj worker)
    {
        mDatabase.child(worker.getId()).setValue(worker);
    }

    public void addReview(Review review)
    {
        DatabaseReference RevDatabase = FirebaseDatabase.getInstance().getReference().child("reviews").child(review.getWorkerID());
        RevDatabase.push().setValue(review);
    }


    private ArrayList<WorkersChangedEventListener> wcListener = new ArrayList<>();
    public interface WorkersChangedEventListener
    {
        void WorkersChanged(List<WorkerDbObj> workerList);
    }
    public void addWorkersChangedEventListener(WorkersChangedEventListener eventListener)
    {
        this.wcListener.add(eventListener);
        List<WorkerDbObj> workers = new ArrayList<WorkerDbObj>();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                workers.clear();
                for (DataSnapshot ds: snapshot.getChildren()) {
                    workers.add(ds.getValue(WorkerDbObj.class));
                }
                for (WorkersChangedEventListener l: wcListener)
                {
                    l.WorkersChanged(workers);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    public void updateLocation(@NonNull String worker_id , Location location)
    {
        locDatabase.child(worker_id).setValue(new Worker_Location(worker_id,location.getLatitude(),location.getLongitude(),new Timestamp(System.currentTimeMillis()).getTime()));
    }

    public static class workerLocReference
    {
        String wokrer_id;
        public workerLocReference(String id)
        {this.wokrer_id = id;}
        WorkerLocationUpdates listener;
        public interface WorkerLocationUpdates
        {void LocationChanged(Worker_Location worker_location);}
        public void setLocationUpdateListener(WorkerLocationUpdates listener)
        {
            this.listener = listener;
            locDatabase.child(wokrer_id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Worker_Location worker_location = snapshot.getValue(Worker_Location.class);
                    listener.LocationChanged(worker_location);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }


    public static class workerReference
    {
        String worker_id;
        public workerReference(String id){this.worker_id = id;}

        private ArrayList<GetWorkerEventListener> WorkerListener = new ArrayList<>();
        public interface GetWorkerEventListener
        {
            void WorkerExists(WorkerDbObj workerDbObj);
            void WorkerDoesntExist();
        }
        public void addWorkerEventListener(GetWorkerEventListener eventListener)
        {
            WorkerListener.add(eventListener);
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChild(worker_id))
                    {
                        WorkerListener.get(WorkerListener.indexOf(eventListener)).WorkerExists(snapshot.child(worker_id).getValue(WorkerDbObj.class));
                    }
                    else{WorkerListener.get(WorkerListener.indexOf(eventListener)).WorkerDoesntExist();}
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }



}
