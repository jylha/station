package com.example.station.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.setContent
import com.example.station.ui.common.LocationPermission
import com.example.station.ui.common.LocationPermissionProvider
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val locationPermission = LocationPermission(this)
    private val navigationViewModel: NavigationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LocationPermissionProvider(locationPermission) {
                StationApp(navigationViewModel)
            }
        }
    }

    override fun onBackPressed() {
        if (!navigationViewModel.navigateBack())
            super.onBackPressed()
    }
}
