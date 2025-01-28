package com.example.callphone.ui.theme.screens

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.callphone.ui.theme.data.FirebaseContactRepository
import com.example.callphone.ui.theme.viewmodel.ContactViewModel
import com.example.callphone.ui.theme.viewmodel.ContactViewModelFactory

@Composable
fun EditContactScreen(
    context: Context,
    contactName: String,
    contactPhone: String,
    firebaseRepository: FirebaseContactRepository,
    navController: NavController,
    viewModel: ContactViewModel = viewModel(factory = ContactViewModelFactory(context, firebaseRepository = firebaseRepository))
) {
    var updatedName by remember { mutableStateOf(contactName) }
    var updatedPhone by remember { mutableStateOf(contactPhone) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Edit Contact", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = updatedName,
            onValueChange = { updatedName = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = updatedPhone,
            onValueChange = { updatedPhone = it },
            label = { Text("Phone") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                    try {
                        if (viewModel.updateContactInPhoneDirectory(contactName, updatedName, updatedPhone)) {
                            viewModel.refreshContacts()
                            Toast.makeText(context, "Contact updated successfully", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        } else {
                            Toast.makeText(context, "Failed to update contact", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Log.e("EditContactScreen", "Error updating contact", e)
                        Toast.makeText(context, "An error occurred while updating contact", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    ActivityCompat.requestPermissions(
                        (context as Activity),
                        arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS),
                        1
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save")
        }

    }
}


