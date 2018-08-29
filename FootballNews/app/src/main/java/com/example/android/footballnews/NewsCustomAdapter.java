package com.example.android.footballnews;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;


public class NewsCustomAdapter extends ArrayAdapter<NewsItem> implements View.OnClickListener{

    NewsCustomAdapter(Context context, int resource, List<NewsItem> data) {
        super(context, resource, data);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            LayoutInflater inflater;
            inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.news_item, null);
        }

        NewsItem newsItem = getItem(position);

        if (newsItem != null) {
            TextView newsTitle = (TextView) view.findViewById(R.id.title);
            TextView newsSection = (TextView) view.findViewById(R.id.section);
            TextView newsDate = (TextView) view.findViewById(R.id.date);
            TextView authorName = (TextView)view.findViewById(R.id.author);

            if (newsTitle != null) {
                newsTitle.setText(newsItem.getNewsTitle());
            }

            if (newsSection != null) {
                newsSection.setText(newsItem.getNewsSection());
            }

            if (newsDate != null) {
                newsDate.setText(newsItem.getNewsDate());
            }

            if (authorName != null){
                authorName.setText(newsItem.getAuthor());
            }
        }

        return view;
    }

    @Override
    public void onClick(View view) {}
}
