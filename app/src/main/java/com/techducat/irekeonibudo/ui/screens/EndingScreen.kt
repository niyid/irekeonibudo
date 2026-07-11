package com.techducat.irekeonibudo.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.techducat.irekeonibudo.data.StoryNode
import com.techducat.irekeonibudo.ui.components.SceneCanvas

@Composable
fun EndingScreen(node: StoryNode, onPlayAgain: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SceneCanvas(scene = node.scene)
        Text(
            text = node.title,
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 20.dp, bottom = 12.dp)
        )
        Text(
            text = node.text,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        Button(onClick = onPlayAgain, modifier = Modifier.fillMaxWidth(0.8f)) {
            Text("Begin a New Journey")
        }
        Spacer(Modifier.height(24.dp))
    }
}
