package com.example.trafficsignapp.ui.theme.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.trafficsignapp.R
import com.example.trafficsignapp.data.User
import com.example.trafficsignapp.TrafficSignApp
import com.example.trafficsignapp.ui.theme.nav.Screen
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import com.google.firebase.auth.ktx.userProfileChangeRequest

@Composable
fun SignUpScreen(navController: NavHostController) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val scope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Sign Up", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = displayName,
            onValueChange = { displayName = it },
            label = { Text("Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation =
                if (passwordVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
            trailingIcon = {
                val icon = if (passwordVisible)
                    Icons.Default.Visibility
                else Icons.Default.VisibilityOff
                IconButton(
                    onClick = { passwordVisible = !passwordVisible }
                ) {
                    Icon(icon, contentDescription = null)
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(24.dp))

        errorMsg?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(8.dp))
        }

        Button(
            onClick = {
                errorMsg = null
                if (displayName.isBlank() || email.isBlank() || password.length < 6) {
                    errorMsg = "Please enter name, valid email, and password ≥ 6 chars"
                    return@Button
                }
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener { creds ->
                        val user = creds.user!!
                        // update display name
                        val profile = userProfileChangeRequest {
                            this.displayName = displayName
                        }
                        user.updateProfile(profile).addOnCompleteListener {
                            // save to Room
                            scope.launch(Dispatchers.IO) {
                                TrafficSignApp.database.userDao().upsert(
                                    User(id = user.uid, email = user.email!!, name = displayName)
                                )
                            }
                            // navigate to main
                            navController.navigate(Screen.Main.route) {
                                popUpTo(Screen.SignUp.route) { inclusive = true }
                            }
                        }
                    }
                    .addOnFailureListener { e ->
                        errorMsg = e.localizedMessage
                    }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Account")
        }

        Spacer(Modifier.height(16.dp))
        TextButton(onClick = {
            navController.popBackStack()  // back to login
        }) {
            Text("Already have an account? Log in", color = Color.Blue)
        }
    }
}