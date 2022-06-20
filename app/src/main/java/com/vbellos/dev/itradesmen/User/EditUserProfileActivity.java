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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.vbellos.dev.itradesmen.Dao.JobDao;
import com.vbellos.dev.itradesmen.Dao.UserDao;
import com.vbellos.dev.itradesmen.Dao.WorkerDao;
import com.vbellos.dev.itradesmen.Utilities.GlideApp;
import com.vbellos.dev.itradesmen.Models.Job;
import com.vbellos.dev.itradesmen.Models.User;
import com.vbellos.dev.itradesmen.Models.WorkerDbObj;
import com.vbellos.dev.itradesmen.R;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class EditUserProfileActivity extends AppCompatActivity {



    private User c_user;
    private WorkerDbObj c_worker;
    private String id = null;
    private TextView name,email,phone,description, jobTXT;
    private ImageView picture;
    private TextView change_picture;
    private RelativeLayout job_layout;
    private ConstraintLayout desc_layout;
    private Button SaveBTN;

    private final int PICK_IMAGE_REQUEST = 22;
    // Uri indicates, where the image will be picked from

    private String img_name;
    // instance for firebase storage and StorageReference
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReference().child("profile_pics");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_profile);

        job_layout = findViewById(R.id.finish_user_job_layout);
        desc_layout = findViewById(R.id.edit_user_desc_layout);

        job_layout.setVisibility(View.GONE);
        desc_layout.setVisibility(View.GONE);

        jobTXT = findViewById(R.id.edit_worker_JobTXT);
        name = findViewById(R.id.edit_user_Name2TXT);

        SaveBTN = findViewById(R.id.edit_user_saveBTN);

        email = findViewById(R.id.edit_user_EmailTXT);
        phone =  findViewById(R.id.edit_user_PhoneTXT);
        description =  findViewById(R.id.edit_worker_DescriptionTXT);
        picture =  findViewById(R.id.edit_user_image);
        change_picture = findViewById(R.id.finish_user_changePictureBTN);
        change_picture.setEnabled(false);

        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            id = FirebaseAuth.getInstance().getCurrentUser().getUid();
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



    public void Initialize() {
        if (c_user != null) {
            name.setText(c_user.getName());
            email.setText(c_user.getEmail());
            phone.setText(c_user.getPhone());
            SaveBTN.setEnabled(true);


            if (c_user.getPicture() != null && c_user.getPicture() != "") {
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
            } else {
                jobTXT.setText("Client");
            }

            change_picture.setEnabled(true);
            change_picture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ChangePicture();
                }
            });
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

    // Override onActivityResult method
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode,
                resultCode,
                data);

        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            try {
                Bitmap image = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
                picture.setImageBitmap(image);
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


    private void uploadImage(@NonNull Bitmap Image)
    {

            SaveBTN.setEnabled(false);
            img_name = UUID.randomUUID().toString();
            // Defining the child of storageReference
            StorageReference ref = storageReference.child(img_name);

        //Compressing Image
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Image.compress(Bitmap.CompressFormat.JPEG, 70, baos);
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
                                    c_user.setPicture(img_name);
                                    SaveBTN.setEnabled(true);

                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            // Error, Image not uploaded
                            change_picture.setText("Upload Failed Try Again");
                            img_name = null;
                            SaveBTN.setEnabled(true);
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

    public void SaveChanges(View view)
    {
        FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();

        Boolean condition_1 = fuser.getUid().equals(c_user.getId());
        Boolean condition_2 = !name.getText().toString().isEmpty() && !email.getText().toString().isEmpty() && !phone.getText().toString().isEmpty();
        Boolean condition_3 = c_worker!= null;
        Boolean condition_4 =  c_user.getEmail().equals(email.getText().toString());
        Boolean email_failure = false;

        if (condition_1&& condition_2)
        {
            c_user.setName(name.getText().toString());
            c_user.setPhone(phone.getText().toString());
            UserDao userDao = new UserDao();
            userDao.updateuser(c_user);


            if(condition_3)
            {
                WorkerDao workerDao = new WorkerDao();
                workerDao.updateWorker(c_worker);
            }

            Toast.makeText(getApplicationContext(), "Changes Saved", Toast.LENGTH_LONG).show();

            if(condition_4) {
                fuser.updateEmail(c_user.getEmail()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        c_user.setEmail(email.getText().toString());
                        userDao.updateuser(c_user);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        new AlertDialog.Builder(EditUserProfileActivity.this)
                                .setMessage("Email is in use")
                                .setCancelable(true)
                                .show();

                    }
                });
            }

        }
        else{
            new AlertDialog.Builder(EditUserProfileActivity.this)
                    .setMessage("empty fields")
                    .setCancelable(true)
                    .show();
        }


    }

}