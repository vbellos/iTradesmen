package com.vbellos.dev.itradesmen.Dao;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vbellos.dev.itradesmen.Models.User;

import java.sql.Timestamp;
import java.util.ArrayList;

public class UserDao {

    private static DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users");


    public void adduser(User user)
    {
        mDatabase.child(user.getId()).setValue(user);
    }

    public void updateuser(User user)
    {
        user.setTimestamp(new Timestamp(System.currentTimeMillis()).getTime());
        mDatabase.child(user.getId()).setValue(user);
    }

    public void updateTimestamp(@NonNull User user)
    {
        mDatabase.child(user.getId()).child("timestamp").setValue(new Timestamp(System.currentTimeMillis()));
    }
    public void updateTimestamp(@NonNull String user_id)
    {
        mDatabase.child(user_id).child("timestamp").setValue(new Timestamp(System.currentTimeMillis()).getTime());
    }




    public static class UserReference {
        String user_id;
        public UserReference(String user_id)
        {this.user_id = user_id;}

        private ArrayList<GetUserEventListener> UserListener = new ArrayList<>();
        public interface GetUserEventListener
        {
            void UserExists(User user);
            void UserDoesntExist();
        }
        public void addUserEventListener(GetUserEventListener eventListener)
        {
            UserListener.add(eventListener);
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChild(user_id))
                    {
                        UserListener.get(UserListener.indexOf(eventListener)).UserExists(snapshot.child(user_id).getValue(User.class));
                    }
                    else{UserListener.get(UserListener.indexOf(eventListener)).UserDoesntExist();}
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }




}
