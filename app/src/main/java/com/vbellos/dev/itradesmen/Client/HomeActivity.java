package com.vbellos.dev.itradesmen.Client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.vbellos.dev.itradesmen.Utilities.MessageNotificationService;
import com.vbellos.dev.itradesmen.User.UserChatsFragment;
import com.vbellos.dev.itradesmen.Utilities.GlideApp;
import com.vbellos.dev.itradesmen.R;
import com.vbellos.dev.itradesmen.User.DarkModePrefManager;
import com.vbellos.dev.itradesmen.User.LoginActivity;
import com.vbellos.dev.itradesmen.Models.User;
import com.vbellos.dev.itradesmen.Dao.UserDao;
import com.vbellos.dev.itradesmen.User.UserSettingsFragment;

public class HomeActivity extends AppCompatActivity implements LocationListener {

    static final int REQ_LOC_CODE = 23;
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    ImageView prof_image;
    TextView textView;
    Location location;

    LocationManager locationManager;




    UserSettingsFragment userSettingsFragment;

    Fragment selected_fragment=null;
    String recreate;
    boolean notifications;
    private static HomeActivity activity;


        private BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        notifications = prefs.getBoolean("notifications", false);

        MessageNotificationService.NotificationsPeriodicRequest(notifications,firebaseUser.getUid());

        activity = this;

        Bundle b = getIntent().getExtras();
        if(b!=null)
        {
            recreate = b.getString("recreate" , "false");
        }else{recreate = "false";}

        textView =  findViewById(R.id.clienthometextView4);
        prof_image = findViewById(R.id.home_profIMG);
        UserDao.UserReference  userReference = new UserDao.UserReference(firebaseUser.getUid());
        userReference.addUserEventListener(new UserDao.UserReference.GetUserEventListener() {
            @Override
            public void UserExists(User user) {

                 StorageReference imgRef = FirebaseStorage.getInstance().getReference().child("profile_pics").child(user.getPicture());
                 loadImage(imgRef);
                textView.setText(user.getName());


            }

            @Override
            public void UserDoesntExist() {
                logout(new View(HomeActivity.this));
            }
        });


        setDarkMode(getWindow());


        InitActivity();


    }


    public void InitActivity()
    {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();

        bottomNavigationView = findViewById(R.id.navigation);

        userSettingsFragment = new UserSettingsFragment(HomeActivity.this);




        LocationTrack();


        userSettingsFragment.setNotificationsChanged(new UserSettingsFragment.NotificationsChanged() {
            @Override
            public void NotificationsChanged(Boolean state) {
                notifications = state;
                MessageNotificationService.NotificationsPeriodicRequest(notifications,firebaseUser.getUid());
            }
        });

        userSettingsFragment.setDarkModeChangedListener(new UserSettingsFragment.darkmodechanged() {
            @Override
            public void DarkModeCheckChanged() {
                DarkModePrefManager darkModePrefManager = new DarkModePrefManager(HomeActivity.this);
                darkModePrefManager.setDarkMode(!darkModePrefManager.isNightMode());
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

                getIntent().putExtra("recreate","true");
                recreate();
            }
        });


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigationMyProfile:
                        selected_fragment = userSettingsFragment;
                        break;
                    case R.id.navigationChats:
                        selected_fragment = new UserChatsFragment();
                        break;
                    case R.id.navigationHome:
                        ClientHomeFragment clientHomeFragment = new ClientHomeFragment();
                        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                            if (location == null) { LocationTrack(); }
                            clientHomeFragment.setLocation(location);
                            selected_fragment = clientHomeFragment;

                        }
                        break;
                    case R.id.navigationSearch:
                        SearchWorkersFragment searchWorkersFragment = new SearchWorkersFragment();
                        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                            if (location == null) {
                                LocationTrack();
                            }
                            searchWorkersFragment.setLocation(location);
                            selected_fragment = searchWorkersFragment;
                        }
                        break;
                }
                item.setChecked(true);

                if(selected_fragment!=null) {
                    fragmentManager.beginTransaction().replace(R.id.main_fragment_container, selected_fragment).commit();
                }
                return false;
            }
        });

        if(recreate.equals("true")) {
            bottomNavigationView.setSelectedItemId(R.id.navigationMyProfile);
        }else{
            bottomNavigationView.setSelectedItemId(R.id.navigationHome);
        }

    }

    public void logout(View view)
    {
        MessageNotificationService.NotificationsPeriodicRequest(false,null);
        FirebaseAuth.getInstance().signOut();
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();
    }




    public  void loadImage(StorageReference ref)
    {
        GlideApp.with(getApplicationContext() /* context */)
                .load(ref)
                .error(R.mipmap.ic_launcher)
                .placeholder(R.drawable.default_avatar)
                .into(prof_image);
    }




    //create a seperate class file, if required in multiple activities
    public void setDarkMode(Window window){
        if(new DarkModePrefManager(this).isNightMode()){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            changeStatusBar(0,window);
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            changeStatusBar(1,window);
        }
    }
    public void changeStatusBar(int mode, Window window){
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(this.getResources().getColor(R.color.contentBodyColor));
            //white mode
            if(mode==1){
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
    }

    private void requestPermission(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQ_LOC_CODE);
        }
    }

    public void LocationTrack()
    {
        requestPermission();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            Criteria criteria = new Criteria();
            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                String bestProvider = locationManager.getBestProvider(criteria, true);
                Location location = locationManager.getLastKnownLocation(bestProvider);
                if (location != null) {
                    this.location = location;

                    locationManager.removeUpdates(this);

                }
            }


    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }

    @Override
    public void onFlushComplete(int requestCode) {
        LocationListener.super.onFlushComplete(requestCode);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        LocationListener.super.onStatusChanged(provider, status, extras);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

        showDialogGPS();
    }


    private void showDialogGPS() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Enable GPS");
        builder.setMessage("Please enable GPS");
        builder.setInverseBackgroundForced(true);
        builder.setPositiveButton("Enable", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                startActivity(
                        new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });
        builder.setNegativeButton("Ignore", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                HomeActivity.this.finish();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}