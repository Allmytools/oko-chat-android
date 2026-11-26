/*
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package oko

import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import isEnterpriseBuild
import org.gradle.api.Project

fun ApplicationAndroidComponentsExtension.configureOkoApplication(project: Project) {
    if (project.isEnterpriseBuild) return

    val root = project.rootDir
    finalizeDsl {
        it.sourceSets.getByName("main").res.apply {
            srcDir(root.resolve("appicon/oko/src/main/res"))
            srcDir(root.resolve("branding/oko/res"))
        }
        it.buildTypes.getByName("debug").resValue("string", "app_name", "OKO dbg")
        it.buildTypes.getByName("release").resValue("string", "app_name", "OKO")
        it.buildTypes.getByName("nightly").resValue("string", "app_name", "OKO nightly")
    }
}
