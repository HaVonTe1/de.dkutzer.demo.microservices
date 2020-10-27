package de.dkutzer.buggy.developer.control


import com.mongodb.client.MongoClient
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters
import com.mongodb.client.model.FindOneAndUpdateOptions
import com.mongodb.client.model.Indexes
import com.mongodb.client.model.ReplaceOptions
import de.dkutzer.buggy.developer.entity.Developer
import de.dkutzer.buggy.developer.entity.toEntity
import io.quarkus.runtime.StartupEvent
import org.bson.Document
import org.bson.conversions.Bson
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.event.Observes
import javax.enterprise.inject.Default
import javax.inject.Inject
import kotlin.collections.ArrayList

@ApplicationScoped
class DeveloperRepository {

    companion object {
        const val DEVELOPERS = "developers"
        const val BUGGY_DB = "buggy"
    }

    @Inject
    lateinit var mongoClient: MongoClient

    fun getCollection(): MongoCollection<Document> {
        return mongoClient.getDatabase(BUGGY_DB).getCollection(DEVELOPERS)
    }

    fun onStart(@Observes ev: StartupEvent?) {
        getCollection().createIndex(Indexes.ascending("id"))
    }


    private val findOneAndUpdateOptions = ReplaceOptions().upsert(true)


    fun findAll(): Iterable<Developer> {
        val developersCol = getCollection()
        val documents = developersCol.find()
        val result: MutableList<Developer> = ArrayList()
        documents.iterator().forEachRemaining {
            result.add(getDeveloperByDocument(it))
        }

        return result
    }

    private fun getDeveloperByDocument(doc: Document) = Developer(doc.getString("id"), doc.getString("firstName"), doc.getString("lastName"))

    fun findById(id: String?): Optional<Developer> {
        val collection = getCollection()
        val iterable = collection.find(Document("id", id))
        if (iterable.iterator().hasNext()) {
            val document = iterable.first()
            return Optional.of(getDeveloperByDocument(document))
        }
        return Optional.empty()
    }


    fun upsert(developer: Developer): Developer {
        val collection = getCollection()
        return if (developer.id != null && developer.id.isNotEmpty()) {
            collection.replaceOne(getFilterByDeveloper(developer), getDocumentByDeveloper(developer), findOneAndUpdateOptions)
            return developer
        } else {
            val document = getDocumentByDeveloper(developer)
            collection.insertOne(document)
            document.toEntity();
        }
    }

    private fun getDocumentByDeveloper(developer: Developer): Document {
        val map: MutableMap<String, Any> = HashMap()
        if (developer.id != null && developer.id.isNotEmpty()) {
            map["id"] = developer.id
        } else {
            map["id"] = UUID.randomUUID().toString()
        }
        map["firstName"] = developer.firstName
        map["lastName"] = developer.lastName
        return Document(map)
    }

    private fun getFilterByDeveloper(developer: Developer): Bson {
        return Filters.eq("id", developer.id)
    }

    private fun getFilterById(id: String): Bson {
        return Document("id", id)
    }

    fun delete(id: String) {
        val collection = getCollection()
        collection.deleteOne(getFilterById(id))
    }

    fun exists(id: String?): Boolean {
        return id != null && id.isNotEmpty() && getCollection().countDocuments(getFilterById(id)) != 0L
    }

    fun deleteAll() {

        getCollection().deleteMany(Document())
    }


}