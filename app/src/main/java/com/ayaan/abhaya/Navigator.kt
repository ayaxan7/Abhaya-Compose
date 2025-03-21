package com.ayaan.abhaya

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ayaan.abhaya.screens.HomeScreen
import com.ayaan.abhaya.screens.SignInScreen
import com.ayaan.abhaya.screens.SignUpScreen

@Composable
fun Navigator(innerPadding: PaddingValues){
    val auth= FirebaseAuth.getInstance()
    val user=auth.currentUser
    val start = if (user != null) Destinations.Home.route else Destinations.Login.route
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    NavHost(
        navController = navController,
        startDestination = start,
        modifier = Modifier.padding(innerPadding)
    ){
        composable(route= Destinations.Home.route) {
            HomeScreen(navController)
        }
        composable(route= Destinations.Login.route) {
            SignInScreen(
                onSignInClick = {
                    navController.navigate(Destinations.Home.route) {
                        popUpTo(Destinations.Login.route) { inclusive = true }
                    }
                },
                    onSignUpClick = { navController.navigate(Destinations.SignUp.route) },
                onForgotPasswordClick = { navController.navigate(Destinations.ForgotPassword.route) }
            )
        }
        composable(route= Destinations.Profile.route) {

        }
        composable(route= Destinations.Friends.route) {

        }
        composable(route=Destinations.SignUp.route) {
            SignUpScreen(
                onSignUpClick = {
                    navController.navigate(Destinations.Home.route) {
                        popUpTo(Destinations.Login.route) { inclusive = true }
                    }
                },
                onSignInClick = { navController.navigate(Destinations.Login.route) }
            )
        }
    }
}
sealed class Destinations(val route: String){
    object Login: Destinations("login")
    object Home: Destinations("home")
    object Profile: Destinations("profile")
    object Friends: Destinations("friends")
    object About: Destinations("about")
    object SignUp: Destinations("signup")
    object ForgotPassword: Destinations("forgotpassword")
}