package com.example.tacademy.recyclerviewtest.fg;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

/** 내 글 중에서 별을 제일 많이 받은 글
 */
public class TopStarPostsFragment extends ParentFragment {

    public TopStarPostsFragment() {
        // Required empty public constructor
    }

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        Query query = databaseReference.child("user-posts").child(getUid())
                .orderByChild("star_count");
        return query;
    }

}
