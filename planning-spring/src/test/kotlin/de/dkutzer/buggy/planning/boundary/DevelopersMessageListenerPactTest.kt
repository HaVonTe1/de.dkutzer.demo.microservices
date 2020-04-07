package de.dkutzer.buggy.planning.boundary

import au.com.dius.pact.consumer.MessagePactBuilder
import au.com.dius.pact.consumer.dsl.PactDslJsonBody
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt
import au.com.dius.pact.consumer.junit5.PactTestFor
import au.com.dius.pact.consumer.junit5.ProviderType
import au.com.dius.pact.core.model.ContentType
import au.com.dius.pact.core.model.OptionalBody
import au.com.dius.pact.core.model.annotations.Pact
import au.com.dius.pact.core.model.annotations.PactFolder
import au.com.dius.pact.core.model.messaging.Message
import au.com.dius.pact.core.model.messaging.MessagePact
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import de.dkutzer.buggy.planning.control.DeveloperRepository
import de.dkutzer.buggy.planning.control.DeveloperServices
import de.dkutzer.buggy.planning.control.IssuesRepository
import de.dkutzer.buggy.planning.control.IssuesServices
import de.dkutzer.buggy.planning.entity.*
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import mu.KotlinLogging
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.Extensions
import org.springframework.test.util.ReflectionTestUtils
import java.util.*


private val logger = KotlinLogging.logger {}
@PactTestFor(providerName = "dkutzer-msdemo-buggy-developers-messaging")
@Extensions(
        ExtendWith(PactConsumerTestExt::class),
        ExtendWith(MockKExtension::class)
)
@PactFolder("../pacts")
class DevelopersMessageListenerPactTest {

    companion object {
        private const val CONSUMER = "dkutzer-msdemo-buggy-planning"

        private const val PROVIDER = "dkutzer-msdemo-buggy-developers-messaging"

        private const val DEV_TEST_ID = "cd3535b8-7781-4755-a58b-05c10354ea99"
    }

    val objectMapper =  jacksonObjectMapper().enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
    val developerRepository= mockk<DeveloperRepository>(relaxed = true)
    val issuesRepository= mockk<IssuesRepository>(relaxed = true)

    val developerService = DeveloperServices(developerRepository)
    val issueService = IssuesServices(issuesRepository )
    val messageListener  = DevelopersMessageListener(developerService, issueService)

    @BeforeEach
    fun setup(){
        clearAllMocks()
    }

    @Pact(provider = PROVIDER, consumer = CONSUMER)
    fun receiveDeveloperDeletedEvent(builder: MessagePactBuilder): MessagePact {

        val body = PactDslJsonBody()
                .uuid("id", UUID.fromString(DEV_TEST_ID))
        return builder
                .given("Developer with ID $DEV_TEST_ID exists")
                .expectsToReceive("DELETED Event for Developer $DEV_TEST_ID")
                .withContent(body)
                .withMetadata(java.util.Map.of(
                         "type", "deleted"))
                .hasPactWith(PROVIDER)
                .toPact()
    }

    @Pact(provider = PROVIDER, consumer = CONSUMER)
    fun receiveDeveloperCreatedEvent(builder: MessagePactBuilder): MessagePact {
        val body = PactDslJsonBody()
                .uuid("id", UUID.fromString(DEV_TEST_ID))
                .stringType("firstName", "Bill")
                .stringType("lastName", "Gates")

        return builder
                .given("No special conditions")
                .expectsToReceive("CREATED Event for Developer $DEV_TEST_ID")
                .withOrderedContent<DeveloperCreated>(body)
                .withMetadata(java.util.Map.of(
                        "type", "created"))
                .hasPactWith(PROVIDER)
                .toPact()
    }

    @Pact(provider = PROVIDER, consumer = CONSUMER)
    fun receiveDeveloperUpdatedEvent(builder: MessagePactBuilder): MessagePact {
        val body = PactDslJsonBody()
                .uuid("id", UUID.fromString(DEV_TEST_ID))
                .stringType("firstName", "Steve")
                .stringType("lastName", "Jobs")
        return builder
                .given("Developer with ID $DEV_TEST_ID exists")
                .expectsToReceive("UPDATED Event for Developer $DEV_TEST_ID")
                .withOrderedContent<DeveloperUpdated>(body)
                .withMetadata(java.util.Map.of(
                        "type", "updated"))

                .hasPactWith(PROVIDER)
                .toPact()
    }
    private inline fun <reified T> PactDslJsonBody.toStringOrdered():String {
        return objectMapper.writeValueAsString(objectMapper.readValue(this.toString(), T::class.java))
    }

    private inline fun <reified T> MessagePactBuilder.withOrderedContent(body:PactDslJsonBody) : MessagePactBuilder {
        val builder = withContent(body)
        val messages = ReflectionTestUtils.getField(this, "messages") as MutableList<Message>
        val message = messages.last()
        message.contents = OptionalBody.body(body.toStringOrdered<T>().toByteArray(ContentType.JSON.asCharset()), ContentType.JSON)
        return builder
    }

    private fun DeveloperEvent.toEntity(): Developer = Developer(id, "$firstName $lastName")

    @Test
    @PactTestFor(pactMethod = "receiveDeveloperDeletedEvent", providerType = ProviderType.ASYNCH)
    fun testReceiveDeveloperDeletedEvent(messages: List<Message>) {
        messageListener.handleDeveloperDeletedEvent(objectMapper.readValue(messages[0].contents.valueAsString(), DeveloperDeleted::class.java));
        verify(exactly = 1) { developerRepository.deleteById(eq(DEV_TEST_ID)) }
    }

    @Test
    @PactTestFor(pactMethod = "receiveDeveloperCreatedEvent", providerType = ProviderType.ASYNCH)
    fun testReceiveDeveloperCreatedEvent(messages: List<Message>) {
        val developerCreated = objectMapper.readValue(messages[0].contents.valueAsString(), DeveloperCreated::class.java)
        every { developerRepository.save(any<Developer>()) } answers {logger.debug { "saved" }; developerCreated.toEntity() }
        messageListener.handleDeveloperCreatedEvent(developerCreated);
        verify(exactly = 1) { developerRepository.save(any<Developer>()) }
    }

    @Test
    @PactTestFor(pactMethod = "receiveDeveloperUpdatedEvent", providerType = ProviderType.ASYNCH)
    fun testReceiveDeveloperUpdatedEvent(messages: List<Message>) {
        val developerUpdated = objectMapper.readValue(messages[0].contents.valueAsString(), DeveloperUpdated::class.java)
        every { developerRepository.save(any<Developer>()) } answers {logger.debug { "saved" }; developerUpdated.toEntity() }
        messageListener.handleDeveloperUpdatedEvent(developerUpdated);
        verify(exactly = 1) { developerRepository.save(any<Developer>()) }
    }


}