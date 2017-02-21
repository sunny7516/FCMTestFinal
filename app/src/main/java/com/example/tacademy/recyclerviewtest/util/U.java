package com.example.tacademy.recyclerviewtest.util;

/**
 * Created by Tacademy on 2017-02-21.
 */
public class U {
    private static U ourInstance = new U();

    public static U getInstance() {
        return ourInstance;
    }

    private U() {
    }
    // 채팅방이 현재 화면인지 체크
    boolean isChattingRoomInside;

    public boolean isChattingRoomInside() {
        return isChattingRoomInside;
    }

    public void setChattingRoomInside(boolean chattingRoomInside) {
        isChattingRoomInside = chattingRoomInside;
    }
}
