package com.ayaan.abhaya.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun NavDrawer(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.align(Alignment.TopStart)) {
            Text(
                text = "Home",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate(Destinations.Home.route) }
                    .padding(16.dp)
            )
            Text(
                text = "Emergency Contacts",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate(Destinations.Friends.route) }
                    .padding(16.dp)
            )
        }

        Text(
            text = "Logout",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .clickable {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate(Destinations.Login.route) {
                        popUpTo(0) // Clear back stack
                    }
                }
                .padding(16.dp)
        )
    }
}
