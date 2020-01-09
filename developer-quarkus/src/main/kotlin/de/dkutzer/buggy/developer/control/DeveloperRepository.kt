package de.dkutzer.buggy.developer.control

import com.google.common.collect.Lists
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Indexes
import com.mongodb.client.model.UpdateOptions
import de.dkutzer.buggy.developer.entity.Developer
import io.quarkus.runtime.StartupEvent
import org.bson.Document
import org.bson.conversions.Bson
import java.util.*
import java.util.function.Consumer
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.event.Observes
import javax.enterprise.inject.Default
import javax.inject.Inject

@ApplicationScoped
class DeveloperRepository {

    companion object {
        const val DEVELOPERS = "developers"
        const val BUGGY_DB = "buggy"
    }

    @Inject
    @field: Default
    lateinit var  mongoClient: MongoClient

    fun  getCollection(): MongoCollection<Document> {
        return mongoClient.getDatabase(BUGGY_DB).getCollection(DEVELOPERS)
    }

    fun onStart(@Observes ev: StartupEvent?) {
        getCollection().createIndex(Indexes.ascending("id"))
    }

    private val upsertOptions = UpdateOptions().upsert(true)


    fun findAll(): Iterable<Developer> {
        val developersCol = getCollection()
        val documents = developersCol.find()
        val result: MutableList<Developer> = Lists.newArrayList()
        documents.iterator().forEachRemaining {
            result.add(getDeveloperByDocument(it))
        }

        return result
    }

    private fun getDeveloperByDocument(doc: Document): Developer {
        val e = Developer()
        e.id = doc.getString("id")
        e.firstName = doc.getString("firstName")
        e.lastName = doc.getString("lastName")
        return e
    }

    fun findById(id: String?): Optional<Developer> {
        val collection = getCollection()
        val iterable = collection.find(Document("id", id))
        if (iterable.iterator().hasNext()) {
            val document = iterable.first()
            return Optional.of(getDeveloperByDocument(document))
        }
        return Optional.empty()
    }



    fun upsert(developer: Developer) : Boolean{
        val collection = getCollection()
        if (developer.id != null && developer.id!!.isNotEmpty()) {
            collection.updateOne(getFilterByDeveloper(developer), getDocumentByDeveloper(developer), upsertOptions)
            return true
        } else {
            collection.insertOne(getDocumentByDeveloper(developer))
            return false
        }
    }

    private fun getDocumentByDeveloper(developer: Developer): Document {
        val map: MutableMap<String, Any> = HashMap()
        if (developer.id != null && developer.id!!.isNotEmpty()) {
            map["id"] = developer.id as String
        }
        map["firstName"] = developer.firstName as String
        map["lastName"] = developer.lastName as String
        return Document(map)
    }

    private fun getFilterByDeveloper(developer: Developer): Bson {
        return Document("id", developer.id)
    }

    private fun getFilterById(id: String): Bson {
        return Document("id", id)
    }

    fun delete(id: String) {
        val collection = getCollection()
        collection.deleteOne(getFilterById(id))
    }

    fun exists(id: String): Boolean {
        return getCollection().countDocuments(getFilterById(id)) != 0L
    }


}