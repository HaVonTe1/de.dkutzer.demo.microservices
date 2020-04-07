package de.dkutzer.buggy.developer.boundary

import au.com.dius.pact.provider.PactVerifyProvider
import au.com.dius.pact.provider.junit.Provider
import au.com.dius.pact.provider.junit.State
import au.com.dius.pact.provider.junit.loader.PactFolder
import au.com.dius.pact.provider.junit5.AmpqTestTarget
import au.com.dius.pact.provider.junit5.PactVerificationContext
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import de.dkutzer.buggy.developer.control.DeveloperEventHandler
import de.dkutzer.buggy.developer.control.MessageGateway
import de.dkutzer.buggy.developer.entity.Developer
import de.dkutzer.buggy.developer.entity.DeveloperDeleted
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.slot
import mu.KotlinLogging
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestTemplate
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.cloud.stream.messaging.Source
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel

private val logger = KotlinLogging.logger {}
// tag::PACT[]
@Provider("dkutzer-msdemo-buggy-developers-messaging")
@PactFolder("../pacts")
@ExtendWith(MockKExtension::class)
class DeveloperMessagingProviderPactTest {
// end::PACT[]

    var messageChannel = mockk<MessageChannel>(name = "messageChannelMock")
    var channel = mockk<Source>(name = "channelMock")

    var messageGateway =  MessageGateway(channel)

    val developerEventHandler = DeveloperEventHandler(messageGateway)

    val objectMapper =  jacksonObjectMapper().configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY,true)

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider::class)
    fun pactVerificationTestTemplate(context  : PactVerificationContext){

        context.verifyInteraction()

    }

    @BeforeEach
    fun before(context: PactVerificationContext) {
        context.target = AmpqTestTarget()
        every { channel.output() } returns messageChannel
    }

    @State("Developer with ID cd3535b8-7781-4755-a58b-05c10354ea99 exists")
    fun createTestDev() {
    }

    @State("No special conditions")
    fun reset() {
    }

    @PactVerifyProvider("UPDATED Event for Developer cd3535b8-7781-4755-a58b-05c10354ea99")
    fun testDeveloperUpdated(): String {
        val slot = slot<Message<MessageGateway.Event>>()
        every { messageChannel.send(capture(slot)) } returns true
        developerEventHandler.handleDeveloperUpdated(Developer("cd3535b8-7781-4755-a58b-05c10354ea99", firstName = "Steve", lastName = "Jobs"))
        val captured = slot.captured.payload
        return objectMapper.writeValueAsString(captured)
    }

    @PactVerifyProvider("CREATED Event for Developer cd3535b8-7781-4755-a58b-05c10354ea99")
    fun testDeveloperCreated():String{
        val slot = slot<Message<MessageGateway.Event>>()
        every { messageChannel.send(capture(slot)) } returns true
        developerEventHandler.handleDeveloperCreated(Developer("cd3535b8-7781-4755-a58b-05c10354ea99", firstName = "Bill", lastName = "Gates"))
        val captured = slot.captured.payload
        return objectMapper.writeValueAsString(captured)
    }
    @PactVerifyProvider("DELETED Event for Developer cd3535b8-7781-4755-a58b-05c10354ea99")
    fun testDeveloperDeleted():String{
        val slot = slot<Message<MessageGateway.Event>>()
        every { messageChannel.send(capture(slot)) } returns true
        developerEventHandler.handleDeveloperDeleted(Developer("cd3535b8-7781-4755-a58b-05c10354ea99", firstName = "Wurscht", lastName = "Pelle"))
        val captured = slot.captured.payload
        return objectMapper.writeValueAsString(captured)
    }
}