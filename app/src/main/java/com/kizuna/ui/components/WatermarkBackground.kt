package com.kizuna.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import android.content.Context
import androidx.compose.ui.platform.LocalContext

@Composable
fun WatermarkBackground(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val mascotResId = context.resources.getIdentifier("mascot", "drawable", context.packageName)

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (mascotResId != 0) {
            Image(
                painter = painterResource(id = mascotResId),
                contentDescription = null,
                modifier = Modifier.alpha(0.1f),
                contentScale = ContentScale.Fit
            )
        }
    }
}
