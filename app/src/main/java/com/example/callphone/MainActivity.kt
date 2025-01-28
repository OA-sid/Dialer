package com.example.callphone

import android.os.Bundle
import android.Manifest
import android.graphics.Color
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import com.example.callphone.ui.theme.viewmodel.AppNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                setContent {
                    AppNavigation(this@MainActivity) // Pass the context to AppNavigation
                }
            } else {
                Toast.makeText(this, "Permission is required to access contacts.", Toast.LENGTH_SHORT).show()
            }
        }

        if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {
            Toast.makeText(this, "Contacts permission is needed to display the contact list.", Toast.LENGTH_LONG).show()
        }

        requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
    }

    private fun enableEdgeToEdge() {
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.navigationBarColor = Color.BLUE
    }


}



