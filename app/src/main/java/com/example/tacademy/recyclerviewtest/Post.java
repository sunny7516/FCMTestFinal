package com.example.tacademy.recyclerviewtest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tacademy on 2017-02-06.
 */

public class Post {
    // 제목
    String title;
    // 내용
    String content;
    // uid (내부 관리용)
    String uid;
    // 작성자 아이디
    String author;
    // star 카운트(좋아요 개수)
    int star_count;
    // 누가 좋아요 했는지
    Map<String, Boolean> stars = new HashMap<>();

    public Post() {
    }

    public Post(String title, String content, String uid, String author) {
        this.title = title;
        this.content = content;
        this.uid = uid;
        this.author = author;
    }

    public Map<String, Object> toPostMap(){
        Map<String, Object> map = new HashMap<>();
        map.put("title", title);
        map.put("content", content);
        map.put("uid", uid);
        map.put("author", author);
        map.put("star_count", star_count);
        map.put("stars", stars);

        return map;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getStar_count() {
        return star_count;
    }

    public void setStar_count(int star_count) {
        this.star_count = star_count;
    }

    public Map<String, Boolean> getStars() {
        return stars;
    }

    public void setStars(Map<String, Boolean> stars) {
        this.stars = stars;
    }
}
