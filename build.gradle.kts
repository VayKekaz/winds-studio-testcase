val ktorVersion = "2.0.0"
val exposedVersion = "0.37.3"
val h2Version = "2.1.210"
val hikariCpVersion = "5.0.1"
val flywayVersion = "8.5.7"
val logbackVersion = "1.2.11"
val junitVersion = "5.8.2"

plugins {
    kotlin("jvm") version "1.6.20"
    kotlin("plugin.serialization") version "1.6.20"
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")

    implementation("com.h2database:h2:$h2Version")
    implementation("com.zaxxer:HikariCP:$hikariCpVersion")
    implementation("org.flywaydb:flyway-core:$flywayVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    implementation("org.webjars:webjars-locator-core:0.50")
    implementation("org.webjars:swagger-ui:4.10.3")
    /* // many attempts to generate swagger.json file based on existing code. no success
    implementation("io.bkbn:kompendium-core:latest.release") // dermo ebanoe
    implementation("io.bkbn:kompendium-swagger-ui:latest.release")
    implementation("com.github.papsign:Ktor-OpenAPI-Generator:-SNAPSHOT") // tozhe kal ebuchiy
    implementation("org.openapitools:openapi-generator:5.4.0")
    implementation("org.openapitools:openapi-generator-gradle-plugin:5.4.0")
     */

    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")
    testImplementation("io.ktor:ktor-client-cio:$ktorVersion")
}

application {
    mainClass.set("MainKt")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
