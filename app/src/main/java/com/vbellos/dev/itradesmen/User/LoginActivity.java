package com.vbellos.dev.itradesmen.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.vbellos.dev.itradesmen.Client.HomeActivity;
import com.vbellos.dev.itradesmen.Dao.UserDao;
import com.vbellos.dev.itradesmen.Models.User;
import com.vbellos.dev.itradesmen.R;
import com.vbellos.dev.itradesmen.Dao.WorkerDao;
import com.vbellos.dev.itradesmen.Worker.WorkerHomeActivity;

public class LoginActivity extends AppCompatActivity {
    EditText email,password;
    private FirebaseAuth fAuth;
    FirebaseUser fuser;
    WorkerDao workerDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setDarkMode(getWindow());

        email = findViewById(R.id.change_pass_editTextPassword);
        password = findViewById(R.id.change_pass_editTextNewPassword2);

        fAuth = FirebaseAuth.getInstance();
        fuser = fAuth.getCurrentUser();

        workerDao = new WorkerDao();

        if(fuser!=null){
            Redirect();
        }

    }
    public void login(View view)
    {

        if(!email.getText().toString().isEmpty() && !password.getText().toString().isEmpty()){
            fAuth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Logged in Successfully", Toast.LENGTH_LONG).show();
                                Redirect();
                            } else {
                                new AlertDialog.Builder(LoginActivity.this).setTitle("Authentication Failure")
                                        .setMessage("Wrong Credentials").setCancelable(true).show();
                            }

                        }});
        }else{
            new AlertDialog.Builder(LoginActivity.this).setTitle("Authentication Failure")
                    .setMessage("Empty Fields").setCancelable(true).show();
        }
    }


    public void register(View view)
    {
        Intent i = new Intent(this,RegisterActivity.class);
        startActivity(i);
    }

    public void FinishProfile()
    {
        Intent i = new Intent(this, FinishUserProfileActivity.class);
        startActivity(i);
        finish();

    }

    public void UserHome()
    {
        Intent i = new Intent(this, HomeActivity.class);
        startActivity(i);
        finish();

    }

    public void WorkerHome()
    {
        Intent i = new Intent(this, WorkerHomeActivity.class);
        startActivity(i);
        finish();

    }

    public void Redirect()
    {
        fuser = fAuth.getCurrentUser();
        UserDao.UserReference userReference = new UserDao.UserReference(fuser.getUid());
        userReference.addUserEventListener(new UserDao.UserReference.GetUserEventListener() {
            @Override
            public void UserExists(User user) {
                if (user.isWorker()) {
                    WorkerHome();
                } else {

                    UserHome();
                }
            }
            @Override
            public void UserDoesntExist() {
                FinishProfile();
            }
        });
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



}
