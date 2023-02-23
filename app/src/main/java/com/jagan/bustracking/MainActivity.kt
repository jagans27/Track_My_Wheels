package com.jagan.bustracking

import android.content.Context
import android.content.ContextParams
import android.content.ContextWrapper
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.jagan.bustracking.backgroundlocation.LocationService
import com.jagan.bustracking.navigation.NavGraph
import com.jagan.bustracking.student.MapScreen
import com.jagan.bustracking.student.StudentDashboard
import com.jagan.bustracking.ui.theme.BusTrackingTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            BusTrackingTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    NavGraph(navController = rememberNavController())
                }
            }
        }


    }

    companion object {
        fun startLocationService(context: Context) {
            val intent = Intent(context, LocationService::class.java)
            intent.action = LocationService.ACTION_START
            context.startService(intent)
        }

        fun stopLocationService(context: Context) {
            val intent = Intent(context, LocationService::class.java)
            intent.action = LocationService.ACTION_STOP
            context.stopService(intent)
        }
    }

}
