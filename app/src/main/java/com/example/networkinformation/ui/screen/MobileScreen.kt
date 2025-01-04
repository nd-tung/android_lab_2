package com.example.networkinformation.ui.screen

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.example.networkinformation.MainActivity
import com.example.networkinformation.MainViewModel
import com.example.networkinformation.CellularInfoState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MobileScreen(navController: NavHostController, viewModel: MainViewModel) {
    val cellularInfoState = viewModel.cellularInfoState.collectAsState().value
    val context = LocalContext.current
    val activity = navController.context as MainActivity

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions()
        } else {
            viewModel.fetchCellularNetworkInfo()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Cellular Network Information",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    )
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.fetchCellularNetworkInfo()
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text(
                text = "Cellular Connection Info",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            cellularInfoState.let { info ->
                CellularInfoItem(title = "Network Operator Name", value = info.networkOperatorName ?: "N/A")
                CellularInfoItem(title = "SIM Operator Name", value = info.simOperatorName ?: "N/A")
                CellularInfoItem(title = "Data State", value = info.dataState.toString())
                CellularInfoItem(title = "Phone Type", value = info.phoneType.toString())
                CellularInfoItem(title = "Network Type", value = info.networkType.toString())
                CellularInfoItem(title = "Cell ID", value = info.cellId.toString())
                CellularInfoItem(title = "LAC", value = info.lac.toString())
                CellularInfoItem(title = "MCC", value = info.mcc ?: "N/A")
                CellularInfoItem(title = "MNC", value = info.mnc ?: "N/A")
            }

            Text(
                text = "Cellular Station Location",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Latitude", style = MaterialTheme.typography.bodyMedium)
                Text(text = cellularInfoState.latitude.toString(), style = MaterialTheme.typography.bodyMedium)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Longitude", style = MaterialTheme.typography.bodyMedium)
                Text(text = cellularInfoState.longitude.toString(), style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun CellularInfoItem(title: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, style = MaterialTheme.typography.bodyMedium)
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
}