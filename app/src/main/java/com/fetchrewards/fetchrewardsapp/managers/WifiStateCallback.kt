package com.fetchrewards.fetchrewardsapp.managers

import android.net.ConnectivityManager
import android.net.Network

class WifiStateCallback(private val onWifiChanged: (Boolean) -> Unit) : ConnectivityManager.NetworkCallback() {
    override fun onAvailable(network: Network) {
        onWifiChanged(true)
    }

    override fun onLost(network: Network) {
        onWifiChanged(false)
    }
}