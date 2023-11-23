package com.practice.stream_chat_practice

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.practice.stream_chat_practice.ui.theme.StreamChatPracticeTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {

    private lateinit var viewModel: LoginViewModel
    private lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            viewModel = hiltViewModel()
            token = getString(R.string.jwt_token)

            subscribeToEvents()

            var showProgressbar: Boolean by remember {
                mutableStateOf(false)
            }

            viewModel.loadingState.observe(this) { uiLoadingState ->
                showProgressbar = when (uiLoadingState) {
                    is LoginViewModel.UiLoadingState.Loading -> true
                    is LoginViewModel.UiLoadingState.NotLoading -> false
                }
            }

            StreamChatPracticeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LoginScreen(
                        onLogin = { username, token ->
                            viewModel.loginUser(username, token)
                        },
                        token = token,
                        showProgressbar = showProgressbar
                    )
                }
            }
        }
    }

    // viewmodel에서 발생하는 one-time event(sharedflow)의 값을 얻기 위함
    private fun subscribeToEvents() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loginEvent.collect { event ->
                    when (event) {
                        is LoginViewModel.LoginEvent.ErrorInputTooShort -> {
                            showToast("Invalid Enter more than 3 characters")
                        }

                        is LoginViewModel.LoginEvent.ErrorLogin -> {
                            val errorMessage = event.error
                            showToast(errorMessage)
                        }

                        is LoginViewModel.LoginEvent.Success -> {
                            showToast("login successful")
                            startActivity(
                                Intent(
                                    this@LoginActivity,
                                    ChannelListActivity::class.java
                                )
                            )
                            finish()
                        }
                    }
                }
            }
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
