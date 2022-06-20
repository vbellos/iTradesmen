package com.vbellos.dev.itradesmen.Worker;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.vbellos.dev.itradesmen.Adapters.ReviewsRecyclerViewAdapter;
import com.vbellos.dev.itradesmen.Adapters.WorkersRecyclerViewAdapter;
import com.vbellos.dev.itradesmen.Client.ViewWorkers.AddReviewFragment;
import com.vbellos.dev.itradesmen.Dao.ReviewDao;
import com.vbellos.dev.itradesmen.Models.Review;
import com.vbellos.dev.itradesmen.R;

import java.util.ArrayList;


public class WorkerReviewsFragment extends Fragment {


    private ReviewsRecyclerViewAdapter rva;
    private RecyclerView recyclerView;
    private String worker_id;
    private RatingBar average_rating;
    private TextView rating_count;
    ArrayList<Float> Ratings;

    public WorkerReviewsFragment() {
        // Required empty public constructor
    }

    public WorkerReviewsFragment(String Worker_id) {

        this.worker_id = Worker_id;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_worker_reviews, container, false);

        Ratings = new ArrayList<>();
        recyclerView = view.findViewById(R.id.worker_reviews_recycler);
        average_rating = view.findViewById(R.id.average_review_ratingBar);
        rating_count = view.findViewById(R.id.average_reviews_count);
        rva = new ReviewsRecyclerViewAdapter(getContext());

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.hasFixedSize();
        recyclerView.setAdapter(rva);

        if(worker_id!=null)
        {

            ReviewDao.UserReviews userReviews = new ReviewDao.UserReviews(worker_id);
            userReviews.setUserReviewsListener(new ReviewDao.UserReviews.UserReviewsListener() {
                @Override
                public void ReviewAdded(Review review) {
                    ((ReviewsRecyclerViewAdapter)getRecyclerView().getAdapter()).AddReview(review);
                    Ratings.add(review.getRating());
                    calc_rating();
                    if(viewCreatedlistener!=null){viewCreatedlistener.viewCreated(view);}
                }
            });
        }

        if(viewCreatedlistener!=null){viewCreatedlistener.viewCreated(view);}

        return view;


    }
    public void calc_rating()
    {
        float total = 0;
        for(Float r:Ratings){total+=r;}
        float avg = total/Ratings.size();
        average_rating.setRating(avg);
        rating_count.setText(Ratings.size()+" Reviews");
    }

    public RecyclerView getRecyclerView()
    {
        return recyclerView;
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