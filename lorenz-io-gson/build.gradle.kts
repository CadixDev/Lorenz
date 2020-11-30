plugins {
    `java-library`
}

dependencies {
    api(project(":lorenz"))
    api("com.google.code.gson:gson:2.8.6")
    api("me.jamiemansfield:gson-simple:0.1.1")
}

tasks.jar.configure {
    manifest.attributes(mapOf("Automatic-Module-Name" to "${project.group}.lorenz.io.gson"))
}
