package com.ayaan.abhaya.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ayaan.abhaya.viewmodels.HomeViewModel

@Composable
fun HomeScreen(navController: NavController, vm: HomeViewModel = viewModel()) {
    LaunchedEffect(Unit) {
        vm.fetchUserData()
    }

    // Collect state flows from the ViewModel
    val userData by vm.userData.collectAsState()
    val userDataLoadingState by vm.userDataLoadingState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        // User information card at the top
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "User Information",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                when (userDataLoadingState) {
                    is HomeViewModel.UserDataLoadingState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Loading user data...")
                    }
                    is HomeViewModel.UserDataLoadingState.Success -> {
                        userData?.let { user ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Name:",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = user.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                                )
                            }

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 4.dp),
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Phone:",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = user.phoneNo,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                                )
                            }
                        }
                    }
                    is HomeViewModel.UserDataLoadingState.Error -> {
                        val error = userDataLoadingState as HomeViewModel.UserDataLoadingState.Error
                        Text(
                            text = "Error: ${error.message}",
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { vm.fetchUserData() }) {
                            Text("Retry")
                        }
                    }
                    else -> { /* Idle state */ }
                }
            }
        }

        // Main content in a Box to position elements
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            // Centered Button
            // Centered Button
            Button(
                onClick = {
                    userData?.let { user ->
                        // You would typically get these from location services
                        // Using placeholder values for demonstration
                        val currentLatitude = 37.422160
                        val currentLongitude = -122.084270

                        vm.sendSos(
                            latitude = currentLatitude,
                            longitude = currentLongitude,
                            name = user.name,
                            phoneNo = user.phoneNo
                        )
                    }
                },
                modifier = Modifier.align(Alignment.Center),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                enabled = userData != null && userDataLoadingState is HomeViewModel.UserDataLoadingState.Success
            ) {
                Text(text = "SOS", style = MaterialTheme.typography.titleMedium)
            }

            // FAB in Bottom Right
            FloatingActionButton(
                onClick = {
                    userData?.let { user ->
                        // You would typically get these from location services
                        // Using placeholder values for demonstration
                        val currentLatitude = 37.422160
                        val currentLongitude = -122.084270

                        vm.sendSos(
                            latitude = currentLatitude,
                            longitude = currentLongitude,
                            name = "Anonymous",
                            phoneNo = "XXXXXXXXX"
                        )
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(Icons.Filled.Notifications, contentDescription = "Add")
            }
        }
    }
}