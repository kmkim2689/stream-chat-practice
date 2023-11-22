# Stream Chat Application

## 0. Sign up for Stream

* https://getstream.io/tutorials/android-chat/
* 단순 연습용 혹은 5인 이하 회사의 서비스인 경우, maker account를 통해 무료로 sdk를 활용할 수 있음
* 일반 계정으로 만들면 30일 동안만 무료로 사용 가능
  * https://getstream.io/maker-account/

* Dashboard로 이동
  * Apps > 작업할 앱 > Key에서 API 키(Key)를 확인 가능

## 1. Setting up the Project

* project level build.gradle
```
plugins {
    id("com.android.application") version "8.1.0-rc01" apply false
    id("org.jetbrains.kotlin.android") version "1.8.10" apply false
    // hilt 추가
    id("com.google.dagger.hilt.android") version "2.47" apply false
}
```

* app module build.gradle
```
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    // hilt 사용
    id ("com.google.dagger.hilt.android")
    // kapt 사용 목적
    id ("kotlin-kapt")
}

android {
    namespace = "com.practice.stream_chat_practice"
    // stream은 34이상 요구
    compileSdk = 34

    defaultConfig {
        applicationId = "com.practice.stream_chat_practice"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    // constraint layout in compose
    implementation ("androidx.constraintlayout:constraintlayout-compose:1.0.1")

    // stream sdk
    implementation("io.getstream:stream-chat-android-compose:6.0.8")
    implementation("io.getstream:stream-chat-android-offline:6.0.8")

    implementation("androidx.compose.material:material-icons-extended:1.6.0-alpha08")

    // hilt
    implementation ("com.google.dagger:hilt-android:2.47")
    kapt("com.google.dagger:hilt-compiler:2.47")
}
```