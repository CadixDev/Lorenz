plugins {
    `java-library`
}

val bombeVersion: String by rootProject

dependencies {
    api("org.cadixdev:bombe:$bombeVersion")
}
