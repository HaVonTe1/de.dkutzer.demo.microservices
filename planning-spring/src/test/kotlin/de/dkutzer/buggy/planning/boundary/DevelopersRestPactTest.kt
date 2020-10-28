package de.dkutzer.buggy.planning.boundary

import au.com.dius.pact.consumer.MockServer
import au.com.dius.pact.consumer.dsl.PactDslJsonBody
import au.com.dius.pact.consumer.dsl.PactDslWithProvider
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt
import au.com.dius.pact.consumer.junit5.PactTestFor
import au.com.dius.pact.consumer.junit5.ProviderType

import au.com.dius.pact.core.model.RequestResponsePact
import au.com.dius.pact.core.model.annotations.Pact
import au.com.dius.pact.core.model.annotations.PactFolder
import au.com.dius.pact.core.model.messaging.Message
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import de.dkutzer.buggy.planning.control.DeveloperRepository
import de.dkutzer.buggy.planning.control.DeveloperServices
import de.dkutzer.buggy.planning.control.IssuesRepository
import de.dkutzer.buggy.planning.control.IssuesServices
import de.dkutzer.buggy.planning.entity.Developer
import de.dkutzer.buggy.planning.entity.DeveloperEvent

import io.mockk.clearAllMocks
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import mu.KotlinLogging
import org.apache.http.Header
import org.apache.http.client.fluent.Request
import org.apache.http.entity.ContentType
import org.apache.http.message.BasicHeader
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.Extensions
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import java.util.*


private val logger = KotlinLogging.logger {}
@PactTestFor(providerName = "dkutzer-msdemo-buggy-developers-rest")
@Extensions(
        ExtendWith(PactConsumerTestExt::class),
        ExtendWith(MockKExtension::class)
)
//@PactFolder("../pacts")

class DevelopersRestListenerPactTest {

    companion object {
        private const val CONSUMER = "dkutzer-msdemo-buggy-planning"

        private const val PROVIDER = "dkutzer-msdemo-buggy-developers-rest"

        private const val DEV_TEST_ID = "cd3535b8-7781-4755-a58b-05c10354ea99"
    }

    val objectMapper =  jacksonObjectMapper().enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
    val developerRepository= mockk<DeveloperRepository>(relaxed = true)
    val issuesRepository= mockk<IssuesRepository>(relaxed = true)

    val developerService = DeveloperServices(developerRepository)
    val issueService = IssuesServices(issuesRepository )

    @BeforeEach
    fun setup(){
        clearAllMocks()
    }

    @Pact(provider = PROVIDER, consumer = CONSUMER)
    fun sendDeveloperDeletedEvent(builder: PactDslWithProvider): RequestResponsePact {
        return builder
                .given("Developer with ID $DEV_TEST_ID exists")
                .uponReceiving("DELETE Request for Developer $DEV_TEST_ID")
                .path("/developers/$DEV_TEST_ID")
                .method("DELETE")
                .willRespondWith()
                .status(HttpStatus.NO_CONTENT.value())
                .toPact()
    }

    @Pact(provider = PROVIDER, consumer = CONSUMER)
    fun sendDeveloperCreatedEvent(builder: PactDslWithProvider): RequestResponsePact {
        val body = PactDslJsonBody()
                .uuid("id", UUID.fromString(DEV_TEST_ID))
                .stringType("firstName", "Bill")
                .stringType("lastName", "Gates")


        return builder
                .given("No special conditions")
                .uponReceiving("CREATE Request for Developer $DEV_TEST_ID")
                .path("/developers/")
                .method("POST")
                .body(body)
                .headers(HttpHeaders.CONTENT_TYPE,MediaType.APPLICATION_JSON_VALUE)
                .willRespondWith()
                .status(HttpStatus.CREATED.value())
                .toPact()
    }

    @Pact(provider = PROVIDER, consumer = CONSUMER)
    fun sendDeveloperUpdatedEvent(builder: PactDslWithProvider): RequestResponsePact {
        val body = PactDslJsonBody()
                .uuid("id", UUID.fromString(DEV_TEST_ID))
                .stringType("firstName", "Steve")
                .stringType("lastName", "Jobs")

        return builder
                .given("Developer with ID $DEV_TEST_ID exists")
                .uponReceiving("UPDATE Request for Developer $DEV_TEST_ID")
                .path("/developers/$DEV_TEST_ID")
                .method("PUT")
                .body(body)
                .headers(HttpHeaders.CONTENT_TYPE,MediaType.APPLICATION_JSON_VALUE)
                .willRespondWith()
                .status(HttpStatus.NO_CONTENT.value())
                .toPact()
    }


    @Test
    @PactTestFor(pactMethod = "sendDeveloperDeletedEvent", providerType = ProviderType.SYNCH)
    fun testSendDeveloperDeletedEvent(mockServer: MockServer) {
        val httpResponse = Request.Delete(mockServer.getUrl() + "/developers/$DEV_TEST_ID").execute().returnResponse()
        assertThat(httpResponse.statusLine.statusCode, `is`(equalTo(HttpStatus.NO_CONTENT.value())))
    }

    @Test
    @PactTestFor(pactMethod = "sendDeveloperCreatedEvent", providerType = ProviderType.SYNCH)
    fun testSendDeveloperCreatedEvent(mockServer: MockServer) {
        val httpResponse = Request.Post(mockServer.getUrl() + "/developers/")
                .bodyString("""{"id":$DEV_TEST_ID, "firstName":"Bill","lastName":"Gates"}""", ContentType.APPLICATION_JSON)
                .execute().returnResponse()
        assertThat(httpResponse.statusLine.statusCode, `is`(equalTo(HttpStatus.CREATED.value())))
    }

    @Test
    @PactTestFor(pactMethod = "sendDeveloperUpdatedEvent", providerType = ProviderType.SYNCH)
    fun testSendDeveloperUpdatedEvent(mockServer: MockServer) {
        val httpResponse = Request.Put(mockServer.getUrl() + "/developers/$DEV_TEST_ID")
                .bodyString("""{"id":$DEV_TEST_ID, "firstName":"Steve","lastName":"Jobs"}""", ContentType.APPLICATION_JSON)
                .execute().returnResponse()
        assertThat(httpResponse.statusLine.statusCode, `is`(equalTo(HttpStatus.NO_CONTENT.value())))
    }


}