package com.example.tacademy.recyclerviewtest.util;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Created by Tacademy on 2017-02-21.
 */

public class ChatPushModel implements Serializable{
    String chatting_room_key, uid, msg, nickname;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public ChatPushModel(String chatting_room_key, String uid, String msg, String nickname) {
        this.chatting_room_key = chatting_room_key;
        this.uid = uid;
        this.msg = msg;
        this.nickname = nickname;
    }

    public String getChatting_room_key() {
        return chatting_room_key;
    }

    public void setChatting_room_key(String chatting_room_key) {
        this.chatting_room_key = chatting_room_key;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
