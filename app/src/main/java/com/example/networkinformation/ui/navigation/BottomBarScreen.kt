package com.example.networkinformation.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomBarScreen (
    val route: String,
    val title: String,
    val icon: ImageVector
){
    object Overall : BottomBarScreen("overall", "Overall", Icons.Default.Info)
    object Wifi : BottomBarScreen("wifi", "Wifi", Icons.Default.Build)
    object Mobile : BottomBarScreen("mobile", "Mobile", Icons.Default.Call)
}