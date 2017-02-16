package com.example.tacademy.recyclerviewtest.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tacademy.recyclerviewtest.R;
import com.example.tacademy.recyclerviewtest.model.ChatModel;

/**
 * Created by Tacademy on 2017-02-02.
 */

public class PostHolder extends RecyclerView.ViewHolder {
    TextView msg;
    TextView txt_left, txt_right;
    LinearLayout left_container;
    LinearLayout right_container;

    // view로부터 component를 획득
    public PostHolder(View itemView) {
        super(itemView);
        msg = (TextView) itemView.findViewById(R.id.msg);
        // 채팅 UI
        txt_left = (TextView) itemView.findViewById(R.id.txt_left);
        txt_right = (TextView) itemView.findViewById(R.id.txt_right);
        left_container = (LinearLayout) itemView.findViewById(R.id.left_container);
        right_container = (LinearLayout) itemView.findViewById(R.id.right_container);
    }

    // 데이터 설정
    public void bindOnPost(String text) {
        msg.setText(text);
    }

    public void bindOnPost(String text, int type) {
        if (type == 1) {  //me
            right_container.setVisibility(View.VISIBLE);
            left_container.setVisibility(View.GONE);
            txt_right.setText(text);
        } else {  //you
            left_container.setVisibility(View.VISIBLE);
            right_container.setVisibility(View.GONE);
            txt_left.setText(text);
        }
    }

    public void bindOnPost(ChatModel msg, String myNickname) {
        // 이번에 표시할 메시지가 내가 쓴 글이다.
        int type = 0;
        if (msg.getSender().equals(myNickname)) type = 1;
        if (type == 1) {  //me
            right_container.setVisibility(View.VISIBLE);
            left_container.setVisibility(View.GONE);
            txt_right.setText(msg.getMsg());
        } else {  //you
            left_container.setVisibility(View.VISIBLE);
            right_container.setVisibility(View.GONE);
            txt_left.setText(msg.getMsg());
        }
    }
}
