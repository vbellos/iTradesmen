package com.vbellos.dev.itradesmen.User;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.vbellos.dev.itradesmen.Dao.ChatDao;
import com.vbellos.dev.itradesmen.Utilities.GlideApp;
import com.vbellos.dev.itradesmen.R;
import com.vbellos.dev.itradesmen.Models.Message;
import com.vbellos.dev.itradesmen.Models.User;
import com.vbellos.dev.itradesmen.Dao.UserDao;
import com.vbellos.dev.itradesmen.Adapters.MessagesRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity {

    String cur_chat_id;
    String user_id;
    ArrayList<Message> messages = new ArrayList<>();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    ChatDao chatDao = new ChatDao();
    String curentUserId= firebaseAuth.getCurrentUser().getUid();


    TextView person_name;
    ImageView person_profile_pic;

    Button sendMessage;
    EditText textMessage;
    RecyclerView messagesView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        if(curentUserId==null){this.finish();}

        person_name = findViewById(R.id.chat_activity_person_name);
        person_profile_pic = findViewById(R.id.chat_activity_profile_pic);

        sendMessage = findViewById(R.id.buttonSendMessage);
        textMessage = findViewById(R.id.editTextTextMessage);
        sendMessage.setEnabled(false);
        messagesView = findViewById(R.id.Message_Recycler);
        MessagesRecyclerViewAdapter rva = new MessagesRecyclerViewAdapter(this);
        messagesView.setLayoutManager(new LinearLayoutManager(this));
        messagesView.setAdapter(rva);

        Bundle b = getIntent().getExtras();
        if (b!=null) {

            user_id = b.getString("user_id");
            if(user_id!=null) {
                InitUser();
                cur_chat_id = b.getString("chat_id");
                if (cur_chat_id == null || cur_chat_id.equals("") ){
                    ChatDao.UsersChat usersChat = new ChatDao.UsersChat(curentUserId, user_id);
                    usersChat.SetUsersChatListener(new ChatDao.UsersChat.usersChatListener() {
                        @Override
                        public void ChatExsists(String chat_id) {
                            cur_chat_id = chat_id;
                            AddMessageListener();
                        }
                        @Override
                        public void ChatDoesntExsists() {
                            cur_chat_id = UUID.randomUUID().toString();
                            chatDao.addChat(curentUserId, user_id, cur_chat_id);
                            AddMessageListener();
                        }
                    });

                }else{AddMessageListener();}
            }else{ finish();}
        }else{finish();}




    }



    public void AddMessageListener()
    {

        sendMessage.setEnabled(true);

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String t = textMessage.getText().toString();
                Message message = new Message();
                message.setMessage_id(UUID.randomUUID().toString());
                message.setSender(firebaseAuth.getCurrentUser().getUid());
                message.setText(t);
                chatDao.addMessage(cur_chat_id,message);
                textMessage.setText("");
            }
        });

        ChatDao.ChatMessagesRefference messagesRefference = new ChatDao.ChatMessagesRefference(cur_chat_id);
        messagesRefference.setUserChatsListener(new ChatDao.ChatMessagesRefference.ChatMessagesListener() {
            @Override
            public void MessageAdded(Message message) {
                messages.add(message);
                if(messagesView.getAdapter()!=null) {
                    ((MessagesRecyclerViewAdapter) messagesView.getAdapter()).Add(message);
                    messagesView.scrollToPosition(messagesView.getAdapter().getItemCount()-1);
                    if(!message.isRead()&& message.getSender().equals(user_id))
                    {

                        chatDao.setMessageRead(cur_chat_id,message);
                    }
                }
            }
        });
    }

    public void InitUser()
    {
        UserDao.UserReference userReference = new UserDao.UserReference(user_id);
        userReference.addUserEventListener(new UserDao.UserReference.GetUserEventListener() {
            @Override
            public void UserExists(User user) {
                person_name.setText(user.getName());
                if(user.getPicture() != null && user.getPicture()!="") {
                    StorageReference imgRef = FirebaseStorage.getInstance().getReference().child("profile_pics").child(user.getPicture());
                    GlideApp.with(ChatActivity.this /* context */)
                            .load(imgRef)
                            .error(R.drawable.default_avatar)
                            .placeholder(R.drawable.default_avatar)
                            .into(person_profile_pic);
                }
                person_profile_pic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(ChatActivity.this , ViewUserProfileActivity.class);
                        i.putExtra("id",user_id);
                        startActivity(i);
                    }
                });
            }

            @Override
            public void UserDoesntExist() {
                finish();
            }
        });
    }

}