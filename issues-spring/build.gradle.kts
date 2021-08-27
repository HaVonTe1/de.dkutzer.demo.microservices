import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.springframework.boot.gradle.tasks.bundling.BootBuildImage


plugins {
    id("org.springframework.boot") version "2.4.4"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.4.31"
    kotlin("plugin.spring") version "1.4.31"
    //    id("org.springframework.experimental.aot") version "0.9.1"
    id("org.asciidoctor.convert") version "1.5.8"
    id("com.adarshr.test-logger") version "1.7.0"

}




group = "de.dkutzer.buggy"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11
configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/milestone/") }
//    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
}

extra["pactVersion"] = "4.0.10"
extra["snippetsDir"] = file("build/generated-snippets")
extra["springBootAdminVersion"] = "2.3.1"
extra["springCloudVersion"] = "2020.0.2"
extra["testcontainersVersion"] = "1.15.2"

dependencies {

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("org.springframework.boot:spring-boot-starter-data-rest")
    implementation("org.springframework.boot:spring-boot-starter-hateoas")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("de.codecentric:spring-boot-admin-starter-client")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.springframework.cloud:spring-cloud-sleuth-zipkin")
    implementation("org.springframework.cloud:spring-cloud-starter-sleuth")
    implementation("org.springframework.cloud:spring-cloud-stream")
    implementation("org.springframework.cloud:spring-cloud-stream-binder-kafka")
    implementation("org.springframework.kafka:spring-kafka")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("io.micrometer:micrometer-registry-prometheus")
    runtimeOnly("io.r2dbc:r2dbc-postgresql")
    runtimeOnly("org.postgresql:postgresql")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.springframework.cloud:spring-cloud-starter-contract-stub-runner")
    testImplementation("org.springframework.cloud:spring-cloud-starter-contract-verifier")
    testImplementation("org.springframework.kafka:spring-kafka-test")
    testImplementation("org.springframework.restdocs:spring-restdocs-webtestclient")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:kafka")
    testImplementation("org.testcontainers:mongodb")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:r2dbc")


    implementation("org.javers:javers-spring-boot-starter-mongo:5.8.5")

//    implementation("org.springframework.cloud:spring-cloud-starter-oauth2")
//    implementation("org.springframework.security.oauth:spring-security-oauth2")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.springframework.cloud:spring-cloud-stream-test-support")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")

    testImplementation("au.com.dius:pact-jvm-provider-junit5:${property("pactVersion")}")
    testImplementation("au.com.dius:pact-jvm-provider-junit5-spring:${property("pactVersion")}")
    testImplementation("com.ninja-squad:springmockk:2.0.0")

}

dependencyManagement {
    imports {
        mavenBom("org.testcontainers:testcontainers-bom:${property("testcontainersVersion")}")
        mavenBom("de.codecentric:spring-boot-admin-dependencies:${property("springBootAdminVersion")}")
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}
tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    outputs.dir(file("build/generated-snippets"))
    useJUnitPlatform()
    testLogging {
        events = setOf(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED)
        exceptionFormat = TestExceptionFormat.FULL
        showStackTraces = true
        showExceptions = true
        showCauses = true
        showStandardStreams = true
    }
}

tasks.withType<BootBuildImage> {
    builder = "paketobuildpacks/builder:tiny"
    environment = mapOf("BP_NATIVE_IMAGE" to "true")
}




springBoot {
    buildInfo()
}
tasks.asciidoctor {
    inputs.dir(file("build/generated-snippets"))
    dependsOn(tasks.test)
    attributes.put("snippets", file("build/generated-snippets"))
    attributes.put("projectdir", file("build/generated-snippets").toString())
}


tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    version = "${project.version}"
    dependsOn(tasks.asciidoctor)

    into("static/docs") {
        from(file("build/asciidoc/html5"))
    }


}