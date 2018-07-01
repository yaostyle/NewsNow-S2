package com.android.chrishsu.newsnow;

// Create News class
public class News {
    // Setting private vars
    private String newsTitle;
    private String newsSectionName;
    private String newsPublicationDate;
    private String newsUrl;
    private String newsAuthor;

    // Create News constructor that takes title, section, date and url
    public News(String newsTitle,
                String newsSectionName,
                String newsPublicationDate,
                String newsUrl,
                String newsAuthor) {
        this.newsTitle = newsTitle;
        this.newsSectionName = newsSectionName;
        this.newsPublicationDate = newsPublicationDate;
        this.newsUrl = newsUrl;
        this.newsAuthor = newsAuthor;
    }

    // Getter for getting news title
    public String getNewsTitle() {
        return newsTitle;
    }

    // Getter for getting news section
    public String getNewsSectionName() {
        return newsSectionName;
    }

    // Getter for getting publication date
    public String getNewsPublicationDate() {
        return newsPublicationDate;
    }

    // Getter for getting news web url
    public String getNewsUrl() {
        return newsUrl;
    }

    //Getter for getting news author
    public String getNewsAuthor() { return newsAuthor; }
}
