package com.example.networkinformation.ui.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.example.networkinformation.MainViewModel
import com.example.networkinformation.ui.navigation.BottomBarScreen
import com.example.networkinformation.ui.navigation.BottomNavGraph


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val viewModel: MainViewModel = viewModel()

    Scaffold(
        modifier = Modifier.padding(0.dp, 20.dp, 0.dp, 0.dp),
        bottomBar = { BottomBar(navController = navController, viewModel = viewModel) }
    ) {
        BottomNavGraph(navController = navController, viewModel = viewModel)
    }
}

@Composable
fun BottomBar(navController: NavHostController, viewModel: MainViewModel) {
    val screens = listOf(
        BottomBarScreen.Overall,
        BottomBarScreen.Wifi,
        BottomBarScreen.Mobile
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    BottomNavigation {
        screens.forEach { screen ->
            AddItems(screen = screen, currentDestination = currentDestination, navController = navController, viewModel = viewModel)
        }
    }
}

@Composable
fun RowScope.AddItems(
    screen: BottomBarScreen,
    currentDestination: NavDestination?,
    navController: NavController,
    viewModel: MainViewModel
){

    BottomNavigationItem(
        label = {
            Text(
                text = screen.title
            )
        },
        icon = {
            Icon(
                imageVector = screen.icon,
                contentDescription = "Navigation Icon"
            )
        },
        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
        onClick = {
            navController.navigate(screen.route)
        },
    )
}