package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CalculatorState
import com.example.ui.theme.BrutalistBorder
import com.example.ui.theme.BrutalistCyan
import com.example.ui.theme.BrutalistDisplayCard
import com.example.ui.theme.BrutalistGreen
import com.example.ui.theme.BrutalistPink
import com.example.ui.theme.BrutalistPurple
import com.example.ui.theme.BrutalistYellow
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryText

@Composable
fun CalculatorDisplay(
    state: CalculatorState,
    onToggleScientific: () -> Unit,
    onToggleRadDeg: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenMediaSettings: () -> Unit = {},
    onMemoryClear: () -> Unit,
    onMemoryRecall: () -> Unit,
    onMemoryAdd: () -> Unit,
    onMemorySubtract: () -> Unit,
    modifier: Modifier = Modifier
) {
    val exprScrollState = rememberScrollState()

    LaunchedEffect(state.expression) {
        exprScrollState.animateScrollTo(exprScrollState.maxValue)
    }

    val cardShape = RoundedCornerShape(18.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        // Hard Black Drop Shadow
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = 4.dp, y = 4.dp)
                .background(BrutalistBorder, shape = cardShape)
        )

        // Main Display Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.5.dp, BrutalistBorder, shape = cardShape),
            shape = cardShape,
            color = BrutalistDisplayCard
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
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
                            shape = RoundedCornerShape(10.dp),
                            color = if (state.isRadMode) BrutalistCyan else Color(0xFFF1EFE6),
                            border = androidx.compose.foundation.BorderStroke(1.8.dp, BrutalistBorder),
                            modifier = Modifier.testTag("toggle_rad_deg")
                        ) {
                            Text(
                                text = if (state.isRadMode) "RAD" else "DEG",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Black,
                                color = BrutalistBorder,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        // Memory indicator
                        if (state.hasMemory) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = BrutalistYellow,
                                border = androidx.compose.foundation.BorderStroke(1.8.dp, BrutalistBorder),
                                modifier = Modifier.testTag("memory_badge")
                            ) {
                                Text(
                                    text = "MEM",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Black,
                                    color = BrutalistBorder,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        // 2nd function indicator if active
                        if (state.isInverseTrig) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = BrutalistPurple,
                                border = androidx.compose.foundation.BorderStroke(1.8.dp, BrutalistBorder),
                                modifier = Modifier.testTag("inverse_trig_badge")
                            ) {
                                Text(
                                    text = "2nd",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Black,
                                    color = BrutalistBorder,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    // Right icons: Media, Scientific & History
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Media Settings Button
                        IconButton(
                            onClick = onOpenMediaSettings,
                            modifier = Modifier.testTag("open_media_settings_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.PlayArrow,
                                contentDescription = "Media Player Settings",
                                tint = BrutalistBorder
                            )
                        }

                        // Scientific toggle button
                        IconButton(
                            onClick = onToggleScientific,
                            modifier = Modifier.testTag("toggle_scientific_button")
                        ) {
                            Icon(
                                imageVector = if (state.isScientificExpanded) Icons.Filled.Calculate else Icons.Filled.Science,
                                contentDescription = "Toggle Scientific Functions",
                                tint = if (state.isScientificExpanded) BrutalistPink else BrutalistBorder
                            )
                        }

                        // History button with count badge
                        BadgedBox(
                            badge = {
                                if (state.history.isNotEmpty()) {
                                    Badge(
                                        containerColor = BrutalistYellow,
                                        contentColor = BrutalistBorder,
                                        modifier = Modifier.border(1.2.dp, BrutalistBorder, RoundedCornerShape(8.dp))
                                    ) {
                                        Text(
                                            text = state.history.size.toString(),
                                            fontWeight = FontWeight.Bold,
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
                                    tint = BrutalistBorder
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
                    BrutalistMemoryChip(label = "MC", enabled = state.hasMemory, testTag = "mem_mc", onClick = onMemoryClear)
                    BrutalistMemoryChip(label = "MR", enabled = state.hasMemory, testTag = "mem_mr", onClick = onMemoryRecall)
                    BrutalistMemoryChip(label = "M+", enabled = true, testTag = "mem_m_plus", onClick = onMemoryAdd)
                    BrutalistMemoryChip(label = "M-", enabled = true, testTag = "mem_m_minus", onClick = onMemorySubtract)
                }

                Spacer(modifier = Modifier.height(10.dp))

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
                        fontWeight = FontWeight.Black,
                        color = if (state.expression.isEmpty()) SecondaryText.copy(alpha = 0.5f) else BrutalistBorder,
                        textAlign = TextAlign.End,
                        maxLines = 1,
                        modifier = Modifier.testTag("calculator_expression_text")
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Live Preview Result or Error Message
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(34.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    if (state.errorMessage != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = BrutalistPink,
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, BrutalistBorder)
                        ) {
                            Text(
                                text = state.errorMessage,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = BrutalistBorder,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                textAlign = TextAlign.End
                            )
                        }
                    } else if (state.previewResult != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = BrutalistGreen,
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, BrutalistBorder)
                        ) {
                            Text(
                                text = state.previewResult,
                                style = MaterialTheme.typography.titleMedium,
                                color = BrutalistBorder,
                                fontWeight = FontWeight.Black,
                                textAlign = TextAlign.End,
                                modifier = Modifier
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                                    .testTag("calculator_preview_text")
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BrutalistMemoryChip(
    label: String,
    enabled: Boolean,
    testTag: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(8.dp),
        color = if (enabled) Color(0xFFF1EFE6) else Color(0xFFE5E0D5),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, if (enabled) BrutalistBorder else BrutalistBorder.copy(alpha = 0.3f)),
        modifier = Modifier.testTag(testTag)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Black,
            color = if (enabled) BrutalistBorder else BrutalistBorder.copy(alpha = 0.4f),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}
