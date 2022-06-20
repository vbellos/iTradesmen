package com.vbellos.dev.itradesmen.Client.ViewWorkers;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vbellos.dev.itradesmen.Models.Job;
import com.vbellos.dev.itradesmen.Models.User;
import com.vbellos.dev.itradesmen.Dao.UserDao;
import com.vbellos.dev.itradesmen.Models.Worker;
import com.vbellos.dev.itradesmen.Dao.WorkerDao;
import com.vbellos.dev.itradesmen.Models.WorkerDbObj;
import com.vbellos.dev.itradesmen.Models.Worker_Location;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class WorkersListLoader {

    private final double max_distance = 50;
    private final long max_time = 24*60;

    private Location location;
    private ArrayList<Worker> workers;
    private List<Job> jobs;


    public WorkersListLoader(Location location ,List<Job> jobs) {
        this.location = location;
        this.jobs = jobs;
        workers = new ArrayList<>();
        load();
    }
    
    public void add(Worker worker)
    {
        workers.add(worker);
        Collections.sort(workers,new DistanceComparator());

        callWorkerListener(worker);

        callListener();
    }

    public ArrayList<Worker> getWorkers(String job_id , double distance, long time)
    {
        ArrayList<Worker> workerArrayList = new ArrayList<>();

        for(Worker w : workers)
        {
            if(w.getDistance() > distance){break;}
            else if(w.getJob().getId().equals(job_id) && compareTimeStamp(w.getWorker_location().getTimestamp())<time)
            {
                workerArrayList.add(w);
            }
        }

        return  workerArrayList;
    }

    public ArrayList<Worker> getWorkersByNewDistance(String job_id , double old_distance , double new_distance, long time)
    {
        ArrayList<Worker> workerArrayList = new ArrayList<>();

        if(new_distance > old_distance) {
            workerArrayList = getWorkers(job_id,new_distance,time);
            workerArrayList.removeAll(getWorkers(job_id,old_distance,time));
        }

        else{
            workerArrayList = getWorkers(job_id,old_distance,time);
            workerArrayList.removeAll(getWorkers(job_id,new_distance,time));
        }
        return  workerArrayList;
    }

    public ArrayList<Worker> getWorkersByNewTime(String job_id , double distance , long old_time , long new_time)
    {
        ArrayList<Worker> workerArrayList = new ArrayList<>();

        if(new_time > old_time) {
            workerArrayList = getWorkers(job_id,distance,new_time);
            workerArrayList.removeAll(getWorkers(job_id,distance,old_time));
        }

        else{
            workerArrayList = getWorkers(job_id,distance,old_time);
            workerArrayList.removeAll(getWorkers(job_id,distance,new_time));
        }
        return  workerArrayList;
    }

    public void load()
    {
        DatabaseReference loc_ref = FirebaseDatabase.getInstance().getReference("worker_location");
        workers.clear();
        loc_ref.addChildEventListener(new ChildEventListener() {


            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    Worker_Location worker_location =snapshot.getValue(Worker_Location.class);
                    if(isLocationAccepted(worker_location))
                    {
                        WorkerDao.workerReference workerReference = new WorkerDao.workerReference(worker_location.getWorker_id());
                        workerReference.addWorkerEventListener(new WorkerDao.workerReference.GetWorkerEventListener() {
                            @Override
                            public void WorkerExists(WorkerDbObj workerDbObj) {
                                if(isJobAccepted(workerDbObj.getJob_id())) {
                                    UserDao.UserReference userReference = new UserDao.UserReference(workerDbObj.getId());
                                    userReference.addUserEventListener(new UserDao.UserReference.GetUserEventListener() {
                                        @Override
                                        public void UserExists(User user) {
                                            Worker worker = new Worker();
                                            worker.InheritWorker(workerDbObj);
                                            worker.InheritUser(user);
                                            worker.setJob(getJobById(workerDbObj.getJob_id()));
                                            worker.setWorker_location(worker_location);
                                            double d = distance(location.getLatitude(),location.getLongitude(),worker_location.getLat(),worker_location.getLng());
                                            DecimalFormat df = new DecimalFormat("#.#");
                                            worker.setDistance(Double.valueOf(df.format(d)));
                                            add(worker);
                                        }
                                        @Override
                                        public void UserDoesntExist() {}
                                    });
                                } else {}
                            }
                            @Override
                            public void WorkerDoesntExist() {}
                        });
                    } else{}

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public Job getJobById(String job_id)
    {
        for (Job j: jobs) {
            if (j.getId().equals(job_id)){return j;}
        } return null;
    }

    public boolean isJobAccepted(String job_id)
    {
        for (Job j: jobs) {
            if (j.getId().equals(job_id)){return true;}
        }return false;
    }


    public boolean isLocationAccepted(Worker_Location wl)
    {
        if(distance(location.getLatitude(),location.getLongitude(),wl.getLat(),wl.getLng()) <= max_distance && compareTimeStamp(wl.getTimestamp()) <= max_time)
        {
            return  true;
        }
        return false;
    }

    public boolean isLocationAccepted(Worker_Location wl , double max_distance, long max_time)
    {
        if(distance(location.getLatitude(),location.getLongitude(),wl.getLat(),wl.getLng()) <= max_distance && compareTimeStamp(wl.getTimestamp()) <= max_time)
        {
            return  true;
        }
        return false;
    }

    public static long compareTimeStamp(long timestamp)
    {

        long diff = new Timestamp(System.currentTimeMillis()).getTime() - timestamp;
        long diffMinutes = diff / (60 * 1000);

        return diffMinutes;
    }

    public double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515; //Miles
        dist = dist * 1.609344; //Km
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public class DistanceComparator implements Comparator<Worker> {
        @Override
        public int compare(Worker w1, Worker w2) {
            return Double.compare(w1.getDistance(), w2.getDistance());
        }
    }

    private EntriesChangedEventListener mListener;
    public interface EntriesChangedEventListener
    {
        void onEntriesChanged();
    }
    public void setEntriesChangedEventListener(EntriesChangedEventListener eventListener)
    {
        this.mListener = eventListener;
    }
    public void callListener()
    {
        if(mListener!=null)
        {
            mListener.onEntriesChanged();
        }
    }





    private  ArrayList<WorkerEventListener> workerEventListener = new ArrayList<>();
    public interface WorkerEventListener
    {
        void onWorkerAdded(Worker worker);
    }
    public void addWorkerEventListener(WorkerEventListener eventListener)
    {
        this.workerEventListener.add(eventListener);
    }
    public void callWorkerListener(Worker worker)
    {
        if(!workerEventListener.isEmpty())
        {
            for (WorkerEventListener wlc : workerEventListener) {
                wlc.onWorkerAdded(worker);
            }
        }
    }

}





