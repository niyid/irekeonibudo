package com.techducat.irekeonibudo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.techducat.irekeonibudo.data.GameRepository
import com.techducat.irekeonibudo.navigation.IrekeOnibudoApp
import com.techducat.irekeonibudo.ui.theme.IrekeOnibudoTheme
import com.techducat.irekeonibudo.viewmodel.GameViewModel
import com.techducat.irekeonibudo.viewmodel.GameViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val repository = remember { GameRepository(applicationContext) }
            val viewModel: GameViewModel = viewModel(factory = GameViewModelFactory(repository))

            IrekeOnibudoTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    IrekeOnibudoApp(viewModel = viewModel)
                }
            }
        }
    }
}
