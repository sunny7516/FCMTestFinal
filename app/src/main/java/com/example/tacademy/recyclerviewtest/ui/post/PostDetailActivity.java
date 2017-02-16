package com.example.tacademy.recyclerviewtest.ui.post;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tacademy.recyclerviewtest.R;
import com.example.tacademy.recyclerviewtest.model.ChatChannelModel;
import com.example.tacademy.recyclerviewtest.model.Comment;
import com.example.tacademy.recyclerviewtest.model.Post;
import com.example.tacademy.recyclerviewtest.model.User;
import com.example.tacademy.recyclerviewtest.ui.base.BaseActiity;
import com.example.tacademy.recyclerviewtest.ui.chat.ChatRoomActivity;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 글 상세보기 + 댓글달기
 */

public class PostDetailActivity extends BaseActiity {
    String postKey;
    DatabaseReference postReference, commentReference, databaseReference;
    EditText comment_input;
    RecyclerView detail_rv;
    ImageView profile, star;
    TextView nickName, star_count, title, content;

    ValueEventListener valueEventListener;
    ChildEventListener childEventListener;
    ComAdapter comAdapter;

    // 작성자의 uid
    String auth_uid;
    Post model;

    // [UI] =========================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        // 키 획득
        postKey = getIntent().getStringExtra("KEY");
        if (postKey == null) {
            Toast.makeText(this, "네트워크가 지연되고 있습니다. 잠시 후에 다시 ..", Toast.LENGTH_SHORT).show();
            finish();
        }
        // 데이터 획득 -> 참조 획득
        postReference = FirebaseDatabase.getInstance().getReference()
                .child("posts").child(postKey);
        // 댓글 가지 -> 참조 획득
        commentReference = FirebaseDatabase.getInstance().getReference()
                .child("post-comments").child(postKey);
        // 글 세팅
        detail_rv = (RecyclerView) findViewById(R.id.detail_rv);
        comment_input = (EditText) findViewById(R.id.comment_input);
        profile = (ImageView) findViewById(R.id.profile);
        star = (ImageView) findViewById(R.id.star);
        nickName = (TextView) findViewById(R.id.nickName);
        star_count = (TextView) findViewById(R.id.star_count);
        title = (TextView) findViewById(R.id.title);
        content = (TextView) findViewById(R.id.content);
        // 댓글 입력 받게 처리
        // 댓글을 쓰면 밑으로 리스팅(Recycler) + adapter, custom, holder
        detail_rv.setLayoutManager(new LinearLayoutManager(this));
        showTop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 이벤트 등록 세팅
        // 1. 작성자의 글을 가져온다. (1개)
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                model = dataSnapshot.getValue(Post.class);
                nickName.setText(model.getAuthor());
                star_count.setText("" + model.getStar_count());
                title.setText(model.getTitle());
                content.setText(model.getContent());
                auth_uid = model.getUid();
                // 내 글이면 채팅하기 버튼 삭제
                if (auth_uid.equals(getUid())) {
                    findViewById(R.id.chatBtn).setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        postReference.addValueEventListener(valueEventListener);
        // 댓글을 뿌리는 아답터를 생성하여 리사이클러뷰에 세팅
        comAdapter = new ComAdapter(this, commentReference);
        detail_rv.setAdapter(comAdapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 이벤트 해제
        if (valueEventListener != null)
            postReference.removeEventListener(valueEventListener);
        comAdapter.closeListener();
    }

    public void showTop() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    // [채팅 신청] =====================================================================
    public void onChatRequest(View view) {
        // x. 채팅 신청하기
        databaseReference = FirebaseDatabase.getInstance().getReference();
        // 내 ID가 유효하면!
        databaseReference
                .child("users")
                .child(getUid())    // 본인 ID
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user == null) {
                            Toast.makeText(PostDetailActivity.this, "이미 탈퇴한 회원입니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            checkYou();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public void checkYou() {
        // 1. 상대방 아이디가 존재하는지 체크
        final String you_id = auth_uid;
        final String you_profile = "http://mblogthumb2.phinf.naver.net/20151207_257/kiste007_1449497318755cqpEL_PNG/143_PGL.png?type=w2";

        databaseReference
                .child("users")
                .child(you_id)    // 본인 ID
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user == null) {
                            Toast.makeText(PostDetailActivity.this, "이미 탈퇴한 회원입니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            // 나하고, 쟤하고 채팅방을 개설한 게 있는지?
                            databaseReference.child("channel").child(getUid()).child(you_id)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot != null) {
                                                ChatChannelModel checkKey = dataSnapshot.getValue(ChatChannelModel.class);
                                                if (checkKey != null) {
                                                    // 이미 존재함 => 채팅방으로 이동 : checkKey
                                                    Toast.makeText(PostDetailActivity.this, "채팅방으로이동합니다", Toast.LENGTH_SHORT).show();
                                                    goChatting(checkKey.getChatting_channel());
                                                    //한 단계 더 밑으로 내려감
                                                    //Iterator<DataSnapshot> ds = dataSnapshot.getChildren().iterator();
                                                    //while(ds.hasNext()){
                                                    //    goChatting(ds.next().getKey());
                                                    //}
                                                    //goChatting(dataSnapshot.getKey());
                                                } else {
                                                    // 존재하지 않음 -> 채널생성 -> 채팅방으로 이동
                                                    makeChannel(you_id, you_profile);
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public void makeChannel(String you_id, String you_profile) {
        // channel에 데이터 설정
        ChatChannelModel ccm = new ChatChannelModel(
                you_id,
                "채팅을 요청합니다.",
                1,
                System.currentTimeMillis(),
                you_profile
        );
        // 입력 준비
        final String key = databaseReference.child("chatting").child("rooms").push().getKey();

        // 채팅방 번호 세팅
        ccm.setChatting_channel(key);
        // 데이터 가공
        Map<String, Object> postMap = ccm.toChannelMap();
        // 입력 데이터 준비
        Map<String, Object> updates = new HashMap<String, Object>();
        updates.put("/channel/" + getUid() + "/" + you_id, postMap);
        updates.put("/channel/" + you_id + "/" + getUid(), postMap);

        // 추가
        databaseReference.updateChildren(updates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null)
                    Log.i("CHAT", "오류" + databaseError.getMessage());
                else {
                    // 채팅 채널 생성이 완료!
                    Toast.makeText(PostDetailActivity.this, "채팅방으로 이동합니다.", Toast.LENGTH_SHORT).show();
                    goChatting(key);
                }
            }
        });
    }

    // 채팅방으로 이동하는 메소드
    public void goChatting(String roomKey) {
        Intent intent = new Intent(this, ChatRoomActivity.class);
        intent.putExtra("chatting_room_key", roomKey);
        intent.putExtra("you", model);
        startActivity(intent);
    }

    // [댓글 리스트] ==================================================================
    // 댓글 입력 이벤트
    public void onComment(View view) {
        final String comment_input_str = comment_input.getText().toString();
        if (TextUtils.isEmpty(comment_input_str)) {
            comment_input.setError("댓글값이 없습니다.");
            return;
        }
        // 사용자 유효한지 체크
        FirebaseDatabase.getInstance().getReference().child("users")
                .child(getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Comment comment
                        = new Comment(getUid(), comment_input_str, user.getId());
                // 글 입력
                commentReference.push().setValue(comment);
                comment_input.setText("");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // 뷰홀더
    class ComViewHolder extends RecyclerView.ViewHolder {
        ImageView profile;
        TextView uid, comment;

        public ComViewHolder(View itemView) {
            super(itemView);
            profile = (ImageView) itemView.findViewById(R.id.Profile);
            uid = (TextView) itemView.findViewById(R.id.uid);
            comment = (TextView) itemView.findViewById(R.id.comment);
        }
    }

    // 어답터
    class ComAdapter extends RecyclerView.Adapter<ComViewHolder> {
        ArrayList<Comment> comments = new ArrayList<Comment>();
        Context context;
        DatabaseReference root;

        public ComAdapter(Context context, DatabaseReference root) {
            this.context = context;
            this.root = root;
            // 데이터 획득!!
            childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Comment comment = dataSnapshot.getValue(Comment.class);
                    comments.add(comment);
                    notifyItemInserted(comments.size() - 1);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            root.addChildEventListener(childEventListener); // 이벤트 등록
        }

        public void closeListener() {
            root.removeEventListener(childEventListener); // 이벤트 제거
        }

        @Override
        public ComViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view =
                    LayoutInflater.from(context).inflate(R.layout.cell_comment_layout, parent, false);
            return new ComViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ComViewHolder holder, int position) {
            Comment comment = comments.get(position);   // 데이터 획득
            holder.uid.setText(comment.getAuthor());    // 작성자
            holder.comment.setText(comment.getComment());   // 댓글
        }

        @Override
        public int getItemCount() {
            return comments.size();
        }
    }
}
