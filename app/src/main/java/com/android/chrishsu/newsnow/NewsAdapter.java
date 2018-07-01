package com.android.chrishsu.newsnow;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

// Create a new adapter for News
public class NewsAdapter extends ArrayAdapter<News> {

    // Create a temporary ViewHolder class for optimizing ListView performance
    static class ViewHolder {
        TextView title;
        TextView section;
        TextView date;
        TextView author;
    }

    // Create NewsAdapter constructor that takes: context & news list
    public NewsAdapter(Context context, List<News> newslist) {
        super(context, 0, newslist);
    }

    // Override getView
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Create a list view for convertView
        View listNewsView = convertView;

        // if convertView is empty (fresh init), inflate its layout
        if (listNewsView == null) {
            listNewsView = LayoutInflater.from(getContext())
                    .inflate(R.layout.news_list_item,
                            parent,
                            false);
        }

        // Create a view holder instance with current news data
        ViewHolder holder = new ViewHolder();
        News currentNews = getItem(position);

        // Connect view holder with layout for news title
        holder.title = listNewsView.findViewById(R.id.news_title);
        holder.title.setText(currentNews.getNewsTitle());

        // Connect view holder with layout for news section
        holder.section = listNewsView.findViewById(R.id.news_section);
        holder.section.setText(currentNews.getNewsSectionName());

        // Connect view holder with layout for news published date
        holder.date = listNewsView.findViewById(R.id.news_pubdate);
        holder.date.setText(currentNews.getNewsPublicationDate());

        // Connect view holder with layout for news author
        holder.author = listNewsView.findViewById(R.id.news_author);
        holder.author.setText(currentNews.getNewsAuthor());

        // Return the list
        return listNewsView;
    }
}
