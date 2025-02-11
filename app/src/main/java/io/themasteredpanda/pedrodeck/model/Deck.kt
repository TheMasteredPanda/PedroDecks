package io.themasteredpanda.pedrodeck.model

import com.google.firebase.storage.FirebaseStorage

class Deck(firebaseStorage: FirebaseStorage, userId: Number) {
    val firestorage: FirebaseStorage = firebaseStorage
    val userId: Number = userId

}