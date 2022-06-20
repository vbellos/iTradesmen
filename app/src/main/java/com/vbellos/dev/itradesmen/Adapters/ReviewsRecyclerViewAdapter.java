package com.vbellos.dev.itradesmen.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.vbellos.dev.itradesmen.Dao.UserDao;
import com.vbellos.dev.itradesmen.Utilities.GlideApp;
import com.vbellos.dev.itradesmen.Models.Review;
import com.vbellos.dev.itradesmen.Models.User;
import com.vbellos.dev.itradesmen.R;


import java.util.ArrayList;

public class ReviewsRecyclerViewAdapter extends RecyclerView.Adapter<ReviewsRecyclerViewAdapter.ViewHolder>{

    private Context context;
    private ArrayList<Review> reviews =new ArrayList<>();

    public ReviewsRecyclerViewAdapter(Context context) {
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item= LayoutInflater.from(parent.getContext()).inflate(R.layout.review_recycler_item,parent,false);
        return new ReviewsRecyclerViewAdapter.ViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Review review = reviews.get(position);
        holder.ratingBar.setRating(review.getRating());
        holder.description.setText(review.getDescription());

        UserDao.UserReference userRef= new UserDao.UserReference(review.getUserID());
        userRef.addUserEventListener(new UserDao.UserReference.GetUserEventListener() {
            @Override
            public void UserExists(User user) {
             holder.username.setText(user.getName());
             if(user.getPicture()!= null && user.getPicture()!="")
             {
                 StorageReference imgRef = FirebaseStorage.getInstance().getReference().child("profile_pics").child(user.getPicture());
                 GlideApp.with(context /* context */)
                         .load(imgRef)
                         .error(R.mipmap.ic_launcher)
                         .placeholder(R.drawable.default_avatar)
                         .into(holder.userImage);
             }
            }
            @Override
            public void UserDoesntExist() {holder.username.setText("User Removed"); }
        });

    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView username,description;
        private ImageView userImage;
        private RatingBar ratingBar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = (TextView) itemView.findViewById(R.id.review_user_Nametxt);
            description =  (TextView) itemView.findViewById(R.id.review_user_text);
            userImage =  (ImageView) itemView.findViewById(R.id.review_user_prof_pic);
            ratingBar =  (RatingBar) itemView.findViewById(R.id.review_user_ratingBar);


        }
    }

    public  void AddReview(Review review)
    {
        this.reviews.add(review);
        notifyDataSetChanged();
    }
}
