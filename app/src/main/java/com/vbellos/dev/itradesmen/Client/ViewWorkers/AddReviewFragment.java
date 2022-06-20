package com.vbellos.dev.itradesmen.Client.ViewWorkers;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import com.vbellos.dev.itradesmen.Dao.ReviewDao;
import com.vbellos.dev.itradesmen.Models.Review;
import com.vbellos.dev.itradesmen.R;


public class AddReviewFragment extends Fragment {

   private String worker_id,user_id;
   private RatingBar ratingBar;
   private EditText editText;
   private Button button;

    public AddReviewFragment() {
        // Required empty public constructor
    }

    public AddReviewFragment(String user_id,String worker_id) {
        this.user_id = user_id;
        this.worker_id=worker_id;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_review, container, false);

        ratingBar = view.findViewById(R.id.add_review_ratingBar);
        editText = view.findViewById(R.id.add_review_desc_text);
        button = view.findViewById(R.id.add_review_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user_id!=null && worker_id!=null){AddReview();}
            }
        });
        if (viewCreatedlistener!=null)
        {viewCreatedlistener.viewCreated(view);}
        return view;

    }
    public void AddReview()
    {
        if(!editText.getText().toString().isEmpty())
        {
            Review review = new Review();
            review.setUserID(user_id);
            review.setWorkerID(worker_id);
            review.setRating(ratingBar.getRating());
            review.setDescription(editText.getText().toString());
            ReviewDao reviewDao = new ReviewDao();
            reviewDao.addReview(review);

            new AlertDialog.Builder(getActivity())
                    .setMessage("Review added successfully")
                    .setCancelable(true)
                    .show();

            editText.setText("");
        }
        else{new AlertDialog.Builder(getActivity())
                .setMessage("Please add a description")
                .setCancelable(true)
                .show();}
    }

    public interface onViewCreated
    {
        void viewCreated(View view);
    }

    private onViewCreated viewCreatedlistener;
    public void setOnViewCreatedListener(onViewCreated listener)
    {
        this.viewCreatedlistener = listener;
    }
}