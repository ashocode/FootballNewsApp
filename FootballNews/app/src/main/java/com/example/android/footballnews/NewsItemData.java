package com.example.android.footballnews;

import android.util.Log;


public class NewsItemData implements NewsItem {
    private static final String TAG = NewsItemData.class.getSimpleName();
    private static final String UNKNOWN = "unknown";
    private String title = UNKNOWN;
    private String section = UNKNOWN;
    private String date = UNKNOWN;
    private String url;
    private String author = UNKNOWN;

    NewsItemData(String newsTitle, String newsSection, String newsDate, String newsUrl, String author) {
        try {
            this.title = newsTitle;
            this.section = newsSection;
            this.date = newsDate;
            this.url = newsUrl;
            this.author = author;
        } catch (NullPointerException e) {
            Log.e(TAG, "Some of the parameters for the constructor is null.", e);
        }
    }

    @Override
    public String getNewsTitle() {
        return this.title;
    }

    @Override
    public String getNewsSection() {
        return this.section;
    }

    @Override
    public String getNewsDate() {
        return this.date;
    }

    @Override
    public String getUrl() {
        return this.url;
    }

    @Override
    public String getAuthor() {
        return this.author;
    }
}
