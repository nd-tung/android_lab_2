package com.example.networkinformation

import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.DhcpInfo
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.telephony.TelephonyManager
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.Manifest
import android.telephony.CellInfo
import android.telephony.gsm.GsmCellLocation
import com.example.networkinformation.httpRequest.CellLocationResponse
import com.example.networkinformation.httpRequest.RetrofitClient
import retrofit2.Call

import retrofit2.Callback
import retrofit2.Response


data class NetworkInfoState(
    val activeNetwork: String = "Don't have any active network",
    val allNetworks: List<String> = emptyList(),
    val allWifiNetworks: List<String> = emptyList(),
    val wifiInfo: WifiDetails? = null
)

data class WifiDetails(
    val ssid: String,
    val bssid: String,
    val ipAddress: String,
    val macAddress: String,
    val linkSpeed: Int,
    val rssi: Int,
    val dhcpInfo: DhcpInfo
)

data class CellularInfoState(
    val dataState: Int? = null,
    val phoneType: Int? = null,
    val networkType: Int? = null,
    val neighboringCellInfo: List<CellInfo>? = null,
    val cellId: Int? = null,
    val lac: Int? = null,
    val mcc: String? = null,
    val mnc: String? = null,
    val networkOperatorName: String? = null,
    val simOperatorName: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
)


class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val _networkInfoState = MutableStateFlow(NetworkInfoState())
    val networkInfoState: StateFlow<NetworkInfoState> = _networkInfoState

    private val _cellularInfoState = MutableStateFlow(CellularInfoState())
    val cellularInfoState: StateFlow<CellularInfoState> = _cellularInfoState

    fun fetchCellularNetworkInfo() {
        viewModelScope.launch {
            val context = getApplication<Application>().applicationContext
            val telephonyManager = context.getSystemService(TelephonyManager::class.java)

            val readPhoneStatePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
            val accessFineLocationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)

            if (readPhoneStatePermission == PackageManager.PERMISSION_GRANTED && accessFineLocationPermission == PackageManager.PERMISSION_GRANTED) {
                try {
                    val dataState = telephonyManager.dataState
                    val phoneType = telephonyManager.phoneType
                    val networkType = telephonyManager.networkType
                    val neighboringCellInfo = telephonyManager.allCellInfo
                    val cellLocation = telephonyManager.cellLocation as? GsmCellLocation

                    val cellId = cellLocation?.cid
                    val lac = cellLocation?.lac
                    val mcc = telephonyManager.networkOperator.take(3).toInt()
                    val mnc = telephonyManager.networkOperator.drop(3).toInt()
                    val networkOperatorName = telephonyManager.networkOperatorName
                    val simOperatorName = telephonyManager.simOperatorName

                    // Check if the cellId and lac are not null
                    if (cellId != null && lac != null) {
                        // Call OpenCellAPI to get the location of the cell tower
                        RetrofitClient.instance.getCellLocation(
                            apiKey = "pk.dc1bf5a7c36343f5305531642cb95466",
                            mcc = mcc,
                            mnc = mnc,
                            lac = lac,
                            cellId = cellId
                        ).enqueue(object : Callback<CellLocationResponse> {
                            override fun onResponse(call: Call<CellLocationResponse>, response: Response<CellLocationResponse>) {
                                if (response.isSuccessful) {
                                    val location = response.body()
                                    if (location != null) {
                                        _cellularInfoState.value = CellularInfoState(
                                            dataState = dataState,
                                            phoneType = phoneType,
                                            networkType = networkType,
                                            neighboringCellInfo = neighboringCellInfo,
                                            cellId = cellId,
                                            lac = lac,
                                            mcc = mcc.toString(),
                                            mnc = mnc.toString(),
                                            networkOperatorName = networkOperatorName,
                                            simOperatorName = simOperatorName,
                                            latitude = location.lat,  // Latitude from API response
                                            longitude = location.lon  // Longitude from API response
                                        )
                                    } else {
                                        // Handle case where location data is null
                                        _cellularInfoState.value = CellularInfoState(
                                            networkOperatorName = "Location not found"
                                        )
                                    }
                                } else {
                                    // Handle unsuccessful response
                                    _cellularInfoState.value = CellularInfoState(
                                        networkOperatorName = "Failed to fetch location"
                                    )
                                }
                            }

                            override fun onFailure(call: Call<CellLocationResponse>, t: Throwable) {
                                // Handle failure case
                                _cellularInfoState.value = CellularInfoState(
                                    networkOperatorName = "API call failed"
                                )
                            }
                        })
                    } else {
                        // If no valid cellId or lac, update state with error
                        _cellularInfoState.value = CellularInfoState(
                            networkOperatorName = "Invalid cell data"
                        )
                    }
                } catch (e: SecurityException) {
                    _cellularInfoState.value = CellularInfoState(
                        networkOperatorName = "Permission not granted"
                    )
                }
            } else {
                _cellularInfoState.value = CellularInfoState(
                    networkOperatorName = "Permissions not granted"
                )
            }
        }
    }



//    fun fetchCellularNetworkInfo() {
//        viewModelScope.launch {
//            val context = getApplication<Application>().applicationContext
//            val telephonyManager = context.getSystemService(TelephonyManager::class.java)
//
//            val readPhoneStatePermission =
//                ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
//            val accessFineLocationPermission =
//                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
//
//            if (readPhoneStatePermission == PackageManager.PERMISSION_GRANTED && accessFineLocationPermission == PackageManager.PERMISSION_GRANTED) {
//                try {
//                    val dataState = telephonyManager.dataState
//                    val phoneType = telephonyManager.phoneType
//                    val networkType = telephonyManager.networkType
//                    val neighboringCellInfo = telephonyManager.allCellInfo
//                    val cellLocation = telephonyManager.cellLocation as? GsmCellLocation
//
//                    val cellId = cellLocation?.cid
//                    val lac = cellLocation?.lac
//                    val mcc = telephonyManager.networkOperator.take(3).toInt()
//                    val mnc = telephonyManager.networkOperator.drop(3).toInt()
//                    val networkOperatorName = telephonyManager.networkOperatorName
//                    val simOperatorName = telephonyManager.simOperatorName
//
//
//                    // Update cellular info state
//                    _cellularInfoState.value = CellularInfoState(
//                        dataState = dataState,
//                        phoneType = phoneType,
//                        networkType = networkType,
//                        neighboringCellInfo = neighboringCellInfo,
//                        cellId = cellId,
//                        lac = lac,
//                        mcc = mcc.toString(),
//                        mnc = mnc.toString(),
//                        networkOperatorName = networkOperatorName,
//                        simOperatorName = simOperatorName
//                    )
//                } catch (e: SecurityException) {
//                    _cellularInfoState.value = CellularInfoState(
//                        networkOperatorName = "Permission not granted"
//                    )
//                }
//            } else {
//                _cellularInfoState.value = CellularInfoState(
//                    networkOperatorName = "Permission not granted"
//                )
//            }
//        }
//    }

    private val wifiManager: WifiManager =
        getApplication<Application>().applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    fun fetchWifiInfo() {
        viewModelScope.launch {
            // Check if Wi-Fi is on
            if (wifiManager.isWifiEnabled) {
                val context = getApplication<Application>().applicationContext
                val fineLocationPermission = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                val wifiStatePermission = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_WIFI_STATE
                )

                if (fineLocationPermission == PackageManager.PERMISSION_GRANTED && wifiStatePermission == PackageManager.PERMISSION_GRANTED) {
                    val wifiInfo = wifiManager.connectionInfo
                    val dhcpInfo = wifiManager.dhcpInfo

                    val wifiDetails = WifiDetails(
                        ssid = wifiInfo.ssid,
                        bssid = wifiInfo.bssid,
                        ipAddress = convertIntToIp(wifiInfo.ipAddress),
                        //Can not get mac address from API 28
                        macAddress = wifiInfo.macAddress,
                        linkSpeed = wifiInfo.linkSpeed,
                        rssi = wifiInfo.rssi,
                        dhcpInfo = dhcpInfo
                    )

                    val wifiScanResults = wifiManager.scanResults

                    // Create a list of SSIDs of available Wi-Fi networks
                    val availableNetworks = wifiScanResults.map { it.SSID }

                    // Update the information about available Wi-Fi networks
                    _networkInfoState.value = _networkInfoState.value.copy(
                        allWifiNetworks = availableNetworks
                    )

                    // Update the network information state
                    _networkInfoState.value = NetworkInfoState(
                        activeNetwork = wifiInfo.ssid,
                        wifiInfo = wifiDetails
                    )
                } else {
                    // Handle the case where permissions are not granted
                    _networkInfoState.value =
                        NetworkInfoState(activeNetwork = "Permissions not granted")
                }
            } else {
                _networkInfoState.value = NetworkInfoState(activeNetwork = "Wi-Fi is off")
            }
        }
    }

    private fun convertIntToIp(ip: Int): String {
        return String.format(
            "%d.%d.%d.%d",
            ip and 0xFF,
            ip shr 8 and 0xFF,
            ip shr 16 and 0xFF,
            ip shr 24 and 0xFF
        )
    }

    fun fetchNetworkInfo() {
        viewModelScope.launch {
            val connectivityManager =
                getApplication<Application>().getSystemService(ConnectivityManager::class.java)
            val telephonyManager =
                getApplication<Application>().getSystemService(TelephonyManager::class.java)
            val bluetoothManager =
                getApplication<Application>().getSystemService(BluetoothManager::class.java)
            val bluetoothAdapter = bluetoothManager.adapter

            val activeNetwork = connectivityManager.activeNetwork
            val activeNetworkCapabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork)

            val activeNetworkType = getNetworkType(activeNetworkCapabilities)

            val allNetworks = mutableListOf<String>()

            // Check for Wi-Fi
            if (activeNetworkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true) {
                allNetworks.add("Wi-Fi")
            }

            // Check for cellular network
            if (telephonyManager.simState == TelephonyManager.SIM_STATE_READY) {
                allNetworks.add("Cellular")
            }

            // Check for Bluetooth
            if (bluetoothAdapter != null && bluetoothAdapter.isEnabled) {
                allNetworks.add("Bluetooth")
            }

            _networkInfoState.value = NetworkInfoState(
                activeNetwork = activeNetworkType,
                allNetworks = allNetworks
            )
        }
    }
}


private fun getNetworkType(capabilities: NetworkCapabilities?): String {
    return when {
        capabilities == null -> "Not defined"
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "Wi-Fi"
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Cellular"
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "Ethernet"
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> "Bluetooth"
        else -> "Other"
    }
}
