package com.howtographql.hackernews;

/*
servlet exposing the API
the goal is to expose the API over the web,
you’ll also make use of graphql-java-servlet
a simple helper library containing a ready-made servlet for ACCEPTING GraphQL queries
and javax.servlet-api (the servlet specification implementation)
 */


import com.coxautodev.graphql.tools.SchemaParser;
import graphql.schema.GraphQLSchema;
import graphql.servlet.SimpleGraphQLServlet;

import javax.servlet.annotation.WebServlet;

@WebServlet(urlPatterns = "/graphql")
public class GraphQLEndpoint extends SimpleGraphQLServlet {

    public GraphQLEndpoint() {
        super(buildSchema());
    }

    private static GraphQLSchema buildSchema() {
        LinkRepo linkRepository = new LinkRepo();
        return SchemaParser.newParser()
                .file("schema.graphqls")
                .resolvers(new Query(linkRepository), new Mutation(linkRepository))
                .build()
                .makeExecutableSchema();


    }
}
