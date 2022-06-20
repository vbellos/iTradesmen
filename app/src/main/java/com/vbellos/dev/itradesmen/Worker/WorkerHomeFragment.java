package com.vbellos.dev.itradesmen.Worker;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.vbellos.dev.itradesmen.Dao.ReviewDao;
import com.vbellos.dev.itradesmen.Dao.UserDao;
import com.vbellos.dev.itradesmen.Dao.WorkerDao;
import com.vbellos.dev.itradesmen.Models.Review;
import com.vbellos.dev.itradesmen.Utilities.GlideApp;
import com.vbellos.dev.itradesmen.Models.User;
import com.vbellos.dev.itradesmen.Models.Worker_Location;
import com.vbellos.dev.itradesmen.R;
import com.vbellos.dev.itradesmen.User.CurrentLocMapsFragment;

import java.sql.Timestamp;
import java.util.ArrayList;

public class WorkerHomeFragment extends Fragment{


    Context context;
    ImageView prof_image;
    TextView nameTXT,last_locTXT,lat_updateTXT,reviews_count;
    FrameLayout mapView;
    CurrentLocMapsFragment mapsFragment ;
    RatingBar ratingBar;




    private Switch locationTrackerSwitch;
    public WorkerHomeFragment(Context context) {
        this.context = context;
    }
    public WorkerHomeFragment( ) {
        this.context = context;
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_worker_home, container, false);
        locationTrackerSwitch = view.findViewById(R.id.locTrackerModeSwitch);
        prof_image = view.findViewById(R.id.worker_home_image);
        nameTXT = view.findViewById(R.id.worker_home_NameTXT);
        last_locTXT = view.findViewById(R.id.worker_home_last_loc);
        lat_updateTXT = view.findViewById(R.id.worker_home_last_loc_update);
        mapView = view.findViewById(R.id.client_home_map_frag);
        ratingBar = view.findViewById(R.id.worker_home_ratingBar);
        reviews_count = view.findViewById(R.id.worker_home_reviews_count);
        mapsFragment = new CurrentLocMapsFragment();

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(mapView.getId(),mapsFragment).commit();


        InitWorker();

        return view;
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

    public void InitWorker()
    {

        UserDao.UserReference userReference = new UserDao.UserReference(FirebaseAuth.getInstance().getCurrentUser().getUid());
        userReference.addUserEventListener(new UserDao.UserReference.GetUserEventListener() {
            @Override
            public void UserExists(User user) {
                nameTXT.setText(user.getName());
                StorageReference imgRef = FirebaseStorage.getInstance().getReference().child("profile_pics").child(user.getPicture());
                loadImage(imgRef);
                ArrayList<Float> Ratings = new ArrayList<Float>();
                ReviewDao.UserReviews userReviews = new ReviewDao.UserReviews(user.getId());
                userReviews.setUserReviewsListener(new ReviewDao.UserReviews.UserReviewsListener() {
                    @Override
                    public void ReviewAdded(Review review) {
                        Ratings.add(review.getRating());
                        calc_rating(Ratings);
                    }
                });
            }

            @Override
            public void UserDoesntExist() {

            }
        });

        WorkerDao.workerLocReference workerLocReference = new WorkerDao.workerLocReference(FirebaseAuth.getInstance().getCurrentUser().getUid());
        workerLocReference.setLocationUpdateListener(new WorkerDao.workerLocReference.WorkerLocationUpdates() {
            @Override

            public void LocationChanged(Worker_Location worker_location) {
                if(worker_location!=null)
                {LatLng last_location = new LatLng(worker_location.getLat(),worker_location.getLng());
                    mapsFragment.setLast_location(last_location);
                    last_locTXT.setText(worker_location.getLat() + " , " +worker_location.getLng());
                    Timestamp time = new Timestamp(worker_location.getTimestamp());
                    lat_updateTXT.setText(time.toString());}

            }
        });


        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);


        locationTrackerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {


                SharedPreferences.Editor editor =prefs.edit();
                editor.putBoolean("loc_track", b);
                editor.apply();
                if(locTrackStateChangedListener!= null)
                {locTrackStateChangedListener.LocTrackStateChanged(b);}
            }
        });
        boolean loc_track = prefs.getBoolean("loc_track", false);
        locationTrackerSwitch.setChecked(loc_track);

    }


    public void calc_rating(ArrayList<Float> Ratings)
    {
        float total = 0;
        for(Float r:Ratings){total+=r;}
        float avg = total/Ratings.size();
        ratingBar.setRating(avg);
        reviews_count.setText(Ratings.size() + " Reviews");
    }

    public interface LocTrackStateChanged
    {
        void LocTrackStateChanged(Boolean state);
    }

    private LocTrackStateChanged locTrackStateChangedListener;
    public void setOnLocTrackStateChangedListener(LocTrackStateChanged listener)
    {
        this.locTrackStateChangedListener = listener;



    }
}