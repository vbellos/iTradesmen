package com.vbellos.dev.itradesmen.Worker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.vbellos.dev.itradesmen.Client.HomeActivity;
import com.vbellos.dev.itradesmen.Dao.WorkerDao;
import com.vbellos.dev.itradesmen.Utilities.MessageNotificationService;
import com.vbellos.dev.itradesmen.Models.WorkerDbObj;
import com.vbellos.dev.itradesmen.R;
import com.vbellos.dev.itradesmen.User.DarkModePrefManager;
import com.vbellos.dev.itradesmen.User.LoginActivity;
import com.vbellos.dev.itradesmen.User.UserChatsFragment;
import com.vbellos.dev.itradesmen.User.UserSettingsFragment;

public class WorkerHomeActivity extends AppCompatActivity implements LocationListener {

    static final int REQ_LOC_CODE = 23;
    private FirebaseAuth fAuth;
    FirebaseUser fuser;
    WorkerDao workerDao = new WorkerDao();
    UserChatsFragment userChatsFragment;
    WorkerReviewsFragment workerReviewsFragment;
    UserSettingsFragment userSettingsFragment;
    WorkerHomeFragment workerHomeFragment;
    Fragment selected_fragment=null;
    String recreate;
    boolean notifications;
    private static WorkerHomeActivity activity;
    NotificationManagerCompat notification_manager;


    private BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_home);

        fAuth = FirebaseAuth.getInstance();
        fuser = fAuth.getCurrentUser();

        activity = this;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        notifications = prefs.getBoolean("notifications", false);
        MessageNotificationService.NotificationsPeriodicRequest(notifications, fuser.getUid());





        setDarkMode(getWindow());

        Bundle b = getIntent().getExtras();
        if(b!=null)
        {
            recreate = b.getString("recreate" , "false");
        }else{recreate = "false";}

        WorkerDao.workerReference workerReference = new WorkerDao.workerReference(fuser.getUid());
        workerReference.addWorkerEventListener(new WorkerDao.workerReference.GetWorkerEventListener() {
            @Override
            public void WorkerExists(WorkerDbObj workerDbObj) {

                InitActivity(workerDbObj);
            }

            @Override
            public void WorkerDoesntExist() {
                logout();
            }
        });


    }



    public void InitActivity(WorkerDbObj workerDbObj)
    {



        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        userSettingsFragment = new UserSettingsFragment(this);
        userSettingsFragment.setDarkModeChangedListener(new UserSettingsFragment.darkmodechanged() {
            @Override
            public void DarkModeCheckChanged() {
                DarkModePrefManager darkModePrefManager = new DarkModePrefManager(WorkerHomeActivity.this);
                darkModePrefManager.setDarkMode(!darkModePrefManager.isNightMode());
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

                getIntent().putExtra("recreate","true");
                recreate();

            }
        });
        userSettingsFragment.setNotificationsChanged(new UserSettingsFragment.NotificationsChanged() {
            @Override
            public void NotificationsChanged(Boolean state) {
                notifications= state;
                MessageNotificationService.NotificationsPeriodicRequest(notifications,fuser.getUid());
            }
        });
        workerHomeFragment = new WorkerHomeFragment(this);
        userChatsFragment = new UserChatsFragment();
        workerHomeFragment.setOnLocTrackStateChangedListener(new WorkerHomeFragment.LocTrackStateChanged() {
            @Override
            public void LocTrackStateChanged(Boolean state) {
                LocationTracker(state);
            }
        });
        workerReviewsFragment = new WorkerReviewsFragment(workerDbObj.getId());
        bottomNavigationView = findViewById(R.id.worker_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.worker_navigationHome:
                        selected_fragment = workerHomeFragment;
                        break;
                    case R.id.worker_navigationReviews:
                        selected_fragment = workerReviewsFragment;
                        break;
                    case R.id.worker_navigationChats:
                        selected_fragment = userChatsFragment;
                        break;
                    case R.id.worker_navigationMyProfile:
                        selected_fragment = userSettingsFragment;
                        break;
                }

                item.setChecked(true);
                if(selected_fragment!=null) {
                    fragmentManager.beginTransaction().replace(R.id.worker_fragment_container, selected_fragment).commit();
                }
                return false;
            }
        });

        if(recreate.equals("true")) {
            bottomNavigationView.setSelectedItemId(R.id.worker_navigationMyProfile);
        }else{
            bottomNavigationView.setSelectedItemId(R.id.worker_navigationHome);
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



    public void logout(View view)
    {
        MessageNotificationService.NotificationsPeriodicRequest(false,null);
        FirebaseAuth.getInstance().signOut();
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();
    }
    public void logout()
    {
        FirebaseAuth.getInstance().signOut();
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();
    }

    public void LocationTracker(Boolean enable)
    {
        requestPermission();
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if(enable) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 50, this);
        }
        else{
            locationManager.removeUpdates(this);
        }
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {
            workerDao.updateLocation(fuser.getUid(),location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        showDialogGPS();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
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
                WorkerHomeActivity.this.finish();

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
