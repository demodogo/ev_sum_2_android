package com.demodogo.ev_sum_2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.demodogo.ev_sum_2.ui.nav.AppNavGraph
import com.demodogo.ev_sum_2.ui.theme.Ev_sum_2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Ev_sum_2Theme {
                AppNavGraph()
            }
        }
    }
}
