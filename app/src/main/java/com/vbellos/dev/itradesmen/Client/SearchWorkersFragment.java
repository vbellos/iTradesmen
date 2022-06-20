package com.vbellos.dev.itradesmen.Client;

import static android.content.Context.LOCATION_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.vbellos.dev.itradesmen.Adapters.JobSpinnerAdapter;
import com.vbellos.dev.itradesmen.Client.ViewWorkers.ViewWorkersPagerAdapter;
import com.vbellos.dev.itradesmen.Dao.JobDao;
import com.vbellos.dev.itradesmen.Models.Job;
import com.vbellos.dev.itradesmen.R;
import com.vbellos.dev.itradesmen.User.DarkModePrefManager;

import java.util.ArrayList;
import java.util.List;


public class SearchWorkersFragment extends Fragment {



    List<Job> Jobs = new ArrayList<Job>();
    RecyclerView recyclerView;
    SeekBar distance , time;
    TextView distanceTXT , timeTXT;

    JobDao jobDao;
    String selected_job;
    Spinner jview;
    JobSpinnerAdapter jobAdapter;

    long max_time = 60;
    double max_distance = 5;

    Location current_location = null;

    TabLayout tabLayout;

    ViewPager2 pager;

    ViewWorkersPagerAdapter w_adapter;

    View current_view;

    public SearchWorkersFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static SearchWorkersFragment newInstance() {
        SearchWorkersFragment fragment = new SearchWorkersFragment();
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_search_workers, container, false);
        current_view = view;

        recyclerView = view.findViewById(R.id.viewWorkersRecycler);
        InitJobSpinner();


        distance = view.findViewById(R.id.distanceSlider);
        distance.setEnabled(false);
        time = view.findViewById(R.id.timeSlider);
        time.setEnabled(false);

        tabLayout = view.findViewById(R.id.tabLayout);
        pager = view.findViewById(R.id.viewPager);
        pager.setUserInputEnabled(false);


        return view;
    }


    public void InitJobSpinner()
    {

        jview = current_view.findViewById(R.id.viewJobsspinner);


        jobDao = new JobDao();
        jobDao.addJobsChangedEventListener(new JobDao.JobsChangedEventListener() {
            @Override
            public void JobsChanged(List<Job> jobList) {
                Jobs = jobList;
                jobAdapter=new JobSpinnerAdapter(getContext(),jobList);
                jview.setAdapter(jobAdapter);
                selected_job = jobList.get(0).getId();

                //RequestLocation();
                if(current_location!=null)
                {InitPager();
                    InitSliders();}
                else{RequestLocation();}
            }
        });
        jview.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selected_job = jobAdapter.getJob(i).getId();
                if(pager.getAdapter()!=null)
                {
                    ((ViewWorkersPagerAdapter)pager.getAdapter()).updateJob(selected_job);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selected_job = null;
            }
        });
    }

    public void InitSliders()
    {

        time.setEnabled(true);
        distance.setEnabled(true);

        distanceTXT = current_view.findViewById(R.id.view_workers_distanceTXT);
        distance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                max_distance = i;
                distanceTXT.setText(i + "Km");
                if(pager.getAdapter()!=null)
                {
                    ((ViewWorkersPagerAdapter)pager.getAdapter()).updateDistance(max_distance);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        timeTXT = current_view.findViewById(R.id.view_workers_timeTXT);
        time.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                max_time = i*60;
                timeTXT.setText(i+ "h");
                if(pager.getAdapter()!=null)
                {
                    ((ViewWorkersPagerAdapter)pager.getAdapter()).updateTime(max_time);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }


    public void setLocation(Location location)
    {
        if (location == null) {
            Toast.makeText(getContext(), "GPS signal not found", Toast.LENGTH_SHORT).show();
        }
        else
        {
            current_location = location;
            //InitJobSpinner();
            if(pager!= null && time!=null && distance!=null) {
                InitPager();
                InitSliders();
            }
        }
    }

    public void InitPager()
    {
        if(current_location!= null && Jobs!= null && !Jobs.isEmpty()) {
            w_adapter = new ViewWorkersPagerAdapter(getParentFragmentManager()  ,getLifecycle(), max_time,max_distance,selected_job,current_location,Jobs);
            pager.setAdapter(w_adapter);
            pager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);

            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    pager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                }
            });
        }
    }

    public void RequestLocation()
    {
        ((HomeActivity) getActivity()).LocationTrack();
    }


}