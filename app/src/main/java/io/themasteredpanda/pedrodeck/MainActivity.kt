package io.themasteredpanda.pedrodeck

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import dagger.hilt.android.AndroidEntryPoint

/**
 *
 * TODO: Test out error messaging queue.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        setContent {
            MaterialTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    if (!viewModel.signedIn()) {
                        LoginScreen(viewModel)
                    } else {
                        DecksScreen()
                    }
                }
            }
        }
    }


    /**
     * Used primarily for debugging.
     */
    companion object {
        fun logger(message: String) {

            val logg = true
            if (logg) {
                println(
                    "[LOGGER] $message"
                )
            }
        }
    }

    @Composable
    fun LoginScreen(viewModel: AppViewModel) {
        ErrorDropdown(viewModel)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(0f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Pedro Deck", fontSize = 25.sp, modifier = Modifier.padding(25.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                TextField(
                    viewModel.emailValue,
                    label = { Text(text = "Email") },
                    onValueChange = { value -> viewModel.onTextChange(value) })
                TextField(
                    viewModel.passwordValue,
                    label = { Text(text = "Password") },
                    visualTransformation = if (viewModel.passwordVisibility) VisualTransformation.None else
                        PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        val iconImage = if (viewModel.passwordVisibility)
                            Icons.Filled.Visibility
                        else Icons.Filled.VisibilityOff

                        val description =
                            if (viewModel.passwordVisibility) "Hide Password" else "Show Password"

                        IconButton(onClick = { viewModel.changePassVisibility() }) {
                            Icon(iconImage, description, tint = Color.Blue)
                        }
                    },
                    onValueChange = { value -> viewModel.onTextChange(value, false) })
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth(.72f)
            ) {
                Button(
                    onClick = { viewModel.signIn() },
                    shape = RoundedCornerShape(0.dp),
                    modifier = Modifier.fillMaxWidth(),
                    content = {
                        Text(text = "Login")
                    })
                Button(
                    onClick = {
                        try {
                            viewModel.registerEP()
                        } catch (ex: Exception) {
                            if (ex is PedroErrorException) {
                                ex.message?.let { viewModel.postError(ex.title, it) }
                                return@Button
                            }

                            throw ex
                        }

                        println(viewModel.authResponse)
                        //TODO doesn't work here, will need to create an error announcement system to dispatch error
                        // messages and close them after the 'Dismiss' button has been executed or the timer has ran
                        // out. Perhaps something like a FIFO queue, that will process each error sequentially?
                        if (viewModel.authResponse!!.state == AuthState.FAILURE) {
                            println("Auth state is failure.")
                            val ex = viewModel.authResponse!!.exception

                            when (ex) {
                                is FirebaseAuthWeakPasswordException -> {
                                    viewModel.postError("Weak Password", "Password is too weak.")
                                }

                                is FirebaseAuthUserCollisionException -> {
                                    viewModel.postError(
                                        "Email Taken",
                                        "Email address has already been used."
                                    )
                                }

                                is FirebaseAuthInvalidCredentialsException -> {
                                    viewModel.postError(
                                        "Password Policy Breach",
                                        "Password doesn't meet policy."
                                    )
                                }

                            }
                            return@Button
                        }
                    },
                    shape = RoundedCornerShape(0.dp),
                    modifier = Modifier.fillMaxWidth(),
                    content = {
                        Text(
                            text = "" +
                                    "Register"
                        )
                    })
            }
        }
    }

    @Composable
    fun DecksScreen() {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Button(onClick = { viewModel.signout() }, content = { Text(text = "Signout") })
        }
    }
}