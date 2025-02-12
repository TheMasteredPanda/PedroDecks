package io.themasteredpanda.pedrodeck

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import dagger.hilt.android.lifecycle.HiltViewModel
import io.themasteredpanda.pedrodeck.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.LinkedList
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(private val auth: AuthRepository) : ViewModel() {
    var store: FirebaseFirestore = Firebase.firestore
    var emailValue: String by mutableStateOf("")
    var passwordValue: String by mutableStateOf("")
    var passwordVisibility: Boolean by mutableStateOf(false)
    var fbUser: FirebaseUser? by mutableStateOf(null)
    var authResponse: AuthResponse? by mutableStateOf(null)
    var queue = MutableStateFlow(emptyList<PresentableError>() as LinkedList<PresentableError>)

    /**
     * TODO: Integrate google one tap signup.
     * TODO: Establish Firebase Storage for images.
     * TODO: Establish a Firestore to associate images as deck, with accounts.
     **/

    init {
        this.store.useEmulator("10.0.2.2", 8080)
        this.fbUser = this.auth.getUser()

        if (this.signedIn()) {
            authResponse = AuthResponse(AuthState.COMPLETE, AuthProcessType.SIGNED_IN)
        }
    }

    fun peekAtError(): PresentableError? {
        return this.queue.value.peek()
    }

    fun removeFirstError() {
        this.queue.value = this.queue.value.drop(1) as LinkedList<PresentableError>
    }

    fun signedIn(): Boolean {
        MainActivity.logger("Checking if user has been signed in.")
        return this.fbUser != null
    }

    fun onTextChange(value: String, email: Boolean = true) = if (email) {
        MainActivity.logger("Changing email value to ${value}.")
        this.emailValue = value
    } else {
        MainActivity.logger("Changing password value to ${value}.")
        this.passwordValue = value
    }

    fun registerEP() {
        MainActivity.logger("Attempting to register a account. Email Value: $emailValue Password Value: $passwordValue")
        this.authResponse = AuthResponse(AuthState.PROCESSING, AuthProcessType.REGISTERING)

        if (this.emailValue == "") {
            throw PedroErrorException("Invalid Email", "Email cannot be empty.")
        }

        if (this.passwordValue == "") {
            throw PedroErrorException("Invalid Password", "Password cannot be empty.")
        }

        MainActivity.logger("Kicking creating to Firebase Auth.")
        this.auth.register(emailValue, passwordValue)
            .addOnSuccessListener { result ->
                run {
                    this.fbUser = result.user
                    this.authResponse = AuthResponse(AuthState.SUCCESS, AuthProcessType.REGISTERING)
                }
            }.addOnFailureListener { exception ->
                run {
                    MainActivity.logger("Failure on account registeration. Message: ${exception.message}")
                    this.authResponse = AuthResponse(
                        AuthState.FAILURE, AuthProcessType.REGISTERING, exception, exception
                            .message
                    )

                    this.postError(
                        "Email Already Used",
                        "Email address is already in use by another account."
                    )
                    return@addOnFailureListener
                }
            }
    }

    fun signIn() {
        MainActivity.logger("Attempting to sign in user. Email value: $emailValue Password value: $passwordValue")
        this.authResponse = AuthResponse(AuthState.PROCESSING, AuthProcessType.SIGNING_IN)

        this.auth.login(emailValue, passwordValue)
            .addOnSuccessListener { result ->
                run {
                    MainActivity.logger("User signed in.")
                    this.fbUser = result.user
                    this.authResponse!!.state = AuthState.SUCCESS
                }
            }.addOnFailureListener { exception ->
                run {
                    MainActivity.logger("Failure. Message: ${exception.message}")
                    val tempAuth = this.authResponse!!
                    tempAuth.state = AuthState.FAILURE
                    tempAuth.exception = exception
                    tempAuth.exceptionMessage = exception.message
                    this.authResponse = tempAuth
                }
            }
    }

    fun signout() {
        MainActivity.logger("Signing user out.")
        this.auth.signout()
        this.fbUser = null
    }

    fun changePassVisibility() {
        this.passwordVisibility = !this.passwordVisibility
        MainActivity.logger("Changed password visibility to ${this.passwordVisibility}")
    }

    fun postError(title: String, message: String, duration: Long = 10) {
        MainActivity.logger("Posting error. Title: $title Message: '$message' Duration: $duration")
        this.queue.value = (this.queue.value + PresentableError(
            title,
            message,
            duration
        )) as LinkedList<PresentableError>
        MainActivity.logger("Error Queue Size: ${this.queue.value.size}")
    }
}
