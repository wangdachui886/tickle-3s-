plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.lightledger.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.lightledger.app"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ndk {
            abiFilters += listOf("arm64-v8a", "x86_64")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    flavorDimensions += "distribution"
    productFlavors {
        create("mainline") {
            dimension = "distribution"
        }
        create("friends") {
            dimension = "distribution"
            applicationIdSuffix = ".friends"
            versionNameSuffix = "-friends"
            resValue("string", "app_name", "tickle beta")
            resValue("string", "widget_label_compact", "tickle beta 4x2 Dark")
            resValue("string", "widget_label_compact_light", "tickle beta 4x2 Light")
            resValue("string", "widget_label_medium", "tickle beta 4x3 Dark")
            resValue("string", "widget_label_medium_light", "tickle beta 4x3 Light")
            resValue("string", "widget_label_large", "tickle beta 4x4 Dark")
            resValue("string", "widget_label_large_light", "tickle beta 4x4 Light")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.12.01")

    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("androidx.activity:activity-compose:1.10.1")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.room:room-ktx:2.6.1")
    implementation("androidx.room:room-runtime:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
}

afterEvaluate {
    tasks.register<Copy>("copyMainlineDebugApk") {
        from(layout.buildDirectory.file("outputs/apk/mainline/debug/app-mainline-debug.apk"))
        into(rootProject.layout.projectDirectory.dir("dist"))
        rename { "tickle-mainline-debug.apk" }
    }
    tasks.named("assembleMainlineDebug") {
        finalizedBy("copyMainlineDebugApk")
    }

    tasks.register<Copy>("copyFriendsDebugApk") {
        from(layout.buildDirectory.file("outputs/apk/friends/debug/app-friends-debug.apk"))
        into(rootProject.layout.projectDirectory.dir("dist"))
        rename { "tickle-friends-debug.apk" }
    }
    tasks.named("assembleFriendsDebug") {
        finalizedBy("copyFriendsDebugApk")
    }
}
