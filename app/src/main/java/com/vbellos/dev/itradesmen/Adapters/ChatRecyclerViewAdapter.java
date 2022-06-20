package com.vbellos.dev.itradesmen.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.vbellos.dev.itradesmen.User.ChatActivity;
import com.vbellos.dev.itradesmen.Dao.ChatDao;
import com.vbellos.dev.itradesmen.Utilities.GlideApp;
import com.vbellos.dev.itradesmen.Models.Chat;
import com.vbellos.dev.itradesmen.R;
import com.vbellos.dev.itradesmen.Models.Message;
import com.vbellos.dev.itradesmen.Models.User;
import com.vbellos.dev.itradesmen.Dao.UserDao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class ChatRecyclerViewAdapter extends RecyclerView.Adapter<ChatRecyclerViewAdapter.ViewHolder>{

    private Context context;
    private ArrayList<Chat> chats;
    public ChatRecyclerViewAdapter(@NonNull Context context)
    {
        this.context=context;
        chats= new ArrayList<>();
    }

    public void AddChat(Chat chat)
    {
        boolean chat_exsists = false;
        for (Chat c:chats) {
            if (c.getChat_id().equals(chat.getChat_id()))
            {
                chats.remove(c);
                break;
            }
        }
        this.chats.add(chat);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChatRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item= LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_recycler_item,parent,false);
        return new ChatRecyclerViewAdapter.ViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatRecyclerViewAdapter.ViewHolder holder, int position) {
        Chat chat = chats.get(position);
        String other_user;
        if(chat.getMembers().get(0).equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
        { other_user = chat.getMembers().get(1); }
        else{other_user = chat.getMembers().get(0);}

        UserDao.UserReference userReference = new UserDao.UserReference(other_user);
        userReference.addUserEventListener(new UserDao.UserReference.GetUserEventListener() {
            @Override
            public void UserExists(User user) {
                holder.name.setText(user.getName());
                if(user.getPicture()!=null && user.getPicture()!="")
                {
                    StorageReference imgRef = FirebaseStorage.getInstance().getReference().child("profile_pics").child(user.getPicture());
                    GlideApp.with(context /* context */)
                            .load(imgRef)
                            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .error(R.mipmap.ic_launcher)
                            .placeholder(R.drawable.default_avatar)
                            .into(holder.prof_pic);
                    ChatDao.MessageRefference messageRefference = new ChatDao.MessageRefference(chat.getChat_id(),chat.getLast_message_id());
                    messageRefference.setMessageListener(new ChatDao.MessageRefference.MessageListener() {
                        @Override
                        public void MessageExsists(Message message) {
                            String ms;
                            if (message.getSender().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                            { ms = "You: "; }
                            else{ ms = user.getName() +": "; }
                            ms = ms + message.getText();
                            holder.last_message.setText(ms);

                            Date date = new Date(message.getTime());
                            holder.last_time.setText(new SimpleDateFormat("HH:mm")
                                    .format(date));
                            holder.last_date.setText(new SimpleDateFormat("MMMM dd")
                                    .format(date));
                        }

                        @Override
                        public void MessageDoesntExsists() {
                            holder.last_message.setText("Removed");
                        }
                    });
                }
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(context, ChatActivity.class);
                        i.putExtra("user_id",other_user);
                        i.putExtra("chat_id",chat.getChat_id());
                        context.startActivity(i);
                    }
                    });
            }
            @Override
            public void UserDoesntExist() {
            }
        });


    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name, last_message,last_time,last_date;
        private ImageView prof_pic;



        public ViewHolder(final View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.chat_user_Nametxt);
            prof_pic = (ImageView) view.findViewById(R.id.chat_user_prof_pic);
            last_message = (TextView) view.findViewById(R.id.chat_user_lastmessage);
            last_time = (TextView) view.findViewById(R.id.chat_user_last_time);
            last_date = (TextView) view.findViewById(R.id.chat_user_last_date);

        }
    }
}
