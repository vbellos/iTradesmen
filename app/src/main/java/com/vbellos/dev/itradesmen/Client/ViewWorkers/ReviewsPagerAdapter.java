package com.vbellos.dev.itradesmen.Client.ViewWorkers;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.vbellos.dev.itradesmen.Worker.WorkerReviewsFragment;

import java.util.ArrayList;
import java.util.List;

public class ReviewsPagerAdapter extends FragmentStateAdapter {

   private String user_id,worker_id;
    WorkerReviewsFragment workerReviewsFragment;
    AddReviewFragment addReviewFragment;
    private View firstview;
    private View secondview;

    public ReviewsPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle,String user_id,String worker_id) {
        super(fragmentManager, lifecycle);
        this.user_id = user_id;
        this.worker_id =worker_id;

        workerReviewsFragment = new WorkerReviewsFragment(worker_id);
        workerReviewsFragment.setOnViewCreatedListener(new WorkerReviewsFragment.onViewCreated() {
            @Override
            public void viewCreated(View view) {
                firstview = view;
                if(viewLoadedlistener!=null){viewLoadedlistener.viewloaded();}
            }
        });

        addReviewFragment = new AddReviewFragment(user_id, worker_id);
        addReviewFragment.setOnViewCreatedListener(new AddReviewFragment.onViewCreated() {
            @Override
            public void viewCreated(View view) {
                secondview = view;
                if(viewLoadedlistener!=null){viewLoadedlistener.viewloaded();}
            }
        });


    }

    public interface ViewLoaded
    {
        void viewloaded();
    }
    ViewLoaded viewLoadedlistener;
    public void SetViewLoadedListener(ViewLoaded listener)
    {
        viewLoadedlistener = listener;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position) {
            case 0:
                return workerReviewsFragment;
            case 1:
                return addReviewFragment;
        }
        return null;

    }

    public View getView(int pos) {
        switch (pos) {
            case 0:
                return firstview;
            case 1:
                return secondview;
        }
        return null;

    }


    @Override
    public int getItemCount() {
        return 2;
    }

}
