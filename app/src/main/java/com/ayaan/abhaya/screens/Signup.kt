package com.ayaan.abhaya.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.ayaan.abhaya.viewmodels.SignUpViewModel
import com.ayaan.abhaya.viewmodels.SignUpState

@Composable
fun SignUpScreen(
    onSignUpClick: () -> Unit,
    onSignInClick: () -> Unit,
    viewModel: SignUpViewModel = viewModel()
){
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember {mutableStateOf("")}
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val context = LocalContext.current
    val signUpState by viewModel.signUpState
    val scope = rememberCoroutineScope()

    val genderOptions = listOf("Female", "Male")
    var selectedGender by remember { mutableStateOf(genderOptions[0]) }

    LaunchedEffect(signUpState) {
        when (signUpState) {
            is SignUpState.Success -> {
                onSignUpClick() // This is the correct place for navigation
                viewModel.resetState()
            }
            is SignUpState.Error -> {
                // Handle error state
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Create Account", style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier = Modifier.height(32.dp))

        // Full Name input
        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = { Text("Full Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            enabled = signUpState !is SignUpState.Loading
        )

        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterStart
        ) {
        // Gender selection
        Text(
            text = "Gender",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            genderOptions.forEach { option ->
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .selectable(
                            selected = (option == selectedGender),
                            onClick = { selectedGender = option },
                            role = Role.RadioButton
                        )
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (option == selectedGender),
                        onClick = null,
                        enabled = signUpState !is SignUpState.Loading
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = option,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            enabled = signUpState !is SignUpState.Loading
        )
        // Email input
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            enabled = signUpState !is SignUpState.Loading
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password input
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            enabled = signUpState !is SignUpState.Loading
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Confirm Password input
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            enabled = signUpState !is SignUpState.Loading
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Error message
        if (signUpState is SignUpState.Error) {
            Text(
                text = (signUpState as SignUpState.Error).message,
                color = Color.Red,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        // Sign Up button
        Button(
            onClick = {
                scope.launch {
                    viewModel.signUpWithEmailPassword(
                        phone = phone,
                        fullName = fullName,
                        email = email,
                        password = password,
                        confirmPassword = confirmPassword,
                        gender = selectedGender
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
            shape = RoundedCornerShape(12.dp),
            enabled = signUpState !is SignUpState.Loading
        ) {
            if (signUpState is SignUpState.Loading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text(text = "Sign Up")
            }
        }

        TextButton(
            onClick = { onSignInClick() },
            modifier = Modifier.padding(start = 2.dp),
            enabled = signUpState !is SignUpState.Loading
        ) {
            Text(
                text = "Already have an account? Log In",
                color = Color.Gray
            )
        }
    }
}