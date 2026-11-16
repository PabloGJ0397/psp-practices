import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version "2.2.20"
}

group = "es.dam.psp"
version = "1.0"

repositories {
    mavenCentral()
}

// Compila para Java 21 con cualquier JDK >= 21 (Kotlin 2.2.20 no soporta aún JDK 25 como destino).
java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}
