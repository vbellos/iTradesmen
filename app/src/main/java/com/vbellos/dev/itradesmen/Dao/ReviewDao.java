package com.vbellos.dev.itradesmen.Dao;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vbellos.dev.itradesmen.Models.Review;

public class ReviewDao {
    private static DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("reviews");

    public void addReview(Review review)
    {
        mDatabase.child(review.getWorkerID()).push().setValue(review);
    }


    public static class UserReviews
    {
        private String user_id;
        public UserReviews(String user_id)
        {
            this.user_id = user_id;
        }

        public interface UserReviewsListener
        {
            void ReviewAdded(Review review);
        }

        private UserReviewsListener listener;

        public void setUserReviewsListener(UserReviewsListener listener)
        {
            this.listener = listener;
            DatabaseReference dbref = FirebaseDatabase.getInstance().getReference().child("reviews");
            dbref.child(user_id).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    listener.ReviewAdded(snapshot.getValue(Review.class));
                }
                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                }
                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                }
                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }

    }

}
