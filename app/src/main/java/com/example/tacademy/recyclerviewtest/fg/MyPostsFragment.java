package com.example.tacademy.recyclerviewtest.fg;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

/**
 * 내가 작성한 글
 */
public class MyPostsFragment extends ParentFragment {

    public MyPostsFragment() {
        // Required empty public constructor
    }

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        Query query = databaseReference.child("user-posts").child(getUid());
        return query;
    }

}
