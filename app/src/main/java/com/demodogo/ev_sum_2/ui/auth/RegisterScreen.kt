package com.demodogo.ev_sum_2.ui.auth

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicNone
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.demodogo.ev_sum_2.data.Validators
import com.demodogo.ev_sum_2.domain.models.DictationTarget
import com.demodogo.ev_sum_2.domain.validators.normalizeEmailFromSpeech
import com.demodogo.ev_sum_2.domain.validators.normalizePasswordFromSpeech
import com.demodogo.ev_sum_2.services.AuthService
import com.demodogo.ev_sum_2.services.SpeechController
import com.demodogo.ev_sum_2.ui.theme.Success
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    onBackToLogin: () -> Unit
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var message by rememberSaveable { mutableStateOf<String?>(null) }
    var isError by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val authService = remember { AuthService() }
    val context = LocalContext.current
    var dictationTarget by remember { mutableStateOf(DictationTarget.EMAIL)}
    var isListening by remember { mutableStateOf(false) }
    var speechError by remember { mutableStateOf<String?>(null) }
    var showDictationDialog by remember { mutableStateOf(false) }
    var shouldStartListening by remember { mutableStateOf(false) }
    val speechController = remember { SpeechController(context) }

    fun getFriendlyErrorMessage(code: Int): String {
        return when (code) {
            7 -> "No se pudo conectar al servicio de voz. Revisa tu internet."
            9 -> "Permiso de micrófono denegado."
            2 -> "Error de red. Intenta de nuevo."
            3 -> "No te escuchamos bien, intenta hablar más fuerte."
            5 -> "El micrófono está ocupado por otra app."
            else -> "Hubo un problema con el dictado. Intenta de nuevo."
        }
    }

    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) {
            speechError = "Permiso de micrófono requerido."
            shouldStartListening = false
        } else if (shouldStartListening) {
            speechController.start()
            shouldStartListening = false
        }
    }

    DisposableEffect(Unit) {
        onDispose { speechController.destroy() }
    }

    LaunchedEffect(Unit) {
        speechController.setListener(
            onReady = {
                isListening = true
                speechError = null
            },
            onPartial = { partial ->
                when (dictationTarget) {
                    DictationTarget.EMAIL -> email = normalizeEmailFromSpeech(partial)
                    DictationTarget.PASSWORD -> password = normalizePasswordFromSpeech(partial)
                }
            },
            onFinal = { final ->
                isListening = false
                when (dictationTarget) {
                    DictationTarget.EMAIL -> email = normalizeEmailFromSpeech(final)
                    DictationTarget.PASSWORD -> password = normalizePasswordFromSpeech(final)
                }
            },
            onError = { code ->
                isListening = false
                speechError = getFriendlyErrorMessage(code)
            },
            onEnd = { isListening = false }
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(0.3f))

            Icon(
                imageVector = Icons.Filled.PersonAdd,
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Crear cuenta",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Regístrate para continuar",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { if (!it.contains(' ')) email = it },
                label = { Text("Email") },
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                    focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
                    unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    cursorColor = MaterialTheme.colorScheme.secondary
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { if (!it.contains(' ')) password = it },
                label = { Text("Contraseña") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    val description = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = description)
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                    focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
                    unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                    focusedTrailingIconColor = MaterialTheme.colorScheme.primary,
                    unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    cursorColor = MaterialTheme.colorScheme.secondary
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val cleanEmail = email.trim()
                    val validationError = when {
                        cleanEmail.isBlank() || password.isBlank() ->
                            "Completa tu email y contraseña"
                        !Validators.isValidEmail(cleanEmail) ->
                            "Email inválido (ej: nombre@dominio.com)"
                        !Validators.isValidPassword(password) ->
                            "Contraseña inválida (mín. 6, 1 letra y 1 número)"
                        else -> null
                    }

                    if (validationError != null) {
                        message = validationError
                        isError = true
                    } else {
                        scope.launch {
                            try {
                                authService.register(cleanEmail, password);
                                message = "Cuenta creada exitosamente"
                                isError = false
                                email = ""
                                password = ""
                            } catch(e: Exception) {
                                message = e.message ?: "Error al crear cuenta"
                                isError = true
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = "Registrar",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            if (message != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isError) MaterialTheme.colorScheme.error.copy(alpha = 0.1f) else Success.copy(alpha = 0.1f)
                    )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = if (isError) Icons.Default.Info else Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = if (isError) MaterialTheme.colorScheme.error else Success
                        )
                        Text(
                            text = message!!,
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        IconButton(onClick = { message = null }) {
                            Icon(Icons.Default.Close, "Cerrar", tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        }
                    }
                }
            }

            if (speechError != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                    )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MicOff,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = speechError!!,
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        IconButton(onClick = { speechError = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Cerrar")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = {
                        if (isListening) {
                            speechController.stop()
                        } else {
                            showDictationDialog = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(90.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isListening)
                            MaterialTheme.colorScheme.errorContainer
                        else
                            MaterialTheme.colorScheme.primary,
                        contentColor = if (isListening)
                            MaterialTheme.colorScheme.onErrorContainer
                        else
                            MaterialTheme.colorScheme.background
                    )
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (isListening) Icons.Default.Mic else Icons.Default.MicNone,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isListening) "Escuchando..." else "Voz",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }


            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "¿Ya tienes cuenta? Inicia sesión",
                modifier = Modifier.clickable { onBackToLogin() },
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
    if (showDictationDialog) {
        AlertDialog(
            onDismissRequest = { showDictationDialog = false },
            title = { Text("Dictado por voz") },
            text = { Text("¿Qué campo deseas completar?") },
            confirmButton = {
                TextButton(onClick = {
                    dictationTarget = DictationTarget.EMAIL
                    showDictationDialog = false
                    shouldStartListening = true
                    micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                }) { Text("Email") }
            },
            dismissButton = {
                TextButton(onClick = {
                    dictationTarget = DictationTarget.PASSWORD
                    showDictationDialog = false
                    shouldStartListening = true
                    micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                }) { Text("Contraseña") }
            }
        )
    }
}
