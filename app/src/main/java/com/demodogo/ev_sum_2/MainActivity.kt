package com.demodogo.ev_sum_2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.demodogo.ev_sum_2.data.ThemeStore
import com.demodogo.ev_sum_2.ui.nav.AppNavGraph
import com.demodogo.ev_sum_2.ui.theme.Ev_sum_2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        ThemeStore.loadTheme(this)

        setContent {
            val isDarkTheme by ThemeStore.isDarkTheme.collectAsState()

            Ev_sum_2Theme(darkTheme = isDarkTheme) {
                Box(modifier = Modifier.fillMaxSize()) {
                    AppNavGraph()

                    IconButton(
                        onClick = { ThemeStore.setDarkTheme(this@MainActivity, !isDarkTheme) },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                            .statusBarsPadding()
                    ) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Theme",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}
