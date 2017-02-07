package com.example.tacademy.recyclerviewtest.fg;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

/**
 * 모든 사람 글 중 Top10
 */
public class TotalPostsFragment extends ParentFragment {

    public TotalPostsFragment() {
        // Required empty public constructor
    }

    @Override
    public Query getQuery(DatabaseReference databaseReference){
        // 쿼리수행
        Query query = databaseReference.child("posts").limitToLast(10);
        return query;
    }

}
