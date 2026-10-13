package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CalculatorKeypad(
    onDigit: (String) -> Unit,
    onDecimal: () -> Unit,
    onOperator: (String) -> Unit,
    onParenthesis: () -> Unit,
    onPercentage: () -> Unit,
    onBackspace: () -> Unit,
    onClear: () -> Unit,
    onEquals: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Row 1: AC, Backspace, Percentage, Divide
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CalculatorButton(
                text = "AC",
                type = KeypadButtonType.ACTION,
                onClick = onClear,
                modifier = Modifier.weight(1f),
                testTag = "btn_clear"
            )
            CalculatorButton(
                text = "⌫",
                type = KeypadButtonType.ACTION,
                onClick = onBackspace,
                modifier = Modifier.weight(1f),
                testTag = "btn_backspace"
            )
            CalculatorButton(
                text = "%",
                type = KeypadButtonType.ACTION,
                onClick = onPercentage,
                modifier = Modifier.weight(1f),
                testTag = "btn_percent"
            )
            CalculatorButton(
                text = "÷",
                type = KeypadButtonType.OPERATOR,
                onClick = { onOperator("÷") },
                modifier = Modifier.weight(1f),
                testTag = "btn_divide"
            )
        }

        // Row 2: 7, 8, 9, Multiply
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CalculatorButton(
                text = "7",
                type = KeypadButtonType.NUMBER,
                onClick = { onDigit("7") },
                modifier = Modifier.weight(1f),
                testTag = "btn_7"
            )
            CalculatorButton(
                text = "8",
                type = KeypadButtonType.NUMBER,
                onClick = { onDigit("8") },
                modifier = Modifier.weight(1f),
                testTag = "btn_8"
            )
            CalculatorButton(
                text = "9",
                type = KeypadButtonType.NUMBER,
                onClick = { onDigit("9") },
                modifier = Modifier.weight(1f),
                testTag = "btn_9"
            )
            CalculatorButton(
                text = "×",
                type = KeypadButtonType.OPERATOR,
                onClick = { onOperator("×") },
                modifier = Modifier.weight(1f),
                testTag = "btn_multiply"
            )
        }

        // Row 3: 4, 5, 6, Subtract
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CalculatorButton(
                text = "4",
                type = KeypadButtonType.NUMBER,
                onClick = { onDigit("4") },
                modifier = Modifier.weight(1f),
                testTag = "btn_4"
            )
            CalculatorButton(
                text = "5",
                type = KeypadButtonType.NUMBER,
                onClick = { onDigit("5") },
                modifier = Modifier.weight(1f),
                testTag = "btn_5"
            )
            CalculatorButton(
                text = "6",
                type = KeypadButtonType.NUMBER,
                onClick = { onDigit("6") },
                modifier = Modifier.weight(1f),
                testTag = "btn_6"
            )
            CalculatorButton(
                text = "−",
                type = KeypadButtonType.OPERATOR,
                onClick = { onOperator("−") },
                modifier = Modifier.weight(1f),
                testTag = "btn_subtract"
            )
        }

        // Row 4: 1, 2, 3, Add
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CalculatorButton(
                text = "1",
                type = KeypadButtonType.NUMBER,
                onClick = { onDigit("1") },
                modifier = Modifier.weight(1f),
                testTag = "btn_1"
            )
            CalculatorButton(
                text = "2",
                type = KeypadButtonType.NUMBER,
                onClick = { onDigit("2") },
                modifier = Modifier.weight(1f),
                testTag = "btn_2"
            )
            CalculatorButton(
                text = "3",
                type = KeypadButtonType.NUMBER,
                onClick = { onDigit("3") },
                modifier = Modifier.weight(1f),
                testTag = "btn_3"
            )
            CalculatorButton(
                text = "+",
                type = KeypadButtonType.OPERATOR,
                onClick = { onOperator("+") },
                modifier = Modifier.weight(1f),
                testTag = "btn_add"
            )
        }

        // Row 5: Parentheses, 0, Decimal, Equals
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CalculatorButton(
                text = "( )",
                type = KeypadButtonType.ACTION,
                onClick = onParenthesis,
                modifier = Modifier.weight(1f),
                testTag = "btn_parens"
            )
            CalculatorButton(
                text = "0",
                type = KeypadButtonType.NUMBER,
                onClick = { onDigit("0") },
                modifier = Modifier.weight(1f),
                testTag = "btn_0"
            )
            CalculatorButton(
                text = ".",
                type = KeypadButtonType.NUMBER,
                onClick = onDecimal,
                modifier = Modifier.weight(1f),
                testTag = "btn_decimal"
            )
            CalculatorButton(
                text = "=",
                type = KeypadButtonType.EQUALS,
                onClick = onEquals,
                modifier = Modifier.weight(1f),
                testTag = "btn_equals"
            )
        }
    }
}
