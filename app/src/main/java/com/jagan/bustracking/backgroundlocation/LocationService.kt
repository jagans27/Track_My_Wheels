package com.jagan.bustracking.backgroundlocation

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.IBinder
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC
import androidx.core.app.ServiceCompat
import com.google.android.gms.location.LocationServices
import com.jagan.bustracking.R
import com.jagan.bustracking.util.BusDetails
import com.jagan.bustracking.util.SharedViewModel
import com.jagan.bustracking.util.StoreData.Companion.dataStoreBusNumber
import com.jagan.bustracking.util.StoreData.Companion.dataStoreDriverName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.*

class LocationService : Service() {

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        var locationDetails = mutableStateOf("Start your sharing..")
        var stateBusDetails: BusDetails? = null
    }

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationClient: LocationClient

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        locationClient = DefaultLocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start() {
        val notification = NotificationCompat.Builder(this, "location")
            .setContentTitle("Tracking bus location.")
            .setContentText("Loading.....")
            .setSmallIcon(R.drawable.applogo)
            .setOngoing(true)
            .setAutoCancel(false)
            .setVisibility(VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setColor(121212)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        locationClient
            .getLocationUpdates(5)
            .catch { e -> e.printStackTrace() }
            .onEach { location ->
                val lat = location.latitude.toString()
                val long = location.longitude.toString()
                val gcd = Geocoder(this, Locale.getDefault())

                val addresses: List<Address> =
                    gcd.getFromLocation(lat.toDouble(), long.toDouble(), 1) as List<Address>
                locationDetails.value = if (addresses.isNotEmpty()) {
                    "Location: ($lat, $long) " + addresses[0].locality.toString()
                } else {
                    "Location: ($lat, $long)"
                }
                val updatedNotification = notification.setContentText(
                    locationDetails.value
                )

                val busDetails = BusDetails(
                    area = addresses[0].locality.toString(),
                    bus_no  = dataStoreBusNumber,
                    driver_name = dataStoreDriverName,
                    lati = lat,
                    long = long,
                    status = "active"
                )

                // store the data in database
                stateBusDetails = busDetails
                SharedViewModel().saveBusLocationDetails(busDetails,applicationContext)

                // notification notify
                notificationManager.notify(1, updatedNotification.build())
            }
            .launchIn(serviceScope)

        startForeground(1, notification.build())
    }

    private fun stop() {
        Log.d("DEBUG","Close")

        if(stateBusDetails!=null){
            stateBusDetails!!.status = "not active"
            SharedViewModel().saveBusLocationDetails(stateBusDetails!!,applicationContext)
        }

        locationDetails.value = "Start your sharing.."
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_DETACH)
        stopSelf()
    }

    override fun onDestroy() {

        if(stateBusDetails!=null){
            stateBusDetails!!.status = "not active"
            SharedViewModel().saveBusLocationDetails(stateBusDetails!!,applicationContext)
        }

        Log.d("DEBUG","Close")
        super.onDestroy()
        locationDetails.value = "Start your sharing.."
        serviceScope.cancel()
    }
}