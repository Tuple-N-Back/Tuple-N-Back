package org.squidfish.tuplenback

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import org.squidfish.tuplenback.ui.theme.TupleNbackTheme
import org.squidfish.tuplenback.views.TupleNBackApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TupleNbackTheme {
                TupleNBackApp()
            }
        }
    }
}
