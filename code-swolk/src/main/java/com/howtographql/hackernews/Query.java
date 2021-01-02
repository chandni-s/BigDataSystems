package com.howtographql.hackernews;

import com.coxautodev.graphql.tools.GraphQLRootResolver;

import java.util.List;

public class Query implements GraphQLRootResolver {

    private final LinkRepo linkrepo;

    public Query(LinkRepo linkrepo) {
        this.linkrepo = linkrepo;
    }

    public List<Link> getAllLinks() {
        return linkrepo.getAllLinks();
    }


}
