package com.example.station

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.setContent
import com.example.station.ui.select.SelectStationsViewModel

class MainActivity : AppCompatActivity() {

    private val navigationViewModel: NavigationViewModel by viewModels()
    private val stationsViewModel: SelectStationsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StationApp(navigationViewModel, stationsViewModel)
        }
    }

    override fun onBackPressed() {
        if (!navigationViewModel.navigateBack())
            super.onBackPressed()
    }
}
