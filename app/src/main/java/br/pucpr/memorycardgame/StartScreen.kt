package br.pucpr.memorycardgame

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.blur
import androidx.compose.ui.text.style.TextAlign

@Composable
fun StartScreen(onStartClick: () -> Unit) {
    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.background_home),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .blur(80.dp),
            contentScale = ContentScale.Crop
        )

        IconButton(
            onClick = { showDialog = true },
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Icon(imageVector = Icons.Filled.Info, contentDescription = "Info", tint = Color.Gray)
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            Text(
                text = "Memory Card Game",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.DarkGray
            )
            Text(
                text = "Are you ready to test your memory?",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = onStartClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text("Start", color = Color.Black)
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("HOW TO PLAY", textAlign = TextAlign.Center)
                }
            },
            text = {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("The game starts with all the cards facing down. On each turn, you flip two cards. If they are the same, you remove them and earn a point; if not, they are flipped back over. The game continues until all pairs are found.", textAlign = TextAlign.Center)
                }
            },
            confirmButton = {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Button(onClick = { showDialog = false }) {
                        Text("Back")
                    }
                }
            }
        )
    }
}