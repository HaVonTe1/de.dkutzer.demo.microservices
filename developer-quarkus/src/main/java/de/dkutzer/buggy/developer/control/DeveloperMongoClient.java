package de.dkutzer.buggy.developer.control;

import com.mongodb.client.MongoClient;

import javax.inject.Inject;

public class DeveloperMongoClient {

    private static DeveloperMongoClient instance;

    @Inject
    private  MongoClient mongoClient;

    private DeveloperMongoClient () {


    }

    public static synchronized DeveloperMongoClient getInstance () {
        if (DeveloperMongoClient.instance == null) {
            DeveloperMongoClient.instance = new DeveloperMongoClient ();
        }
        return DeveloperMongoClient.instance;
    }

    public MongoClient getMongoClient() {
        return mongoClient;
    }
}
