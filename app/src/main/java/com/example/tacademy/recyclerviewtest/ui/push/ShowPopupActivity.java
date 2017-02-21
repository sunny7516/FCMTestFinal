package com.example.tacademy.recyclerviewtest.ui.push;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.tacademy.recyclerviewtest.R;
import com.example.tacademy.recyclerviewtest.ui.sign.SignUpActivity;
import com.example.tacademy.recyclerviewtest.util.ChatPushModel;

/**
 * 메시지가 오면 단독으로 뜬다 (폰이 꺼져 있어도 깨워서 뜬다)
 */

public class ShowPopupActivity extends AppCompatActivity {

    ChatPushModel cpm;

    // 백키 무시
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 푸시 메시지 획득
        cpm = (ChatPushModel)getIntent().getSerializableExtra("FCM");
        // 윈도우 설정 : 잠겨있어도 보이고, 화면을 유지하고, 뒤를 블러처리하고
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
                WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        // 등장 및 퇴장 애니메이션
        overridePendingTransition(R.anim.start_enter, R.anim.start_exit);
        // 화면 설정
        setContentView(R.layout.activity_show_popup);
        // 버튼 이벤트 처리
        TextView title = (TextView) findViewById(R.id.title);
        title.setText(cpm.getNickname()+"님이 보낸 메시지");
        TextView content = (TextView) findViewById(R.id.content);
        content.setText(cpm.getMsg());
    }

    // 앱으로 이동
    public void onOK(View view) {
        Intent intent = new Intent(this, SignUpActivity.class);
        // 푸시 메시지 전달
        intent.putExtra("FCM", cpm);
        startActivity(intent);
        finish();
    }

    // 창만 닫고 끝
    public void onClose(View view) {
        finish();
        //System.exit(0);
    }

    @Override
    public void finish() {
        // 닫기 애니메이션
        overridePendingTransition(R.anim.end_enter, R.anim.end_exit);

        super.finish();
    }
}