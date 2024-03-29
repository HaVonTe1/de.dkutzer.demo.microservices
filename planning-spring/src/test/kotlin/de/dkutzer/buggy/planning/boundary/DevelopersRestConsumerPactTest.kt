package de.dkutzer.buggy.planning.boundary

import au.com.dius.pact.consumer.MockServer
import au.com.dius.pact.consumer.dsl.PactDslJsonBody
import au.com.dius.pact.consumer.dsl.PactDslWithProvider
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt
import au.com.dius.pact.consumer.junit5.PactTestFor
import au.com.dius.pact.consumer.junit5.ProviderType
import au.com.dius.pact.core.model.RequestResponsePact
import au.com.dius.pact.core.model.annotations.Pact
import au.com.dius.pact.core.model.annotations.PactDirectory

import mu.KotlinLogging
import org.apache.http.HttpHeaders
import org.apache.http.HttpResponse
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpDelete
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpPut
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClientBuilder
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.core.IsEqual.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.Extensions
import org.springframework.cloud.contract.spec.internal.HttpStatus
import org.springframework.http.MediaType
import java.io.IOException
import java.security.GeneralSecurityException
import java.util.*


private val logger = KotlinLogging.logger {}
@PactTestFor(providerName = "dkutzer-msdemo-buggy-developers-rest")
@Extensions(
        ExtendWith(PactConsumerTestExt::class)
)
@PactDirectory("../pacts")

class DevelopersRestListenerPactTest {

    companion object {
        private const val CONSUMER = "dkutzer-msdemo-buggy-planning"

        private const val PROVIDER = "dkutzer-msdemo-buggy-developers-rest"

        private const val DEV_TEST_ID = "cd3535b8-7781-4755-a58b-05c10354ea99"
    }

    @Pact(provider = PROVIDER, consumer = CONSUMER)
    fun sendDeveloperDeletedEvent(builder: PactDslWithProvider): RequestResponsePact {
        return builder
                .given("Developer with ID $DEV_TEST_ID exists")
                .uponReceiving("DELETE Request for Developer $DEV_TEST_ID")
                .path("/developers/$DEV_TEST_ID")
                .method("DELETE")
                .willRespondWith()
                .status(HttpStatus.NO_CONTENT)
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
                .headers(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .willRespondWith()
                .status(HttpStatus.CREATED)
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
                .headers(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .willRespondWith()
                .status(HttpStatus.NO_CONTENT)
                .toPact()
    }





    @Test
    @PactTestFor(pactMethod = "sendDeveloperDeletedEvent", providerType = ProviderType.SYNCH)
    fun testSendDeveloperDeletedEvent(mockServer: MockServer) {
        val httpResponse = HttpClientBuilder.create().build().execute(HttpDelete(mockServer.getUrl() + "/developers/$DEV_TEST_ID"))
        assertThat(httpResponse!!.statusLine.statusCode, `is`(equalTo(HttpStatus.NO_CONTENT)))
    }

    @Test
    @PactTestFor(pactMethod = "sendDeveloperCreatedEvent", providerType = ProviderType.SYNCH)
    fun testSendDeveloperCreatedEvent(mockServer: MockServer) {
        val httpResponse = HttpClientBuilder.create().build()
            .execute(HttpPost(mockServer.getUrl() + "/developers/").apply {
                entity = StringEntity(
                    """{"id":"$DEV_TEST_ID", "firstName":"Bill","lastName":"Gates"}""",
                    ContentType.APPLICATION_JSON
                )
            })
        assertThat(httpResponse!!.statusLine.statusCode, `is`(equalTo(HttpStatus.CREATED)))
    }

    @Test
    @PactTestFor(pactMethod = "sendDeveloperUpdatedEvent", providerType = ProviderType.SYNCH)
    fun testSendDeveloperUpdatedEvent(mockServer: MockServer) {
        val httpResponse = HttpClientBuilder.create().build().execute(HttpPut(mockServer.getUrl() + "/developers/$DEV_TEST_ID").apply { entity=StringEntity("""{"id":"$DEV_TEST_ID", "firstName":"Steve","lastName":"Jobs"}""", ContentType.APPLICATION_JSON) })
        assertThat(httpResponse.statusLine.statusCode, `is`(equalTo(HttpStatus.NO_CONTENT)))
    }


}
