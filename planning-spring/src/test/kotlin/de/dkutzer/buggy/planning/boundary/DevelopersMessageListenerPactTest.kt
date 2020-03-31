package de.dkutzer.buggy.planning.boundary

import au.com.dius.pact.consumer.MessagePactBuilder
import au.com.dius.pact.consumer.MockServer
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt
import au.com.dius.pact.consumer.junit5.PactTestFor
import au.com.dius.pact.consumer.junit5.ProviderType
import au.com.dius.pact.core.model.annotations.Pact
import au.com.dius.pact.core.model.messaging.Message
import au.com.dius.pact.core.model.messaging.MessagePact
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import de.dkutzer.buggy.planning.control.DeveloperRepository
import de.dkutzer.buggy.planning.control.DeveloperServices
import de.dkutzer.buggy.planning.entity.DeveloperDeleted
import io.pactfoundation.consumer.dsl.LambdaDsl
import io.pactfoundation.consumer.dsl.LambdaDsl.newJsonBody
import io.pactfoundation.consumer.dsl.LambdaDslJsonBody
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito
import org.springframework.messaging.support.GenericMessage
import java.util.*

@PactTestFor(providerName = "dkutzer-msdemo-buggy-developers-messaging")
@ExtendWith(PactConsumerTestExt::class)
class DevelopersMessageListenerPactTest {

    companion object {
        private const val CONSUMER = "dkutzer-msdemo-buggy-planning"

        private const val PROVIDER = "dkutzer-msdemo-buggy-developers-messaging"

        private const val DEV_TEST_ID = "cd3535b8-7781-4755-a58b-05c10354ea99"
    }

    private val objectMapper : ObjectMapper = ObjectMapper()
    private val developerRepository: DeveloperRepository = Mockito.mock(DeveloperRepository::class)
    private var developerService: DeveloperServices = DeveloperServices(developerRepository)
    private var messageListener : DevelopersMessageListener = DevelopersMessageListener(developerService)

    @Pact(provider = PROVIDER, consumer = CONSUMER)
    fun receiveDeveloperDeletedEvent(builder: MessagePactBuilder): MessagePact {
        return builder
                .given("Developer with ID $DEV_TEST_ID exists")
                .expectsToReceive("DELETED Event for Developer $DEV_TEST_ID")
                .withContent(newJsonBody {
                    it.uuid("id", UUID.fromString(DEV_TEST_ID))
                        }.build())
                .withMetadata(java.util.Map.of(
                         "type", "deleted"))

                .hasPactWith(PROVIDER)
                .toPact()
    }

    @Test
    @PactTestFor(pactMethod = "receiveDeveloperDeletedEvent", providerType = ProviderType.ASYNCH)
    fun testReceiveMachineDeletedEvent(messages: List<Message>) {

        val payload = messages[0].contents.valueAsString()
        val genericMessage = GenericMessage(payload)

        messageListener.handleDeveloperDeletedEvent(objectMapper.readValue(payload, DeveloperDeleted::class.java));
    }


}