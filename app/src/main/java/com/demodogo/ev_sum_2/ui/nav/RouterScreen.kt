package com.demodogo.ev_sum_2.ui.nav

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun RouterScreen(
    isLoggedIn: Boolean,
    onGoHome: () -> Unit,
    onGoLogin: () -> Unit
) {
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) onGoHome()
        else onGoLogin()
    }

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}
