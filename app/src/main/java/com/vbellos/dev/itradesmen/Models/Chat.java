package com.vbellos.dev.itradesmen.Models;

import java.util.ArrayList;

public class Chat {

    String chat_id,other_properties;
    ArrayList<String> members;
    String last_message_id;
    String user_1,user_2;

    public String getUser_1() {
        return user_1;
    }

    public void setUser_1(String user_1) {
        this.user_1 = user_1;
    }

    public String getUser_2() {
        return user_2;
    }

    public void setUser_2(String user_2) {
        this.user_2 = user_2;
    }

    public String getLast_message_id() {
        return last_message_id;
    }

    public void setLast_message_id(String last_message_id) {
        this.last_message_id = last_message_id;
    }

    public Chat() { }

    public String getChat_id() {
        return chat_id;
    }

    public void setChat_id(String chat_id) {
        this.chat_id = chat_id;
    }



    public String getOther_properties() {
        return other_properties;
    }

    public void setOther_properties(String other_properties) {
        this.other_properties = other_properties;
    }

    public ArrayList<String> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<String> members) {
        this.members = members;
    }
}
