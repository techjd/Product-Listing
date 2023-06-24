package com.techjd.productlisting.presentation.activities

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.appbar.MaterialToolbar
import com.techjd.productlisting.R
import com.techjd.productlisting.R.layout
import com.techjd.productlisting.databinding.ActivityMainBinding
import com.techjd.productlisting.utils.NetworkObserver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

  private lateinit var materialToolbar: MaterialToolbar
  private lateinit var binding: ActivityMainBinding

  private val networkRequest = NetworkRequest.Builder()
    .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
    .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
    .build()

  private val networkCallback = object : ConnectivityManager.NetworkCallback() {
    override fun onAvailable(network: Network) {
      super.onAvailable(network)
      Log.d("NETWORK", "onAvailable: AVAILABLE")
      lifecycleScope.launch {
        NetworkObserver.publish(true)
      }
    }

    override fun onLost(network: Network) {
      super.onLost(network)
      Log.d("NETWORK", "onAvailable: LOST")
      lifecycleScope.launch {
        NetworkObserver.publish(false)
      }
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val connectivityManager =
      getSystemService(ConnectivityManager::class.java) as ConnectivityManager
    connectivityManager.requestNetwork(networkRequest, networkCallback)

    binding = ActivityMainBinding.inflate(layoutInflater)
    val view = binding.root
    setContentView(view)

    val navHostFragment =
      supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
    val navController = navHostFragment.navController

    materialToolbar = binding.topAppBar.topAppBar

    navController.addOnDestinationChangedListener { _, destination, _ ->
      when(destination.id) {
        R.id.productsListFragment -> {
          materialToolbar.title = "\uD83D\uDECD️ Products"
        }
        R.id.addProductsFragment -> {
          materialToolbar.title = "➕ Add Product"
        }
      }
    }
  }
}