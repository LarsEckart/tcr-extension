# tcr-extension
JUnit 5 extension for Kent Beck's test commit revert workflow.

# How to use

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/LarsEckart/tcr-extension")
            credentials {
                username = project.findProperty("gpr.user") ?: System.getenv("GH_USERNAME")
                password = project.findProperty("gpr.key") ?: System.getenv("GH_TOKEN")
            }
        }
    }
    
    

    testImplementation("com.github.larseckart:tcr-extension:0.3.0")