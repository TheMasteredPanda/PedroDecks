package io.themasteredpanda.pedrodeck.repository

import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import io.themasteredpanda.pedrodeck.model.Deck

class Decks(userId: Number) {
    private val firestorage: FirebaseStorage
    private val firestore: FirebaseFirestore
    private val storageReference: StorageReference
    private val userId: Number = userId

    init {
        /**
         * Firestore will keep all metadata relevant to each deck.
         *
         * For each Deck, the metadata created will be:
         * - Deck Name
         * - Created Date
         * - Last Edited
         *
         * Cloud Storage will hold the cards. Each deck will be kept in
         * a folder under the user's id. The id of the deck will be
         * related to the metadata kept in Firestore.
         *
         */
        firestorage = Firebase.storage
        firestore = Firebase.firestore
        storageReference = firestorage.reference
    }

    fun getDeckMeta(successCallback: (QuerySnapshot) -> Unit, failureCallback: (Exception) -> Unit) {
        val task = firestore.collection(userId.toString()).get()
        task.addOnSuccessListener { data -> run { successCallback(data) } }
        task.addOnFailureListener { exception -> run { failureCallback(exception)} }
    }

    fun getDeckURLs(deckId: String , successCallback: (ListResult) -> Unit, failureCallback: (Exception) -> Unit) {
        val task = storageReference.child(userId.toString() + '_' + deckId).listAll()
        task.addOnSuccessListener { list -> run { successCallback(list) } }
        task.addOnFailureListener { ex -> run { failureCallback(ex) } }
    }

    fun downloadDeck(urls: List<String>, successCallback: (ListResult) -> Unit, failureCallback: (Exception) -> Unit, firstOnly: Boolean = false) {

    }

    fun createDeck() { }
}