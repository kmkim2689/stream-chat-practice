package com.practice.stream_chat_practice

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practice.stream_chat_practice.util.Constants.MIN_USERNAME_LENGTH
import dagger.hilt.android.lifecycle.HiltViewModel
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.models.User
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val client: ChatClient
) : ViewModel() {

    private val _loginEvent = MutableSharedFlow<LoginEvent>()
    val loginEvent = _loginEvent.asSharedFlow()

    private val _loadingState = MutableLiveData<UiLoadingState>()
    val loadingState: LiveData<UiLoadingState>
        get() = _loadingState

    private fun isValidUsername(username: String): Boolean {
        return username.length >= MIN_USERNAME_LENGTH
    }

    fun loginUser(
        username: String,
        token: String? = null // null : for the case the user want to login as a guest
    ) {
        println("login user")
        val trimmedUsername = username.trim()
        viewModelScope.launch {
            if (isValidUsername(trimmedUsername) && token != null) {
                println("login start - registered")
                loginRegisteredUser(trimmedUsername, token)
            } else if (isValidUsername(trimmedUsername) && token == null) {
                println("login start - guest")
                loginGuestUser(trimmedUsername)
            } else {
                _loginEvent.emit(LoginEvent.ErrorInputTooShort)
            }
        }

    }

    private fun loginRegisteredUser(username: String, token: String) {
        println("login registered user")
        // inbuilt user class
        // stream sdk는 User Object를 보낼 것을 요구함. User Authentication 목적
        // io.getstream.chat.android.models
        val user = User(
            id = username,
            name = username
        )

        _loadingState.value = UiLoadingState.Loading

        // client 객체 활용
        // connectUser 함수 : ChatClient에 속해있는 함수로, User 클래스를 stream sdk와 연결하고 서버에 인증하는 역할을 하는 함수
        client.connectUser(
            user = user,
            token = token
        ).enqueue { result ->

            _loadingState.value = UiLoadingState.NotLoading

            // enqueue -> retrofit의 call 인터페이스를 구현하는 형태
            // result of the authentication
            if (result.isSuccess) {
                viewModelScope.launch {
                    _loginEvent.emit(LoginEvent.Success)
                }
            } else {
                viewModelScope.launch {
                    _loginEvent.emit(LoginEvent.ErrorLogin(
                        result.errorOrNull()?.message ?: "unknown error"
                    ))
                }
            }
        }
    }

    private fun loginGuestUser(username: String) {
        // connectUser가 아닌, connectGuestUser 활용
        println("login guest user")

        _loadingState.value = UiLoadingState.Loading

        client.connectGuestUser(
            userId = username,
            username = username
        ).enqueue { result ->

            _loadingState.value = UiLoadingState.NotLoading

            if (result.isSuccess) {
                viewModelScope.launch {
                    _loginEvent.emit(LoginEvent.Success)
                }
            } else {
                viewModelScope.launch {
                    _loginEvent.emit(LoginEvent.ErrorLogin(
                        result.errorOrNull()?.message ?: "unknown error"
                    ))
                }
            }
        }
    }

    sealed class LoginEvent {
        object ErrorInputTooShort : LoginEvent()
        data class ErrorLogin(val error: String) : LoginEvent()
        object Success : LoginEvent()
    }

    sealed class UiLoadingState {
        object Loading : UiLoadingState()
        object NotLoading : UiLoadingState()
    }
}