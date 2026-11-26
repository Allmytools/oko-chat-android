/*
 * SPDX-License-Identifier: AGPL-3.0-only
 */

plugins {
    id("io.element.android-compose-library")
    alias(libs.plugins.paparazzi)
}

android {
    namespace = "io.element.android.oko.screenshot"
}

androidComponents {
    finalizeDsl { extension ->
        extension.sourceSets.getByName("main").res.directories.apply {
            add(rootDir.resolve("appicon/oko/src/main/res").path)
            add(rootDir.resolve("branding/oko/res").path)
        }
    }
}

tasks.withType<Test>().configureEach {
    // Layoutlib keeps a resource table per JVM. One worker is enough for this small suite.
    maxParallelForks = 1
}

dependencies {
    constraints.add("testImplementation", "com.google.guava:guava") {
        attributes {
            attribute(
                TargetJvmEnvironment.TARGET_JVM_ENVIRONMENT_ATTRIBUTE,
                objects.named(TargetJvmEnvironment::class.java, TargetJvmEnvironment.STANDARD_JVM)
            )
        }
    }
    testImplementation(libs.test.junit)
    implementation(projects.features.home.impl)
    implementation(projects.features.login.impl)
    implementation(projects.features.messages.impl)
    implementation(projects.features.preferences.impl)
    implementation(projects.libraries.compound)
    implementation(projects.libraries.designsystem)
    implementation(projects.libraries.emoji.api)
    implementation(projects.libraries.matrix.api)
}
