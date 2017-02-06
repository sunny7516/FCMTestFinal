package com.example.tacademy.recyclerviewtest.model;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tacademy.recyclerviewtest.Post;
import com.example.tacademy.recyclerviewtest.R;

/**
 * Created by Tacademy on 2017-02-06.
 */

public class PostViewHolder extends RecyclerView.ViewHolder {
    ImageView profile, star;
    TextView nickName, star_count, title, content;

    public PostViewHolder(View itemView) {
        super(itemView);
        profile = (ImageView)itemView.findViewById(R.id.profile);
        star = (ImageView)itemView.findViewById(R.id.star);
        nickName = (TextView)itemView.findViewById(R.id.nickName);
        star_count = (TextView)itemView.findViewById(R.id.star_count);
        title = (TextView)itemView.findViewById(R.id.title);
        content = (TextView)itemView.findViewById(R.id.content);

    }
    // 데이터를 개별 뷰에 설정
    public void bindToPost(Post model,View.OnClickListener listener){
        profile.setOnClickListener(listener);
        star.setOnClickListener(listener);
        nickName.setText(model.getAuthor());
        star_count.setText(""+model.getStar_count());
        title.setText(model.getTitle());
        content.setText(model.getContent());
    }
}
