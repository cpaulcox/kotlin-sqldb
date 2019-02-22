import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
//    base
    kotlin("jvm") version "1.3.21"
}

repositories {
    jcenter()
    mavenCentral()
}

group = "cpc"
version = "1.0"


val junit_platform_ver = "1.3.2"
val junit_jupiter_ver = "5.3.2"
val log4j2_ver = "2.10.0"


dependencies {
    // Kotlin
    compile("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    compile("org.jetbrains.kotlin:kotlin-reflect")

    // Log4j2 + Jackson for YAML config support and SLF4J adapter
    compile("org.apache.logging.log4j:log4j-api:$log4j2_ver")
    compile("org.apache.logging.log4j:log4j-core:$log4j2_ver")
    compile("org.apache.logging.log4j:log4j-slf4j-impl:$log4j2_ver")
    compile("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.7.3")
    compile("com.fasterxml.jackson.core:jackson-databind:2.5.4")


    // Junit 5
    testCompile("org.junit.jupiter:junit-jupiter-api:$junit_jupiter_ver")
    testRuntime("org.junit.jupiter:junit-jupiter-engine:$junit_jupiter_ver")
    testRuntime("org.junit.platform:junit-platform-launcher:$junit_platform_ver")

    // sqlite
    testCompile("org.xerial:sqlite-jdbc:3.23.1")

    // Hikari Connection Pool
    testCompile(group = "com.zaxxer", name = "HikariCP", version = "3.2.0")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<Wrapper> {
    gradleVersion = "5.1.1"
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

