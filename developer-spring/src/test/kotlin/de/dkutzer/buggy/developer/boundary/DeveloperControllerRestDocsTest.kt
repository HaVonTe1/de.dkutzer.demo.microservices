package de.dkutzer.buggy.developer.boundary


import de.dkutzer.buggy.developer.entity.Developer
import org.hamcrest.Matchers
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.constraints.ConstraintDescriptions
import org.springframework.restdocs.hypermedia.HypermediaDocumentation.*
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.util.StringUtils
import org.springframework.web.context.WebApplicationContext


@ExtendWith(RestDocumentationExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class DeveloperControllerRestDocsTest() {

    var mockMvc: MockMvc? = null

    final val constraintDescriptions = ConstraintDescriptions(Developer::class.java)
    val requestFields = listOf(
            fieldWithPath("id").description("Id").type("String").attributes(org.springframework.restdocs.snippet.Attributes.key("constraints").value(StringUtils.collectionToDelimitedString(constraintDescriptions.descriptionsForProperty("id"),". "))),
            fieldWithPath("firstName").description("Vorname").type("String").attributes(org.springframework.restdocs.snippet.Attributes.key("constraints").value(StringUtils.collectionToDelimitedString(constraintDescriptions.descriptionsForProperty("firstName"),". "))),
            fieldWithPath("lastName").description("Nachname").type("String").attributes(org.springframework.restdocs.snippet.Attributes.key("constraints").value(StringUtils.collectionToDelimitedString(constraintDescriptions.descriptionsForProperty("lastName"),". ")))

    )
    val responseFields = listOf(
            fieldWithPath("firstName").description("Vorname").type("String"),
            fieldWithPath("lastName").description("Nachname").type("String"),
            subsectionWithPath("_links").description("<<resources-note-links,Links>> to other resources")
    )


    @BeforeEach
    fun setUp(context: WebApplicationContext?, restDocumentation: RestDocumentationContextProvider?) {
        mockMvc = MockMvcBuilders.webAppContextSetup(context!!)
                .apply<DefaultMockMvcBuilder>(MockMvcRestDocumentation.documentationConfiguration(restDocumentation).uris().withScheme("https")
                        .withHost("buggy.io").withPort(443).and()
                        .operationPreprocessors().withRequestDefaults(Preprocessors.prettyPrint())
                        .withResponseDefaults(Preprocessors.prettyPrint()))
                .build()
    }

    @Test
    fun test1(){

        val operationName = "developer-create"

        val developerInDto = "{\"id\":\"1\",\"firstName\":\"Steve\",\"lastName\":\"Jobs\"}"
        // when - the resource is passed via POST
        // when - the resource is passed via POST
        mockMvc!!.perform(RestDocumentationRequestBuilders.post("/developers/")
                .content(developerInDto)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).accept(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated)
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName", Matchers.`is`("Steve"))) // rest docs
                .andDo(
                        document(
                                operationName,
                                requestFields(requestFields),
                                responseFields(responseFields),
                                links(halLinks(),
                                    linkWithRel("self").description("Link to the alpha resource"),
                                    linkWithRel("developer").description("Link to the bravo resource"))

                ))
//
//        Assert.assertEquals(developerRepository.count(), 1)
//        Assert.assertTrue(developerRepository.findByName("Steve Jobs").isPresent())

    }


}