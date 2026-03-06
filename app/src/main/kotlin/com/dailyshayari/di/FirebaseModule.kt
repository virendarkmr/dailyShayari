package com.dailyshayari.di

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase

object FirebaseModule {

    // Centralized Storage Config
    private const val IS_PRODUCTION = false // Toggle this to true for production

    private const val TEST_STORAGE_BUCKET = "dailyshayari-3b6eb.firebasestorage.app"
    private const val PROD_STORAGE_BUCKET = "dailyshayari-prod.firebasestorage.app" // Placeholder for your future Indian bucket

    val storageBucket: String
        get() = if (IS_PRODUCTION) PROD_STORAGE_BUCKET else TEST_STORAGE_BUCKET

    val firebaseStorageBaseUrl: String
        get() = "https://firebasestorage.googleapis.com/v0/b/$storageBucket/o"

    fun provideFirestore(): FirebaseFirestore {
        val firestore = Firebase.firestore
        firestore.firestoreSettings = firestoreSettings {
            isPersistenceEnabled = true
        }
        return firestore
    }
}
