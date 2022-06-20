package com.vbellos.dev.itradesmen.User;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.vbellos.dev.itradesmen.Dao.UserDao;
import com.vbellos.dev.itradesmen.Utilities.GlideApp;
import com.vbellos.dev.itradesmen.Models.User;
import com.vbellos.dev.itradesmen.R;
import com.vbellos.dev.itradesmen.Utilities.MessageNotificationService;

public class UserSettingsFragment extends Fragment {

    private Switch darkModeSwitch, notificationSwitch;
    ImageView prof_image;
    TextView textView;
    View currentView;
    TextView changePass,edit_prof,edit_prof2;
    Context context;

   public UserSettingsFragment() {
        // Required empty public constructor
   }

    public UserSettingsFragment(Context context) {
        this.context = context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_user_settings, container, false);
        currentView = view;


        if(new DarkModePrefManager(getContext()).isNightMode()){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        //function for enabling dark mode
        setDarkModeSwitch();
        SetNotificationSwitch();

        edit_prof = view.findViewById(R.id.edit_prof);
        edit_prof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editProf();
            }
        });
        edit_prof2 = view.findViewById(R.id.edit_prof_2);
        edit_prof2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editProf();
            }
        });
        changePass = view.findViewById(R.id.changePassTXT);
        changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(),ChangePassActivity.class);
                startActivity(i);

            }
        });
        textView =  view.findViewById(R.id.usernameTextView);
        prof_image = view.findViewById(R.id.profileCircleImageView);
        UserDao.UserReference  userReference = new UserDao.UserReference(FirebaseAuth.getInstance().getCurrentUser().getUid());
        userReference.addUserEventListener(new UserDao.UserReference.GetUserEventListener() {
            @Override
            public void UserExists(User user) {

                StorageReference imgRef = FirebaseStorage.getInstance().getReference().child("profile_pics").child(user.getPicture());
                loadImage(imgRef);
                textView.setText(user.getName());

            }

            @Override
            public void UserDoesntExist() {
                getActivity().finish();
            }
        });

        return view;
    }


    private void setDarkModeSwitch(){
        darkModeSwitch = currentView.findViewById(R.id.darkModeSwitch);
        darkModeSwitch.setChecked(new DarkModePrefManager(getContext()).isNightMode());
        darkModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(darkmodechanged !=null){
                    darkmodechanged.DarkModeCheckChanged();}


            }
        });
    }

    private void SetNotificationSwitch()
    {
        notificationSwitch = currentView.findViewById(R.id.settings_notification_switch);
        if(context!=null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            boolean notifications = prefs.getBoolean("notifications", false);
            notificationSwitch.setChecked(notifications);
            notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("notifications", b);
                    editor.apply();
                    if (notificationsChanged != null) {
                        notificationsChanged.NotificationsChanged(b);
                    }
                }
            });
        }


    }

    public interface NotificationsChanged
    {
        void NotificationsChanged(Boolean state);
    }
    NotificationsChanged notificationsChanged;
    public void setNotificationsChanged(NotificationsChanged listener)
    {
        notificationsChanged= listener;
    }

    public interface darkmodechanged
    {
        void DarkModeCheckChanged();
    }
    darkmodechanged darkmodechanged;
    public void setDarkModeChangedListener(darkmodechanged darkModeChangedListener)
    {
        darkmodechanged = darkModeChangedListener;}


    public  void loadImage(StorageReference ref) {
        if (getContext() != null) {

            GlideApp.with(getContext() /* context */)
                    .load(ref)
                    .error(R.mipmap.ic_launcher)
                    .placeholder(R.drawable.default_avatar)
                    .into(prof_image);
        }
    }



    public void editProf()
    {
        Intent i = new Intent(getContext(), EditUserProfileActivity.class);
        startActivity(i);
    }






}