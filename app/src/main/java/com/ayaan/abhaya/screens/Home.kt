package com.ayaan.abhaya.screens

import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ayaan.abhaya.navigation.NavDrawer
import com.ayaan.abhaya.navigation.SosBottomBar
import com.ayaan.abhaya.navigation.TopBar
import com.ayaan.abhaya.utils.LocationHelper
import com.ayaan.abhaya.viewmodels.HomeViewModel
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    navController: NavController,
    vm: HomeViewModel = viewModel(),
) {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val locationHelper = remember { LocationHelper(context) }

    LaunchedEffect(Unit) {
        vm.fetchUserData()
    }

    // Collect state flows from the ViewModel
    val userData by vm.userData.collectAsState()
    val userDataLoadingState by vm.userDataLoadingState.collectAsState()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                NavDrawer(navController)
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopBar(
                    navController = navController,
                    title = "Home",
                    onMenuClick = {
                        scope.launch {
                            drawerState.open()
                        }
                    }
                )
            },
            bottomBar = {
                SosBottomBar(
                    onSosClick = {
                        userData?.let { user ->
                            val sharedPrefs = context.getSharedPreferences(
                                "user_data",
                                android.content.Context.MODE_PRIVATE
                            )
                            sharedPrefs.edit().apply {
                                putString("name", user.name)
                                putString("number", user.phoneNo)
                                apply()
                            }
                            vm.viewModelScope.launch {
                                val location = locationHelper.getCurrentLocation()
                                if (location != null) {
                                    vm.sendSos(
                                        latitude = location.latitude,
                                        longitude = location.longitude,
                                        name = user.name,
                                        phoneNo = user.phoneNo
                                    )
                                }
                            }
                        }
                    },
                    onAnonymousSosClick = {
                        vm.viewModelScope.launch {
                            val location = locationHelper.getCurrentLocation()
                            if (location != null) {
                                vm.sendSos(
                                    latitude = location.latitude,
                                    longitude = location.longitude,
                                    name = "Anonymous",
                                    phoneNo = "XXXXXXXXX"
                                )
                            }
                        }
                    },
                    isEnabled = userData != null && userDataLoadingState is HomeViewModel.UserDataLoadingState.Success
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // WebView implementation
                AndroidView(
                    factory = { context ->
                        WebView(context).apply {
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            webViewClient = WebViewClient()
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            loadUrl("https://abhaya-map.vercel.app/")
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )

                // Show loading indicator or error message
                when (userDataLoadingState) {
                    is HomeViewModel.UserDataLoadingState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    is HomeViewModel.UserDataLoadingState.Error -> {
                        val error = userDataLoadingState as HomeViewModel.UserDataLoadingState.Error
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
                        }
                    }
                    else -> { /* Success or Idle - WebView is visible */ }
                }
            }
        }
    }
}