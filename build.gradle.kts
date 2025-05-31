plugins {
    java
    application
    id("org.javamodularity.moduleplugin") version "1.8.12"
    id("org.beryx.jlink") version "2.25.0"
}

group = "com.oop10x"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val junitVersion = "5.10.2"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

application {
    mainModule.set("com.oop10x.steddyvalley")
    mainClass.set("com.oop10x.steddyvalley.Main")
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

jlink {
    imageZip.set(layout.buildDirectory.file("distributions/app.zip"))
    options.set(
        listOf("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages")
    )
    launcher {
        name = "app"
    }
}

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to "com.oop10x.steddyvalley.Main"
        )
    }
}
