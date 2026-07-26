plugins {
    `java-library`
    `maven-publish`
    `signing`
}

version = "0.0.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
        vendor.set(JvmVendorSpec.AZUL)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    api("org.junit.jupiter:junit-jupiter-api:6.1.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api:6.1.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:6.1.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:6.1.1")

    testImplementation("com.approvaltests:approvaltests:31.0.0")
}

tasks.register<Test>("testsOn17") {
    useJUnitPlatform()
    testClassesDirs = sourceSets["test"].output.classesDirs
    classpath = sourceSets["test"].runtimeClasspath
    javaLauncher.set(javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(17))
    })
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        showExceptions = true
        showStackTraces = true
    }
}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    repositories {
        maven {
            name = "sonatype"
            url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = project.findProperty("sonatypeUsername") as String?
                password = project.findProperty("sonatypePassword") as String?
            }
        }
    }
    publications {
        create<MavenPublication>("sonatype") {
            artifactId = "junit-tcr-extensions"
            group = "com.larseckart"
            version = project.version.toString()
            from(components["java"])

            pom {
                name.set("JUnit extensions for test-commit-revert")
                url.set("https://github.com/LarsEckart/tcr-extension")
                description.set("JUnit 6 Extension for test-commit-revert")
                licenses {
                    license {
                        name.set("Apache License 2.0")
                        url.set("http://www.apache.org/licenses/")
                    }
                }
                developers {
                    developer {
                        id.set("larseckart")
                        name.set("Lars Eckart")
                        email.set("lars.eckart@hey.com")
                    }
                }
                scm {
                    connection.set("scm:git:git@github.com:larseckart/tcr-extension.git")
                    url.set("https://github.com/larseckart/tcr-extension")
                }
                issueManagement {
                    url.set("https://github.com/larseckart/tcr-extension/issues")
                    system.set("GitHub")
                }
            }
        }
    }
}

signing {
    val signingKey = project.findProperty("signingKey") as String?
    val signingPassword = project.findProperty("signingPassword") as String?
    useInMemoryPgpKeys(signingKey, signingPassword)

    sign(publishing.publications["sonatype"])
}

// Task to print version for scripts
tasks.register("printVersion") {
    doLast {
        println(version)
    }
}
