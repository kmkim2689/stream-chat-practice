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

* 앱에서 사용할 api key와 jwt 토큰 확보
  * api 키는 대시보드에서 확보 가능
  * jwt 토큰 발급 방법(수동으로)
    * Chat Messaging > Explorer > users > 해당 유저의 id 복사
    * https://getstream.io/chat/docs/android/tokens_and_authentication/?language=kotlin 문서 참고
    * 왼쪽에 secret, 오른쪽에 복사한 아이디를 붙여넣으면 jwt 토큰이 발급됨

  * resource string에 두고 사용
  ```
  <resources>
    <string name="app_name">Stream-Chat-Practice</string>

    <string name="api_key">apikey</string>
    <string name="jwt_token">발급받은토큰</string>
  </resources>
  ```
  
## 2. 로그인 구현

### 2.1. 로그인 UI 구현
* LoginScreen
```
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen() {

    var username by remember {
        mutableStateOf(TextFieldValue(""))
    }

    var showProgressbar: Boolean by remember {
        mutableStateOf(false)
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 35.dp, end = 35.dp)
    ) {
        val (
            logo, usernameTextField, btnLoginAsUser, btnLoginAsGuest, progressBar
        ) = createRefs()

        Image(
            modifier = Modifier
                .size(120.dp)
                .constrainAs(logo) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top, margin = 100.dp)
                },
            painter = painterResource(id = R.drawable.ic_chat_logo),
            contentDescription = "logo"
        )

        OutlinedTextField(
            modifier = Modifier
                .constrainAs(usernameTextField) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(logo.bottom, margin = 32.dp)
                },
            value = username,
            onValueChange = { newValue ->
                username = newValue
            },
            label = {
                Text(text = "Enter username")
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )

        Button(
            onClick = { /*TODO*/ },
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(btnLoginAsUser) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(usernameTextField.bottom, margin = 16.dp)
                }
        ) {
            Text(text = "Login As User")
        }


        Button(
            onClick = { /*TODO*/ },
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(btnLoginAsGuest) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(btnLoginAsUser.bottom, margin = 16.dp)
                }
        ) {
            Text(text = "Login As Guest")
        }

        if (showProgressbar) {
            CircularProgressIndicator(
                modifier = Modifier.constrainAs(progressBar) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(btnLoginAsGuest.bottom, margin = 16.dp)
                }
            )
        }

    }
}
```

* Hilt를 사용하기 위한 과정 : Application 파일 커스텀
```
@HiltAndroidApp
class ChatApplication : Application() {
}
```


* LoginViewModel
  * Stream Chat Sdk에서는 Chat Client Object를 요구
  * 생성자에 클라이언트를 받도록 추가한다.

```
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val client: ChatClient
) : ViewModel() {
    
}
```

### 2.2. Module 구현

* ViewModel에서 사용해야 할 ChatClient 주입 목적
* StreamOfflinePluginFactory
  * to provide offline support
  * basically contains a class offline plugin employing a new caching mechanism powered by the side effects we applied to the chat client functions
* StreamStatePluginFactory
* ChatClient
```
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOfflinePluginFactory(
        @ApplicationContext context: Context
    ) = StreamOfflinePluginFactory(context)

    @Provides
    @Singleton
    fun provideStreamStatePluginFactory(
        @ApplicationContext context: Context
    ) = StreamStatePluginFactory(
        config = StatePluginConfig(),
        appContext = context
    )

    @Provides
    @Singleton
    fun provideChatClient(
        @ApplicationContext context: Context,
        offlinePluginFactory: StreamOfflinePluginFactory,
        streamStatePluginFactory: StreamStatePluginFactory
    ) = ChatClient.Builder(context.getString(R.string.api_key), context)
        .withPlugins(offlinePluginFactory, streamStatePluginFactory)
        .logLevel(ChatLogLevel.ALL)
        .build()
}
```