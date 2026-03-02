package com.demodogo.ev_sum_2.data.repositories

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.tasks.await

class LocationRepository (private val context: Context) {
    private val fused by lazy { LocationServices.getFusedLocationProviderClient(context) }

    fun hasPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

        val coarse = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        return fine || coarse;
    }


    suspend fun getLatLng(): Pair<Double, Double> {
        try {
            val last = fused.lastLocation.await()
            if (last != null) return last.latitude to last.longitude

            val current = fused.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                null
            ).await()
            if (current != null) return current.latitude to current.longitude

            throw IllegalStateException("Ubicación no disponible (GPS apagado o sin señal).")
        } catch (se: SecurityException) {
            throw IllegalStateException("Permiso de ubicación no concedido.", se)
        } catch (e: Exception) {
            throw IllegalStateException("Error obteniendo ubicación: ${e.message}", e)
        }
    }
}