plugins {
    id("com.android.library")
    id("kotlin-android")
    id("maven-publish")
}

android {
    namespace = "dora.skin"
    compileSdk = 32

    defaultConfig {
        minSdk = 21
        targetSdk = 32
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
    buildFeatures {
        dataBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.github.dora4:dora:1.1.10")
}

afterEvaluate {
    publishing {
        publications {
            register("release", MavenPublication::class) {
                from(components["release"])
                groupId = "com.github.dora4"
                artifactId = rootProject.project.name
                version = "1.0"
            }
        }
    }
}