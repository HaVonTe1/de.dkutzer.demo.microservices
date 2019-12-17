package de.dkutzer.buggy.developer.control;

import com.google.common.collect.Lists;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import de.dkutzer.buggy.developer.entity.Developer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bson.Document;
import org.bson.conversions.Bson;


import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class DeveloperRepository
{
    public static final String DEVELOPERS = "developers";
    public static final String BUGGY_DB = "buggy";

    @Inject
    MongoClient mongoClient;

    private UpdateOptions upsertOptions = new UpdateOptions().upsert(true);


    public Iterable<Developer> findAll() {

        final MongoCollection<Document> developersCol = getCollection();
        final FindIterable<Document> documents = developersCol.find();
        List<Developer> result = Lists.newArrayList();
        for(Document doc : documents){
            final Developer e = getDeveloperByDocument(doc);
            result.add(e);
        }
        return result;
    }

    private Developer getDeveloperByDocument(Document doc) {
        final Developer e = new Developer();
        e.setId(doc.getString("id"));
        e.setFirstName(doc.getString("firstName"));
        e.setLastName(doc.getString("lastName"));
        return e;
    }

    public Optional<Developer> findById(final String id){
        MongoCollection<Document> collection = getCollection();
        FindIterable<Document> iterable = collection.find(new Document("id", id));
        if(iterable.iterator().hasNext()){
            Document document = iterable.first();
            return Optional.of(getDeveloperByDocument(document));
        }
        return Optional.empty();
    }

    private MongoCollection<Document> getCollection() {
        return mongoClient.getDatabase(BUGGY_DB).getCollection(DEVELOPERS);
    }

    public void upsert(final Developer developer){

        MongoCollection<Document> collection = getCollection();
        collection.updateOne(getFilterByDeveloper(developer), getDocumentByDeveloper(developer), upsertOptions);
    }

    private Document getDocumentByDeveloper(Developer developer) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", developer.getId());
        map.put("firstName", developer.getFirstName());
        map.put("lastName",developer.getLastName());
        return new Document(map);
    }

    private Bson getFilterByDeveloper(Developer developer) {
        return new Document("id", developer.getId());
    }
}
