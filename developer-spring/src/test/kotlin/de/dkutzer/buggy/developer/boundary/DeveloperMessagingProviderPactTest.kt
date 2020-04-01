package de.dkutzer.buggy.developer.boundary

import au.com.dius.pact.provider.PactVerifyProvider
import au.com.dius.pact.provider.junit.Provider
import au.com.dius.pact.provider.junit.State
import au.com.dius.pact.provider.junit.loader.PactFolder
import au.com.dius.pact.provider.junit5.AmpqTestTarget
import au.com.dius.pact.provider.junit5.PactVerificationContext
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider
import au.com.dius.pact.provider.spring.junit5.PactVerificationSpringProvider
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ninjasquad.springmockk.MockkBean
import com.ninjasquad.springmockk.SpykBean
import de.dkutzer.buggy.developer.control.DeveloperRepository
import de.dkutzer.buggy.developer.control.MessageGateway
import de.dkutzer.buggy.developer.entity.Developer
import de.dkutzer.buggy.developer.entity.DeveloperCreated
import de.dkutzer.buggy.developer.entity.DeveloperDeleted
import de.dkutzer.buggy.developer.entity.DeveloperUpdated
import io.mockk.*
import io.mockk.proxy.MockKInstantiatior
import org.hamcrest.Matchers
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestTemplate
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.MediaType
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@Provider("dkutzer-msdemo-buggy-developers-messaging")
@PactFolder("../pacts")
class DeveloperMessagingProviderPactTest {



    val objectMapper =  jacksonObjectMapper()

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider::class)
    fun pactVerifactionTestTemplate(context  : PactVerificationContext){

        context.verifyInteraction()

    }

    @BeforeEach
    fun before(context: PactVerificationContext) {
        context.target = AmpqTestTarget()
    }

    @State("Developer with ID cd3535b8-7781-4755-a58b-05c10354ea99 exists")
    fun createTestDev() {
    }

    @State("No special conditions")
    fun reset() {
    }

    @PactVerifyProvider("UPDATED Event for Developer cd3535b8-7781-4755-a58b-05c10354ea99")
    fun testDeveloperUpdated():String{
        return """{"firstName":"Steve","lastName":"Jobs","id":"cd3535b8-7781-4755-a58b-05c10354ea99"}"""
    }

    @PactVerifyProvider("CREATED Event for Developer cd3535b8-7781-4755-a58b-05c10354ea99")
    fun testDeveloperCreated():String{
        return """{"firstName":"Bill","lastName":"Gates","id":"cd3535b8-7781-4755-a58b-05c10354ea99"}"""
    }
    @PactVerifyProvider("DELETED Event for Developer cd3535b8-7781-4755-a58b-05c10354ea99")
    fun testDeveloperDeleted():String{
        return objectMapper.writeValueAsString( DeveloperDeleted("cd3535b8-7781-4755-a58b-05c10354ea99"))
    }
}