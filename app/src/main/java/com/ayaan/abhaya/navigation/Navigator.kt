package com.ayaan.abhaya.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ayaan.abhaya.screens.Friends
import com.ayaan.abhaya.screens.HomeScreen
import com.ayaan.abhaya.screens.SignInScreen
import com.ayaan.abhaya.screens.SignUpScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun Navigator(innerPadding: PaddingValues) {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val start = if (user != null) Destinations.Home.route else Destinations.Login.route
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    NavHost(
        navController = navController,
        startDestination = start,
        modifier = Modifier.padding(innerPadding)
    ) {
        composable(route = Destinations.Home.route) {
            HomeScreen(navController)
        }
        composable(route = Destinations.Login.route) {
            SignInScreen(
                onSignInClick = {
                navController.navigate(Destinations.Home.route) {
                    popUpTo(Destinations.Login.route) { inclusive = true }
                }
            },
                onSignUpClick = { navController.navigate(Destinations.SignUp.route) },
                onForgotPasswordClick = { navController.navigate(Destinations.ForgotPassword.route) })
        }
        composable(route = Destinations.Profile.route) {

        }
        composable(route = Destinations.Friends.route) {
            Friends(navController = navController )
        }
        composable(route = Destinations.SignUp.route) {
            SignUpScreen(onSignUpClick = {
                navController.navigate(Destinations.Home.route) {
                    popUpTo(Destinations.Login.route) { inclusive = true }
                }
            }, onSignInClick = { navController.navigate(Destinations.Login.route) })
        }
    }
}

sealed class Destinations(val route: String) {
    data object Login : Destinations("login")
    data object Home : Destinations("home")
    data object Profile : Destinations("profile")
    data object Friends : Destinations("friends")
    data object About : Destinations("about")
    data object SignUp : Destinations("signup")
    data object ForgotPassword : Destinations("forgotpassword")
}