package com.vbellos.dev.itradesmen.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.auth.FirebaseAuthCredentialsProvider;
import com.vbellos.dev.itradesmen.R;

public class ChangePassActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    EditText cur_pass,new_pass,new_pass2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        cur_pass=findViewById(R.id.change_pass_editTextPassword);
        new_pass=findViewById(R.id.change_pass_editTextNewPassword);
        new_pass2=findViewById(R.id.change_pass_editTextNewPassword2);

    }




    public void changePass(View view)
    {
        boolean fields_not_empty = !cur_pass.getText().toString().isEmpty() && !new_pass2.getText().toString().isEmpty()  && !new_pass.getText().toString().isEmpty();
        boolean fiels_match =new_pass2.getText().toString().equals(new_pass.getText().toString());

        if(fields_not_empty)
        {
            if (fiels_match){


                AuthCredential credential =  EmailAuthProvider.getCredential(firebaseUser.getEmail(),cur_pass.getText().toString());
                firebaseUser.reauthenticate(credential).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        firebaseUser.updatePassword(new_pass.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                new AlertDialog.Builder(ChangePassActivity.this)
                                        .setMessage("Password changed successfully!")
                                        .setCancelable(true)
                                        .show();
                                ChangePassActivity.this.finish();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                new AlertDialog.Builder(ChangePassActivity.this)
                                        .setMessage("Failed to update password \n Try again later!")
                                        .setCancelable(true)
                                        .show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        new AlertDialog.Builder(ChangePassActivity.this)
                                .setMessage("Failed to authenticate")
                                .setCancelable(true)
                                .show();
                    }
                });
            }
            else {
                new AlertDialog.Builder(ChangePassActivity.this)
                        .setMessage("Passwords don't match")
                        .setCancelable(true)
                        .show();
            }
        }
        else{
            new AlertDialog.Builder(ChangePassActivity.this)
                    .setMessage("Empty fields")
                    .setCancelable(true)
                    .show();
        }

    }
}