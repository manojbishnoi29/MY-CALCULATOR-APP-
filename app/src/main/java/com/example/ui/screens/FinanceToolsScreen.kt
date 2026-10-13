package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.SecondaryText
import com.example.viewmodel.CalculatorViewModel
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

@Composable
fun FinanceToolsScreen(
    viewModel: CalculatorViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val financeState by viewModel.financeState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("finance_tools_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Tab Row: Tip & Split vs Discount & Tax
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = AccentCyan,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = AccentCyan
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(14.dp))
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(imageVector = Icons.Filled.ReceiptLong, contentDescription = null, modifier = Modifier.size(18.dp))
                        Text("Tip & Split", fontWeight = FontWeight.Bold)
                    }
                },
                modifier = Modifier.testTag("tab_tip_split")
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(imageVector = Icons.Filled.LocalOffer, contentDescription = null, modifier = Modifier.size(18.dp))
                        Text("Discount & Tax", fontWeight = FontWeight.Bold)
                    }
                },
                modifier = Modifier.testTag("tab_discount_tax")
            )
        }

        if (selectedTab == 0) {
            TipCalculatorSection(viewModel = viewModel, state = financeState)
        } else {
            DiscountCalculatorSection(viewModel = viewModel, state = financeState)
        }
    }
}

@Composable
private fun TipCalculatorSection(
    viewModel: CalculatorViewModel,
    state: com.example.viewmodel.FinanceToolsUiState
) {
    val df = DecimalFormat("$#,##0.00", DecimalFormatSymbols(Locale.US))

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Summary Result Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth().testTag("tip_summary_card")
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Per Person Breakdown",
                    style = MaterialTheme.typography.labelLarge,
                    color = SecondaryText
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = "Total / Person",
                            style = MaterialTheme.typography.bodySmall,
                            color = SecondaryText
                        )
                        Text(
                            text = df.format(state.totalPerPerson),
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = AccentCyan,
                            modifier = Modifier.testTag("total_per_person_text")
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Tip / Person",
                            style = MaterialTheme.typography.bodySmall,
                            color = SecondaryText
                        )
                        Text(
                            text = df.format(state.tipPerPerson),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = AccentAmber,
                            modifier = Modifier.testTag("tip_per_person_text")
                        )
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Total Tip: ${df.format(state.tipAmount)}", style = MaterialTheme.typography.bodyMedium, color = SecondaryText)
                    Text(text = "Total Bill: ${df.format(state.totalWithTip)}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }

        // Bill Amount Input
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(text = "Bill Amount", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onBackground)
            OutlinedTextField(
                value = state.billAmount,
                onValueChange = { input ->
                    if (input.isEmpty() || input.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                        viewModel.setBillAmount(input)
                    }
                },
                prefix = { Text("$ ", fontWeight = FontWeight.Bold, color = AccentCyan) },
                modifier = Modifier.fillMaxWidth().testTag("bill_amount_input"),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentCyan,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }

        // Tip Percentage Selection
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = "Tip Percentage", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onBackground)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(10.0, 15.0, 18.0, 20.0).forEach { pct ->
                    val isSelected = !state.isCustomTip && state.tipPercent == pct
                    Surface(
                        onClick = { viewModel.setTipPercent(pct) },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) AccentCyan else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.weight(1f).testTag("tip_chip_${pct.toInt()}")
                    ) {
                        Text(
                            text = "${pct.toInt()}%",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color(0xFF0F172A) else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(vertical = 12.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }

            // Custom Tip Input
            OutlinedTextField(
                value = state.customTipPercent,
                onValueChange = { input ->
                    if (input.isEmpty() || input.matches(Regex("^\\d*\\.?\\d*$"))) {
                        viewModel.setCustomTipPercent(input)
                    }
                },
                placeholder = { Text("Custom Tip %") },
                suffix = { Text("%", color = SecondaryText) },
                modifier = Modifier.fillMaxWidth().testTag("custom_tip_input"),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentCyan,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }

        // Split Between People Stepper
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Split Between", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(text = "${state.splitCount} ${if (state.splitCount == 1) "person" else "people"}", style = MaterialTheme.typography.bodySmall, color = SecondaryText)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FilledIconButton(
                        onClick = { viewModel.setSplitCount(state.splitCount - 1) },
                        enabled = state.splitCount > 1,
                        modifier = Modifier.size(40.dp).testTag("split_decrement_btn"),
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Icon(imageVector = Icons.Filled.Remove, contentDescription = "Decrease people")
                    }

                    Text(
                        text = state.splitCount.toString(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.testTag("split_count_text")
                    )

                    FilledIconButton(
                        onClick = { viewModel.setSplitCount(state.splitCount + 1) },
                        modifier = Modifier.size(40.dp).testTag("split_increment_btn"),
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = AccentCyan, contentColor = Color(0xFF0F172A))
                    ) {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = "Increase people")
                    }
                }
            }
        }

        // Round Up Switch
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Round Up Total", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(text = "Round total to next full dollar", style = MaterialTheme.typography.bodySmall, color = SecondaryText)
                }

                Switch(
                    checked = state.roundUpTotal,
                    onCheckedChange = { viewModel.toggleRoundUpTotal() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = AccentCyan,
                        checkedTrackColor = AccentCyan.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier.testTag("round_up_switch")
                )
            }
        }
    }
}

@Composable
private fun DiscountCalculatorSection(
    viewModel: CalculatorViewModel,
    state: com.example.viewmodel.FinanceToolsUiState
) {
    val df = DecimalFormat("$#,##0.00", DecimalFormatSymbols(Locale.US))

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Summary Result Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth().testTag("discount_summary_card")
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Final Pay Price",
                    style = MaterialTheme.typography.labelLarge,
                    color = SecondaryText
                )

                Text(
                    text = df.format(state.finalPrice),
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = AccentAmber,
                    modifier = Modifier.testTag("final_discount_price_text")
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "You Save: ${df.format(state.savingsAmount)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = AccentCyan
                    )
                    Text(
                        text = "Tax: ${df.format(state.taxAmount)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SecondaryText
                    )
                }
            }
        }

        // Original Price
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(text = "Original Price", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onBackground)
            OutlinedTextField(
                value = state.originalPrice,
                onValueChange = { input ->
                    if (input.isEmpty() || input.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                        viewModel.setOriginalPrice(input)
                    }
                },
                prefix = { Text("$ ", fontWeight = FontWeight.Bold, color = AccentCyan) },
                modifier = Modifier.fillMaxWidth().testTag("orig_price_input"),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentCyan,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }

        // Discount Percent
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(text = "Discount Percentage", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onBackground)
            OutlinedTextField(
                value = state.discountPercent,
                onValueChange = { input ->
                    if (input.isEmpty() || input.matches(Regex("^\\d*\\.?\\d*$"))) {
                        viewModel.setDiscountPercent(input)
                    }
                },
                suffix = { Text("%", color = SecondaryText) },
                modifier = Modifier.fillMaxWidth().testTag("discount_percent_input"),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentCyan,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }

        // Sales Tax Percent
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(text = "Sales Tax (Optional)", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onBackground)
            OutlinedTextField(
                value = state.taxPercent,
                onValueChange = { input ->
                    if (input.isEmpty() || input.matches(Regex("^\\d*\\.?\\d*$"))) {
                        viewModel.setTaxPercent(input)
                    }
                },
                suffix = { Text("%", color = SecondaryText) },
                modifier = Modifier.fillMaxWidth().testTag("tax_percent_input"),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentCyan,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    }
}
