package com.ayaan.abhaya.screens
import android.content.Context
import android.content.Intent
import android.provider.ContactsContract
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ayaan.abhaya.navigation.NavDrawer
import com.ayaan.abhaya.navigation.TopBar
import kotlinx.coroutines.launch

@Composable
fun Friends(navController: NavController) {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                NavDrawer(navController)
            }
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column {
                TopBar(
                    navController = navController,
                    title = "Emergency Contacts",
                    onMenuClick = {
                        scope.launch {
                            drawerState.open()
                        }
                    }
                )
                // Your main content goes here
            }

            FloatingActionButton(
                onClick = {  openContactsPicker(context)},
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Contact")
            }
        }
    }
}
fun openContactsPicker(context: Context) {
    val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
    context.startActivity(intent)
}