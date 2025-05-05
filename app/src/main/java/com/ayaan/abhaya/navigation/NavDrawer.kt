package com.ayaan.abhaya.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun NavDrawer(navController: NavController){
  Column {
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
}