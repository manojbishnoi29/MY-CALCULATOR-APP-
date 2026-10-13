package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.components.CalculatorDisplay
import com.example.ui.components.CalculatorKeypad
import com.example.ui.components.HistorySheet
import com.example.ui.components.ScientificKeypad
import com.example.viewmodel.CalculatorViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel,
    onOpenMediaSettings: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val state by viewModel.calculatorState.collectAsState()
    var isHistoryOpen by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("calculator_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Display Area
            CalculatorDisplay(
                state = state,
                onToggleScientific = { viewModel.toggleScientificExpanded() },
                onToggleRadDeg = { viewModel.toggleRadMode() },
                onOpenHistory = { isHistoryOpen = true },
                onOpenMediaSettings = onOpenMediaSettings,
                onMemoryClear = { viewModel.onMemoryClear() },
                onMemoryRecall = { viewModel.onMemoryRecall() },
                onMemoryAdd = { viewModel.onMemoryAdd() },
                onMemorySubtract = { viewModel.onMemorySubtract() },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f, fill = false))

            // Scientific Keypad (Animated expansion)
            AnimatedVisibility(
                visible = state.isScientificExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                ScientificKeypad(
                    isInverse = state.isInverseTrig,
                    isRadMode = state.isRadMode,
                    onToggleInverse = { viewModel.toggleInverseTrig() },
                    onToggleRad = { viewModel.toggleRadMode() },
                    onFunction = { viewModel.onScientificFunction(it) },
                    onConstant = { viewModel.onConstant(it) },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Standard Keypad
            CalculatorKeypad(
                onDigit = { viewModel.onDigit(it) },
                onDecimal = { viewModel.onDecimal() },
                onOperator = { viewModel.onOperator(it) },
                onParenthesis = { viewModel.onParenthesis() },
                onPercentage = { viewModel.onPercentage() },
                onBackspace = { viewModel.onBackspace() },
                onClear = { viewModel.onClear() },
                onEquals = { viewModel.onEquals() },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        // History Bottom Sheet
        if (isHistoryOpen) {
            HistorySheet(
                history = state.history,
                sheetState = sheetState,
                onDismiss = { isHistoryOpen = false },
                onItemClick = { item, loadExpr ->
                    viewModel.onHistoryItemClick(item, loadExpr)
                },
                onClearHistory = { viewModel.clearHistory() }
            )
        }
    }
}
