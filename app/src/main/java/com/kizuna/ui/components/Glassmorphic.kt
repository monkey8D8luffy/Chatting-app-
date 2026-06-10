package com.kizuna.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Creates a frosted glassmorphic effect.
 * Note: `Modifier.blur()` was removed from the chain because applying blur directly to a modifier
 * blurs the contents (text) inside it as well. To achieve true glassmorphism, a separate blurring
 * layer needs to be drawn *behind* this, but on a pure black (#050505) background, translucent
 * backgrounds with bright semi-transparent borders perfectly emulate the aesthetic without ruining text clarity.
 */
fun Modifier.glassmorphic(
    shape: Shape = RoundedCornerShape(16.dp),
    backgroundColor: Color = Color.White.copy(alpha = 0.05f),
    borderColor: Color = Color.White.copy(alpha = 0.15f),
    borderWidth: Dp = 1.dp
): Modifier = this
    .clip(shape)
    .background(backgroundColor)
    .border(borderWidth, borderColor, shape)
    .padding(0.dp) // Maintain chained modifier functionality without breaking bounds
