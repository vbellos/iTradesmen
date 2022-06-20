package com.vbellos.dev.itradesmen.Client.ViewWorkers;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.vbellos.dev.itradesmen.Adapters.WorkersRecyclerViewAdapter;
import com.vbellos.dev.itradesmen.Models.Job;
import com.vbellos.dev.itradesmen.Models.Worker;

import java.util.ArrayList;
import java.util.List;

public class ViewWorkersPagerAdapter extends FragmentStateAdapter {

    private long max_time;
    private double max_distance;
    private String selected_job;
    private Location current_location;
    private List<Job> jobs;
    boolean mapLoaded = false;
    private  WorkersListLoader workersListLoader;
    private ArrayList<Worker> workers = new ArrayList<Worker>();



    viewWorkersCardFragment viewWorkersCardFragment;
    viewWorkersMapFragment viewWorkersMapFragment;

    public ViewWorkersPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, long max_time, double max_distance, String selected_job, Location current_location, List<Job> jobs) {
        super(fragmentManager, lifecycle);
        this.max_time = max_time;
        this.max_distance = max_distance;
        this.selected_job = selected_job;
        this.current_location = current_location;
        this.jobs = jobs;

        viewWorkersCardFragment = new viewWorkersCardFragment();

        viewWorkersMapFragment = new viewWorkersMapFragment();

        this.workersListLoader = new WorkersListLoader(current_location, jobs);

        workersListLoader.addWorkerEventListener(new WorkersListLoader.WorkerEventListener() {
            @Override
            public void onWorkerAdded(Worker worker) {
                AddWorker(worker);
            }
        });

        viewWorkersMapFragment.SetMapLoadingStateListener(new viewWorkersMapFragment.MapLoadingState() {
            @Override
            public void MapLoaded() {
                mapLoaded = true;
                viewWorkersMapFragment.InitView(max_distance,current_location);
                viewWorkersMapFragment.LoadList(workers);
            }
        });


    }



    @Override
    public int getItemCount() {
        return 2;
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return viewWorkersCardFragment;
            case 1:
                return viewWorkersMapFragment;
        }
        return null;
    }



    public void updateJob(String selected_job)
    {
        this.selected_job =selected_job;
        workers.clear();
        workers.addAll(workersListLoader.getWorkers(selected_job,max_distance,max_time));
        if(viewWorkersCardFragment.getRecyclerView()!=null)
        {
            ((WorkersRecyclerViewAdapter)viewWorkersCardFragment.getRecyclerView().getAdapter()).LoadList(workers);
        }

        if(mapLoaded) {
            viewWorkersMapFragment.LoadList(workers);
        }
    }
    public void updateDistance(double new_distance){

        ArrayList<Worker> workerArrayList = workersListLoader.getWorkersByNewDistance(selected_job,max_distance,new_distance,max_time);
        if(new_distance > max_distance)
        {workers.addAll(workerArrayList);}
        else{workers.removeAll(workerArrayList);}
        this.max_distance = new_distance;
        if(mapLoaded) {
            viewWorkersMapFragment.InitView(max_distance,current_location
            );}


    }
    public void updateTime(long new_time){

        ArrayList<Worker> workerArrayList = workersListLoader.getWorkersByNewTime(selected_job,max_distance,max_time,new_time);
        if (new_time > max_time)
        {AddAll(workerArrayList);}
        else{RemoveAll(workerArrayList);}
        this.max_time = new_time;

    }

    public void AddWorker(Worker worker)
    {
        if(workersListLoader.isLocationAccepted(worker.getWorker_location() , max_distance,max_time) && worker.getJob().getId().equals(selected_job)) {
            this.workers.add(worker);
            if(viewWorkersCardFragment.getRecyclerView()!=null) {
                ((WorkersRecyclerViewAdapter) viewWorkersCardFragment.getRecyclerView().getAdapter()).AddWorker(worker);
            }
            if (mapLoaded) {
                viewWorkersMapFragment.AddWorker(worker);
            }
        }
    }

    public void AddAll(ArrayList<Worker> workerArrayList)
    {
        workers.addAll(workerArrayList);
        if(viewWorkersCardFragment.getRecyclerView()!=null) {
            ((WorkersRecyclerViewAdapter) viewWorkersCardFragment.getRecyclerView().getAdapter()).AddAll(workerArrayList);
        }
        if(mapLoaded) {
            viewWorkersMapFragment.AddAll(workerArrayList);
        }
    }

    public void RemoveAll(ArrayList<Worker> workerArrayList)
    {

        for(Worker worker: workerArrayList)
        {RemoveWorker(worker);}
        if(viewWorkersCardFragment.getRecyclerView()!=null) {
            ((WorkersRecyclerViewAdapter) viewWorkersCardFragment.getRecyclerView().getAdapter()).RemoveAll(workerArrayList);
        }
        if(mapLoaded) {
            viewWorkersMapFragment.RemoveAll(workerArrayList);
        }
    }

    public void RemoveWorker(Worker worker)
    {
        this.workers.remove(worker);

        if((RecyclerView)viewWorkersCardFragment.getRecyclerView()!=null){
            ((WorkersRecyclerViewAdapter)viewWorkersCardFragment.getRecyclerView().getAdapter()).RemoveWorker(worker);
        }

        if(mapLoaded) {
            viewWorkersMapFragment.RemoveWorker(worker);
        }
    }



}
