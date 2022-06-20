package com.vbellos.dev.itradesmen.Dao;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vbellos.dev.itradesmen.Models.Chat;
import com.vbellos.dev.itradesmen.Models.Message;

import java.sql.Timestamp;
import java.util.ArrayList;

public class ChatDao {

    DatabaseReference dbref = FirebaseDatabase.getInstance().getReference();


    public void addChat(Chat chat)
    {
        dbref.child("chats").child(chat.getChat_id()).setValue(chat);
        dbref.child("user_chats").child(chat.getMembers().get(0)).child(chat.getChat_id()).setValue(chat.getChat_id());
        dbref.child("user_chats").child(chat.getMembers().get(1)).child(chat.getChat_id()).setValue(chat.getChat_id());
    }

    public void addChat(String user1,String user_2,String chat_id)
    {
        Chat chat = new Chat();
        chat.setMembers(new ArrayList<>());
        chat.getMembers().add(user1);
        chat.getMembers().add(user_2);
        chat.setLast_message_id("");
        chat.setChat_id(chat_id);
                //UUID.randomUUID().toString();
        chat.setUser_1(user1);
        chat.setUser_2(user_2);



        dbref.child("chats").child(chat.getChat_id()).setValue(chat);

        //dbref.child("chats").child(chat.getChat_id()).child("members").child(user1).setValue(user1);
        //dbref.child("chats").child(chat.getChat_id()).child("members").child(user_2).setValue(user_2);

        dbref.child("user_chats").child(user1).child(chat.getChat_id()).setValue(chat.getChat_id());
        dbref.child("user_chats").child(user_2).child(chat.getChat_id()).setValue(chat.getChat_id());
    }


    public void addMessage(String chat_id,Message message)
    {
        message.setTime(new Timestamp(System.currentTimeMillis()).getTime());
        dbref.child("chat_messages").child(chat_id).child(message.getMessage_id()).setValue(message);
        dbref.child("chats").child(chat_id).child("last_message_id").setValue(message.getMessage_id());
    }
    public void setMessageRead(String chat_id,Message message)
    {
        dbref.child("chat_messages").child(chat_id).child(message.getMessage_id()).child("read").setValue(true);
    }

    public static class UserChats
    {
        private String user_id;
        public UserChats(String user_id)
        {
            this.user_id = user_id;
        }

        public interface UserChatsListener
        {
            void ChatAdded(String chat_id);
        }

        private UserChatsListener listener;

        public void setUserChatsListener(UserChatsListener listener)
        {
            this.listener = listener;
            DatabaseReference dbref = FirebaseDatabase.getInstance().getReference();
            dbref.child("user_chats").child(user_id).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    listener.ChatAdded(snapshot.getValue(String.class));
                }
                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                }
                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                }
                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }

    }

    public static class ChatReference
    {
        private String chat_id;
        public ChatReference(String chat_id)
        {
            this.chat_id = chat_id;
        }

        public interface ChatListener
        {
            void ChatDataChanged(Chat chat);
            void OnError();
        }

        private ChatListener listener;

        public void setUserChatsListener(ChatListener listener)
        {
            this.listener = listener;
            DatabaseReference dbref = FirebaseDatabase.getInstance().getReference();
            dbref.child("chats").child(chat_id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    listener.ChatDataChanged(snapshot.getValue(Chat.class));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    listener.OnError();
                }
            });
        }

    }

    public static class ChatMessagesRefference
    {
        private String chat_id;
        public ChatMessagesRefference(String chat_id)
        {
            this.chat_id = chat_id;
        }

        public interface ChatMessagesListener
        {
            void MessageAdded(Message message);
        }

        private ChatMessagesListener listener;

        public void setUserChatsListener(ChatMessagesListener listener)
        {
            this.listener = listener;
            DatabaseReference dbref = FirebaseDatabase.getInstance().getReference();
            dbref.child("chat_messages").child(chat_id).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    listener.MessageAdded(snapshot.getValue(Message.class));
                }
                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                }
                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                }
                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }

    }

    public static class UsersChat
    {

        private String user_1,user_2;
        public UsersChat(String user_1 ,String user_2)
        {
            this.user_1 = user_1;
            this.user_2 = user_2;
        }

        public  interface usersChatListener
        {
            void ChatExsists(String chat_id);
            void ChatDoesntExsists();
        }
        int checked = 0;
        boolean found = false;

        usersChatListener usersChatListener;

        public  void SetUsersChatListener(usersChatListener listener)
        {
            this.usersChatListener = listener;

            DatabaseReference dbref = FirebaseDatabase.getInstance().getReference();
            dbref.child("user_chats").child(user_1).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ArrayList<String> Chats = new ArrayList<String>();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Chats.add(ds.getValue(String.class));
                    }
                        for(String c_id : Chats)
                        {
                            dbref.child("chats").child(c_id).child("members").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    ArrayList<String> members = new ArrayList<>();
                                    for(DataSnapshot ds : snapshot.getChildren()){members.add(ds.getValue(String.class));}
                                    if(members.contains(user_2)){
                                        found = true;
                                        usersChatListener.ChatExsists(c_id);
                                    }
                                    checked+=1;
                                    if(checked==Chats.size() &&!found)
                                    {usersChatListener.ChatDoesntExsists();}
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                        if(Chats.size() == 0){usersChatListener.ChatDoesntExsists();}
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            usersChatListener.ChatDoesntExsists();
                        }
                    });
                }

        }


    public static class MessageRefference
    {
        private String chat_id,message_id;
        public MessageRefference(String chat_id,String message_id)
        {
            this.chat_id = chat_id;
            this.message_id=message_id;
        }

        public interface MessageListener
        {
            void MessageExsists(Message message);
            void MessageDoesntExsists();
        }

        private MessageListener listener;

        public void setMessageListener(MessageListener listener)
        {
            this.listener = listener;
            DatabaseReference dbref = FirebaseDatabase.getInstance().getReference();
            dbref.child("chat_messages").child(chat_id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.hasChild(message_id)){listener.MessageExsists(snapshot.child(message_id).getValue(Message.class));}
                    else{listener.MessageDoesntExsists();}
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    listener.MessageDoesntExsists();
                }
            });
        }

    }
    public static class UnreadMessagesRef
    {
        String user_id;
        public UnreadMessagesRef(String user_id)
        {
            this.user_id = user_id;
        }

        public interface UnreadMessageListener
        {
            void UnreadMessageAdded(Message message);
        }
        UnreadMessageListener listener;
        public void setUnreadMessageListener(UnreadMessageListener unreadMessageListener)
        {
            listener = unreadMessageListener;UserChats userChats = new UserChats(user_id);
            userChats.setUserChatsListener(new UserChats.UserChatsListener() {
                @Override
                public void ChatAdded(String chat_id) {
                    ChatReference chatReference = new ChatReference(chat_id);
                    chatReference.setUserChatsListener(new ChatReference.ChatListener() {
                        @Override
                        public void ChatDataChanged(Chat chat) {
                            MessageRefference messageRefference = new MessageRefference(chat_id,chat.getLast_message_id());
                            messageRefference.setMessageListener(new MessageRefference.MessageListener() {
                                @Override
                                public void MessageExsists(Message message) {
                                    if(!message.getSender().equals(user_id) && !message.isRead())
                                    {
                                        if(listener!=null){listener.UnreadMessageAdded(message);}
                                    }
                                }
                                @Override
                                public void MessageDoesntExsists() {
                                }
                            });
                        }
                        @Override
                        public void OnError() {
                        }
                    });
                }
            });

        }

    }

}
