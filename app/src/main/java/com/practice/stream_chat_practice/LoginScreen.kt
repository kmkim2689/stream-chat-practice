package com.practice.stream_chat_practice

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout

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