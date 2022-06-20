package com.vbellos.dev.itradesmen.Client.ViewWorkers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.vbellos.dev.itradesmen.Dao.WorkerDao;
import com.vbellos.dev.itradesmen.Models.Worker_Location;
import com.vbellos.dev.itradesmen.Utilities.GlideApp;
import com.vbellos.dev.itradesmen.R;
import com.vbellos.dev.itradesmen.User.ViewUserProfileActivity;
import com.vbellos.dev.itradesmen.Models.Worker;
import com.vbellos.dev.itradesmen.Utilities.TinyDB;

import java.util.ArrayList;

public class viewWorkersMapFragment extends Fragment {


    private ArrayList<Worker> workers = new ArrayList<>();
    GoogleMap map;

    public viewWorkersMapFragment(){}

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {

            map = googleMap;
            if(Maplistener!=null){Maplistener.MapLoaded();}
            TinyDB tinyDB = new TinyDB(getContext());
            String search_key= "users_searches_"+ FirebaseAuth.getInstance().getCurrentUser().getUid();
            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(@NonNull Marker marker) {
                    Worker w = (Worker) marker.getTag();
                    ArrayList<String> searches = tinyDB.getListString(search_key);
                    if(searches == null){searches = new ArrayList<String>();}
                    if(searches.contains(w.getId())){ searches.remove(w.getId());}
                    searches.add(w.getId());
                    tinyDB.putListString(search_key,searches);
                    Intent i = new Intent(getContext() , ViewUserProfileActivity.class);
                    i.putExtra("id",w.getId());
                    getContext().startActivity(i);

                    return false;
                }
            });

        }
    };




    public  void InitView(double range,Location location)
    {
        LatLng myloc = new LatLng(location.getLatitude(), location.getLongitude());
        int width = this.getView().getWidth();
        getZoomForMetersWide(map,width,myloc,((int)range*1000));
    }

    public void LoadList(ArrayList<Worker> workerArrayList)
    {
        workers.clear();
        map.clear();
        AddAll(workerArrayList);
    }

    public void AddWorker(Worker worker)
    {

                MarkerOptions markerOptions = new MarkerOptions();
                LatLng latLng = new LatLng(worker.getWorker_location().getLat(), worker.getWorker_location().getLng());
                markerOptions.position(latLng);
                worker.setUserLocMarker(map.addMarker(markerOptions));
                worker.getUserLocMarker().setTag(worker);
                this.workers.add(worker);
                GlideApp.with(this)
                        .asBitmap()
                        .load(FirebaseStorage.getInstance().getReference().child("job_icons").child(worker.getJob().getIcon()))
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                worker.getUserLocMarker().setIcon(generateIcon(resource));
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                            }
                        });
                WorkerDao.workerLocReference workerLocReference = new WorkerDao.workerLocReference(worker.getId());
                workerLocReference.setLocationUpdateListener(new WorkerDao.workerLocReference.WorkerLocationUpdates() {
                    @Override
                    public void LocationChanged(Worker_Location worker_location) {
                        worker.getUserLocMarker().setPosition(new LatLng(worker_location.getLat(),worker_location.getLng()));
                    }
                });
    }

    public void RemoveWorker(Worker worker)
    {
        for (Worker w:workers) {
            if(w.getId().equals(worker.getId())){
                if (w.getUserLocMarker()!=null) {
                    w.getUserLocMarker().remove();
                }
            workers.remove(w);
            break;
            }
        }
    }

    public void RemoveAll(ArrayList<Worker> workerArrayList)
    {
        if(map!=null) {
            for (Worker w : workerArrayList) {
                RemoveWorker(w);
            }
        }
    }

    public void AddAll(ArrayList<Worker> workerArrayList)
    {

            for (Worker w : workerArrayList) {
                AddWorker(w);
            }

    }



    private BitmapDescriptor generateIcon(Bitmap resource)
    {
        int height =128;
        int width = 128;
        Bitmap b = Bitmap.createScaledBitmap(resource,width,height,false);
        return BitmapDescriptorFactory.fromBitmap(b);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_view_workers_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    public  void getZoomForMetersWide(GoogleMap googleMap, int mapViewWidth, LatLng latLngPoint, int desiredMeters) {
        DisplayMetrics metrics = this.getContext().getApplicationContext().getResources().getDisplayMetrics();
        float mapWidth = mapViewWidth / metrics.density;

        final int EQUATOR_LENGTH = 40075004;
        final int TIME_ANIMATION_MILIS = 1500;
        final double latitudinalAdjustment = Math.cos(Math.PI * latLngPoint.latitude / 180.0);
        final double arg = EQUATOR_LENGTH * mapWidth * latitudinalAdjustment / (desiredMeters * 256.0);
        double valToZoom = Math.log(arg) / Math.log(2.0);

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngPoint, Float.valueOf(String.valueOf(valToZoom))), TIME_ANIMATION_MILIS , null);
    }

    public interface MapLoadingState
    {
        void MapLoaded();
    }
    private MapLoadingState Maplistener;

    public void SetMapLoadingStateListener(MapLoadingState listener)
    {
        this.Maplistener = listener;
    }


}

