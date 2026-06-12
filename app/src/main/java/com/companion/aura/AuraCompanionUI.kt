package com.companion.aura

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AuraCompanionUI(
    state: AuraState,
    thoughtText: String,
    onTap: () -> Unit,
    onDrag: (Float, Float) -> Unit
) {
    var expanded by remember { mutableStateOf(true) }
    val transition = rememberInfiniteTransition(label = "aura-motion")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(animation = tween(2600), repeatMode = RepeatMode.Restart),
        label = "phase"
    )
    val glow by transition.animateFloat(
        initialValue = 0.58f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(animation = tween(1400), repeatMode = RepeatMode.Reverse),
        label = "glow"
    )

    val hover = sin(phase.toDouble()).toFloat() * 10f
    val wingFlutter = kotlin.math.abs(sin((phase * 2.8f).toDouble()).toFloat()) * 22f

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(8.dp)
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    expanded = !expanded
                    onTap()
                })
            }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    onDrag(dragAmount.x, dragAmount.y)
                }
            }
            .offset(y = hover.dp)
    ) {
        Box(
            modifier = Modifier
                .size(132.dp)
                .shadow(18.dp, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2f, size.height / 2f)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(state.coreColor.copy(alpha = 0.55f * glow), Color.Transparent),
                        center = center,
                        radius = size.width * 0.62f
                    ),
                    center = center,
                    radius = size.width * 0.62f
                )

                drawOval(
                    color = state.wingColor.copy(alpha = 0.78f),
                    topLeft = Offset(center.x - 88f - wingFlutter, center.y - 42f),
                    size = Size(82f + wingFlutter, 72f)
                )
                drawOval(
                    color = state.wingColor.copy(alpha = 0.78f),
                    topLeft = Offset(center.x + 6f, center.y - 42f),
                    size = Size(82f + wingFlutter, 72f)
                )

                for (i in 0 until 10) {
                    val angle = phase + i * ((2f * PI).toFloat() / 10f)
                    val radius = 54f + (i % 3) * 7f
                    drawCircle(
                        color = if (i % 2 == 0) state.coreColor else Color.White,
                        radius = 2.2f + (glow * 1.4f),
                        center = Offset(
                            center.x + cos(angle.toDouble()).toFloat() * radius,
                            center.y + sin(angle.toDouble()).toFloat() * radius
                        )
                    )
                }

                drawCircle(
                    color = Color.White.copy(alpha = 0.32f),
                    center = center,
                    radius = 42f,
                    style = Stroke(width = 2.5f)
                )
            }

            Image(
                painter = painterResource(id = state.imageRes),
                contentDescription = state.displayName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(78.dp)
                    .clip(CircleShape)
                    .border(1.dp, Color.White.copy(alpha = 0.55f), CircleShape)
            )
        }

        AnimatedVisibility(visible = expanded, enter = fadeIn(), exit = fadeOut()) {
            Box(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .background(
                        Brush.linearGradient(listOf(Color(0xEE08101E), Color(0xDD23103A))),
                        RoundedCornerShape(18.dp)
                    )
                    .border(1.dp, state.coreColor.copy(alpha = 0.55f), RoundedCornerShape(18.dp))
                    .padding(horizontal = 14.dp, vertical = 11.dp)
                    .widthIn(max = 230.dp)
            ) {
                Text(
                    text = thoughtText,
                    color = Color(0xFFF2F6FF),
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }
        }
    }
}
