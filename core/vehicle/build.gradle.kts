plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.tagobogo.core.vehicle"
    compileSdk = 35
    defaultConfig { minSdk = 28 }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    // 이 모듈을 사용하는 곳에서도 SDK 타입을 참조할 수 있도록 공개한다.
    api(libs.pleos.vehicle)
    api(libs.pleos.navihelper)
    api(libs.pleos.fusedlocation)
    api(libs.pleos.stt)
    api(libs.pleos.tts)
    implementation(project(":core:derive"))
    implementation(libs.androidx.core)
    testImplementation(libs.junit)
}
