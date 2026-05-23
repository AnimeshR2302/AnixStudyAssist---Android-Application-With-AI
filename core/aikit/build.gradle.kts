import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.google.devtools.ksp)
    alias(libs.plugins.secretsPlugin)
}

android {
    namespace = "com.anix.android.anixstudyassist.aikit"
    compileSdk = 37

    buildFeatures {
        buildConfig = true
    }

    secrets {
        defaultPropertiesFileName = "local.properties"
    }

    defaultConfig {
        minSdk = 33

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localProperties.load(localPropertiesFile.inputStream())
        }

        val geminiApiKey = localProperties.getProperty("GEMINI_API_KEY")
            ?: localProperties.getProperty("MY_API_KEY")
            ?: ""
        val escapedGeminiApiKey = geminiApiKey
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")

        buildConfigField(
            "String",
            "GEMINI_API_KEY",
            "\"$escapedGeminiApiKey\""
        )
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // Hilt for Dependency Injection
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // ML Kit GenAI APIs
    implementation(libs.bundles.on.device.ml.kit)
    implementation(libs.google.genai)

    implementation(libs.guava)
    implementation(libs.kotlinx.coroutines.guava)
    implementation(libs.kotlinx.coroutines.jdk8)
}