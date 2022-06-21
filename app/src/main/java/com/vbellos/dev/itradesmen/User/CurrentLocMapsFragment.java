package com.vbellos.dev.itradesmen.User;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.vbellos.dev.itradesmen.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class CurrentLocMapsFragment extends Fragment {

    LatLng last_location;
    GoogleMap map;
    Marker userlocmarker;
    int zoom = 12;



    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            map = googleMap;
            map.getUiSettings().setAllGesturesEnabled(false);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(new LatLng(0,0));
            userlocmarker = map.addMarker(markerOptions);
            if(last_location!= null){
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(last_location,zoom);
                map.moveCamera(cameraUpdate);
                userlocmarker.setPosition(last_location);
            }
        }
    };

    public void setLast_location(LatLng location)
    {
        last_location=location;
        if(map!= null){CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(last_location,zoom);
            map.animateCamera(cameraUpdate);
            userlocmarker.setPosition(last_location);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_worker_current_loc_maps, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.current_loc_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

    }

}