package com.example.callphone.ui.theme.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.callphone.ui.theme.data.FirebaseContactRepository
import com.example.callphone.ui.theme.viewmodel.ContactViewModel
import com.example.callphone.ui.theme.viewmodel.ContactViewModelFactory


@Composable
fun DetailScreen(
    context: Context,
    contactName: String,
    navController: NavController,
    firebaseRepository: FirebaseContactRepository
) {
    val viewModel: ContactViewModel = viewModel(
        factory = ContactViewModelFactory(context, firebaseRepository)
    )
    val contactDetails = viewModel.getContactDetails(contactName).collectAsState(initial = null)

    val callPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(context, "Call permission is required.", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Title
        Text(
            text = "Contact Details",
            fontSize = 30.sp,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        contactDetails.value?.let { details ->
            // Card for Contact Details
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                elevation = CardDefaults.cardElevation(6.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Name: ${details.name}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Phone: ${details.phone}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        val encodedName = Uri.encode(details.name)
                        val encodedPhone = Uri.encode(details.phone)
                        navController.navigate("edit_screen/$encodedName/$encodedPhone")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Edit Contact")
                }

                Button(
                    onClick = {
                        try {
                            if (viewModel.deleteContactFromPhoneDirectory(details.name)) {
                                viewModel.refreshContacts()
                                Toast.makeText(
                                    context,
                                    "Deleted: ${details.name}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                navController.popBackStack()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Failed to delete contact",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: Exception) {
                            Log.e("DetailScreen", "Error deleting contact", e)
                            Toast.makeText(
                                context,
                                "An error occurred while deleting contact",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete Contact")
                }

                Button(
                    onClick = {
                        if (context.checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                            val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:${details.phone}"))
                            context.startActivity(intent)
                        } else {
                            callPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary)
                ) {
                    Text("Call Contact")
                }
            }
        } ?: run {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Loading contact details or contact not found.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
