package com.demodogo.ev_sum_2.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.demodogo.ev_sum_2.domain.models.Phrase
import com.demodogo.ev_sum_2.services.AuthService
import com.demodogo.ev_sum_2.services.PhraseService
import com.demodogo.ev_sum_2.services.TextToSpeechController
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    onOpenLocation: () -> Unit
) {
    val authService = remember { AuthService() }
    val phraseService = remember { PhraseService() }
    val scope = rememberCoroutineScope()

    val email = authService.currentEmail() ?: "Usuario"

    var newPhrase by rememberSaveable { mutableStateOf("") }
    var search by rememberSaveable { mutableStateOf("") }

    var editingPhraseId by rememberSaveable { mutableStateOf<String?>(null) }
    var editingText by rememberSaveable { mutableStateOf("") }
    var showEditDialog by rememberSaveable { mutableStateOf(false) }

    var phrases by remember { mutableStateOf<List<Phrase>>(emptyList()) }
    var isLoading by rememberSaveable { mutableStateOf(false) }
    var error by rememberSaveable { mutableStateOf<String?>(null) }
    var info by rememberSaveable { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val tts = remember { TextToSpeechController(context) }

    DisposableEffect(Unit) {
        onDispose { tts.destroy() }
    }

    fun copyToClipboard(text: String) {
        clipboard.setText(AnnotatedString(text))
        info = "Copiado al portapapeles."
        error = null
    }

    fun loadPhrases() {
        scope.launch {
            isLoading = true
            error = null
            try {
                phrases = phraseService.get()
            } catch(e: Exception) {
                error = e.message ?: "Error al cargar las frases"
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) { loadPhrases() }

    val filtered = remember(phrases, search) {
        val q = search.trim()
        if (q.isBlank()) phrases
        else phrases.filter { it.text.contains(q, ignoreCase = true ) }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
                Text(
                    text = "Inicio",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Black
                )
            }

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Usuario conectado:", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text(email, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }

            OutlinedButton(
                onClick = onOpenLocation,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("📍 Ver mi ubicación", fontWeight = FontWeight.Black)
            }

            OutlinedTextField(
                value = newPhrase,
                onValueChange = { newPhrase = it },
                label = { Text("¿Qué quieres decir?", fontWeight = FontWeight.Bold) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                textStyle = MaterialTheme.typography.bodyLarge
            )

            Button(
                onClick = {
                    val text = newPhrase.trim()
                    if (text.isBlank()) {
                        error = "Escribe algo antes de guardar."
                        return@Button
                    }
                    scope.launch {
                        isLoading = true
                        try {
                            phraseService.add(text)
                            newPhrase = ""
                            info = "Guardado con éxito."
                            loadPhrases()
                        } catch (e: Exception) {
                            error = e.message
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("GUARDAR NUEVA FRASE", fontWeight = FontWeight.Black)
            }

            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                label = { Text("Buscar frase guardada") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            if (isLoading) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())

            LazyColumn(
                modifier = Modifier.heightIn(max = 400.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(filtered) { p ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = p.text,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Button(
                                onClick = { tts.speak(p.text) },
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.VolumeUp, null, modifier = Modifier.size(24.dp))
                                Spacer(Modifier.width(12.dp))
                                Text("REPRODUCIR VOZ", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Button(
                                    onClick = { copyToClipboard(p.text) },
                                    modifier = Modifier.weight(1f).height(48.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.secondary,
                                        contentColor = MaterialTheme.colorScheme.background
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.ContentCopy, null, modifier = Modifier.size(20.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text("Copiar", fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = {
                                        editingPhraseId = p.id
                                        editingText = p.text
                                        showEditDialog = true
                                    },
                                    modifier = Modifier.weight(1f).height(48.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceBright,
                                        contentColor = MaterialTheme.colorScheme.background
                                    ),
                                ) {
                                    Icon(Icons.Default.Edit, null, modifier = Modifier.size(20.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text("Editar", fontWeight = FontWeight.Bold)
                                }
                            }

                            Button(
                                onClick = {
                                    scope.launch {
                                        phraseService.delete(p.id)
                                        loadPhrases()
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer,
                                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Delete, null, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("BORRAR FRASE", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Button(
                onClick = {
                    authService.logout()
                    onLogout()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, null)
                Spacer(Modifier.width(12.dp))
                Text("CERRAR SESIÓN", fontWeight = FontWeight.Black)
            }
        }
    }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Editar frase") },
            text = {
                OutlinedTextField(
                    value = editingText,
                    onValueChange = { editingText = it },
                    label = { Text("Frase") },
                    singleLine = false,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val id = editingPhraseId
                        if (id == null) {
                            showEditDialog = false
                            return@TextButton
                        }

                        scope.launch {
                            isLoading = true
                            try {
                                phraseService.update(id, editingText)
                                info = "Frase actualizada."
                                error = null
                                showEditDialog = false
                                editingPhraseId = null
                                editingText = ""
                                loadPhrases()
                            } catch (e: Exception) {
                                error = e.message ?: "No se pudo actualizar."
                                info = null
                            } finally {
                                isLoading = false
                            }
                        }
                    }
                ) { Text("Guardar") }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) { Text("Cancelar") }
            }
        )
    }
}
