package com.jagan.bustracking.driver

import android.Manifest.permission.*
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jagan.bustracking.backgroundlocation.hasLocationPermission
import com.jagan.bustracking.MainActivity.Companion.startLocationService
import com.jagan.bustracking.MainActivity.Companion.stopLocationService
import com.jagan.bustracking.R
import com.jagan.bustracking.backgroundlocation.LocationService.Companion.locationDetails
import com.jagan.bustracking.ui.theme.Dark_Blue1
import com.jagan.bustracking.ui.theme.Yellow1
import com.jagan.bustracking.util.StoreData.Companion.dataStoreBusNumber
import com.jagan.bustracking.util.StoreData.Companion.dataStoreDriverName

@Composable
fun TrackScreen() {
    val locationDetailsUpdated = remember { locationDetails }
    val context = LocalContext.current


    // permission
    val multiplePermissionsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permission ->
        permission.entries.forEach { _ -> }
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        Image(
            painter = painterResource(id = R.drawable.applogonobackground),
            contentDescription = "app logo",
            Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Start Sharing Bus Route",
            fontSize = 20.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(30.dp))

        Card(
            modifier = Modifier
                .height(200.dp)
                .width(300.dp),
            shape = RoundedCornerShape(10.dp),
            backgroundColor = Dark_Blue1
        ) {

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Box(
                    modifier = Modifier
                        .height(60.dp)
                        .padding(start = 30.dp, end = 30.dp, top = 10.dp, bottom = 10.dp)
                        .clip(RoundedCornerShape(30.dp))
                        .fillMaxWidth()
                        .background(Yellow1)
                        .clickable {
                            // check the bus detail field are filled
                            if (dataStoreDriverName.isNotEmpty() && dataStoreBusNumber.isNotEmpty()) {

                                multiplePermissionsLauncher.launch(
                                    arrayOf(ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION)
                                )
                                if (context.hasLocationPermission()) {
                                    try {
                                        startLocationService(context)
                                        Toast
                                            .makeText(
                                                context,
                                                "sharing started",
                                                Toast.LENGTH_SHORT
                                            )
                                            .show()
                                    } catch (e: Exception) {
                                        Toast
                                            .makeText(
                                                context, "Some thing went wrong", Toast.LENGTH_SHORT
                                            )
                                            .show()
                                    }
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    "fill driver details to start",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }, contentAlignment = Alignment.Center
                ) {
                    Text(text = "Start Tracking", color = Color.White)
                }

                Spacer(modifier = Modifier.height(2.dp))

                Box(
                    modifier = Modifier
                        .height(60.dp)
                        .padding(start = 30.dp, end = 30.dp, top = 10.dp, bottom = 10.dp)
                        .clip(RoundedCornerShape(30.dp))
                        .fillMaxWidth()
                        .background(Yellow1)
                        .clickable {
                            try {
                                stopLocationService(context)
                                Toast
                                    .makeText(context, "sharing stopped", Toast.LENGTH_SHORT)
                                    .show()
                            } catch (e: Exception) {
                                Toast
                                    .makeText(context, "Some thing went wrong", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }, contentAlignment = Alignment.Center
                ) {
                    Text(text = "Stop Tracking", color = Color.White)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = locationDetailsUpdated.value,
                    fontSize = 12.sp,
                    color = Color.White,
                )
            }
        }
    }
}
