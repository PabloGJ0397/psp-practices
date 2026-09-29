plugins {
    kotlin("jvm") version "2.2.20"
}

group = "es.dam.psp"
version = "1.0"

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(17)
}
