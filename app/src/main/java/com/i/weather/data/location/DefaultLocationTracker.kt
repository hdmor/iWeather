package com.i.weather.data.location

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.i.weather.domain.location.LocationTracker
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class DefaultLocationTracker @Inject constructor(
    private val locationClient: FusedLocationProviderClient,
    private val application: Application
) : LocationTracker {

    override suspend fun getCurrentLocation(): Location? {

        val hasAccessFineLocationPermission =
            ContextCompat.checkSelfPermission(application, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

        val hasAccessCoarseLocationPermission =
            ContextCompat.checkSelfPermission(application, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        val locationManager = application.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled =
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) || locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        return if (!hasAccessCoarseLocationPermission || !hasAccessFineLocationPermission || !isGpsEnabled) null
        else suspendCancellableCoroutine { continueWithCoroutine ->

            locationClient?.lastLocation?.let {
                locationClient.lastLocation.apply {

                    if (isComplete) {
                        continueWithCoroutine.resume(if (isSuccessful) result else null)
                        return@suspendCancellableCoroutine
                    }
                    addOnSuccessListener {
                        Log.i("iWeather", "onSuccess: latitude=${it.latitude}, longitude=${it.longitude}")
                        continueWithCoroutine.resume(it)
                    }
                    addOnFailureListener {
                        Log.i("iWeather", "onFailure: ${it.message}")
                        continueWithCoroutine.resume(null)
                    }
                    addOnCanceledListener {
                        Log.i("iWeather", "onCancel")
                        continueWithCoroutine.cancel()
                    }
                }
            }
        }

    }
}