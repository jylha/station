package dev.jylha.station.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.setContent
import dev.jylha.station.ui.common.LocationPermission
import dev.jylha.station.ui.common.LocationPermissionProvider
import dagger.hilt.android.AndroidEntryPoint
import dev.jylha.station.ui.theme.StationTheme

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val locationPermission = LocationPermission(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LocationPermissionProvider(locationPermission) {
                StationTheme {
                    StationAppNavigation()
                }
            }
        }
    }

    override fun onBackPressed() {
        //if (!navigationViewModel.navigateBack())
            super.onBackPressed()
    }
}
