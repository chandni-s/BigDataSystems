package com.howtographql.hackernews;

import java.util.ArrayList;
import java.util.List;

public class LinkRepo {

    // add-save Links

    private List<Link> links;

    public LinkRepo() {
        this.links = new ArrayList<>();
        links.add(new Link("www.google.com", "Google"));
    }

    public void addLink(Link link) {
        System.out.println("got called");
        links.add(link);
    }

    public List<Link> getAllLinks() {
        links.forEach(link -> {
            System.out.println(link.getDescription() + " " + link.getUrl());
        });
        return links;
    }

}
