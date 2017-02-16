package com.example.tacademy.recyclerviewtest.fg;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tacademy.recyclerviewtest.model.Post;
import com.example.tacademy.recyclerviewtest.ui.post.PostDetailActivity;
import com.example.tacademy.recyclerviewtest.R;
import com.example.tacademy.recyclerviewtest.model.PostViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;

public abstract class ParentFragment extends Fragment {
    public ParentFragment() {

    }

    RecyclerView recyclerView;
    FirebaseRecyclerAdapter firebaseRecyclerAdapter;

    // 쿼리를 획득하는 메소드를 만들어서, 자식이 재정의하여
    // 쿼리 결과가 달라지게 처리한다.
    // abstract 정의하면 함수 선언만 하고 사용할 때 내부 정의한다.
    public abstract Query getQuery(DatabaseReference databaseReference);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_total_posts, container, false);
        // 화면 구성 세팅..
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        //리사이클러뷰 위로 뭔가가 있어서 자동으로 올라가서 가려지면
        // 이것을 넣어서 현재 위치를 유지시킨다.
        recyclerView.setFocusable(false);
        // 레이아웃 세팅
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        // 쿼리 수행
        Query query = getQuery(FirebaseDatabase.getInstance().getReference());
        // 아답터 생성
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Post, PostViewHolder>(
                Post.class,
                R.layout.cell_post_layout,
                PostViewHolder.class,
                // 쿼리 결과
                query
        ) {
            // 레이아웃을 담기는 그릇, 데이터가 담기는 그릇, 필요한 인덱스
            @Override
            protected void populateViewHolder(PostViewHolder viewHolder, Post model, int position) {
                // 1. position -> 데이터 획득 (참조 획득)
                final DatabaseReference databaseReference = getRef(position);
                // 2. viewHolder -> 이벤트 등록
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 상세보기로 이동
                        Intent intent = new Intent(getContext(), PostDetailActivity.class);
                        intent.putExtra("KEY", databaseReference.getKey());
                        // 글 하나에 대한 고유값 key
                        getContext().startActivity(intent);
                    }
                });
                if (model.getStars().containsKey(getUid())) {
                    // 별처리 (색을 채운다)
                    viewHolder.star.setImageResource(R.drawable.star_on);
                } else {
                    // 별처리 (색을 비운다)
                    viewHolder.star.setImageResource(R.drawable.star_off);
                }
                // 3. viewHolder -> 데이터 세팅 (bindToPost)
                viewHolder.bindToPost(model, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 별을 클릭해서 좋아요 처리, 다시 눌러서 해제 처리
                        if (v.getId() == R.id.star) {
                            // 별 클릭
                            Log.i("CHAT", "별클릭");
                            // 기존의 데이터를 변경 ->
                            updateStarStats(databaseReference);
                        } else {
                            // 프로필 클릭
                        }
                    }
                });
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);

        return view;
    }

    public void updateStarStats(DatabaseReference databaseReference) {
        // 별 숫자 변경 및 어떤 사람이 눌렀는지 여부 처리
        databaseReference.runTransaction(new Transaction.Handler() {
            // 트랜잭션 수행
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Post post = mutableData.getValue(Post.class);
                if (post == null) {
                    return Transaction.success(mutableData);
                }
                if (post.getStars().containsKey(getUid())) {
                    // 좋아요 철회
                    post.setStar_count(post.getStar_count() - 1);
                    post.getStars().remove(getUid());
                } else {
                    // 처음으로 좋아요를 눌렀다.
                    post.setStar_count(post.getStar_count() + 1);
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
    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
