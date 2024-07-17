plugins {
    kotlin("jvm") version "2.0.0"
}

group = "eu.blackcult"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    implementation("com.github.AgeOfWar:Telejam:v7.15")
    implementation("org.mongodb:mongodb-driver-sync:4.2.3")
    implementation("com.google.code.gson:gson:2.11.0")
}

sourceSets {
    main {
        resources {
            srcDirs("src/main/resources")
        }
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<Copy> {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

tasks.withType<ProcessResources> {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

kotlin {
    jvmToolchain(21)
}