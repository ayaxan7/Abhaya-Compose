package com.ayaan.abhaya.screens
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.provider.ContactsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ayaan.abhaya.navigation.NavDrawer
import com.ayaan.abhaya.navigation.TopBar
import com.ayaan.abhaya.viewmodels.FriendsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("Range")
@Composable
fun Friends(navController: NavController, viewModel: FriendsViewModel = viewModel()) {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val friends by viewModel.friends.collectAsState()

    val contactPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickContact()
    ) { uri ->
        uri?.let {
            val contentResolver = context.contentResolver
            val cursor = contentResolver.query(uri, null, null, null, null)
            if (cursor != null && cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                val idIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID)
                val contactId = cursor.getString(idIndex)
                val name = cursor.getString(nameIndex)

                val hasPhoneNumber = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0
                if (hasPhoneNumber) {
                    val phoneCursor = contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
                        arrayOf(contactId),
                        null
                    )
                    phoneCursor?.use {
                        if (it.moveToFirst()) {
                            val phoneNumberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                            val phoneNumber = it.getString(phoneNumberIndex)
                            viewModel.saveContactUnderUser(context, name, phoneNumber)
                            viewModel.fetchFriends() // Refresh list
                        }
                    }
                }
                cursor.close()
            }
        }
    }
    LaunchedEffect(Unit) {
        while (true) {
            viewModel.fetchFriends()
            delay(5000) // Adjust the delay as needed (e.g., 5000ms = 5 seconds)
        }
    }
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = { ModalDrawerSheet { NavDrawer(navController) } }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                TopBar(
                    navController = navController,
                    title = "Emergency Contacts",
                    onMenuClick = { scope.launch { drawerState.open() } }
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (friends.isEmpty()) {
                    Text(
                        text = "No friends added yet.",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        friends.forEach { friend ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Name: ${friend.name}", style = MaterialTheme.typography.titleMedium)
                                    Text("Phone: ${friend.phone}", style = MaterialTheme.typography.bodyMedium)
                                }
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.End
                                ) {
                                    IconButton(
                                        onClick = {
                                            viewModel.deleteFriend(context, friend.phone)
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete Contact",
                                            tint = Color.Black
                                        )
                                    }
                                }

                            }
                        }

                    }
                }
            }

            FloatingActionButton(
                onClick = { contactPickerLauncher.launch(null) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Contact")
            }
        }
    }
}