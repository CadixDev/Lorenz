plugins {
    `java-library`
}

dependencies {
    api(project(":lorenz"))
}

tasks.jar.configure {
    manifest.attributes(mapOf("Automatic-Module-Name" to "${project.group}.lorenz.io.enigma"))
}
