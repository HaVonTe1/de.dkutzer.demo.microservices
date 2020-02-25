package de.dkutzer.buggy.developer.boundary


import de.dkutzer.buggy.developer.entity.Developer
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
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.util.StringUtils
import org.springframework.web.context.WebApplicationContext


@ExtendWith(RestDocumentationExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class DeveloperControllerRestDocsTest() {

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
                .build()
    }

    @Test
    fun `Testing The Creating of a new Developer (RestDocs)`() {

        val operationName = "developer-create"

        val developer = Developer("1", "Bill", "Gates")
        val fields: ConstrainedFields = ConstrainedFields(developer::class.java)

        // given - new Developer to be created
        val developerInDto = "{\"id\":\"1\",\"firstName\":\"Steve\",\"lastName\":\"Jobs\"}"
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