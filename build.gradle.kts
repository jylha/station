plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.test) apply false
    alias(libs.plugins.dagger.hilt.android) apply false
    alias(libs.plugins.google.play.licenses) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.androidx.baselineprofile) apply false
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}

// To generate reports: ./gradlew assembleRelease -PcomposeCompilerReports=true --rerun-tasks
subprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>()
        .matching { it !is org.jetbrains.kotlin.gradle.internal.KaptGenerateStubsTask }
        .configureEach {
            kotlinOptions {
                if (project.findProperty("composeCompilerReports") == "true") {
                    freeCompilerArgs += listOf(
                        "-P",
                        "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=" +
                                project.buildDir.absolutePath + "/reports"
                    )
                    freeCompilerArgs += listOf(
                        "-P",
                        "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=" +
                                project.buildDir.absolutePath + "/reports"
                    )
                }
            }
        }
}
