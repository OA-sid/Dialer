package com.example.callphone.ui.theme.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.callphone.ui.theme.data.FirebaseContactRepository
import com.example.callphone.ui.theme.screens.ContactScreen
import com.example.callphone.ui.theme.screens.DetailScreen
import com.example.callphone.ui.theme.screens.EditContactScreen
import com.example.callphone.ui.theme.screens.KeypadScreen

@Composable
fun AppNavigation(context: Context) {
    val firebaseRepository = FirebaseContactRepository() // Initialize the repository
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "contact_screen") {
        composable("contact_screen") {
            ContactScreen(
                context = context,
                navController = navController,
                firebaseRepository
            )
        }
        composable("keypad_screen") {
            KeypadScreen(context = context, navController = navController)
        }
        composable("detail_screen/{contactName}") { backStackEntry ->
            val contactName = backStackEntry.arguments?.getString("contactName")?.let { Uri.decode(it) } ?: ""
            DetailScreen(
                context = context,
                contactName = contactName,
                navController = navController,
                firebaseRepository = firebaseRepository
            )
        }
        composable("edit_screen/{contactName}/{contactPhone}") { backStackEntry ->
            val contactName = backStackEntry.arguments?.getString("contactName")?.let { Uri.decode(it) } ?: ""
            val contactPhone = backStackEntry.arguments?.getString("contactPhone")?.let { Uri.decode(it) } ?: ""
            EditContactScreen(
                context = context,
                contactName = contactName,
                contactPhone = contactPhone,
                navController = navController,
                firebaseRepository = firebaseRepository
            )
        }
    }
}




