package com.demodogo.ev_sum_2.services

import android.Manifest
import android.content.Context
import android.location.Geocoder
import android.os.Build
import androidx.annotation.RequiresPermission
import com.demodogo.ev_sum_2.data.repositories.LocationRepository
import com.demodogo.ev_sum_2.domain.models.DeviceLocation
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import kotlin.coroutines.resume

data class LocationResult(
    val coords: DeviceLocation,
    val address: String?
)

class LocationService (private val context: Context) {
    private val repo = LocationRepository(context);

    fun hasPermission(): Boolean = repo.hasPermission();

    @RequiresPermission(anyOf = [
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ])
    suspend fun getLocationWithAddress(): LocationResult {
        return try {
            val (lat, lng) = repo.getLatLng()
            val address = reverseGeocodeSafe(lat, lng)
            LocationResult(DeviceLocation(lat, lng), address)
        } catch (e: SecurityException) {
            throw IllegalStateException("Permiso de ubicación no concedido.", e)
        }
    }


    private suspend fun reverseGeocodeSafe(lat: Double, lon: Double): String? {
        return try {
            val geocoder = Geocoder(context, Locale.forLanguageTag("es-CL"))

            if (Build.VERSION.SDK_INT >= 33) {
                suspendCancellableCoroutine { cont ->
                    geocoder.getFromLocation(lat, lon, 1) { results ->
                        if (!cont.isActive) return@getFromLocation
                        val line = results.firstOrNull()?.getAddressLine(0)
                        cont.resume(line)
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                geocoder.getFromLocation(lat, lon, 1)
                    ?.firstOrNull()
                    ?.getAddressLine(0)
            }
        } catch (_: Exception) {
            null
        }
    }


}