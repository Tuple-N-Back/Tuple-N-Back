package org.squidfish.tuple_n_back

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import org.squidfish.tuple_n_back.ui.theme.TupleNbackTheme

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