package com.floern.genericbot.frame.stackexchange.api.model;

public class SuggestedEdit {

    private User proposing_user;
    private long creation_date;
    private String post_type;
    private long post_id;
    private int suggested_edit_id;
    private String comment;

    public User getProposingUser() {
        return proposing_user;
    }

    public long getCreationDate() {
        return creation_date;
    }

    public String getPostType() {
        return post_type;
    }

    public long getPostId() {
        return post_id;
    }

    public int getSuggestedEditId() {
        return suggested_edit_id;
    }

    public String getComment() {
        return comment;
    }

}
