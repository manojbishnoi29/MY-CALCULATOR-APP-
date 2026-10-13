package com.example.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class KeypadButtonType {
    NUMBER,
    OPERATOR,
    ACTION,
    EQUALS,
    SCIENTIFIC
}

@Composable
fun CalculatorButton(
    text: String,
    type: KeypadButtonType,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = "btn_$text",
    height: Dp = 68.dp,
    contentColor: Color? = null,
    backgroundColor: Color? = null
) {
    val view = LocalView.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.93f else 1.0f,
        animationSpec = tween(durationMillis = 100),
        label = "button_scale"
    )

    val bgColor = backgroundColor ?: when (type) {
        KeypadButtonType.NUMBER -> Color(0xFF1E222D)
        KeypadButtonType.OPERATOR -> Color(0xFF1A3344)
        KeypadButtonType.ACTION -> Color(0xFF282F3D)
        KeypadButtonType.EQUALS -> Color(0xFFF59E0B)
        KeypadButtonType.SCIENTIFIC -> Color(0xFF1E2330)
    }

    val fgColor = contentColor ?: when (type) {
        KeypadButtonType.NUMBER -> Color(0xFFF8FAFC)
        KeypadButtonType.OPERATOR -> Color(0xFF38BDF8)
        KeypadButtonType.ACTION -> Color(0xFF94A3B8)
        KeypadButtonType.EQUALS -> Color(0xFF0F172A)
        KeypadButtonType.SCIENTIFIC -> Color(0xFF818CF8)
    }

    Surface(
        onClick = {
            try {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            } catch (_: Exception) {}
            onClick()
        },
        interactionSource = interactionSource,
        modifier = modifier
            .scale(scale)
            .height(height)
            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
            .testTag(testTag),
        shape = RoundedCornerShape(22.dp),
        color = bgColor,
        contentColor = fgColor,
        shadowElevation = if (type == KeypadButtonType.EQUALS) 4.dp else 1.dp
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = fgColor,
                fontSize = if (text.length > 3) 16.sp else if (text.length > 2) 20.sp else 24.sp,
                fontWeight = if (type == KeypadButtonType.NUMBER || type == KeypadButtonType.EQUALS) FontWeight.Bold else FontWeight.SemiBold
            )
        }
    }
}
