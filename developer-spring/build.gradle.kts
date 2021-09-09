import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent


import org.springframework.boot.gradle.tasks.bundling.BootBuildImage


plugins {
    id("com.adarshr.test-logger") version "1.7.0"
    id("org.springframework.boot") version "2.5.4"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    //id("org.asciidoctor.convert") version "1.5.8"
    kotlin("jvm") version "1.5.21"
    kotlin("plugin.spring") version "1.5.21"
}


group = "de.dkutzer.buggy"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_16

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}



repositories {
    maven { url = uri("https://repo.spring.io/release") }
    mavenCentral()
}


extra["pactVersion"] = "4.0.10"
extra["snippetsDir"] = file("build/generated-snippets")
extra["springBootAdminVersion"] = "2.4.3"
extra["springCloudVersion"] = "2020.0.3"
extra["testcontainersVersion"] = "1.16.0"
extra["keycloakVersion"] = "15.0.2"
extra["junitJupiterVersion"] = "5.4.2"


dependencies {

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-data-rest")
    implementation("org.springframework.boot:spring-boot-starter-hateoas")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-quartz")
   // implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("de.codecentric:spring-boot-admin-starter-client")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.springframework.cloud:spring-cloud-sleuth-zipkin")
    implementation("org.springframework.cloud:spring-cloud-starter-sleuth")
    implementation("org.springframework.cloud:spring-cloud-stream-binder-kafka")
    implementation("org.javers:javers-spring-boot-starter-mongo:6.2.4")
    implementation ("io.github.microutils:kotlin-logging:2.0.11")
    implementation ("org.apache.commons:commons-lang3:3.12.0")
    implementation ("org.keycloak:keycloak-spring-boot-starter")
   // developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("io.micrometer:micrometer-registry-prometheus")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.cloud:spring-cloud-starter-contract-stub-runner")
    testImplementation("org.springframework.cloud:spring-cloud-starter-contract-verifier")
    testImplementation("org.springframework.kafka:spring-kafka-test")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
  //  testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.testcontainers:testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:kafka")
    testImplementation("org.testcontainers:mongodb")



    testImplementation("au.com.dius:pact-jvm-provider-junit5:${property("pactVersion")}")
    testImplementation("au.com.dius:pact-jvm-provider-junit5-spring:${property("pactVersion")}")
    testImplementation("com.ninja-squad:springmockk:3.0.1")

}


dependencyManagement {
    imports {
        mavenBom("org.testcontainers:testcontainers-bom:${property("testcontainersVersion")}")
        mavenBom("de.codecentric:spring-boot-admin-dependencies:${property("springBootAdminVersion")}")
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
        mavenBom("org.keycloak.bom:keycloak-adapter-bom:${property("keycloakVersion")}")
    }
}
tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "16"
    }
}
tasks.withType<BootBuildImage> {
    builder = "paketobuildpacks/builder:tiny"
    environment = mapOf("BP_NATIVE_IMAGE" to "false")
}

tasks.test {
    outputs.dir(file("build/generated-snippets"))
    useJUnitPlatform()
    environment("TESTCONTAINERS_RYUK_DISABLED","false")
    systemProperty("pact.verifier.publishResults", "true")
    systemProperty("pact.provider.tag", "dev")
    systemProperty("pact.provider.version", "${project.version}")
    testLogging {
        events = setOf(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED)
        exceptionFormat = TestExceptionFormat.FULL
        showStackTraces = true
        showExceptions = true
        showCauses = true
        showStandardStreams = true
    }
}
springBoot {
    buildInfo()
}
//tasks.asciidoctor {
//    inputs.dir(file("build/generated-snippets"))
//    dependsOn(tasks.test)
//    attributes.put("snippets", file("build/generated-snippets"))
//    attributes.put("projectdir", file("build/generated-snippets").toString())
//}


tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    version = "${project.version}"
    //dependsOn(tasks.asciidoctor)

    into("static/docs") {
        from(file("build/asciidoc/html5"))
    }


}
