package de.dkutzer.buggy.developer.boundary

import au.com.dius.pact.provider.junit.IgnoreNoPactsToVerify
import au.com.dius.pact.provider.junit.Provider
import au.com.dius.pact.provider.junit.loader.PactBroker
import au.com.dius.pact.provider.junit.loader.PactFolder
import au.com.dius.pact.provider.junit5.HttpTestTarget
import au.com.dius.pact.provider.junit5.PactVerificationContext
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager
import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.security.TestSecurity
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestTemplate
import org.junit.jupiter.api.extension.ExtendWith
import org.testcontainers.containers.MongoDBContainer
import java.net.URL


@QuarkusTest
@Provider("dkutzer-msdemo-buggy-developers-rest")
@PactFolder("../pacts")
//@PactBroker(host = "localhost",port="9292", scheme = "http" )
@IgnoreNoPactsToVerify
@TestSecurity(authorizationEnabled = false)
@QuarkusTestResource(MongoDBTestResource::class)
class DeveloperControllerTest {

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider::class)
    fun pactVerificationTestTemplate(context: PactVerificationContext? = null) {
        context?.verifyInteraction()
    }

    @BeforeEach
    fun before(pactVerificationContext: PactVerificationContext? = null) {

        val fromUrl = HttpTestTarget.fromUrl(URL("http://localhost:8081"))
        pactVerificationContext?.target = fromUrl

    }
}

class MongoDBTestResource : QuarkusTestResourceLifecycleManager {


    val mongoDBContainer: MongoDBContainer = MongoDBContainer()

    override fun start(): Map<String, String> {

        mongoDBContainer.start()
        System.setProperty("quarkus.mongodb.connection-string",
                "mongodb://" + mongoDBContainer.containerIpAddress + ":" + mongoDBContainer.firstMappedPort)
        return emptyMap()
    }

    override fun stop() {
        mongoDBContainer.close()
    }
}

