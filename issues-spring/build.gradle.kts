import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent


plugins {
    id("org.springframework.boot") version "2.2.6.RELEASE"
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
    id("org.asciidoctor.convert") version "1.5.8"
    id("com.adarshr.test-logger") version "1.7.0"
    kotlin("jvm") version "1.3.71"
    kotlin("plugin.spring") version "1.3.71"
}


group = "de.dkutzer.buggy"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/milestone/") }
//    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
}

extra["snippetsDir"] = file("build/generated-snippets")
extra["springBootAdminVersion"] = "2.2.1"
extra["pactVersion"] = "4.0.8"
extra["springCloudVersion"] = "Hoxton.SR3"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    implementation("org.springframework.boot:spring-boot-starter-data-rest")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.springframework.cloud:spring-cloud-starter-sleuth")
    implementation("org.springframework.cloud:spring-cloud-stream-binder-kafka")
    implementation("org.springframework.cloud:spring-cloud-starter-zipkin")
    implementation("de.codecentric:spring-boot-admin-starter-client")
    implementation("org.javers:javers-spring-boot-starter-mongo:5.8.5")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.cloud:spring-cloud-starter-oauth2")
    implementation("org.springframework.security.oauth:spring-security-oauth2")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.springframework.cloud:spring-cloud-stream-test-support")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")

    testImplementation("au.com.dius:pact-jvm-provider-junit5:${property("pactVersion")}")
    testImplementation("au.com.dius:pact-jvm-provider-junit5-spring:${property("pactVersion")}")
    testImplementation("com.ninja-squad:springmockk:2.0.0")

}
dependencyManagement {
    imports {
        mavenBom("de.codecentric:spring-boot-admin-dependencies:${property("springBootAdminVersion")}")
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}

tasks.test {
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