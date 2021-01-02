package com.howtographql.hackernews;

public class Link {

    // POJO

    private final String url;
    private final String description;

    public Link(String url, String desc) {
        this.url = url;
        this.description = desc;
    }

    public String getUrl() {
        return url;
    }

    public String getDescription() {
        return description;
    }
}
