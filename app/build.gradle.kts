plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.google.devtools.ksp)
}

android {
    namespace = "com.anix.android.anixstudyassist"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.anix.android.anixstudyassist"
        minSdk = 33
        //noinspection OldTargetApi
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "META-INF/DEPENDENCIES"
            excludes += "META-INF/DEPENDENCIES.txt"
            excludes += "META-INF/LICENSE"
            excludes += "META-INF/LICENSE.txt"
            excludes += "META-INF/license.txt"
            excludes += "META-INF/NOTICE"
            excludes += "META-INF/NOTICE.txt"
            excludes += "META-INF/notice.txt"
            excludes += "META-INF/INDEX.LIST"
            excludes += "META-INF/versions"
            excludes += "META-INF/versions/9/module-info.class"
        }
    }
}

dependencies {

    implementation(project(":core:ui"))
    implementation(project(":feature:aichat"))
    implementation(project(":feature:entry"))
    implementation(project(":feature:landing"))
    implementation(project(":feature:topic"))
    implementation(project(":feature:settings"))
    implementation(project(":feature:data-store"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)

    // Material 3
    implementation(libs.bundles.material3)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Navigation 3
    implementation(libs.bundles.navigation3)
}
