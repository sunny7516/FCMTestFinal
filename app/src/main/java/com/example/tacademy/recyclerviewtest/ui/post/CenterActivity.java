package com.example.tacademy.recyclerviewtest.ui.post;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.tacademy.recyclerviewtest.R;
import com.example.tacademy.recyclerviewtest.ui.sign.SignUpActivity;
import com.example.tacademy.recyclerviewtest.db.StorageHelper;
import com.example.tacademy.recyclerviewtest.fg.MyChattingChannelFragment;
import com.example.tacademy.recyclerviewtest.fg.MyPostsFragment;
import com.example.tacademy.recyclerviewtest.fg.TopStarPostsFragment;
import com.example.tacademy.recyclerviewtest.fg.TotalPostsFragment;
import com.google.firebase.auth.FirebaseAuth;

public class CenterActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_center);

        //==상태바 색상===
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.RED);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 글쓰기로 이동
                Intent intent = new Intent(CenterActivity.this, NewPostActivity.class);
                startActivity(intent);
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_center, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            // 로그아웃 처리
            FirebaseAuth.getInstance().signOut();
            StorageHelper.getInstance().setString(this, "eamil", "");
            StorageHelper.getInstance().setString(this, "password", "");
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: return new TotalPostsFragment();        // 전체글 보기
                case 1: return new MyPostsFragment();           // 내글 보기
                case 2: return new TopStarPostsFragment();      // 탑글 보기
                case 3: return new MyChattingChannelFragment(); // 나의 채팅 목록 보기
            }
            return null;
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "자유게시판";
                case 1:
                    return "내글보기";
                case 2:
                    return "인기글";
                case 3:
                    return "채팅목록";
            }
            return null;
        }
    }
}
