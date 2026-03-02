package com.demodogo.ev_sum_2.ui.location

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.demodogo.ev_sum_2.services.LocationService
import com.demodogo.ev_sum_2.services.TextToSpeechController
import kotlinx.coroutines.launch

@Composable
fun DeviceLocationScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val service = remember { LocationService(context) }
    val clipboard = LocalClipboardManager.current
    val tts = remember { TextToSpeechController(context) }

    DisposableEffect(Unit) { onDispose { tts.destroy() } }

    var isLoading by rememberSaveable { mutableStateOf(false) }
    var error by rememberSaveable { mutableStateOf<String?>(null) }
    var info by rememberSaveable { mutableStateOf<String?>(null) }

    var address by rememberSaveable { mutableStateOf<String?>(null) }
    var lat by rememberSaveable { mutableStateOf<Double?>(null) }
    var lon by rememberSaveable { mutableStateOf<Double?>(null) }

    fun copy(text: String) {
        clipboard.setText(AnnotatedString(text))
        info = "Copiado al portapapeles."
        error = null
    }

    fun spokenText(): String {
        val a = address?.takeIf { it.isNotBlank() }
        return if (a != null) "Tu ubicación actual es: $a"
        else if (lat != null && lon != null) "Tu ubicación actual es latitud ${"%.5f".format(lat)} y longitud ${"%.5f".format(lon)}"
        else "Aún no tengo tu ubicación."
    }

    fun copyText(): String {
        val a = address?.takeIf { it.isNotBlank() }
        return if (a != null && lat != null && lon != null)
            "Ubicación: $a (lat=${"%.6f".format(lat)}, lon=${"%.6f".format(lon)})"
        else if (lat != null && lon != null)
            "Ubicación: lat=${"%.6f".format(lat)}, lon=${"%.6f".format(lon)}"
        else "Ubicación no disponible."
    }

    fun fetchLocation() {
        scope.launch {
            isLoading = true
            error = null
            info = null
            try {
                val result = service.getLocationWithAddress()
                lat = result.coords.latitude
                lon = result.coords.longitude
                address = result.address
                info = "Ubicación actualizada."
            } catch (e: Exception) {
                error = e.message ?: "Error obteniendo ubicación."
            } finally {
                isLoading = false
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        val ok = (perms[Manifest.permission.ACCESS_FINE_LOCATION] == true) ||
                (perms[Manifest.permission.ACCESS_COARSE_LOCATION] == true)

        if (!ok) {
            error = "Permiso de ubicación denegado."
            info = null
        } else {
            fetchLocation()
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Buscar dispositivo", style = MaterialTheme.typography.headlineMedium)

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.06f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Dirección:", style = MaterialTheme.typography.titleMedium)
                    Text(address ?: "—", style = MaterialTheme.typography.bodyLarge)

                    Text("Coordenadas:", style = MaterialTheme.typography.titleMedium)
                    Text(
                        if (lat != null && lon != null) "lat=${"%.6f".format(lat)}, lon=${"%.6f".format(lon)}" else "—",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            if (isLoading) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())

            if (error != null) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.10f))
                ) { Text(error!!, modifier = Modifier.padding(14.dp)) }
            }

            if (info != null) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f))
                ) { Text(info!!, modifier = Modifier.padding(14.dp)) }
            }

            Button(
                onClick = {
                    if (service.hasPermission()) fetchLocation()
                    else permissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.MyLocation, contentDescription = null)
                Spacer(Modifier.width(10.dp))
                Text("Obtener ubicación")
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = { tts.speak(spokenText()) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.VolumeUp, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Hablar")
                }

                OutlinedButton(
                    onClick = { copy(copyText()) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Copiar")
                }
            }

            Spacer(Modifier.weight(1f))

            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Volver")
            }
        }
    }
}