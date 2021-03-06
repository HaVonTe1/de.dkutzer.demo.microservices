package de.dkutzer.buggy.developer.boundary

import au.com.dius.pact.provider.PactVerifyProvider
import au.com.dius.pact.provider.junit.IgnoreNoPactsToVerify
import au.com.dius.pact.provider.junit.Provider
import au.com.dius.pact.provider.junit.State
import au.com.dius.pact.provider.junit.loader.PactBroker
import au.com.dius.pact.provider.junit.loader.PactFolder
import au.com.dius.pact.provider.junit5.HttpTestTarget
import au.com.dius.pact.provider.junit5.PactVerificationContext
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider
import de.dkutzer.buggy.developer.control.DeveloperRepository
import de.dkutzer.buggy.developer.control.MessageGateway
import de.dkutzer.buggy.developer.entity.Developer
import io.mockk.every
import io.mockk.slot

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestTemplate
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.messaging.Message
import org.springframework.security.test.context.support.TestExecutionEvent
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.context.support.WithSecurityContext
import org.springframework.test.context.ActiveProfiles

import java.net.URL


@Provider("dkutzer-msdemo-buggy-developers-rest")
//@PactFolder("../pacts")
@PactBroker(host = "localhost",port="9292", scheme = "http" )
@IgnoreNoPactsToVerify
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@WithMockUser(username="spring", roles = ["BUGGY_UI"],setupBefore = TestExecutionEvent.TEST_EXECUTION)
class DeveloperControllerTest {

    companion object {

        private const val DEV_TEST_ID = "cd3535b8-7781-4755-a58b-05c10354ea99"
    }

    @LocalServerPort
    protected var port: Int = 0

    @Autowired
    lateinit var developerRepository : DeveloperRepository

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider::class)
    fun pactVerificationTestTemplate(context: PactVerificationContext? = null) {
        context?.verifyInteraction()
    }

    @BeforeEach
    fun before(pactVerificationContext: PactVerificationContext? = null) {

        val fromUrl = HttpTestTarget.fromUrl(URL("http://localhost:"+port))
        pactVerificationContext?.target = fromUrl


//        System.setProperty("pact.provider.tag", "dev");
//        System.setProperty("pact.provider.version", "version1");
//        System.setProperty("pact.verifier.publishResults", "true");

    }

    @State("Developer with ID $DEV_TEST_ID exists")
    fun createTestDev() {
        developerRepository.save(Developer(DEV_TEST_ID,"John","Lennon"))
    }

    @State("No special conditions")
    fun reset() {
        developerRepository.deleteAll()
    }

}



