import org.cadixdev.gradle.licenser.Licenser
import org.cadixdev.gradle.licenser.LicenseExtension

plugins {
    `java-library`
    jacoco
    id("org.cadixdev.licenser") version "0.5.0" apply false
}

val projectName: String by project
val projectUrl: String by project
val projectInceptionYear: String by project
val bombeVersion: String by project
val groovyVersion: String by project
val spockVersion: String by project

val isSnapshot = version.toString().endsWith("-SNAPSHOT")

allprojects {
    group = "org.cadixdev"
    version = "0.6.0-SNAPSHOT"
}

subprojects {
    apply<JavaLibraryPlugin>()
    apply<GroovyPlugin>()
    apply<JacocoPlugin>()
    apply<MavenPublishPlugin>()
    apply<Licenser>()

    repositories {
        mavenCentral()
        if (bombeVersion.endsWith("-SNAPSHOT")) {
            maven("https://oss.sonatype.org/content/groups/public/")
        }
    }

    dependencies {
        // Standard JUnit
        testImplementation(platform("org.junit:junit-bom:5.7.0"))
        testImplementation("org.junit.jupiter:junit-jupiter-api")
        testImplementation("org.junit.jupiter:junit-jupiter-engine")

        // Spock
        testImplementation("org.codehaus.groovy:groovy-all:$groovyVersion")
        testImplementation("org.spockframework:spock-core:$spockVersion")
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(11))
        }
        withSourcesJar()
        withJavadocJar()
    }

    tasks.javadoc {
        options.optionFiles(rootProject.file("gradle/javadoc.options"))
    }

    configure<LicenseExtension> {
        header = rootProject.file("HEADER.txt")
    }

    tasks.withType<JavaCompile>().configureEach {
        options.release.set(8)
    }

    tasks.test {
        useJUnitPlatform()
    }

    tasks.jacocoTestReport {
        reports {
            xml.isEnabled = false
            csv.isEnabled = false
            html.destination = file("${buildDir}/jacocoHtml")
        }
    }

    tasks.processResources {
        from(rootProject.file("LICENSE.txt"))
    }

    configure<PublishingExtension> {
        publications {
            create<MavenPublication>("maven") {
                groupId = project.group.toString()
                artifactId = project.name
                version = project.version.toString()

                from(components["java"])
                withoutBuildIdentifier()

                pom {
                    name.set(projectName)
                    description.set(project.description)
                    packaging = "jar"
                    url.set(projectUrl)
                    inceptionYear.set(projectInceptionYear)

                    scm {
                        connection.set("scm:git:https://github.com/CadixDev/Lorenz.git")
                        developerConnection.set("scm:git:git@github.com:CadixDev/Lorenz.git")
                        url.set("https://github.com/CadixDev/Lorenz")
                    }

                    issueManagement {
                        system.set("GitHub")
                        url.set("https://github.com/CadixDev/Lorenz/issues")
                    }

                    licenses {
                        license {
                            name.set("MIT License")
                            url.set("https://opensource.org/licenses/MIT")
                            distribution.set("repo")
                        }
                    }

                    developers {
                        developer {
                            id.set("jamierocks")
                            name.set("Jamie Mansfield")
                            email.set("jmansfield@cadixdev.org")
                            url.set("https://www.jamiemansfield.me")
                            timezone.set("Europe/London")
                        }
                    }
                }
            }
        }

        repositories {
            val url = if (isSnapshot) {
                "https://oss.sonatype.org/content/repositories/snapshots/"
            } else {
                "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
            }
            maven(url) {
                credentials(PasswordCredentials::class)
                name = "ossrh"
            }
        }
    }

    if (project.hasProperty("ossrhUsername") && project.hasProperty("ossrhPassword")) {
        apply<SigningPlugin>()
        configure<SigningExtension> {
            useGpgCmd()
            setRequired {
                !isSnapshot && (
                    gradle.taskGraph.hasTask("publishAllPublicationsToOssrhRepository")
                        || gradle.taskGraph.hasTask("publishMavenPublicationToOssrhRepository")
                )
            }
            sign(project.extensions.getByType<PublishingExtension>().publications["maven"])
        }
    }
}
