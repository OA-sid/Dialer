package com.example.callphone.ui.theme.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseContactRepository {

    private val contactsCollection = FirebaseFirestore.getInstance().collection("contacts")



    // Fetch all contacts from Firestore
    suspend fun getContacts(): List<Contact> {
        return contactsCollection.get().await().toObjects(Contact::class.java)
    }

}
