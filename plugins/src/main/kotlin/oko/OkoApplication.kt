/*
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package oko

import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import isEnterpriseBuild
import org.gradle.api.Project

private const val OKO_APPLICATION_ID = "com.interatletika.oko"

fun ApplicationAndroidComponentsExtension.configureOkoApplication(project: Project) {
    if (project.isEnterpriseBuild) return

    val root = project.rootDir
    val isTestRelease = project.providers.gradleProperty("oko.test").orElse("true").get().toBooleanStrict()
    finalizeDsl {
        it.sourceSets.getByName("main").res.apply {
            directories.add(root.resolve("appicon/oko/src/main/res").path)
            directories.add(root.resolve("branding/oko/res").path)
        }
        it.defaultConfig.applicationId = OKO_APPLICATION_ID
        it.buildTypes.getByName("debug").apply {
            resValue("string", "app_name", "OKO dbg")
            resValue("string", "login_redirect_scheme", "$OKO_APPLICATION_ID.debug")
        }
        it.buildTypes.getByName("release").apply {
            applicationIdSuffix = if (isTestRelease) ".test" else null
            resValue("string", "app_name", if (isTestRelease) "OKO test" else "OKO")
            resValue("string", "login_redirect_scheme", OKO_APPLICATION_ID + applicationIdSuffix.orEmpty())

            // The dedicated workflow signs the release using the selected OKO key.
            signingConfig = null
        }
        it.buildTypes.getByName("nightly").apply {
            resValue("string", "app_name", "OKO nightly")
            resValue("string", "login_redirect_scheme", "$OKO_APPLICATION_ID.nightly")
        }
    }
}
