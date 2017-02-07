package com.example.tacademy.recyclerviewtest.model;

/**
 * 댓글 담는 그릇
 */

public class Comment {
    String uid, comment, author;

    public Comment() {
    }

    public Comment(String uid, String comment, String author) {
        this.uid = uid;
        this.comment = comment;
        this.author = author;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
