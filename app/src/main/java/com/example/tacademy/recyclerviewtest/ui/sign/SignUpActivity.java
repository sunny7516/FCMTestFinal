package com.example.tacademy.recyclerviewtest.ui.sign;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tacademy.recyclerviewtest.R;
import com.example.tacademy.recyclerviewtest.db.StorageHelper;
import com.example.tacademy.recyclerviewtest.model.Post;
import com.example.tacademy.recyclerviewtest.model.User;
import com.example.tacademy.recyclerviewtest.ui.base.BaseActiity;
import com.example.tacademy.recyclerviewtest.ui.chat.ChatRoomActivity;
import com.example.tacademy.recyclerviewtest.ui.post.CenterActivity;
import com.example.tacademy.recyclerviewtest.util.ChatPushModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.iid.FirebaseInstanceId;

public class SignUpActivity extends BaseActiity {

    EditText email_et, password_et;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    //    String msg;
    ChatPushModel cpm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        // 푸시 메시지를 전달 받았다(없을 수도 있음)
        cpm = (ChatPushModel) getIntent().getSerializableExtra("FCM");

        // [1] 노티 관련 =======================================================================
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null)
            notificationManager.cancel(getIntent().getIntExtra("NOTI_ID", 0));
        // [2] 벳지 처리 =======================================================================
        // 벳지 초기화 (앱을 구동하면 무조건 뱃지를 0으로 만든다)
        // 만약 특정 메시지를 확인 후에 없애는 방식이면 그 지점으로 이동시킨다.
        Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        intent.putExtra("badge_count", 0);
        intent.putExtra("badge_count_package_name", getPackageName());
        intent.putExtra("badge_count_class_name", getComponentName().getClassName());
        sendBroadcast(intent);
        StorageHelper.getInstance().setInt(this, "APP_NOREAD_MSG", 0);        // 저장값 초기화

        // ui 구성 =========================================================================
        email_et = (EditText) findViewById(R.id.email_et);
        password_et = (EditText) findViewById(R.id.password_et);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        // 만약에 UID가 로그인을 해야지만 활성화 된다면, 별도의 저장값을 유지하면 됨.
        // 이미 가입한 회원이면 회원가입 버튼을 비활성화 혹은 삭제
        if (getUid() != null) {
            findViewById(R.id.signupBtn).setVisibility(View.GONE);
        }
        // 자동 로그인 처리 ===================================================================
        String email = StorageHelper.getInstance().getString(SignUpActivity.this, "email");
        String pwd = StorageHelper.getInstance().getString(SignUpActivity.this, "password");
        if (email != null && pwd != null && !email.equals("") && !pwd.equals("")) {
            email_et.setText(email);
            password_et.setText(pwd);
            onLogin(null);
        }
        // 최초는 null이고, 활성화 이후에는 값이 도달한다.
        //String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        //Log.i("FCM", "토큰|" + refreshedToken);
    }

    /**
     * 회원가입 2017.02.15
     * 회원가입 버튼을 누르면 fb의 인증 쪽으로 이메일/비번이 등록된다.
     * //@param sunny
     */
    public void onSignUp(View view) {
        if (!isValidate()) return;
        // 1. 로딩
        showProgress("회원가입중...");
        // 2. 이메일비번 획득
        final String email = email_et.getText().toString();
        String pwd = password_et.getText().toString();

        // 3. 인증쪽에 데이터 입력
        firebaseAuth.createUserWithEmailAndPassword(email, pwd)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // 4. 로딩닫기
                        hideProgress();
                        if (task.isSuccessful()) {
                            Log.i("CHAT", "성공");
                            // 4-1. 회원정보 디비에 입력
                            onUserSaved(email);
                            // 5. 로그인 처리로 이동
                        } else {
                            // 실패
                            Log.i("CHAT", "실패" + task.getException().getMessage());
                            Toast.makeText(SignUpActivity.this, "회원가입실패:" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * fb 인증으로 가입 성공하면, fb 데이터베이스의 users 밑으로 회원 정보를 등록한다.
     * // @param sunny
     */
    public void onUserSaved(String emailParam) {
        // 가입 정보 재료 획득 =======================================================================
        // 토큰이 활성화 될때까지 못넘어간다 = > 블럭 코드라서 앱이 먹통이 된다.
        String token = FirebaseInstanceId.getInstance().getToken();
        while (token == null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // 회원 정보를 디비에 입력
        String id = emailParam.split("@")[0];
        String email = emailParam;
        // 회원 정보 생성 ===========================================================================
        User user = new User(id, email, FirebaseInstanceId.getInstance().getToken());
        // 디비 입력
        databaseReference.child("users").child(getUid()).setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //로그인
                            onLogin(null);
                        }
                    }
                });
    }

    public void onLogin(View view) {
        if (!isValidate()) return;
        // 1. 로딩
        showProgress("로그인중...");
        // 2. 이메일비번 획득
        final String email = email_et.getText().toString();
        final String pwd = password_et.getText().toString();

        // 3. 로그인
        firebaseAuth.signInWithEmailAndPassword(email, pwd)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // 4. 로딩닫기
                        hideProgress();
                        if (task.isSuccessful()) {
                            Log.i("CHAT", "성공");
                            // 서비스로 이동
                            StorageHelper.getInstance().setString(SignUpActivity.this, "nickname", email.split("@")[0]);
                            StorageHelper.getInstance().setString(SignUpActivity.this, "email", email);
                            StorageHelper.getInstance().setString(SignUpActivity.this, "password", pwd);
                            // 로그인을 성공하면 토큰을 바로 갱신한다.
                            // 매번 하는 것보다는 변경되었을 때 혹은 값이 다를 때만 수행
                            updateToken();
                            goCenter();
                        } else {
                            // 실패
                            Log.i("CHAT", "실패" + task.getException());
                        }
                    }
                });
        if (!isValidate()) return;
    }

    public void updateToken() {
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference()
                .child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        // 수정을 위한 트랜잭션
        userReference.runTransaction(new Transaction.Handler() {
            // 트랜잭션 수행
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                User user = mutableData.getValue(User.class);
                if (user == null) {
                    return Transaction.success(mutableData);
                }
                String token = StorageHelper.getInstance().getString(SignUpActivity.this, "token");
                if (token == null) {
                    token = FirebaseInstanceId.getInstance().getToken();
                    StorageHelper.getInstance().setString(SignUpActivity.this, "token", token);
                }
                user.setToken(token);
                // 변경 내용을 다시 삽입
                mutableData.setValue(user);
                Log.i("FCM", "토큰 수정 완료");
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                // 완료
            }
        });
    }

    public void goCenter() {
        //if (getIntent().getStringExtra("FCM") != null) {
        if (cpm != null) {
            // 푸시 메시지 안에는 채팅채널값, 상대방 uid, 닉네임 등 정보가 같이 들어 있어야
            // 채팅방으로 바로 들어갈 수 있다.
            // 채팅 푸시를 받고 (자동)로그인까지 했다 -> ChatRoomActivity로 이동!
            Intent intent = new Intent(this, ChatRoomActivity.class);
            intent.putExtra("chatting_room_key", cpm.getChatting_room_key());
            intent.putExtra("you", new Post("", "", cpm.getUid(), cpm.getNickname()));
            startActivity(intent);
            finish();

//            Intent intent = new Intent(this, MainActivity2.class);
//            startActivity(intent);
//            finish();
        } else {
            // 그냥 왔다
            Intent intent = new Intent(this, CenterActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public boolean isValidate() {
        if (TextUtils.isEmpty(email_et.getText().toString())) {
            email_et.setError("이메일을 입력하세요");
            return false;
        } else {
            String email = email_et.getText().toString();
/*            boolean flag = Pattern.matches("^[a-zA-Z0-9]+@[a-zA-Z0-9]+$", email);
            if(!flag){
                email_et.setError("이메일 형식에 맞추어 입력하세요");
                return false;
            }*/
            email_et.setError(null);
        }
        if (TextUtils.isEmpty(password_et.getText().toString())) {
            password_et.setError("비밀번호를 입력하세요");
            return false;
        } else {
            if (password_et.length() < 6) {
                password_et.setError("비밀번호는 6자 이상이어야 합니다.");
                return false;
            }
            password_et.setError(null);
        }
        return true;
    }
}

