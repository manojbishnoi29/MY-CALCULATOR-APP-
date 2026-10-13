package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CalculatorState
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentRose
import com.example.ui.theme.SecondaryText

@Composable
fun CalculatorDisplay(
    state: CalculatorState,
    onToggleScientific: () -> Unit,
    onToggleRadDeg: () -> Unit,
    onOpenHistory: () -> Unit,
    onMemoryClear: () -> Unit,
    onMemoryRecall: () -> Unit,
    onMemoryAdd: () -> Unit,
    onMemorySubtract: () -> Unit,
    modifier: Modifier = Modifier
) {
    val exprScrollState = rememberScrollState()

    // Auto-scroll expression to the end whenever it updates
    LaunchedEffect(state.expression) {
        exprScrollState.animateScrollTo(exprScrollState.maxValue)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.End
    ) {
        // Top Action Bar: Modes & Indicators
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left badges & toggles
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Rad / Deg toggle pill
                Surface(
                    onClick = onToggleRadDeg,
                    shape = RoundedCornerShape(12.dp),
                    color = if (state.isRadMode) AccentCyan.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.testTag("toggle_rad_deg")
                ) {
                    Text(
                        text = if (state.isRadMode) "RAD" else "DEG",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (state.isRadMode) AccentCyan else SecondaryText,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }

                // Memory indicator
                if (state.hasMemory) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = AccentAmber.copy(alpha = 0.2f),
                        modifier = Modifier.testTag("memory_badge")
                    ) {
                        Text(
                            text = "M",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = AccentAmber,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        )
                    }
                }

                // 2nd function indicator if active
                if (state.isInverseTrig) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.testTag("inverse_trig_badge")
                    ) {
                        Text(
                            text = "2nd",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Right icons: Scientific Toggle & History Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Scientific toggle button
                IconButton(
                    onClick = onToggleScientific,
                    modifier = Modifier.testTag("toggle_scientific_button")
                ) {
                    Icon(
                        imageVector = if (state.isScientificExpanded) Icons.Filled.Calculate else Icons.Filled.Science,
                        contentDescription = "Toggle Scientific Functions",
                        tint = if (state.isScientificExpanded) AccentCyan else SecondaryText
                    )
                }

                // History button with count badge
                BadgedBox(
                    badge = {
                        if (state.history.isNotEmpty()) {
                            Badge(
                                containerColor = AccentCyan,
                                contentColor = Color(0xFF0F172A)
                            ) {
                                Text(
                                    text = state.history.size.toString(),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                ) {
                    IconButton(
                        onClick = onOpenHistory,
                        modifier = Modifier.testTag("open_history_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.History,
                            contentDescription = "Calculation History",
                            tint = SecondaryText
                        )
                    }
                }
            }
        }

        // Memory quick-action chips bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            MemoryChip(label = "MC", enabled = state.hasMemory, testTag = "mem_mc", onClick = onMemoryClear)
            MemoryChip(label = "MR", enabled = state.hasMemory, testTag = "mem_mr", onClick = onMemoryRecall)
            MemoryChip(label = "M+", enabled = true, testTag = "mem_m_plus", onClick = onMemoryAdd)
            MemoryChip(label = "M-", enabled = true, testTag = "mem_m_minus", onClick = onMemorySubtract)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Main Expression Display Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(exprScrollState),
            contentAlignment = Alignment.CenterEnd
        ) {
            val displayText = if (state.expression.isEmpty()) "0" else state.expression
            Text(
                text = displayText,
                style = if (displayText.length > 12) MaterialTheme.typography.displayMedium else MaterialTheme.typography.displayLarge,
                color = if (state.expression.isEmpty()) SecondaryText.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.End,
                maxLines = 1,
                modifier = Modifier.testTag("calculator_expression_text")
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Live Preview Result or Error Message
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            if (state.errorMessage != null) {
                Text(
                    text = state.errorMessage,
                    style = MaterialTheme.typography.titleMedium,
                    color = AccentRose,
                    textAlign = TextAlign.End,
                    modifier = Modifier.testTag("calculator_error_text")
                )
            } else if (state.previewResult != null) {
                Text(
                    text = state.previewResult,
                    style = MaterialTheme.typography.titleLarge,
                    color = AccentCyan.copy(alpha = 0.9f),
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.End,
                    modifier = Modifier.testTag("calculator_preview_text")
                )
            }
        }
    }
}

@Composable
private fun MemoryChip(
    label: String,
    enabled: Boolean,
    testTag: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(8.dp),
        color = if (enabled) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        modifier = Modifier.testTag(testTag)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = if (enabled) SecondaryText else SecondaryText.copy(alpha = 0.4f),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
