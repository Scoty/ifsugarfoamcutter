import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
}

kotlin {
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
//            implementation("com.formdev:flatlaf:3.6.1") waiting for version 3.7  to replace JFileChooser with SystemFileChooser
        }
    }
}


compose.desktop {
    application {
        mainClass = "studio.ifsugar.ifsugarfoamcutter.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "If Sugar Foam Cutter"
            packageVersion = "1.3.1"
            windows {
                iconFile.set(project.file("src/jvmMain/resources/ifsugar.ico"))
            }
            macOS {
                iconFile.set(project.file("src/jvmMain/resources/ifsugar.icns"))
            }
        }
    }
}
