package com.howtographql.hackernews;

import com.coxautodev.graphql.tools.GraphQLRootResolver;

public class Mutation implements GraphQLRootResolver {

    private LinkRepo linkRepo;

    public Mutation(LinkRepo linkRepo) {
        this.linkRepo = linkRepo;
    }

    public Link createLink(String url, String descr) {
        System.out.println("creating link in mutation");
        Link ret = new Link(url, descr);
        linkRepo.addLink(ret);
        return ret;
    }

}
