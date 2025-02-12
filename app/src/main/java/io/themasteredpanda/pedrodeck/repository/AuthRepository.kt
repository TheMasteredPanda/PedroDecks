package io.themasteredpanda.pedrodeck.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

class AuthRepository @Inject constructor(private val auth: FirebaseAuth) {

    init {
        this.auth.useEmulator("10.0.2.2", 9099)
    }

    fun register(email: String, password: String): Task<AuthResult> {
        return auth.createUserWithEmailAndPassword(email, password)
    }

    fun login(email: String, password: String): Task<AuthResult> {
        return auth.signInWithEmailAndPassword(email, password)
    }

    fun signout() {
        this.auth.signOut()
    }

    fun getUser(): FirebaseUser? {
        return this.auth.currentUser
    }
}