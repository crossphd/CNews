package com.crossphd.cnews;

/**
 * Created by chris on 1/24/2018.
 */

public class Article {

    private String mSource;
    private String mAuthor;
    private String mTitle;
    private String mUrl;
    private String mImageUrl;
    private String mDate;

    public Article(String source, String author, String title, String url, String date, String image){
        mSource = source;
        mAuthor = author;
        mTitle = title;
        mUrl = url;
        mDate = date;
        mImageUrl = image;
    }

    public String getmSource() {
        return mSource;
    }

    public String getmAuthor() {
        return mAuthor;
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getmUrl() {
        return mUrl;
    }

    public String getmDate() {
        return mDate;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }
}
