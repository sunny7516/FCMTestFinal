package com.example.tacademy.recyclerviewtest.ui.push;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.example.tacademy.recyclerviewtest.R;
import com.example.tacademy.recyclerviewtest.model.ReqSendFCM;
import com.example.tacademy.recyclerviewtest.model.ResSendFCM;
import com.example.tacademy.recyclerviewtest.net.RetroFitImpFactory;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RetroTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retro_test);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 통신
                sendFCM();
            }
        });
    }
    public void sendFCM(){

        // 통신
        Call<ResSendFCM> resSendFCMCall =
        RetroFitImpFactory.getInstance().getService().sendFCM(new ReqSendFCM("gkAZ2AyM71R30J0WKBqdFHT0VHl1","hi"));
        // 수행
        resSendFCMCall.enqueue(new Callback<ResSendFCM>() {
            @Override
            public void onResponse(Call<ResSendFCM> call, Response<ResSendFCM> response) {
                // 성공 : response.body() = new Gson().fromJson(data, ResSendFCM.class);
                Log.i("RETRO", response.body().getBody()+ " : "+response.body().getCode());
            }

            @Override
            public void onFailure(Call<ResSendFCM> call, Throwable t) {
                // 실패
            }
        });
    }
}