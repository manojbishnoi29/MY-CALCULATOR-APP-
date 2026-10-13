package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ConversionUnit
import com.example.model.UnitCategory
import com.example.model.UnitDefinitions
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.SecondaryText
import com.example.viewmodel.CalculatorViewModel

@Composable
fun UnitConverterScreen(
    viewModel: CalculatorViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.unitConverterState.collectAsState()
    val context = LocalContext.current
    val availableUnits = UnitDefinitions.unitsByCategory[state.category] ?: emptyList()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("unit_converter_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Category Horizontal Selector
        Text(
            text = "Select Category",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(UnitCategory.values()) { category ->
                val isSelected = state.category == category
                Surface(
                    onClick = { viewModel.selectUnitCategory(category) },
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) AccentCyan else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.testTag("cat_chip_${category.name}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = getCategoryIcon(category),
                            contentDescription = null,
                            tint = if (isSelected) Color(0xFF0F172A) else SecondaryText,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = category.displayName,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color(0xFF0F172A) else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Conversion Card 1: FROM
        ConversionUnitCard(
            title = "From",
            amount = state.inputAmount,
            onAmountChange = { viewModel.setUnitInputAmount(it) },
            selectedUnit = state.fromUnit,
            availableUnits = availableUnits,
            onUnitSelected = { viewModel.setFromUnit(it) },
            isEditable = true,
            testTagPrefix = "from"
        )

        // Middle Swap Button
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            FilledIconButton(
                onClick = { viewModel.swapUnits() },
                modifier = Modifier
                    .size(48.dp)
                    .testTag("swap_units_button"),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = AccentCyan,
                    contentColor = Color(0xFF0F172A)
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.SwapVert,
                    contentDescription = "Swap Units",
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Conversion Card 2: TO
        ConversionUnitCard(
            title = "To",
            amount = state.convertedAmount,
            onAmountChange = {},
            selectedUnit = state.toUnit,
            availableUnits = availableUnits,
            onUnitSelected = { viewModel.setToUnit(it) },
            isEditable = false,
            onCopyResult = {
                val clip = ClipData.newPlainText("Converted Unit", "${state.convertedAmount} ${state.toUnit.symbol}")
                (context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager)?.setPrimaryClip(clip)
                Toast.makeText(context, "Copied result to clipboard", Toast.LENGTH_SHORT).show()
            },
            testTagPrefix = "to"
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Quick Reference Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Quick Reference (${state.category.displayName})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Show top 3 relationships
                availableUnits.take(3).forEach { u ->
                    val eqToBase = UnitDefinitions.convert(1.0, state.category, u, availableUnits.first())
                    Text(
                        text = "1 ${u.symbol} (${u.name}) = $eqToBase ${availableUnits.first().symbol}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SecondaryText
                    )
                }
            }
        }
    }
}

@Composable
private fun ConversionUnitCard(
    title: String,
    amount: String,
    onAmountChange: (String) -> Unit,
    selectedUnit: ConversionUnit,
    availableUnits: List<ConversionUnit>,
    onUnitSelected: (ConversionUnit) -> Unit,
    isEditable: Boolean,
    testTagPrefix: String,
    onCopyResult: (() -> Unit)? = null
) {
    var dropdownExpanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("${testTagPrefix}_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    color = SecondaryText
                )

                // Unit Selector Dropdown trigger
                Box {
                    Surface(
                        onClick = { dropdownExpanded = true },
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.testTag("${testTagPrefix}_unit_selector")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "${selectedUnit.name} (${selectedUnit.symbol})",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = AccentCyan
                            )
                            Icon(
                                imageVector = Icons.Filled.ArrowDropDown,
                                contentDescription = null,
                                tint = AccentCyan
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                    ) {
                        availableUnits.forEach { unit ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "${unit.name} (${unit.symbol})",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                },
                                onClick = {
                                    onUnitSelected(unit)
                                    dropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (isEditable) {
                OutlinedTextField(
                    value = amount,
                    onValueChange = { input ->
                        // Validate number characters
                        if (input.isEmpty() || input.matches(Regex("^-?\\d*\\.?\\d*$"))) {
                            onAmountChange(input)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("${testTagPrefix}_amount_input"),
                    textStyle = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentCyan,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(14.dp)
                )
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(14.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = amount,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = AccentCyan,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("${testTagPrefix}_result_text")
                    )

                    if (onCopyResult != null) {
                        IconButton(
                            onClick = onCopyResult,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ContentCopy,
                                contentDescription = "Copy Converted Value",
                                tint = SecondaryText
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun getCategoryIcon(cat: UnitCategory): ImageVector {
    return when (cat) {
        UnitCategory.LENGTH -> Icons.Filled.Straighten
        UnitCategory.WEIGHT -> Icons.Filled.FitnessCenter
        UnitCategory.TEMPERATURE -> Icons.Filled.Thermostat
        UnitCategory.AREA -> Icons.Filled.CropFree
        UnitCategory.VOLUME -> Icons.Filled.LocalDrink
        UnitCategory.SPEED -> Icons.Filled.Speed
        UnitCategory.STORAGE -> Icons.Filled.Memory
        UnitCategory.TIME -> Icons.Filled.Schedule
    }
}
