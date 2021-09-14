package de.dkutzer.buggy.developer.boundary


import de.dkutzer.buggy.developer.entity.Developer
import mu.KotlinLogging
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.constraints.ConstraintDescriptions
import org.springframework.restdocs.hypermedia.HypermediaDocumentation.*
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
import org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.snippet.Attributes.key
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.util.StringUtils
import org.springframework.web.context.WebApplicationContext
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName


private val logger = KotlinLogging.logger {}

@ExtendWith(RestDocumentationExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
@DirtiesContext
class DeveloperControllerRestDocsTest() {

    companion object {
        var mongodbImage = DockerImageName
            .parse("mongo:4.2.15")
            .asCompatibleSubstituteFor("mongo")

        @Container
        private val mongoContainer = MongoDBContainer(mongodbImage)

        @Container
        private val kafkaContainer = KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:5.4.3"))


        @DynamicPropertySource
        @JvmStatic
        fun registerDynamicProperties(registry: DynamicPropertyRegistry) {

            registry.add("spring.data.mongodb.uri", mongoContainer::getReplicaSetUrl)
            registry.add("spring.cloud.stream.kafka.binder.brokers", kafkaContainer::getHost)
            registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers)
        }
    }



    var mockMvc: MockMvc? = null

    val responseFields = listOf(
            fieldWithPath("firstName").description("Vorname").type("String"),
            fieldWithPath("lastName").description("Nachname").type("String"),
            subsectionWithPath("_links").description("<<resources-note-links,Links>> to other resources")
    )



    @BeforeEach
    fun setUp(context: WebApplicationContext?, restDocumentation: RestDocumentationContextProvider?) {
        mockMvc = MockMvcBuilders.webAppContextSetup(context!!)
                .apply<DefaultMockMvcBuilder>(documentationConfiguration(restDocumentation).uris().withScheme("https")
                        .withHost("buggy.io").withPort(443).and()
                        .operationPreprocessors().withRequestDefaults(prettyPrint())
                        .withResponseDefaults(prettyPrint()))
                .apply<DefaultMockMvcBuilder>(SecurityMockMvcConfigurers.springSecurity())
                .build()
    }

    @Test
    @WithMockUser(username = "test", password = "test", roles = ["USER"])
    fun `Testing The Creating of a new Developer (RestDocs)`() {

        val operationName = "developer-create"

        val developer = Developer("1", "Bill", "Gates")
        val fields: ConstrainedFields = ConstrainedFields(developer::class.java)

        // given - new Developer to be created
        val developerInDto = """{"id":"cd3535b8-7781-4755-a58b-05c10354ea99", "firstName":"Steve", "lastName":"Jobs"}"""
        // when - the resource is passed via POST
        mockMvc!!.perform(post("/developers/")
                .content(developerInDto)
                .contentType(MediaType.APPLICATION_JSON_VALUE).accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isCreated)
                .andExpect(jsonPath("$.firstName", `is`("Steve")))
                .andDo(
                        document(// rest docs
                                operationName,
                                requestFields(
                                        fields.withPath("id").description("Id").type("String"),
                                        fields.withPath("firstName").description("Vorname").type("String"),
                                        fields.withPath("lastName").description("Nachname").type("String")),
                                responseFields(responseFields),
                                links(halLinks(),
                                        linkWithRel("self").description("Link to the alpha resource"),
                                        linkWithRel("developer").description("Link to the bravo resource"))

                        ))


    }


    class ConstrainedFields internal constructor(input: Class<*>) {

        private val constraintDescriptions: ConstraintDescriptions = ConstraintDescriptions(input)

        fun withPath(path: String): FieldDescriptor {
            return fieldWithPath(path).attributes(key("constraints").value(StringUtils
                    .collectionToDelimitedString(this.constraintDescriptions
                            .descriptionsForProperty(path), ". ")))
        }
    }
}
