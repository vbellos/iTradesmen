package com.vbellos.dev.itradesmen.User;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.vbellos.dev.itradesmen.Adapters.ChatRecyclerViewAdapter;
import com.vbellos.dev.itradesmen.Dao.ChatDao;
import com.vbellos.dev.itradesmen.Models.Chat;
import com.vbellos.dev.itradesmen.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserChatsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserChatsFragment extends Fragment {


    private String user_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private RecyclerView user_chats_recycler;

    public UserChatsFragment() {
        // Required empty public constructor
    }

    public static UserChatsFragment newInstance() {
        UserChatsFragment fragment = new UserChatsFragment();

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);


        user_chats_recycler=view.findViewById(R.id.user_chats_recyclerview);
        ChatRecyclerViewAdapter rva = new ChatRecyclerViewAdapter(getActivity());
        user_chats_recycler.setLayoutManager(new LinearLayoutManager(getContext()));
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

        // Inflate the layout for this fragment
        return view;
    }

    public  void AddChat(Chat chat)
    {
        if(user_chats_recycler.getAdapter()!=null)
        {
            ((ChatRecyclerViewAdapter)user_chats_recycler.getAdapter()).AddChat(chat);
        }
    }

}