package com.example.networkinformation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.networkinformation.MainViewModel
import com.example.networkinformation.ui.screen.MobileScreen
import com.example.networkinformation.ui.screen.OverallScreen
import com.example.networkinformation.ui.screen.WifiScreen

@Composable
fun BottomNavGraph(navController: NavHostController, viewModel : MainViewModel) {
    NavHost(
        navController = navController,
        startDestination = BottomBarScreen.Overall.route
    ){
        composable(route = BottomBarScreen.Overall.route){
            OverallScreen(navController = navController, viewModel = viewModel)
        }
        composable(route = BottomBarScreen.Wifi.route){
            WifiScreen(navController = navController, viewModel = viewModel)
        }
        composable(route = BottomBarScreen.Mobile.route){
            MobileScreen(navController = navController, viewModel = viewModel)
        }
    }
}