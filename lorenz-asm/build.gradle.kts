plugins {
    `java-library`
}

val bombeVersion: String by rootProject

dependencies {
    api(project(":lorenz"))
    api("org.cadixdev:bombe-asm:$bombeVersion")
}
