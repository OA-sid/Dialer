package com.example.callphone.ui.theme.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.callphone.ui.theme.data.FirebaseContactRepository

class ContactViewModelFactory(
    private val context: Context,
    private val firebaseRepository: FirebaseContactRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContactViewModel::class.java)) {
            return ContactViewModel(context, firebaseRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


