package org.squidfish.tuplenback

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import org.squidfish.tuplenback.ui.theme.TupleNBackTheme
import org.squidfish.tuplenback.views.TupleNBackApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TupleNBackTheme {
                TupleNBackApp()
            }
        }
    }
}
