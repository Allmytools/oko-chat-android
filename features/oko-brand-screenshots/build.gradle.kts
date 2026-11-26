/*
 * SPDX-License-Identifier: AGPL-3.0-only
 */

plugins {
    id("io.element.android-compose-library")
    alias(libs.plugins.paparazzi)
}

android {
    namespace = "io.element.android.oko.screenshot"
    sourceSets.getByName("main").res.apply {
        srcDir(rootDir.resolve("appicon/oko/src/main/res"))
        srcDir(rootDir.resolve("branding/oko/res"))
    }
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
    testImplementation(projects.features.home.impl)
    testImplementation(projects.features.login.impl)
    testImplementation(projects.features.messages.impl)
    testImplementation(projects.features.preferences.impl)
    testImplementation(projects.libraries.compound)
    testImplementation(projects.libraries.designsystem)
    testImplementation(projects.libraries.emoji.api)
    testImplementation(projects.libraries.matrix.api)
}
