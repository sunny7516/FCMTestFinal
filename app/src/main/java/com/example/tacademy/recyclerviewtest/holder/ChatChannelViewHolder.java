package com.example.tacademy.recyclerviewtest.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tacademy.recyclerviewtest.R;
import com.example.tacademy.recyclerviewtest.model.ChatChannelModel;

import java.util.Calendar;

/**
 * Created by Tacademy on 2017-02-16.
 */

public class ChatChannelViewHolder extends RecyclerView.ViewHolder {
    ImageView ic_profile, star;
    TextView nickName, lastMsg, badge_count, time;

    public ChatChannelViewHolder(View itemView) {
        super(itemView);
        ic_profile = (ImageView)itemView.findViewById(R.id.ic_profile);
        star = (ImageView)itemView.findViewById(R.id.star);
        nickName = (TextView) itemView.findViewById(R.id.nickName);
        lastMsg = (TextView)itemView.findViewById(R.id.lastMsg);
        badge_count = (TextView)itemView.findViewById(R.id.badge_count);
        time = (TextView)itemView.findViewById(R.id.time);
    }
    public void bindToPost(ChatChannelModel model){
        // 셀 화면 세팅
        // 셀을 누르면 채팅방으로 이동
        nickName.setText(model.getUid());
        lastMsg.setText(model.getLastMsg());
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(model.getTime());
        time.setText(c.get(c.YEAR) +"-"+(c.get(c.MONTH)+1)+"-"+(c.get(c.DAY_OF_MONTH)));
    }
}
