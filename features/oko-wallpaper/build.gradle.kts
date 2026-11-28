/*
 * SPDX-License-Identifier: AGPL-3.0-only
 */

plugins {
    id("io.element.android-compose-library")
}

android {
    namespace = "io.element.android.oko.wallpaper"
}

dependencies {
    implementation(projects.libraries.compound)
    implementation(libs.coroutines.core)
}
