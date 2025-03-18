package com.fetchrewards.fetchrewardsapp.ui

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.provider.Settings
import android.view.ContextThemeWrapper
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.fetchrewards.fetchrewardsapp.R
import com.fetchrewards.fetchrewardsapp.adapters.ItemParentAdapter
import com.fetchrewards.fetchrewardsapp.api.ApiManager
import com.fetchrewards.fetchrewardsapp.databinding.ActivityMainBinding
import com.fetchrewards.fetchrewardsapp.managers.WifiStateCallback
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var recyclerView : RecyclerView
    private lateinit var progressLayout : LinearLayout
    private lateinit var swipeRefreshLayout : SwipeRefreshLayout
    private lateinit var connectivityManager : ConnectivityManager
    private lateinit var wifiCallback : WifiStateCallback
    private val apiManager = ApiManager()

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        recyclerView = binding.itemsRecyclerView
        progressLayout = binding.progressLayout
        swipeRefreshLayout = binding.swipeRefreshLayout

        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        fetchItems()
        swipeRefreshLayout.setOnRefreshListener {
            fetchItems()
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun promptEnableWifi() {
        AlertDialog.Builder(this)
            .setTitle("Wi-Fi Required")
            .setMessage("Wi-Fi is disabled. Please turn it on to continue.")
            .setPositiveButton("Enable Wi-Fi") { _, _ ->
                startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        wifiCallback = WifiStateCallback { isConnected ->
            if (!isConnected) {
                promptEnableWifi()
            }
        }
        val request = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()
        connectivityManager.registerNetworkCallback(request, wifiCallback)
    }

    override fun onStart() {
        super.onStart()
        if (!isWifiEnabled()) {
            promptEnableWifi()
        }
    }

    override fun onPause() {
        super.onPause()
        connectivityManager.unregisterNetworkCallback(wifiCallback)
    }

    private fun isWifiEnabled(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
    }

    private fun showSnackbar() {
        val ctw = ContextThemeWrapper(this, R.style.CustomSnackbarTheme)
        val snackbar = Snackbar.make(ctw, binding.root, "Failed To Fetch Items", Snackbar.LENGTH_SHORT).setAction("Retry") {
            fetchItems()
        }
        val snackbarView = snackbar.view
        val shapeModel = ShapeAppearanceModel().toBuilder()
            .setAllCorners(CornerFamily.ROUNDED, 100f)
            .build()
        val shapeDrawable = MaterialShapeDrawable(shapeModel)
        snackbarView.background = shapeDrawable
        snackbar.show()
    }

    private fun fetchItems() {
        progressLayout.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.Main).launch {
            val items = apiManager.fetchItems().values.toList()
            if(items.isNotEmpty())
                recyclerView.adapter = ItemParentAdapter(this@MainActivity, items)
            else showSnackbar()
            progressLayout.visibility = View.GONE
        }
    }

}