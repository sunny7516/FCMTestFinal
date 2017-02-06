package com.example.tacademy.recyclerviewtest;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tacademy.recyclerviewtest.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class NewPostActivity extends BaseActiity{
    EditText title, content;
    FloatingActionButton fab;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        title=(EditText)this.findViewById(R.id.title);
        content=(EditText)this.findViewById(R.id.content);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSendPost();
            }
        });
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    public void onSendPost(){
        // 작성글 입력
        final String title_str = title.getText().toString();
        final String content_str = content.getText().toString();
        // 제목, 내용이 존재해야 함
        if(TextUtils.isEmpty(title_str)){
            title.setError("필수 입력값입니다!");
            return;
        }
        if(TextUtils.isEmpty(content_str)){
            content.setError("필수 입력값입니다!");
            return;
        }
        // 비속어 처리
        // 입력을 못하게 막아야함 (편집불가)
        setEditable(false);
        // 회원이 맞는지 체크!!
        Log.i("CHAT", FirebaseAuth.getInstance().getCurrentUser().getUid());
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // 1. uid가 존재하는지 체크
        databaseReference.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if(user == null){
                    // 2. 없으면 불가
                    Toast.makeText(NewPostActivity.this, "회원이 아닙니다.", Toast.LENGTH_SHORT).show();
                    setEditable(true);
                    finish();
                }else{
                    // 글 작성 업로드
                    // 3. 있으면 이후 작업 진행
                    // 로딩 시작
                    showProgress("글 업로드 중");
                    // 글 작성 업로드
                    String key =
                    databaseReference.child("posts").push().getKey();
                    Post post = new Post(title_str, content_str, uid, user.getId());
                    Map<String, Object> postMap = post.toPostMap();
                    // 여러 가지의 업데이트 방식으로 한번에 데이터를 삽입
                    // 덮어쓰는 개념으로 수정이 편함
                    Map<String, Object> updates = new HashMap<String, Object>();
                    updates.put("/posts/"+key, postMap);
                    updates.put("/user-posts/"+uid+"/"+key, postMap);

                    // 추가
                    databaseReference.updateChildren(updates, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError != null)
                            Log.i("CHAT", "오류" + databaseError.getMessage());
                            // 입력 잠금 해제
                            setEditable(true);
                            // 로딩 닫기
                            hideProgress();
                            // 화면 닫힘
                            finish();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void setEditable(boolean flag){
        title.setEnabled(flag);
        content.setEnabled(flag);
        if(flag)    // 편집할 수 있으면 버튼 보이기
        fab.setVisibility(View.VISIBLE);
        else        // 편집할 수 없으면 버튼 숨기기
        fab.setVisibility(View.GONE);
    }

}
