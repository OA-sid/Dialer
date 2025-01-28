package com.example.callphone.ui.theme.screens


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.collectAsState
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.callphone.ui.theme.data.FirebaseContactRepository
import com.example.callphone.ui.theme.viewmodel.ContactViewModel
import com.example.callphone.ui.theme.viewmodel.ContactViewModelFactory


@Composable
fun ContactScreen(
    context: Context,
    navController: NavController,
    firebaseRepository: FirebaseContactRepository,
    viewModel: ContactViewModel = viewModel(factory = ContactViewModelFactory(context,firebaseRepository))
) {
    val contacts by viewModel.contacts.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "Contact List",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )



            if (contacts.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No contacts found.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Box(Modifier.fillMaxWidth()){
                    // Sync Contacts Button
                    Button(onClick = {
                        viewModel.refreshContacts() // Manually trigger sync
                        Toast.makeText(context, "Contacts synced with Firebase", Toast.LENGTH_SHORT)
                            .show()
                    }) {
                        Text(text = "Sync Contacts")
                    }
                }
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {



                    items(contacts.size) { index ->
                        val contact = contacts[index]


                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val encodedName = Uri.encode(contact.name)
                                    val route = "detail_screen/$encodedName"
                                    navController.navigate(route)
                                },
                            elevation = CardDefaults.cardElevation(4.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(
                                            color = MaterialTheme.colorScheme.primary,
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = contact.name.firstOrNull()?.toString()?.uppercase()
                                            ?: "?",
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Column {
                                    Text(
                                        text = contact.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = contact.phone,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Floating Action Button for the Keypad screen
        FloatingActionButton(
            onClick = {
                navController.navigate("keypad_screen")
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(
                imageVector = Icons.Default.Phone,
                contentDescription = "Open Keypad",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}



