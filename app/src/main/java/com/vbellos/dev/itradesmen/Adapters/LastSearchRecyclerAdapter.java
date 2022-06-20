package com.vbellos.dev.itradesmen.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.vbellos.dev.itradesmen.Dao.UserDao;
import com.vbellos.dev.itradesmen.User.ViewUserProfileActivity;
import com.vbellos.dev.itradesmen.Utilities.GlideApp;
import com.vbellos.dev.itradesmen.Models.User;
import com.vbellos.dev.itradesmen.Models.Worker;
import com.vbellos.dev.itradesmen.R;
import com.vbellos.dev.itradesmen.User.ChatActivity;

import java.util.ArrayList;

public class LastSearchRecyclerAdapter extends RecyclerView.Adapter<LastSearchRecyclerAdapter.ViewHolder>{
    private ArrayList<String> workers;
    private Context context;

    public LastSearchRecyclerAdapter(ArrayList<String> users, Context context) {
        this.workers = users;
        this.context = context;
    }
    public void AddUser(String user)
    {

        for (String c: workers) {
            if (c.equals(user))
            {
                workers.remove(c);
                break;
            }
        }
        this.workers.add(user);

        notifyDataSetChanged();
    }
    public void RemoveUser(String user)
    {
        if(workers.contains(user)){workers.remove(user);}
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item= LayoutInflater.from(parent.getContext()).inflate(R.layout.last_search_recycler_item,parent,false);

        return new LastSearchRecyclerAdapter.ViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String cur_user = workers.get(position);

        UserDao.UserReference userReference = new UserDao.UserReference(cur_user);
        userReference.addUserEventListener(new UserDao.UserReference.GetUserEventListener() {
            @Override
            public void UserExists(User user) {
                holder.name.setText(user.getName());
                if(user.getPicture()!=null && user.getPicture()!="") {
                    StorageReference imgRef = FirebaseStorage.getInstance().getReference().child("profile_pics").child(user.getPicture());
                    GlideApp.with(context /* context */)
                            .load(imgRef)
                            .error(R.mipmap.ic_launcher)
                            .placeholder(R.drawable.default_avatar)
                            .into(holder.prof_pic);
                }
                holder.bg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(context , ViewUserProfileActivity.class);
                        i.putExtra("id",cur_user);
                        context.startActivity(i);
                    }
                });
            }

            @Override
            public void UserDoesntExist() {
                RemoveUser(cur_user);
            }
        });


    }

    @Override
    public int getItemCount() {
        return workers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private ImageView prof_pic;
        private ConstraintLayout bg;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.last_search_username);
            prof_pic = (ImageView) itemView.findViewById(R.id.last_search_prof_pic);
            bg = (ConstraintLayout) itemView.findViewById(R.id.last_search_bg_layout);

        }
    }
}
