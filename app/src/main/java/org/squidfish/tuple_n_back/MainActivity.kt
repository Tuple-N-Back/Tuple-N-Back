package org.squidfish.tuple_n_back

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.squidfish.tuple_n_back.models.GridViewModel
import org.squidfish.tuple_n_back.models.SoundViewModel
import org.squidfish.tuple_n_back.ui.theme.TupleNbackTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TupleNbackTheme {
                GameApp(GridViewModel(1), SoundViewModel(1, 9),1f)
            }
        }
    }
}