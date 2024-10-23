package io.themasteredpanda.pedrodecks.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType.Companion.Password
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import io.themasteredpanda.pedrodecks.ConnectionManager

@Composable
fun Login(conn: ConnectionManager? = null) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp, alignment = Alignment.CenterVertically),
        modifier = Modifier.fillMaxSize()
    ) {
        UsernameField()
        PasswordField()
        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            LoginButton()
            RegisterButton()
        }

    }
}

@Composable
fun LoginButton() {
    Button(onClick = { /*TODO*/ }) {
        Text(text = "Login")
    }
}

@Composable
fun RegisterButton() {
    Button(onClick = { /*TODO*/ }) {
        Text(text = "Register")
    }
}

@Composable
fun PasswordField() {
    var password by rememberSaveable {
        mutableStateOf("")
    }

    Text(text = "Password")
    TextField(
        value = password,
        onValueChange = { password = it },
        placeholder = { Text("Enter password.") },
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = Password)
    )
}

@Composable
fun UsernameField() {
    var username by rememberSaveable {
        mutableStateOf("")
    }

    Text(text = "Username")
    TextField(value = username, onValueChange = { username = it })
}

@Preview
@Composable
fun RegisterPopup() {
    var username = ""
    var password = ""
    var confirmPassword = ""
    var email = ""
    var usernameError = false
    var passwordError = false
    var emailError = false

    Popup {
        Box(
            modifier = Modifier
                .fillMaxSize(), contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .background(Color.LightGray),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .padding(10.dp)
                ) {
                    Button(onClick = { /*TODO*/ }) {
                        Text(text = "X")
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(text = "Username")
                    TextField(
                        value = username,
                        isError = usernameError,
                        placeholder = { Text(text = "Bob") },
                        onValueChange = { username = it })
                    Text(text = "Email")
                    TextField(
                        value = email,
                        isError = emailError,
                        placeholder = { Text(text = "bobsemail@gmail.com") },
                        onValueChange = { email = it })
                    Text(text = "Password")
                    TextField(
                        value = password,
                        isError = passwordError,
                        placeholder = { Text(text = "Secret password") },
                        onValueChange = { password = it },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = Password)
                    )
                    Text(text = "Confirm Password")
                    TextField(
                        value = confirmPassword,
                        isError = passwordError,
                        placeholder = { Text(text = "Secret password") },
                        onValueChange = { confirmPassword = it })
                    Button(onClick = {
                        if (username.isEmpty()) {
                            usernameError = true
                        }

                        if (password.isEmpty() || confirmPassword.isEmpty() || (password.isEmpty() && confirmPassword.isEmpty())) {
                            passwordError = true
                        }

                        if (email.isEmpty()) {
                            emailError = true
                        }

                        //Create account, or send confirmation email here.
                    }) {
                        Text(text = "Register")
                    }
                }
            }
        }

    }
}


@Preview(
    showBackground = true, name = "Login Preview",
)
@Composable
fun LoginPreview() {
    Login()
}