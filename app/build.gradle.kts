plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.chaquo)
}

android {
    namespace = "com.example.summaryartyou"
    compileSdk = 34


    defaultConfig {
        applicationId = "com.example.summaryartyou"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        //For Python
        ndk {
            // On Apple silicon, you can omit x86_64.
            abiFilters += listOf("armeabi-v7a", "arm64-v8a", "x86_64")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
        getByName("debug") {
            signingConfig = signingConfigs.getByName("debug")
        }
    }
//    externalNativeBuild {
//        ndkBuild {
//            path = file("jni/Android.mk")
//        }
//    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}
chaquopy {
    defaultConfig {
        version = "3.8"
        pyc{
            src = false
        }
        buildPython("C:\\Users\\Lenovo\\AppData\\Local\\Programs\\Python\\Python38\\python.exe")
        pip {
            // A requirement specifier, with or without a version number:
            install("youtube-transcript-api")
            install("openai")
            install("pytube")
            install("newspaper3k")
            install("pydantic==1.10.15")
            install("google-generativeai")
            install("groq")
            //install("grpcio==1.62.2")
        }
        pip{
            options("--no-index", "--find-links=C:\\Users\\Lenovo\\Python Modules")
            install("-r", "C:\\Users\\Lenovo\\AndroidStudioProjects\\SummaryArtYou\\requirements.txt")
        }
    }
}


dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3.android)
    implementation(libs.appcompat)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.dotenv.kotlin)
    implementation(libs.navigation.compose)
    implementation(libs.gson)
    implementation(libs.material)
    implementation(libs.coil.compose)
    implementation(libs.coil.gif)
    implementation(libs.text.recognition)

}