package com.example.tacademy.recyclerviewtest.model;

import java.util.HashMap;
import java.util.Map;

/**
 * 채팅 채널에 저장된 상대방 정보
 *   "상대방아이디":"kkk",
     "마지막메세지":"hi",
     "읽기여부":1,
     "시간(송신/저장)":454345678
     "상대방프로필":"http://x.x./a.jpg"
     */

public class ChatChannelModel {
    String uid;
    String lastMsg;
    int readCount;
    long time;
    String profile;
    String chatting_channel;    // 생성할 때 만들어지는 것이 아니기 때문에 생성자에는 넣지 않음.


    public ChatChannelModel() {
    }

    public ChatChannelModel(String uid, String lastMsg, int readCount, long time, String profile) {
        this.uid = uid;
        this.lastMsg = lastMsg;
        this.readCount = readCount;
        this.time = time;
        this.profile = profile;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public int getReadCount() {
        return readCount;
    }

    public void setReadCount(int readCount) {
        this.readCount = readCount;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getChatting_channel() {
        return chatting_channel;
    }

    public void setChatting_channel(String chatting_channel) {
        this.chatting_channel = chatting_channel;
    }

    public Map<String, Object> toChannelMap(){
        Map<String, Object> map = new HashMap<>();
        map.put("uid", uid);
        map.put("lastMsg", lastMsg);
        map.put("readCount", readCount);
        map.put("time", time);
        map.put("profile", profile);
        map.put("chatting_channel", chatting_channel);
        return map;
    }
}
