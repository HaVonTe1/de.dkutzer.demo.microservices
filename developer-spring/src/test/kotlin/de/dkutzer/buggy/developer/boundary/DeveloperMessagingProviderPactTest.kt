package de.dkutzer.buggy.developer.boundary

import au.com.dius.pact.provider.junit.Provider
import au.com.dius.pact.provider.junit.State
import au.com.dius.pact.provider.junit5.AmpqTestTarget
import au.com.dius.pact.provider.junit5.PactVerificationContext
import au.com.dius.pact.provider.spring.junit5.PactVerificationSpringProvider
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestTemplate
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension

@Provider("dkutzer-msdemo-buggy-developers-messaging")
@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = [
            "management.server.port=0",
            "pactbroker.host=\${pact.broker.host:localhost}",
            "pactbroker.port=\${pact.broker.port:80}",
            "pactbroker.scheme=\${pact.broker.scheme:http}",
            "pactbroker.tags=\${pact.broker.tags:latest}",
            "pactbroker.auth.username=\${pact.broker.auth.username:scott}",
            "pactbroker.auth.password=\${pact.broker.auth.password:tiger}",
            "logging.level.org.apache.kafka.clients.consumer.ConsumerConfig=warn",
            "logging.level.org.apache.kafka.clients.producer.ProducerConfig=warn",
            "spring.sleuth.enabled=false"
            ])
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class DeveloperMessagingProviderPactTest {

    @TestTemplate
    @ExtendWith(PactVerificationSpringProvider::class)
    fun pactVerifactionTestTemplate(context  : PactVerificationContext){

        context.verifyInteraction()
    }

    @BeforeEach
    fun before(context: PactVerificationContext) {
        context.target = AmpqTestTarget()
    }

    @State("No special conditions")
    fun reset() {

        //
    }

}