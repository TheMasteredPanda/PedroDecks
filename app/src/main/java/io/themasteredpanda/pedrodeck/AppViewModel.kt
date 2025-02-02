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

data class ErrorMessage(var visible: Boolean = true, val title: String, val message: String)


class AppViewModel : ViewModel() {
    private var auth: FirebaseAuth = Firebase.auth
    var emailValue: String by mutableStateOf("")
    var passwordValue: String by mutableStateOf("")
    var passwordVisibility: Boolean by mutableStateOf(false)
    var fbUser: FirebaseUser? by mutableStateOf(null)
    var authResponse: AuthResponse? by mutableStateOf(null)
    var errorMessage: ErrorMessage? by mutableStateOf(null)

    /**
     * TODO: Handle common exceptions in both signup and registration processes.
     * TODO: Integrate google one tap signup.
     * TODO: Establish Firebase Storage for images.
     * TODO: Establish a Firestore to associate images as deck, with accounts.
     * TODO: Handle exceptions in connectivity.
     **/

    init {
        this.auth.useEmulator("10.0.2.2", 5000)
        this.fbUser = this.auth.currentUser

        if (this.signedIn()) {
            //As no auth process was executed, authresponse set to complete.
            authResponse = AuthResponse(AuthState.COMPLETE, AuthProcessType.SIGNED_IN)
        }
    }

    fun signedIn(): Boolean {
        return this.fbUser != null
    }

    fun onTextChange(value: String, email: Boolean = true) = if (email) {
        this.emailValue = value
    } else {
        this.passwordValue = value
    }

    fun registerEP() {
        this.authResponse = AuthResponse(AuthState.PROCESSING, AuthProcessType.REGISTERING)

        if (this.emailValue == "") {
            throw PedroErrorException("Invalid Email", "Email cannot be empty.")
        }

        if (this.passwordValue == "") {
            throw PedroErrorException("Invalid Password", "Password cannot be empty.")
        }

        this.auth.createUserWithEmailAndPassword(emailValue, passwordValue).addOnSuccessListener { result ->
            run {
                this.fbUser = result.user
                this.authResponse = AuthResponse(AuthState.SUCCESS, AuthProcessType.REGISTERING)
            }
        }.addOnFailureListener { exception ->
            run {
                println("Exception in registeration thrown.")
                this.authResponse = AuthResponse(
                    AuthState.FAILURE, AuthProcessType.REGISTERING, exception, exception
                        .message
                )

                println("Auth Response after exception caught:")
                println(this.authResponse)
                //SRP Princples not followed.
                if (exception is FirebaseAuthUserCollisionException) {
                    this.auth.signInWithEmailAndPassword(emailValue, passwordValue).addOnCompleteListener { result1 ->
                        run {
                            if (result1.isSuccessful) {
                                this.fbUser = auth.currentUser
                            }
                        }
                    }.addOnFailureListener { exception ->
                        run {
                            throw exception
                        }
                    }
                }
            }
        }
    }

    fun signIn() {
        this.authResponse = AuthResponse(AuthState.PROCESSING, AuthProcessType.SIGNING_IN)

        this.auth.signInWithEmailAndPassword(emailValue, passwordValue).addOnSuccessListener { result ->
            run {
                this.fbUser = result.user
                this.authResponse!!.state = AuthState.SUCCESS
            }
        }.addOnFailureListener { exception ->
            run {
                val tempAuth = this.authResponse!!
                tempAuth.state = AuthState.FAILURE
                tempAuth.exception = exception
                tempAuth.exceptionMessage = exception.message
                this.authResponse = tempAuth
            }
        }
    }

    fun signout() {
        this.auth.signOut()
        this.fbUser = null
    }

    fun changePassVisibility() {
        this.passwordVisibility = !this.passwordVisibility
    }

    fun clearErrorMessage() {
        this.errorMessage?.visible = false
        this.errorMessage = null
    }

    fun createErrorMessage(title: String, message: String) {
        this.errorMessage = ErrorMessage(title = title, message = message)
    }
}
