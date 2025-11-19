plugins {
	java
	id("org.springframework.boot") version "3.5.7"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "hvl.dat250.group8"
version = "0.0.1-SNAPSHOT"
description = "Polling application"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(25)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    // implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.data:spring-data-jpa:3.5.5")
    runtimeOnly("com.h2database:h2")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation("org.hibernate.orm:hibernate-core:7.1.6.Final")
    implementation("jakarta.persistence:jakarta.persistence-api:3.2.0")
    implementation("com.h2database:h2:2.4.240")
    // https://mvnrepository.com/artifact/org.springframework/spring-tx
    implementation("org.springframework:spring-tx:6.2.12")
    implementation("org.springframework:spring-orm:6.2.12")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")

    // https://mvnrepository.com/artifact/io.valkey/valkey-java
    implementation("io.valkey:valkey-java:5.5.0")

    // https://mvnrepository.com/artifact/org.springdoc/springdoc-openapi-starter-webmvc-ui
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.14")

    implementation("org.springframework.kafka:spring-kafka")
    implementation("org.springframework.boot:spring-boot-starter-amqp")

    // Security
    implementation("org.springframework.boot:spring-boot-starter-security:3.5.7")
    implementation("io.jsonwebtoken:jjwt-api:0.13.0")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.13.0")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.13.0")

    // https://mvnrepository.com/artifact/me.paulschwarz/spring-dotenv
    implementation("me.paulschwarz:spring-dotenv:4.0.0")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
