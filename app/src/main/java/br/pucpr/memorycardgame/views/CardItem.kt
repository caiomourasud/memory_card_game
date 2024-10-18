package br.pucpr.memorycardgame.views

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import br.pucpr.memorycardgame.models.MemoryCard

@Composable
fun CardItem(card: MemoryCard, onClick: () -> Unit, modifier: Modifier = Modifier, showShadow: Boolean) {
    val rotation = remember { Animatable(0f) }
    val cardSize = 90.dp

    LaunchedEffect(card.isFlipped || card.isMatched) {
        rotation.animateTo(if (card.isFlipped || card.isMatched) 180f else 0f, animationSpec = tween(durationMillis = 300))
    }

    Box(
        modifier = modifier
            .size(cardSize)
            .padding(4.dp)
            .clip(RoundedCornerShape(16.dp))
            .then(if (showShadow) Modifier.shadow(4.dp, RoundedCornerShape(20.dp), true) else Modifier)
            .offset(x = 2.dp, y = (-2).dp)
            .clickable(
                onClick = { onClick() },
                indication = rememberRipple(color = Color.Gray, radius = cardSize),
                interactionSource = remember { MutableInteractionSource() }
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(rotationY = rotation.value)
                .background(Color.Transparent)
                .clip(RoundedCornerShape(16.dp))
        ) {
            val imageResId = if (rotation.value < 90f) card.backImageResId else card.imageResId
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}