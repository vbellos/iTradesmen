package com.vbellos.dev.itradesmen.Client.ViewWorkers;

import android.location.Location;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vbellos.dev.itradesmen.Adapters.WorkersRecyclerViewAdapter;
import com.vbellos.dev.itradesmen.Models.Job;
import com.vbellos.dev.itradesmen.R;

import java.util.List;

import javax.annotation.Nullable;


public class viewWorkersCardFragment extends Fragment {



    private WorkersRecyclerViewAdapter rva;
    private RecyclerView recyclerView;


    public viewWorkersCardFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater,@Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_workers_card, container, false);
        recyclerView = view.findViewById(R.id.viewWorkersRecyclerView);


        rva = new WorkersRecyclerViewAdapter(getContext());

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.hasFixedSize();
        recyclerView.setAdapter(rva);

        return view;
    }


    public RecyclerView getRecyclerView()
    {
        return recyclerView;
    }


}