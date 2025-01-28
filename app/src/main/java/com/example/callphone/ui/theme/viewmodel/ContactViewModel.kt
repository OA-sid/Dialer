package com.example.callphone.ui.theme.viewmodel

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import android.provider.ContactsContract
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.callphone.ui.theme.data.Contact
import com.example.callphone.ui.theme.data.FirebaseContactRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await

class ContactViewModel(
    private val context: Context,
    private val firebaseRepository: FirebaseContactRepository
) : ViewModel() {

    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())
    val contacts: StateFlow<List<Contact>> = _contacts.asStateFlow()


    init {
            loadContacts()
    }



    fun refreshContacts() {
        viewModelScope.launch {
            val localContacts = loadContactsFromDevice(context)
            syncContactsWithFirebase(localContacts)  // Upload local contacts
            fetchContactsFromFirebase()              // Fetch updated list from Firebase
        }
    }

    private fun fetchContactsFromFirebase() {
        viewModelScope.launch {
            try {
                val fetchedContacts = firebaseRepository.getContacts()
                if (fetchedContacts.isNotEmpty()) {
                    _contacts.update { fetchedContacts }
                } else {
                    Log.w("ContactViewModel", "No contacts found on Firebase.")
                }
            } catch (e: Exception) {
                Log.e("ContactViewModel", "Error fetching contacts from Firebase: $e")
            }
        }
    }








    private fun loadContacts() {
        viewModelScope.launch(Dispatchers.IO) {
            val contactList = mutableListOf<Contact>()
            val resolver = context.contentResolver

            val cursor = resolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                arrayOf(
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER
                ),
                null,
                null,
                "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} ASC"
            )

            cursor?.use {
                val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

                while (it.moveToNext()) {
                    val name = it.getString(nameIndex)
                    val phone = it.getString(numberIndex)
                    if (!name.isNullOrEmpty() && !phone.isNullOrEmpty()) {
                        contactList.add(Contact(name, phone))
                    }
                }
            }

            // Update contacts flow with local contacts
            _contacts.update { contactList }
        }
    }


    private fun syncContactsWithFirebase(contactList: List<Contact>) {
        val db = FirebaseFirestore.getInstance()
        val batch = db.batch()

        contactList.forEach { contact ->
            val contactDocRef = db.collection("contacts").document(contact.phone)
            val contactMap = hashMapOf(
                "name" to contact.name,
                "phone" to contact.phone
            )

            batch.set(contactDocRef, contactMap)
        }

        batch.commit()
            .addOnSuccessListener {
                Log.d("Firebase", "Contacts synced successfully.")
            }
            .addOnFailureListener { e ->
                Log.w("Firebase", "Error syncing contacts", e)
            }
    }



    private fun loadContactsFromDevice(context: Context): List<Contact> {
        val contactList = mutableListOf<Contact>()
        val resolver = context.contentResolver

        val cursor = resolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
            ),
            null,
            null,
            "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} ASC"
        )

        cursor?.use {
            val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            while (it.moveToNext()) {
                val name = it.getString(nameIndex)
                val phone = it.getString(numberIndex)
                if (!name.isNullOrEmpty() && !phone.isNullOrEmpty()) {
                    contactList.add(Contact(name, phone))
                }
            }
        }

        return contactList
    }



    // Update contact's details
    @SuppressLint("Range")
    fun updateContactInPhoneDirectory(oldName: String, newName: String, newPhone: String): Boolean {
        val resolver = context.contentResolver

        val cursor = resolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(ContactsContract.CommonDataKinds.Phone.CONTACT_ID),
            "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} = ?",
            arrayOf(oldName),
            null
        )

        cursor?.use {
            if (it.moveToFirst()) {
                val contactId = it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID))

                val nameContentValues = ContentValues().apply {
                    put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, newName)
                }
                resolver.update(
                    ContactsContract.Data.CONTENT_URI,
                    nameContentValues,
                    "${ContactsContract.Data.CONTACT_ID} = ? AND ${ContactsContract.Data.MIMETYPE} = ?",
                    arrayOf(contactId, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                )

                val phoneContentValues = ContentValues().apply {
                    put(ContactsContract.CommonDataKinds.Phone.NUMBER, newPhone)
                }
                resolver.update(
                    ContactsContract.Data.CONTENT_URI,
                    phoneContentValues,
                    "${ContactsContract.Data.CONTACT_ID} = ? AND ${ContactsContract.Data.MIMETYPE} = ?",
                    arrayOf(contactId, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                )

                return true // Update successful
            }
        }

        return false // Update failed (contact not found or query issue)
    }

    // Delete a contact
    @SuppressLint("Range")
    fun deleteContactFromPhoneDirectory(name: String): Boolean {
        val resolver = context.contentResolver
        val uri = ContactsContract.RawContacts.CONTENT_URI
        val cursor = resolver.query(
            uri,
            arrayOf(ContactsContract.RawContacts._ID),
            "${ContactsContract.Contacts.DISPLAY_NAME_PRIMARY} = ?",
            arrayOf(name),
            null
        )

        cursor?.use {
            if (it.moveToFirst()) {
                val contactId = it.getLong(it.getColumnIndex(ContactsContract.RawContacts._ID))
                val deleteUri = Uri.withAppendedPath(uri, contactId.toString())
                resolver.delete(deleteUri, null, null)
                return true
            }
        }

        return false
    }

    // Get contact details by name
    fun getContactDetails(name: String): StateFlow<Contact?> {
        val contactFlow = MutableStateFlow<Contact?>(null)
        viewModelScope.launch {
            val contact = _contacts.value.find { it.name == name }
            contactFlow.update { contact }
        }
        return contactFlow.asStateFlow()
    }
}
