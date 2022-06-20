package com.vbellos.dev.itradesmen.User;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.vbellos.dev.itradesmen.Client.ViewWorkers.ReviewsPagerAdapter;
import com.vbellos.dev.itradesmen.Dao.UserDao;
import com.vbellos.dev.itradesmen.Utilities.GlideApp;
import com.vbellos.dev.itradesmen.Models.Job;
import com.vbellos.dev.itradesmen.Dao.JobDao;
import com.vbellos.dev.itradesmen.Models.User;
import com.vbellos.dev.itradesmen.R;
import com.vbellos.dev.itradesmen.Dao.WorkerDao;
import com.vbellos.dev.itradesmen.Models.WorkerDbObj;

public class ViewUserProfileActivity extends AppCompatActivity {

    private User c_user;
    private WorkerDbObj c_worker;
    private String id = null;
    private TextView name,email,phone,description,name_2, jobTXT;
    private ImageView picture;
    private TextView send_message;
    private RelativeLayout job_layout;
    private ConstraintLayout desc_layout;
    private ConstraintLayout worker_reviews_container;
   private  ViewPager2 pager;
    private TabLayout tabLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user_profile);

        job_layout = findViewById(R.id.view_user_job_layout);
        desc_layout = findViewById(R.id.view_user_desc_layout);

        job_layout.setVisibility(View.INVISIBLE);
        desc_layout.setVisibility(View.INVISIBLE);

         pager = findViewById(R.id.view_user_reviews_pager);
         tabLayout = findViewById(R.id.view_user_reviews_tablayout);
        worker_reviews_container = findViewById(R.id.worker_reviews_container);
        worker_reviews_container.setVisibility(View.INVISIBLE);
        jobTXT = findViewById(R.id.view_worker_JobTXT);
        name = findViewById(R.id.view_user_NameTXT);
        name_2 = findViewById(R.id.view_user_Name2TXT);
        email = findViewById(R.id.view_user_EmailTXT);
        phone =  findViewById(R.id.view_user_PhoneTXT);
        description =  findViewById(R.id.view_worker_DescriptionTXT);
        picture =  findViewById(R.id.worker_home_image);
        send_message = findViewById(R.id.sendMessage);
        send_message.setEnabled(false);
        Bundle b = getIntent().getExtras();
        if(b != null) {
            id = b.getString("id");
            UserDao.UserReference userReference = new UserDao.UserReference(id);
            userReference.addUserEventListener(new UserDao.UserReference.GetUserEventListener() {
                @Override
                public void UserExists(User user) {
                    c_user = user;
                    if (user.isWorker()) {
                        WorkerDao.workerReference workerReference = new WorkerDao.workerReference(id);
                        workerReference.addWorkerEventListener(new WorkerDao.workerReference.GetWorkerEventListener() {
                            @Override
                            public void WorkerExists(WorkerDbObj workerDbObj) {
                                c_worker = workerDbObj;

                                Initialize();
                            }
                            @Override
                            public void WorkerDoesntExist() {
                                finish();
                            }
                        });
                    }
                    else{Initialize();}
                }

                @Override
                public void UserDoesntExist() {
                    finish();
                }
            });

        }
        else{finish();}

    }


    public void Initialize()
    {
        if(c_user !=null) {
            name.setText(c_user.getName());
            name_2.setText(c_user.getName());
            email.setText(c_user.getEmail());
            phone.setText(c_user.getPhone());

            if(c_user.getPicture() != null && c_user.getPicture()!="") {
                StorageReference imgRef = FirebaseStorage.getInstance().getReference().child("profile_pics").child(c_user.getPicture());
                GlideApp.with(this /* context */)
                        .load(imgRef)
                        .error(R.mipmap.ic_launcher)
                        .placeholder(R.drawable.default_avatar)
                        .into(picture);
            }

            if (c_worker != null) {


                desc_layout.setVisibility(View.VISIBLE);
                job_layout.setVisibility(View.VISIBLE);
                InitWorkerReviews();
                description.setText(c_worker.getDescription());

                JobDao.JobReference jobReference = new JobDao.JobReference(c_worker.getJob_id());
                jobReference.addGetJobEventListener(new JobDao.JobReference.GetJobEventListener() {
                    @Override
                    public void JobExists(Job job) {
                        jobTXT.setText(job.getTitle());
                    }

                    @Override
                    public void JobDoesntExist() {
                        jobTXT.setText(c_worker.getJob_id());
                    }
                });

                jobTXT.setText(c_worker.getJob_id());


            }
            else
            {
                jobTXT.setText("Client");}

            send_message.setEnabled(true);
            send_message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getApplicationContext(), ChatActivity.class);
                    i.putExtra("user_id",c_user.getId());
                    i.putExtra("chat_id","");
                    startActivity(i);
                }
            });
        }


    }

    public void InitWorkerReviews() {
        String cur_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String wor_id = c_worker.getId();
        if (cur_id != null && wor_id != null) {
            worker_reviews_container.setVisibility(View.VISIBLE);


            ReviewsPagerAdapter reviewsPagerAdapter = new ReviewsPagerAdapter(getSupportFragmentManager(), getLifecycle(), cur_id, wor_id);
            pager.setAdapter(reviewsPagerAdapter);
            pager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);



            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    pager.setCurrentItem(tab.getPosition());

                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });


        }
    }




}