plugins {
    id("java")
    id("org.springframework.boot") version "3.2.2"
    id("io.spring.dependency-management") version "1.1.4"
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

group = "com.root"
version = "1.0.0"

val artifactName = "safeRoute"
val projectOwner = "potato"


tasks.bootJar {
    manifest {
        attributes(
            "Implementation-Title" to artifactName,
            "Automatic-Module-Name" to artifactName,
            "Implementation-Version" to version,
            "Create-By" to projectOwner
        )

        archiveFileName.set("${artifactName}.${version}.jar")
        isEnabled = true
    }
}

tasks.jar {
    isEnabled = false
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}


dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.0.2")

    implementation("com.opencsv:opencsv:5.5") {
        exclude("commons-logging" ,"commons-logging")
    }

    implementation("net.minidev:json-smart:2.4.10")

    runtimeOnly("com.h2database:h2:2.1.210")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

repositories {
    mavenCentral()
}