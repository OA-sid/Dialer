package com.example.callphone.ui.theme.screens

import android.Manifest
import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun KeypadScreen(context: Context, navController: NavController) {
    var phoneNumber by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Bottom
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Display the phone number
        Text(
            text = phoneNumber,
            style = MaterialTheme.typography.titleLarge.copy(color = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            textAlign = TextAlign.Center
        )

        // Keypad Buttons
        Keypad(
            onButtonClick = { phoneNumber += it },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Row with Backspace Button for delete last digit
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = {
                if (phoneNumber.isNotEmpty()) {
                    phoneNumber = phoneNumber.dropLast(1)
                }
            }) {
                Text("BackSpace")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Call button
        Button(
            onClick = {
                if (phoneNumber.isNotEmpty()) {
                    if (context.checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$phoneNumber"))
                        context.startActivity(intent)
                    } else {
                        Toast.makeText(
                            context,
                            "Call permission is required.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(context, "Enter a valid phone number.", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(50)),
            colors = ButtonDefaults.buttonColors(Color.Green)
        ) {
            Text("Call", color = Color.White)
        }
    }
}

@Composable
fun Keypad(onButtonClick: (String) -> Unit, modifier: Modifier = Modifier) {
    val keypadButtons = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("*", "0", "#")
    )

    Column(modifier = modifier) {
        for (row in keypadButtons) {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                for (button in row) {
                    Button(
                        onClick = { onButtonClick(button) },
                        modifier = Modifier.size(80.dp),
                        colors = ButtonDefaults.buttonColors(Color.DarkGray)
                    ) {
                        Text(button, fontSize = 24.sp, color = Color.White)
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}