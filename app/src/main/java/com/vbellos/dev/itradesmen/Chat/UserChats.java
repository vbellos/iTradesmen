package com.vbellos.dev.itradesmen.Chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.vbellos.dev.itradesmen.Dao.ChatDao;
import com.vbellos.dev.itradesmen.Models.Chat;
import com.vbellos.dev.itradesmen.R;
import com.vbellos.dev.itradesmen.Adapters.ChatRecyclerViewAdapter;

public class UserChats extends AppCompatActivity {

    private String user_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private RecyclerView user_chats_recycler;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_chats);
        user_chats_recycler=findViewById(R.id.user_chats_recyclerview);
        ChatRecyclerViewAdapter rva = new ChatRecyclerViewAdapter(this);
        user_chats_recycler.setLayoutManager(new LinearLayoutManager(this));
        user_chats_recycler.setAdapter(rva);

        ChatDao.UserChats userChats = new ChatDao.UserChats(user_uid);
        userChats.setUserChatsListener(new ChatDao.UserChats.UserChatsListener() {
            @Override
            public void ChatAdded(String chat_id) {
                ChatDao.ChatReference chatReference = new ChatDao.ChatReference(chat_id);
                chatReference.setUserChatsListener(new ChatDao.ChatReference.ChatListener() {
                    @Override
                    public void ChatDataChanged(Chat chat) {
                        AddChat(chat);
                    }

                    @Override
                    public void OnError() {
                    }
                });
            }
        });


    }

    public  void AddChat(Chat chat)
    {
        if(user_chats_recycler.getAdapter()!=null)
        {
            ((ChatRecyclerViewAdapter)user_chats_recycler.getAdapter()).AddChat(chat);
        }
    }
}