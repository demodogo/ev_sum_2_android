package com.demodogo.ev_sum_2.ui.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.demodogo.ev_sum_2.data.UserStore
import com.demodogo.ev_sum_2.data.Validators
import com.demodogo.ev_sum_2.ui.theme.*

@Composable
fun RegisterScreen(
    onBackToLogin: () -> Unit
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var message by rememberSaveable { mutableStateOf<String?>(null) }
    var isError by rememberSaveable { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Background
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
                tint = Primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Crear cuenta",
                style = MaterialTheme.typography.headlineLarge,
                color = Primary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Regístrate para continuar",
                style = MaterialTheme.typography.bodyLarge,
                color = Primary
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
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = Primary.copy(alpha = 0.3f),
                    focusedLeadingIconColor = Primary,
                    unfocusedLeadingIconColor = Primary.copy(alpha = 0.3f),
                    focusedTextColor = Primary,
                    unfocusedTextColor = Primary,
                    cursorColor = Secondary
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
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = Primary.copy(alpha = 0.3f),
                    focusedLeadingIconColor = Primary,
                    unfocusedLeadingIconColor = Primary.copy(alpha = 0.3f),
                    focusedTrailingIconColor = Primary,
                    unfocusedTrailingIconColor = Primary.copy(alpha = 0.3f),
                    focusedTextColor = Primary,
                    unfocusedTextColor = Primary,
                    cursorColor = Secondary
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
                        !UserStore.canRegister(cleanEmail) ->
                            "Email ya registrado o límite de usuarios alcanzado"
                        else -> null
                    }

                    if (validationError != null) {
                        message = validationError
                        isError = true
                    } else {
                        if (UserStore.register(cleanEmail, password)) {
                            message = "¡Registro exitoso! Ya puedes iniciar sesión."
                            isError = false
                            email = ""
                            password = ""
                        } else {
                            message = "Ocurrió un error inesperado al registrar el usuario."
                            isError = true
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary,
                    contentColor = Surface
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
                        containerColor = if (isError) Error.copy(alpha = 0.1f) else Success.copy(alpha = 0.1f)
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
                            tint = if (isError) Error else Success
                        )
                        Text(
                            text = message!!,
                            modifier = Modifier.weight(1f),
                            color = Primary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        IconButton(onClick = { message = null }) {
                            Icon(Icons.Default.Close, "Cerrar", tint = Primary.copy(alpha = 0.6f))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Divider(color = Primary.copy(alpha = 0.3f), modifier = Modifier.weight(1f))
                Text(
                    text = "ACCESIBILIDAD",
                    color = Primary,
                    modifier = Modifier.padding(horizontal = 8.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
                Divider(color = Primary.copy(alpha = 0.3f), modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { /* mejora futura */ },
                    enabled = false,
                    modifier = Modifier
                        .weight(1f)
                        .height(90.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        disabledContainerColor = Primary.copy(alpha = 0.1f),
                        disabledContentColor = Primary.copy(alpha = 0.7f)
                    )
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.RecordVoiceOver, contentDescription = "Text to Speech")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("TTS", fontWeight = FontWeight.Bold)
                    }
                }

                Button(
                    onClick = { /* mejora futura */ },
                    enabled = false,
                    modifier = Modifier
                        .weight(1f)
                        .height(90.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        disabledContainerColor = Primary.copy(alpha = 0.1f),
                        disabledContentColor = Primary.copy(alpha = 0.7f)
                    )
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.Visibility, contentDescription = "High contrast")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Contraste", fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Estas opciones están deshabilitadas por el momento.",
                style = MaterialTheme.typography.bodySmall,
                color = Primary.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                Text(
                    text = "Ya tienes cuenta? ",
                    color = Primary,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Inicia Sesión",
                    color = Secondary,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.clickable { onBackToLogin() }
                )
            }
        }
    }
}
