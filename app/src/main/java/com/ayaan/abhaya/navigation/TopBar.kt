package com.ayaan.abhaya.navigation

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    navController: NavController,
    title: String,
    onMenuClick: () -> Unit
) {
//    TopAppBar(
//        title = {
//            Text(
//                text = title,
//                style = MaterialTheme.typography.titleMedium, // smaller font size
////                modifier = Modifier
////                    .height(32.dp) // tighter height
//            )
//        },
//        modifier = Modifier.height(48.dp), // reduced overall height
//        navigationIcon = {
//            IconButton(
//                onClick = onMenuClick,
//                modifier = Modifier
//                    .size(40.dp)
//            ) {
//                Icon(
//                    imageVector = Icons.Default.Menu,
//                    contentDescription = "Menu",
//                    modifier = Modifier.size(20.dp)
//                )
//            }
//        },
//        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
//            containerColor = Color.White,
//            titleContentColor = Color.Black,
//            navigationIconContentColor = Color.Black,
//            actionIconContentColor = Color.Black
//        )
//    )
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.White,
            titleContentColor = Color.Black,
            navigationIconContentColor = Color.Black,
            actionIconContentColor = Color.Black
        ),
        title = {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onMenuClick,
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    modifier = Modifier.size(20.dp)
                )
            }
        },
//        navigationIcon = {
//            IconButton(onClick = { /* do something */ }) {
//                Icon(
//                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
//                    contentDescription = "Localized description"
//                )
//            }
//        },
//        actions = {
//            IconButton(onClick = { /* do something */ }) {
//                Icon(
//                    imageVector = Icons.Filled.Menu,
//                    contentDescription = "Localized description"
//                )
//            }
//        },
    )
}



