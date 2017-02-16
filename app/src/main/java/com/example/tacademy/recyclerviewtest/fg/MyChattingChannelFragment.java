package com.example.tacademy.recyclerviewtest.fg;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tacademy.recyclerviewtest.R;
import com.example.tacademy.recyclerviewtest.holder.ChatChannelViewHolder;
import com.example.tacademy.recyclerviewtest.model.ChatChannelModel;
import com.example.tacademy.recyclerviewtest.model.Post;
import com.example.tacademy.recyclerviewtest.ui.chat.ChatRoomActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;

public class MyChattingChannelFragment extends Fragment {
    RecyclerView recyclerView;
    FirebaseRecyclerAdapter firebaseRecyclerAdapter;
    LinearLayoutManager linearLayoutManager;

    public MyChattingChannelFragment() {}
    public Query getQuery(DatabaseReference databaseReference) {
        // 나의 채팅리스트 요청
        Query query = databaseReference.child("channel").child(getUid());
        return query;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_total_posts, container, false);
        // 화면 구성 세팅..
        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
        // 리사이클러뷰 위로 뭔가가 있어서 자동으로 올라가서 가려지면
        // 이걳을 넣어서 현재 위치를 유지시킨다.
        recyclerView.setFocusable(false);
        // 레이아웃 세팅
        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        // 쿼리수행
        Query query = getQuery(FirebaseDatabase.getInstance().getReference());
        // 아답터 생성
        firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<ChatChannelModel, ChatChannelViewHolder>(
                ChatChannelModel.class,
                R.layout.cell_chat_channel_layout,
                ChatChannelViewHolder.class,
                // 쿼리 결과
                query
        ){
            // 레이아웃을 담기는 그릇, 데이터가 담기를 그릇, 필요한 인덱스
            @Override
            protected void populateViewHolder(ChatChannelViewHolder viewHolder, final ChatChannelModel model, int position) {
                // 1. position -> 데이터 획득 (참조 획득)
                final DatabaseReference databaseReference = getRef(position);
                // 1. 채팅방 키 획득 작성 실습
                // 2. viewHolder-> 이벤트 등록
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 채팅방으로 이동
                        Intent intent = new Intent(getContext(), ChatRoomActivity.class);
                        intent.putExtra("chatting_room_key", model.getChatting_channel());
                        // 채팅방으로 갈 때 상대방 정보에 대한 class를 담는게 정석이고,
                        // 여기서는 코드를 많이 변경하지 않는 범위에서 Post를 재활용하는 측면으로 구성된다.
                        intent.putExtra("you", new Post("","",model.getUid(), "quest"));
                        startActivity(intent);
                    }
                });
                viewHolder.bindToPost(model);
            }
        };
        recyclerView.setAdapter( firebaseRecyclerAdapter );

        return view;
    }
    public void updateStarStats( DatabaseReference databaseReference )
    {
        // 별 숫자 변경 및 어던 사람이 눌렀는지 여부 처리
        databaseReference.runTransaction(new Transaction.Handler() {
            // 트렌젝션 수행
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Post post = mutableData.getValue(Post.class);
                if( post == null ){
                    return Transaction.success(mutableData);
                }
                if( post.getStars().containsKey(getUid()) ){
                    // 좋아요 철회!!
                    post.setStar_count( post.getStar_count() - 1);
                    post.getStars().remove(getUid());
                }else{
                    // 처음으로 좋아요를 눌렀다
                    post.setStar_count( post.getStar_count() + 1);
                    post.getStars().put(getUid(), true);
                }
                // 변경 내용을 다시 삽입
                mutableData.setValue(post);
                return Transaction.success(mutableData);
            }
            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                // 완료
            }
        });
    }
    // 나의 아이디
    public String getUid()
    {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}


//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_my_chatting_channel, container, false);
//
//        return view;
//    }
