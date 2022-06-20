package com.vbellos.dev.itradesmen.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.vbellos.dev.itradesmen.Utilities.GlideApp;
import com.vbellos.dev.itradesmen.R;
import com.vbellos.dev.itradesmen.Models.Message;
import com.vbellos.dev.itradesmen.Models.User;
import com.vbellos.dev.itradesmen.Dao.UserDao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class MessagesRecyclerViewAdapter extends RecyclerView.Adapter {


    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private Context context;
    private ArrayList<Message> messages;

    public MessagesRecyclerViewAdapter(@NonNull Context context) {
        this.context = context;
        messages = new ArrayList<Message>();

    }

    public void Add(Message message)
    {
        this.messages.add(message);
        Collections.sort(messages,new TimeComparator());
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = (Message) messages.get(position);

        if (message.getSender().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_recycler_item_sent, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_recycler_item_received, parent, false);
            return new ReceivedMessageHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = (Message) messages.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }


    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, nameText,dateText;
        ImageView profileImage;



        ReceivedMessageHolder(View itemView) {
            super(itemView);

            nameText = (TextView) itemView.findViewById(R.id.text_gchat_user_other);
            timeText = (TextView) itemView.findViewById(R.id.text_gchat_timestamp_other);
            dateText = (TextView) itemView.findViewById(R.id.text_gchat_date_other);
            messageText = (TextView) itemView.findViewById(R.id.text_gchat_message_other);
            profileImage = (ImageView) itemView.findViewById(R.id.image_gchat_profile_other);
        }

        void bind(Message message) {
            UserDao.UserReference userReference = new UserDao.UserReference(message.getSender());
            userReference.addUserEventListener(new UserDao.UserReference.GetUserEventListener() {
                @Override
                public void UserExists(User user) {
                    nameText.setText(user.getName());
                    if(user.getPicture()!=null && user.getPicture()!="")
                    {
                        StorageReference imgRef = FirebaseStorage.getInstance().getReference().child("profile_pics").child(user.getPicture());
                        GlideApp.with(context /* context */)
                                .load(imgRef)
                                .error(R.mipmap.ic_launcher)
                                .placeholder(R.drawable.default_avatar)
                                .into(profileImage);
                    }
                }
                @Override
                public void UserDoesntExist() { }
            });

            messageText.setText(message.getText());

            // Format the stored timestamp into a readable String using method.
            Date date = new Date(message.getTime());
            dateText.setText(new SimpleDateFormat("MMMM dd")
                    .format(date));
            timeText.setText(new SimpleDateFormat("HH:mm")
                    .format(date));

        }
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText,dateText;

        SentMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.text_gchat_message_me);
            timeText = (TextView) itemView.findViewById(R.id.text_gchat_timestamp_me);
            dateText = (TextView) itemView.findViewById(R.id.text_gchat_date_me);
        }

        void bind(Message message) {
            messageText.setText(message.getText());

            // Format the stored timestamp into a readable String using method.
            Date date = new Date(message.getTime());
            timeText.setText(new SimpleDateFormat("HH:mm")
                    .format(date));
            dateText.setText(new SimpleDateFormat("MMMM dd")
                    .format(date));
        }
    }

    public class TimeComparator implements Comparator<Message> {
        @Override
        public int compare(Message w1, Message w2) {
            return Long.compare(w1.getTime(), w2.getTime());
        }
    }
}
