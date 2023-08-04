package com.example.weatherapp

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.location.LocationRequest
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.weatherapp.utils.Constants
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority


class MainActivity : AppCompatActivity() {

    private val REQUEST_LOCATION_CODE = 1

    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)


        if (!isLocationEnabled()) {
            Toast.makeText(
                this@MainActivity,
                "The Location is not Enabled",
                Toast.LENGTH_LONG
            ).show()

            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        } else {
            requestPermission()
        }

    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }


    private fun requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            showDialogMessage()
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            requestPermission()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                REQUEST_LOCATION_CODE
            )
        }
    }

    private fun showDialogMessage() {
        AlertDialog.Builder(this@MainActivity)
            .setPositiveButton("Go To Settings") { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("CLOSE") { dialog, _ ->
                dialog.cancel()
            }
            .setTitle("Location permission needed")
            .setMessage("The permission is needed for accessing the location.It can enabled under the Application Settings")
            .show()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_CODE && grantResults.isNotEmpty()) {
            Toast.makeText(
                this,
                "Permission granted",
                Toast.LENGTH_SHORT
            )
                .show()

            requestLocationData()
        } else {
            Toast.makeText(
                this,
                "The permission was not granted",
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    private fun requestLocationData() {
        val locationRequest = com.google.android.gms.location.LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            1000
        )
            .build()

        /*remember this code is auto generate to grant the permission for requestLocationUpdates*/
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        /* until here */
        mFusedLocationProviderClient.requestLocationUpdates(
            locationRequest, object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    Toast.makeText(
                        this@MainActivity,
                        "Latitude: ${locationResult.lastLocation?.latitude} \n Longitude: ${locationResult.lastLocation?.longitude}",
                        Toast.LENGTH_SHORT
                    ).show()

                    getNetworkWeatherDetails()
                }
            },
            Looper.myLooper()
        )
    }


    private fun getNetworkWeatherDetails() {
        if (Constants.isNetworkAvailable(this)) {
            Toast.makeText(
                this,
                "There is internet connection",
                Toast.LENGTH_LONG
            ).show()
        } else {
            Toast.makeText(
                this,
                "There's is no internet connection",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}