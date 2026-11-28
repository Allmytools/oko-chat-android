/*
 * SPDX-License-Identifier: AGPL-3.0-only
 */

plugins {
    id("io.element.android-compose-library")
}

android {
    namespace = "io.element.android.oko.wallpaper"
    testNamespace = "io.element.android.oko.wallpaper.test"
    defaultConfig.testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
}

dependencies {
    implementation(projects.libraries.compound)
    implementation(libs.coroutines.core)
    androidTestImplementation(libs.androidx.activity.activity)
    androidTestImplementation(libs.androidx.compose.ui.test.junit)
    androidTestImplementation(libs.test.runner)
}
