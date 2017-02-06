package com.example.tacademy.recyclerviewtest;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.EditText;

import com.example.tacademy.recyclerviewtest.model.ChatMessage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    AutoCompleteTextView msg_input;
    int index[] = new int[1000];
    MyAdapter myAdapter = new MyAdapter();
    LinearLayoutManager linearLayoutManager;
    GridLayoutManager gridLayoutManager;
    StaggeredGridLayoutManager staggeredGridLayoutManager;

    String autoKeyword[] = {
            "이상해씨",
            "이상해풀",
            "이상해꽃"
    };
    ///////////////////////////////////////////////////////////
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ArrayList<ChatMessage> arrayList = new ArrayList<ChatMessage>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        databaseReference.child("chat").addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                // s는 이전 데이터의 키를 가르킨다.(LinkedList)
                // Log.i("CHAT", dataSnapshot.toString() + "/" + s);
                // 파싱 : 데이터를 ChatMesssage틀에 넣어준다.
                ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);
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
        // 콤퍼넌트 리소스를 자바 객체로 연결
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        msg_input = (AutoCompleteTextView) findViewById(R.id.msg_input);

        //뷰의 스타일(매니저) 정의, 선형, 그리드형, 높이가 불규칙한 그리드형
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        // 자동완성 기능 추가
        msg_input.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, autoKeyword));

        gridLayoutManager = new GridLayoutManager(this, 2);
        gridLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        //recyclerView.setLayoutManager(gridLayoutManager);

        // 가변 그리드
        staggeredGridLayoutManager
                = new StaggeredGridLayoutManager(2, OrientationHelper.VERTICAL);
        //recyclerView.setLayoutManager(staggeredGridLayoutManager);

        // 채팅관련
        // 데이터를 뒤집어서 표현할 때(최신순), 서버에서온 데이터가 최신순이면 필요없음
        // FB로 데이터를 받아올 때는 최신순 처리가 곤란하므로 유용함.
        // staggeredGridLayoutManager.setReverseLayout(true);
        // 마지막 데이터가 보이게(staggeredGridLayoutmanager 미지원)
        // 데이터가 동적으로 바뀌면 적용이 안됨. 새로 세팅해야함.
        linearLayoutManager.setStackFromEnd(true);
        //gridLayoutmanager.setStackFromEnd(true);
        //데이터 공급원 아답터 연결
        recyclerView.setAdapter(myAdapter);
        // 특정 위치로 맞추기
        linearLayoutManager.scrollToPosition(arrayList.size() - 1);
    }

    // 전송 버튼 누르면 호출
    public void onSend(View view) {
        // 1. 입력데이터 추출
        String msg = msg_input.getText().toString();
        // 2. 서버 전송 -> 여기서는 데이터 직접 추가(서버가 없으니까)
        // 3. insert 적절한 경로(가지를 하나 만들어서)에다 메시지 입력
        databaseReference.child("chat").push()
                .setValue(new ChatMessage("삼다수", msg))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.i("CHAT", "등록완료");
                        } else {
                            Log.i("CHAT", "등록실패");
                        }
                    }
                });        // 페이크
        // index[arrayList.size() - 1] = 1;
        // 3. 화면 갱신
        //myAdapter.notifyDataSetChanged();
        // 4. 리스트 가장 마지막으로 갱신(최신 텍스트의 위치로)
        //linearLayoutManager.scrollToPosition(arrayList.size() - 1);
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

    // 추천 키워드, 자동완성 UI를 직접 구성하거나, 내용을 가변시킬 수 있다.
    class MyKeywordAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return null;
        }
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
            ((PostHolder) holder).bindOnPost(arrayList.get(position), 1);
        }
    }

}
