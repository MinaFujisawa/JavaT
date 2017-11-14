package com.example.aki.javaq.Domain.Entity;

/**
 * Created by AKI on 2017-07-13.
 */

public class PostComment {
    private String mCommentId;
    private String mPostId;
    private String mCommentBody;
    private long mCommentTime;
    private String mUserId;

    public PostComment(){}

    public PostComment(String mCommentId, String mPostId, String mCommentBody, long mCommentTime, String mUserId) {
        this.mCommentId = mCommentId;
        this.mPostId = mPostId;
        this.mCommentBody = mCommentBody;
        this.mCommentTime = mCommentTime;
        this.mUserId = mUserId;
    }

    public String getCommentId() {
        return mCommentId;
    }

    public void setCommentId(String mCommentId) {
        this.mCommentId = mCommentId;
    }

    public String getPostId() {
        return mPostId;
    }

    public void setPostId(String mPostId) {
        this.mPostId = mPostId;
    }

    public String getCommentBody() {
        return mCommentBody;
    }

    public void setCommentBody(String mCommentBody) {
        this.mCommentBody = mCommentBody;
    }

    public long getCommentTime() {
        return mCommentTime;
    }

    public void setCommentTime(long mCommentTime) {
        this.mCommentTime = mCommentTime;
    }

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String mUserId) {
        this.mUserId = mUserId;
    }
}