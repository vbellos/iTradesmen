package com.vbellos.dev.itradesmen.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.vbellos.dev.itradesmen.Adapters.JobSpinnerAdapter;
import com.vbellos.dev.itradesmen.Client.HomeActivity;
import com.vbellos.dev.itradesmen.Dao.JobDao;
import com.vbellos.dev.itradesmen.Dao.UserDao;
import com.vbellos.dev.itradesmen.Dao.WorkerDao;
import com.vbellos.dev.itradesmen.Models.Job;
import com.vbellos.dev.itradesmen.Models.User;
import com.vbellos.dev.itradesmen.Models.WorkerDbObj;
import com.vbellos.dev.itradesmen.R;
import com.vbellos.dev.itradesmen.Worker.WorkerHomeActivity;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class FinishUserProfileActivity extends AppCompatActivity {


    // request code
    private final int PICK_IMAGE_REQUEST = 22;

    private String img_name = null;

    // instance for firebase storage and StorageReference
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReference().child("profile_pics");


    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    TextView name,phone,email;
    RadioGroup rdg;
    RadioButton client,worker;
    boolean isClient;
    ImageView profImage;
    TextView change_picture;
    Button finishBTN,cancelBTN;

    JobDao jobDao;
    Job selected_job;
    Spinner jview;
    JobSpinnerAdapter jobAdapter;
    EditText description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_user_profile);

        description = findViewById(R.id.finish_worker_DescriptionTXT);
        cancelBTN = findViewById(R.id.finish_user_cancelBTN);
        cancelBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                FinishUserProfileActivity.this.finish();
                Intent i = new Intent(FinishUserProfileActivity.this,LoginActivity.class);
                startActivity(i);
            }
        });
        finishBTN = findViewById(R.id.finish_user_saveBTN);
        finishBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishProf();
            }
        });
        rdg = findViewById(R.id.f_radioGroup);
        client= findViewById(R.id.f_radioButtonClient);
        worker = findViewById(R.id.f_radioButtonClient);
        isClient = true;
        profImage = findViewById(R.id.finish_user_image);
        profImage.setImageResource(R.drawable.default_avatar);
        change_picture = findViewById(R.id.finish_user_changePictureBTN);
        change_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangePicture();
            }
        });


        rdg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (client.isChecked()) {isClient =true;setWorkerViews(false);}
                else{isClient = false;setWorkerViews(true);}
            }
        });

        rdg.check(client.getId());

        name = findViewById(R.id.finish_user_NameTXT);
        phone = findViewById(R.id.finish_user_PhoneTXT);
        email = findViewById(R.id.finish_user_EmailTXT);

        selected_job = null;
        jview = findViewById(R.id.finish_user_spinner);
        description = findViewById(R.id.finish_worker_DescriptionTXT);

        jobDao = new JobDao();
        jobDao.addJobsChangedEventListener(new JobDao.JobsChangedEventListener() {
            @Override
            public void JobsChanged(List<Job> jobList) {
                jobAdapter=new JobSpinnerAdapter(FinishUserProfileActivity.this,jobList);
                jview.setAdapter(jobAdapter);
            }
        });
        jview.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selected_job = jobAdapter.getJob(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selected_job = null;
            }
        });
        if (user==null)
        {
            logout();
        }
        else
        {
            if(user.getEmail()!=""){
                email.setText(user.getEmail());
                email.setEnabled(false);
            }

        }
    }


    public void setWorkerViews(boolean enabled)
    {
        ConstraintLayout job_layout,desc_layout;
        job_layout = findViewById(R.id.finish_user_job_layout);
        desc_layout = findViewById(R.id.finish_user_desc_layout);
        if(enabled){
            desc_layout.setVisibility(View.VISIBLE);
            job_layout.setVisibility(View.VISIBLE);
        }
        else{
            desc_layout.setVisibility(View.INVISIBLE);
            job_layout.setVisibility(View.INVISIBLE);
        }
    }


    public void ChangePicture()
    {
        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST);
    }


    private void uploadImage(@NonNull Bitmap image)
    {

            finishBTN.setEnabled(false);
            img_name = UUID.randomUUID().toString();
            // Defining the child of storageReference
            StorageReference ref = storageReference.child(img_name);

        //Compressing Image
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] data = baos.toByteArray();

            // adding listeners on upload
            // or failure of image
            ref.putBytes(data)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {

                                    // Image uploaded successfully
                                    change_picture.setText("Change\nPicture");
                                    finishBTN.setEnabled(true);

                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            // Error, Image not uploaded
                            change_picture.setText("Upload Failed Try Again");
                            img_name = null;
                            finishBTN.setEnabled(true);
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {

                                // Progress Listener for loading
                                // percentage on the dialog box
                                @Override
                                public void onProgress(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());

                                    change_picture.setText("Uploading:\n"+ progress + " %");
                                }
                            });

    }



    public void finishProf()
    {
        Boolean condition_1 = !name.getText().toString().isEmpty() && !email.getText().toString().isEmpty() && !phone.getText().toString().isEmpty()&& img_name!=null;
        Boolean conditon_2 = !description.getText().toString().isEmpty() && selected_job!=null;

        if (condition_1){

            if(isClient){
                RegUser();
                goClientHhome();
            }
            else
            {
                if (conditon_2){
                    RegUser();
                    RegWorker();
                    goWorkerHhome();
                }
                else{
                    new AlertDialog.Builder(FinishUserProfileActivity.this)
                            .setMessage("empty fields")
                            .setCancelable(true)
                            .show();
                }
            }
        }
        else{
            new AlertDialog.Builder(FinishUserProfileActivity.this)
                    .setMessage("empty fields")
                    .setCancelable(true)
                    .show();
        }





    }

    public  void RegUser()
    {
        User u = new User();
        UserDao userDao = new UserDao();
        u.setWorker(!isClient);
        u.setId(user.getUid());
        u.setName(name.getText().toString());
        u.setEmail(email.getText().toString());
        u.setPhone(phone.getText().toString());
        u.setPicture(img_name);
        userDao.adduser(u);
    }

    public  void RegWorker()
    {
        WorkerDbObj workerDbObj = new WorkerDbObj(user.getUid(),selected_job.getId(),description.getText().toString());
        WorkerDao workerDao = new WorkerDao();
        workerDao.addWorker(workerDbObj);
    }

    // Override onActivityResult method
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            try {
                Bitmap image = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
                profImage.setImageBitmap(image);
                uploadImage(image);

            } catch (IOException e) {
                e.printStackTrace();
            }


        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
        }



        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            // Get the Uri of data
            Uri filePath = data.getData();

                File temp_crop = new File(getCacheDir(),"temp_crop.png");
                Uri temp = Uri.fromFile(temp_crop);
                UCrop.of(filePath,temp)
                        .withAspectRatio(1, 1)
                        .withMaxResultSize(1024, 1024)
                        .start(this);

        }
    }

    public void goClientHhome()
    {
        Intent i = new Intent(this, HomeActivity.class);
        startActivity(i);
        finish();
    }

    public void goWorkerHhome()
    {
        Intent i = new Intent(this, WorkerHomeActivity.class);
        startActivity(i);
        finish();
    }

    public void logout()
    {
        FirebaseAuth.getInstance().signOut();
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();
    }
}