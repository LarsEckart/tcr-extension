plugins {
    `java-library`
    `maven-publish`
    `signing`
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
        vendor.set(JvmVendorSpec.AZUL)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    api("org.junit.jupiter:junit-jupiter-api:5.10.0")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testImplementation("com.approvaltests:approvaltests:18.7.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
}

tasks.register<Test>("testsOn17") {
    javaLauncher.set(javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(17))
    })
}

val test by tasks.getting(Test::class) {
    useJUnitPlatform()
    testLogging {
        showExceptions = true
        showStackTraces	= true
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
            version = "0.0.2"
            from(components["java"])

            pom {
                name.set("JUnit5 extensions for text-commit-revert")
                url.set("https://github.com/LarsEckart/tcr-extension")
                description.set("JUnit 5 Extension for test-commit-revert")
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
    if (hasProperty("signing.gnupg.keyName") && hasProperty("signing.gnupg.passphrase")) {
        useGpgCmd()
        println("will do useGpgCmd")
    } else if (hasProperty("signingKey") && hasProperty("signingPassword")) {
        println("will do useInMemoryPgpKeys")

        val signingKey: String? by project
        val signingPassword: String? by project
        useInMemoryPgpKeys(signingKey, signingPassword)
    }
    println("will do sign")

    val sonatypeUsername: String? by project
    println("will do publish as $sonatypeUsername")
    sign(publishing.publications["sonatype"])
}
