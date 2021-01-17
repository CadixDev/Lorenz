plugins {
    `java-library`
}

val bombeVersion: String by rootProject
val asmVersion: String by rootProject

dependencies {
    api("org.cadixdev:bombe:$bombeVersion")

    // Optional dependencies
    implementation("org.ow2.asm:asm-commons:$asmVersion")
    implementation("org.cadixdev:bombe-jar:$bombeVersion")
}

tasks.jar.configure {
    manifest.attributes(mapOf("Automatic-Module-Name" to "${project.group}.lorenz"))
}
