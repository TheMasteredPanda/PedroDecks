package io.themasteredpanda.pedrodeck

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.themasteredpanda.pedrodeck.repository.AuthRepository
import io.themasteredpanda.pedrodeck.repository.FirestoreRepository
import io.themasteredpanda.pedrodeck.repository.StorageRepository
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class InjectionModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = Firebase.auth

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = Firebase.firestore

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage = Firebase.storage

    @Provides
    @Singleton
    fun provideAuthRepository(auth: FirebaseAuth): AuthRepository {
        return AuthRepository(auth)
    }

    @Provides
    @Singleton
    fun provideFirestoreRepository(firestore: FirebaseFirestore): FirestoreRepository {
        return FirestoreRepository(firestore)
    }

    @Provides
    @Singleton
    fun provideStorageRepository(storage: FirebaseStorage): StorageRepository {
        return StorageRepository(storage)
    }


}
