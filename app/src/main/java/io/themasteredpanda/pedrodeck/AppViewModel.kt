package io.themasteredpanda.pedrodeck

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import java.util.LinkedList

data class ErrorMessage(var visible: Boolean = true, val title: String, val message: String)

open class AppViewModel : ViewModel() {
    var auth: FirebaseAuth = Firebase.auth
    var store: FirebaseFirestore = Firebase.firestore
    var emailValue: String by mutableStateOf("")
    var passwordValue: String by mutableStateOf("")
    var passwordVisibility: Boolean by mutableStateOf(false)
    var fbUser: FirebaseUser? by mutableStateOf(null)
    var authResponse: AuthResponse? by mutableStateOf(null)
    var queue = mutableStateOf(LinkedList<PresentableError>())

    /**
     * TODO: Handle common exceptions in both signup and registration processes.
     * TODO: Integrate google one tap signup.
     * TODO: Establish Firebase Storage for images.
     * TODO: Establish a Firestore to associate images as deck, with accounts.
     * TODO: Handle exceptions in connectivity.
     **/

    init {
        this.auth.useEmulator("10.0.2.2", 9099)
        this.store.useEmulator("10.0.2.2", 8080)
        this.fbUser = this.auth.currentUser

        if (this.signedIn()) {
            authResponse = AuthResponse(AuthState.COMPLETE, AuthProcessType.SIGNED_IN)
        }
    }

    fun peekAtError(): PresentableError? {
        return this.queue.value.peek()
    }

    fun removeFirstError() {
        this.queue.value = LinkedList(this.queue.value).apply { removeFirst() }
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
        MainActivity.logger("Attempting to register a account. Email Value: ${emailValue} Password Value: ${passwordValue}")
        this.authResponse = AuthResponse(AuthState.PROCESSING, AuthProcessType.REGISTERING)

        if (this.emailValue == "") {
            throw PedroErrorException("Invalid Email", "Email cannot be empty.")
        }

        if (this.passwordValue == "") {
            throw PedroErrorException("Invalid Password", "Password cannot be empty.")
        }

        MainActivity.logger("Kicking creating to Firebase Auth.")
        this.auth.createUserWithEmailAndPassword(emailValue, passwordValue)
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
                    //SRP Princples not followed.
                    MainActivity.logger("Attempting to sign in user. Email Value: $emailValue Password Value: $passwordValue")
                    if (exception is FirebaseAuthUserCollisionException) {
                        this.auth.signInWithEmailAndPassword(emailValue, passwordValue)
                            .addOnCompleteListener { result1 ->
                                run {
                                    MainActivity.logger("Sign in successful.")
                                    if (result1.isSuccessful) {
                                        this.fbUser = auth.currentUser
                                    }
                                }
                            }.addOnFailureListener { exception ->
                                run {
                                    throw (if (exception.message != null) exception.message else "")?.let {
                                        PedroErrorException(
                                            "Sign in failed",
                                            it
                                        )
                                    }!!
                                }
                            }
                    }
                }
            }
    }

    fun signIn() {
        MainActivity.logger("Attempting to sign in user. Email value: $emailValue Password value: $passwordValue")
        this.authResponse = AuthResponse(AuthState.PROCESSING, AuthProcessType.SIGNING_IN)

        this.auth.signInWithEmailAndPassword(emailValue, passwordValue)
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
        this.auth.signOut()
        this.fbUser = null
    }

    fun changePassVisibility() {
        this.passwordVisibility = !this.passwordVisibility
        MainActivity.logger("Changed password visibility to ${this.passwordVisibility}")
    }

    fun postError(title: String, message: String, duration: Long = 10) {
        MainActivity.logger("Posting error. Title: $title Message: '$message' Duration: $duration")
        this.queue.value =
            LinkedList(queue.value).apply { add(PresentableError(title, message, duration)) }
        MainActivity.logger("Error Queue Size: ${this.queue.value.size}")
    }
}
