package com.example.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BrutalistBorder
import com.example.ui.theme.BrutalistCyan
import com.example.ui.theme.BrutalistKeyNum
import com.example.ui.theme.BrutalistOrange
import com.example.ui.theme.BrutalistPink
import com.example.ui.theme.BrutalistPurple
import com.example.ui.theme.BrutalistYellow

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
    height: Dp = 64.dp,
    contentColor: Color? = null,
    backgroundColor: Color? = null
) {
    val view = LocalView.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Neo-Brutalist 3D mechanical press displacement
    val offsetX by animateDpAsState(
        targetValue = if (isPressed) 3.dp else 0.dp,
        animationSpec = tween(durationMillis = 60),
        label = "btn_offset_x"
    )
    val offsetY by animateDpAsState(
        targetValue = if (isPressed) 3.dp else 0.dp,
        animationSpec = tween(durationMillis = 60),
        label = "btn_offset_y"
    )

    val bgColor = backgroundColor ?: when (type) {
        KeypadButtonType.NUMBER -> BrutalistKeyNum
        KeypadButtonType.OPERATOR -> BrutalistYellow
        KeypadButtonType.ACTION -> BrutalistPink
        KeypadButtonType.EQUALS -> BrutalistOrange
        KeypadButtonType.SCIENTIFIC -> BrutalistPurple
    }

    val fgColor = contentColor ?: BrutalistBorder

    val cornerRadius = 14.dp
    val buttonShape = RoundedCornerShape(cornerRadius)

    Box(
        modifier = modifier
            .height(height)
            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
            .testTag(testTag)
    ) {
        // Hard Black Drop Shadow (Neo-Brutalist anchor)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(x = 3.dp, y = 3.dp)
                .background(BrutalistBorder, shape = buttonShape)
        )

        // Active Button Plate with heavy border
        Surface(
            onClick = {
                try {
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                } catch (_: Exception) {}
                onClick()
            },
            interactionSource = interactionSource,
            modifier = Modifier
                .fillMaxSize()
                .offset(x = offsetX, y = offsetY)
                .border(2.5.dp, BrutalistBorder, buttonShape),
            shape = buttonShape,
            color = bgColor,
            contentColor = fgColor
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = text,
                    color = fgColor,
                    fontSize = if (text.length > 3) 15.sp else if (text.length > 2) 18.sp else 22.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}
