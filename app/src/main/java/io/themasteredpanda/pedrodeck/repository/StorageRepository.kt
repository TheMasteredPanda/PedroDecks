package io.themasteredpanda.pedrodeck.repository

import com.google.firebase.storage.FirebaseStorage
import javax.inject.Inject

class StorageRepository @Inject constructor(private val storage: FirebaseStorage)