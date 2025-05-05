package com.ayaan.abhaya.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.ayaan.abhaya.navigation.NavDrawer
import com.ayaan.abhaya.navigation.TopBar
import com.ayaan.abhaya.utils.LocationHelper
import kotlinx.coroutines.launch

@Composable
fun Friends(navController: NavController) {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    ModalNavigationDrawer(
        drawerState = drawerState, drawerContent = {
            ModalDrawerSheet {
                NavDrawer(navController)
            }
        }) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopBar(
                navController = navController, title = "Emergency Contacts", onMenuClick = {
                    scope.launch {
                        drawerState.open()
                    }
                }
            )
        }
    }
}