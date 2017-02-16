package com.example.tacademy.recyclerviewtest.ui.chat;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.example.tacademy.recyclerviewtest.R;
import com.example.tacademy.recyclerviewtest.holder.PostHolder;
import com.example.tacademy.recyclerviewtest.model.ChatMessage;
import com.example.tacademy.recyclerviewtest.model.ChatModel;
import com.example.tacademy.recyclerviewtest.model.Post;
import com.example.tacademy.recyclerviewtest.ui.base.BaseActiity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ChatRoomActivity extends BaseActiity {
    // ui 관련 ======================================================================
    RecyclerView recyclerView;
    AutoCompleteTextView msg_input;
    ChatRoomActivity.MyAdapter myAdapter = new ChatRoomActivity.MyAdapter();
    LinearLayoutManager linearLayoutManager;
    // fb 데이터 관련 =================================================================
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    // 데이터 관리 ====================================================================
    ArrayList<ChatModel> arrayList = new ArrayList<ChatModel>();
    int index[] = new int[1000];
    String chatting_room_key;
    Post post;
    //===============================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        // 데이터 받기 ===============================================================
        chatting_room_key = getIntent().getStringExtra("chatting_room_key");
        post = (Post) getIntent().getSerializableExtra("you");
        // 콤퍼넌트 리소스를 자바 객체로 연결
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        msg_input = (AutoCompleteTextView) findViewById(R.id.msg_input);
        linearLayoutManager = new LinearLayoutManager(this);    // 선형
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setAdapter(myAdapter); // 데이터 공급원 아답터 연결
        // fb =======================================================================
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        databaseReference.child("chatting").child("rooms").child(chatting_room_key)
                .addChildEventListener(new ChildEventListener() {
                    // 아이템을 검색하거나, 추가할 때 호출(select, insert)
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        // s는 이전 데이터의 키를 가르킨다.(LinkedList)
                        // Log.i("CHAT", dataSnapshot.toString() + "/" + s);
                        // 파싱 : 데이터를 ChatMesssage틀에 넣어준다.
                        ChatModel chatMessage = dataSnapshot.getValue(ChatModel.class);
                        // 추가로 확보된 데이터 리스트에 추가
                        arrayList.add(chatMessage);
                        // 위치를 마지막 메시지 자리로
                        linearLayoutManager.scrollToPosition(arrayList.size() - 1);
                        // 갱신
                        myAdapter.notifyDataSetChanged();
                    }

                    // 아이템의 변화가 감지되면 호출(update)
                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    // 아이템이 삭제되면 호출(delete)
                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);
                        Log.i("CHAT", "삭제완료");
                    }

                    // 아이템의 순서가 변경되면 호출
                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        /*
        for(String s : data){
            arrayList.add(s);
        }
*/
        // 특정 위치로 맞추기
        linearLayoutManager.scrollToPosition(arrayList.size() - 1);
    }

    // 전송 버튼 누르면 호출
    public void onSend(View view) {
        // 1. 입력데이터 추출
        String msg = msg_input.getText().toString();
        long sendTime = System.currentTimeMillis();
        ChatModel chatModel = new ChatModel(
                getNickName(),
                msg,
                "t",
                sendTime,
                1
        );
        // 2. 서버 전송 -> 여기서는 데이터 직접 추가(서버가 없으니까)
        // 3. insert 적절한 경로(가지를 하나 만들어서)에다 메시지 입력
        databaseReference.child("chatting").child("rooms").child(chatting_room_key)
                .child(sendTime + "")
                .setValue(chatModel)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.i("CHAT", "등록완료");
                            // 상대방에게 푸시메시지 발송!!
                        } else {
                            Log.i("CHAT", "등록실패");
                        }
                    }
                });
        // 5. 입력값 제거
        msg_input.setText("");
        // 6. 키보드 내리기
        // closeKeyword(this, msg_input);
    }

    public void closeKeyword(Context context, EditText editText) {
        InputMethodManager methodManager =
                (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        methodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    // 아답터
    class MyAdapter extends RecyclerView.Adapter {
        // 데이터의 개수
        @Override
        public int getItemCount() {
            return arrayList.size();
        }

        // ViewHolder 생성
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // xml -> view
            View itemView =
                    LayoutInflater.from(parent.getContext())
                            //.inflate(R.layout.cell_cardview_layout, parent, false);
                            .inflate(R.layout.sendbird_view_group_user_message, parent, false);

            return new PostHolder(itemView);
        }

        // ViewHolder에 데이터를 설정(바인딩)한다.
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            // 보이고자 하는 셀에 내용을 설정한다.
            //((PostHolder) holder).bindOnPost(arrayList.get(position));  //data[position]);
            // ((PostHolder) holder).bindOnPost(arrayList.get(position), index[position]);//data[position]);
            ((PostHolder) holder).bindOnPost(arrayList.get(position), getNickName());
        }
    }
}
