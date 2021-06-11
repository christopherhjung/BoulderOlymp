plugins {
    kotlin("jvm") version "1.4.32"
    id("org.springframework.boot") version "2.3.1.RELEASE"
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
    kotlin("plugin.spring") version  "1.4.30"
    id("edu.sc.seis.launch4j")version "2.5.0"
}

group = "com.boulderolymp.cli"
version = "1.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.2.0");
    implementation("com.github.ajalt.clikt:clikt:3.2.0")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = java.sourceCompatibility.toString()
    }
}

launch4j {
    mainClassName = "com.boulderolymp.cli.ApplicationKt"
}

