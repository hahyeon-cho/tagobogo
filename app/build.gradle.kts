plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.tagobogo"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.tagobogo"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(project(":core:vehicle"))
    implementation(project(":core:data"))
    implementation(project(":core:derive"))
    implementation(project(":feature:memo"))
    implementation(project(":feature:report"))

    implementation(libs.androidx.core)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.lifecycle.service)

    testImplementation(libs.junit)
}
