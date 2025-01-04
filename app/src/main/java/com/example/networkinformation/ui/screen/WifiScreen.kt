package com.example.networkinformation.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.networkinformation.MainActivity
import com.example.networkinformation.MainViewModel
import com.example.networkinformation.NetworkInfoState
import com.example.networkinformation.WifiDetails

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WifiScreen(navController: NavHostController, viewModel: MainViewModel) {
    val networkInfoState = viewModel.networkInfoState.collectAsState().value
    val activity = navController.context as MainActivity

    LaunchedEffect(Unit) {
        activity.requestPermissions()
        viewModel.fetchWifiInfo()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Network Information",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    )
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.fetchNetworkInfo()
                        viewModel.fetchWifiInfo()
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
                text = "Wi-Fi Connection Info",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Wifi is off
            if (networkInfoState.activeNetwork == "Wi-Fi is off") {
                Text(text = "Wi-Fi is off", style = MaterialTheme.typography.bodyMedium)
                return@Column
            }

            // Wifi is on
            networkInfoState.wifiInfo?.let { wifiInfo ->
                WifiInfoCard(wifiInfo = wifiInfo)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Show available networks
            if (networkInfoState.allWifiNetworks.isNotEmpty()) {
                Text(
                    text = "Available Networks",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )
                networkInfoState.allNetworks.forEach { network ->
                    Text(text = network, style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                Text(
                    text = "Available Networks",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Currently, there is no other available connected wifi network",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun WifiInfoCard(wifiInfo: WifiDetails) {
    Column(modifier = Modifier.fillMaxWidth()) {
        WifiInfoItem(title = "SSID", value = wifiInfo.ssid)
        WifiInfoItem(title = "BSSID", value = wifiInfo.bssid)
        WifiInfoItem(title = "IP Address", value = wifiInfo.ipAddress)
        WifiInfoItem(title = "MAC Address", value = wifiInfo.macAddress)
        WifiInfoItem(title = "Link Speed", value = "${wifiInfo.linkSpeed} Mbps")
        WifiInfoItem(title = "RSSI", value = "${wifiInfo.rssi} dBm")

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "DHCP Information",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
        )
        WifiInfoItem(title = "Gateway", value = convertIntToIp(wifiInfo.dhcpInfo.gateway))
        WifiInfoItem(title = "DNS1", value = convertIntToIp(wifiInfo.dhcpInfo.dns1))
        WifiInfoItem(title = "DNS2", value = convertIntToIp(wifiInfo.dhcpInfo.dns2))
        WifiInfoItem(title = "Lease Duration", value = "${wifiInfo.dhcpInfo.leaseDuration} seconds")
    }
}

@Composable
fun WifiInfoItem(title: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, style = MaterialTheme.typography.bodyMedium)
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
}

fun convertIntToIp(ip: Int): String {
    return String.format(
        "%d.%d.%d.%d",
        ip and 0xFF,
        ip shr 8 and 0xFF,
        ip shr 16 and 0xFF,
        ip shr 24 and 0xFF
    )
}
