package com.vbellos.dev.itradesmen.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.vbellos.dev.itradesmen.R;

public class RegisterActivity extends AppCompatActivity {

    Button register;
    EditText email,passwd,confpasswd;
    private FirebaseAuth fAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.reg_emailTXT);
        passwd = findViewById(R.id.reg_passwdTXT);
        confpasswd = findViewById(R.id.reg_confpasswdTXT);

        register = findViewById(R.id.reg_signupBTN);
        register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (email.getText().toString().isEmpty() || passwd.getText().toString().isEmpty()) {
                    new AlertDialog.Builder(RegisterActivity.this)

                            .setMessage("empty fields")
                            .setCancelable(true)
                            .show();
                } else if (passwd.getText().toString().length() < 8) {
                    new AlertDialog.Builder(RegisterActivity.this)
                            .setTitle("Weak Pass")
                            .setMessage("Weak pass")
                            .setCancelable(true)
                            .show();
                }
                    else if (!passwd.getText().toString().equals(confpasswd.getText().toString()) )
                    {
                        new AlertDialog.Builder(RegisterActivity.this)
                                .setTitle("Passwords don't match")
                                .setMessage("Passwords don't match")
                                .setCancelable(true)
                                .show();
                    }
                 else {
                    fAuth.createUserWithEmailAndPassword(email.getText().toString(), passwd.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "Reg Succ", Toast.LENGTH_LONG).show();
                                        FinishProfile();

                                    } else {
                                        new AlertDialog.Builder(RegisterActivity.this)
                                                .setTitle("DB error")
                                                .setMessage("DB error")
                                                .setCancelable(true)
                                                .show();
                                    }
                                }
                            });
                }
            }
        });
    }


    public void FinishProfile()
    {
        Intent i = new Intent(this, FinishUserProfileActivity.class);
        startActivity(i);
        finish();

    }

}
