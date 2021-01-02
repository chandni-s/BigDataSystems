package com.howtographql.hackernews;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

class RestClient {

    void getAllUrls() {
        String query = "{ \"query\" : \"query { getAllLinks { url description } }\" }";
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        try {
            HttpPost req = new HttpPost("http://localhost:8080/graphql");
            StringEntity params = new StringEntity(query);
            req.addHeader("content-type", "application/json");
            req.setEntity(params);
            HttpResponse res = httpClient.execute(req);
            String resJson = EntityUtils.toString(res.getEntity());
            System.out.println(resJson);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void createLink() {
        String query = "{ \"mutation\" : \"mutation createLink { createLink(url: \"www.fb.com\", descr: \"testing mutate\") {url description} } \" }";
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        try {
            HttpPost req = new HttpPost("http://localhost:8080/graphql");
            StringEntity params = new StringEntity(query);
            req.addHeader("content-type", "application/json");
            req.setEntity(params);
            HttpResponse res = httpClient.execute(req);
            String resJson = EntityUtils.toString(res.getEntity());
            System.out.println(resJson);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

class test {
    public static void main(String[] args) {
        RestClient c = new RestClient();
        c.getAllUrls();
        c.createLink();
        c.getAllUrls();
    }
}


