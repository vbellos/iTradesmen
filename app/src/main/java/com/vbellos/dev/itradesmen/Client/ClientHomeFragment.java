package com.vbellos.dev.itradesmen.Client;

import android.location.Location;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.vbellos.dev.itradesmen.Adapters.LastSearchRecyclerAdapter;
import com.vbellos.dev.itradesmen.Dao.UserDao;
import com.vbellos.dev.itradesmen.Utilities.GlideApp;
import com.vbellos.dev.itradesmen.Models.User;
import com.vbellos.dev.itradesmen.R;
import com.vbellos.dev.itradesmen.User.CurrentLocMapsFragment;
import com.vbellos.dev.itradesmen.Utilities.TinyDB;

import java.util.ArrayList;
import java.util.Collections;

public class ClientHomeFragment extends Fragment {


    ImageView prof_image,clear_searches;
    TextView nameTXT;
    FrameLayout mapView;
    CurrentLocMapsFragment mapsFragment ;
    RecyclerView recyclerView;

    public void setLocation(Location location) {
        this.location = location;
        if(mapsFragment!=null) {
            mapsFragment.setLast_location(new LatLng(location.getLatitude(), location.getLongitude()));
        }
    }

    Location location;

    public ClientHomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_client_home, container, false);
        prof_image = view.findViewById(R.id.client_home_image);
        nameTXT = view.findViewById(R.id.client_home_NameTXT);
        mapView = view.findViewById(R.id.client_home_map_frag);
        mapsFragment = new CurrentLocMapsFragment();
        recyclerView = view.findViewById(R.id.client_home_recent_recycler);
        clear_searches = view.findViewById(R.id.client_home_clear);
        clear_searches.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TinyDB tinyDB = new TinyDB(getContext());
                String search_key= "users_searches_"+ FirebaseAuth.getInstance().getCurrentUser().getUid();
                tinyDB.putListString(search_key,new ArrayList<>());
                loadSearches();

            }
        });

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(mapView.getId(),mapsFragment).commit();
        if(location!=null){setLocation(location);}

        loadSearches();
        InitClient();
        return view;
    }

    public void InitClient()
    {
        UserDao.UserReference userReference = new UserDao.UserReference(FirebaseAuth.getInstance().getCurrentUser().getUid());
        userReference.addUserEventListener(new UserDao.UserReference.GetUserEventListener() {
            @Override
            public void UserExists(User user) {
                nameTXT.setText(user.getName());
                StorageReference imgRef = FirebaseStorage.getInstance().getReference().child("profile_pics").child(user.getPicture());
                loadImage(imgRef);
            }

            @Override
            public void UserDoesntExist() {
            }
        });
    }

    public  void loadImage(StorageReference ref) {
        if (getContext() != null) {

            GlideApp.with(getContext() /* context */)
                    .load(ref)
                    .error(R.mipmap.ic_launcher)
                    .placeholder(R.drawable.default_avatar)
                    .into(prof_image);
        }
    }

    public void loadSearches()
    {
        TinyDB tinyDB = new TinyDB(getContext());
        String search_key= "users_searches_"+ FirebaseAuth.getInstance().getCurrentUser().getUid();
        ArrayList<String> searches = tinyDB.getListString(search_key);
        if(searches == null){searches = new ArrayList<String>();}
        Collections.reverse(searches);
        //tinyDB.putListString(search_key,new ArrayList<String>());
        LastSearchRecyclerAdapter rva = new LastSearchRecyclerAdapter(searches,getContext());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.hasFixedSize();
        recyclerView.setAdapter(rva);
    }
}