package de.dkutzer.buggy.developer.boundary


import au.com.dius.pact.provider.junit5.HttpTestTarget
import au.com.dius.pact.provider.junit5.PactVerificationContext
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider
import au.com.dius.pact.provider.junitsupport.IgnoreNoPactsToVerify
import au.com.dius.pact.provider.junitsupport.Provider
import au.com.dius.pact.provider.junitsupport.State
import au.com.dius.pact.provider.junitsupport.loader.PactBroker
import au.com.dius.pact.provider.junitsupport.loader.PactFolder
import de.dkutzer.buggy.developer.control.DeveloperRepository
import de.dkutzer.buggy.developer.entity.Developer
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestTemplate
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import java.net.URL


@Testcontainers
@DirtiesContext
@Provider("dkutzer-msdemo-buggy-developers-rest")
//@PactBroker(host = "localhost",port="9292", scheme = "http" )
@PactFolder("../pacts")
@IgnoreNoPactsToVerify
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class DeveloperRestPactProviderTest {

    companion object {

        private const val DEV_TEST_ID = "cd3535b8-7781-4755-a58b-05c10354ea99"

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
