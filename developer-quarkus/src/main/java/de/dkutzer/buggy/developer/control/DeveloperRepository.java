package de.dkutzer.buggy.developer.control;

import com.google.common.collect.Lists;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import de.dkutzer.buggy.developer.entity.Developer;
import java.util.List;
import org.bson.Document;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class DeveloperRepository
{
    @Inject
    MongoClient mongoClient;


    public Iterable<Developer> findAll() {

        final MongoCollection<Document> developersCol = mongoClient.getDatabase("buggy").getCollection("developers");
        final FindIterable<Document> documents = developersCol.find();
        List<Developer> result = Lists.newArrayList();
        for(Document doc : documents){
            final Developer e = new Developer();
            e.setId(doc.getString("id"));
            e.setFirstName(doc.getString("firstName"));
            e.setLastName(doc.getString("lastName"));
            result.add(e);
        }
        return result;
    }
}