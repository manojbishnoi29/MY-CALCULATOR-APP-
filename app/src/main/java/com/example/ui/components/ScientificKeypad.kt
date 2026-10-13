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
fun ScientificKeypad(
    isInverse: Boolean,
    isRadMode: Boolean,
    onToggleInverse: () -> Unit,
    onToggleRad: () -> Unit,
    onFunction: (String) -> Unit,
    onConstant: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Row 1
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalculatorButton(
                text = if (isInverse) "1st" else "2nd",
                type = KeypadButtonType.SCIENTIFIC,
                onClick = onToggleInverse,
                modifier = Modifier.weight(1f),
                height = 50.dp,
                testTag = "btn_sc_2nd"
            )
            CalculatorButton(
                text = if (isRadMode) "rad" else "deg",
                type = KeypadButtonType.SCIENTIFIC,
                onClick = onToggleRad,
                modifier = Modifier.weight(1f),
                height = 50.dp,
                testTag = "btn_sc_rad_deg"
            )
            CalculatorButton(
                text = if (isInverse) "sin⁻¹" else "sin",
                type = KeypadButtonType.SCIENTIFIC,
                onClick = { onFunction(if (isInverse) "asin" else "sin") },
                modifier = Modifier.weight(1f),
                height = 50.dp,
                testTag = "btn_sc_sin"
            )
            CalculatorButton(
                text = if (isInverse) "cos⁻¹" else "cos",
                type = KeypadButtonType.SCIENTIFIC,
                onClick = { onFunction(if (isInverse) "acos" else "cos") },
                modifier = Modifier.weight(1f),
                height = 50.dp,
                testTag = "btn_sc_cos"
            )
            CalculatorButton(
                text = if (isInverse) "tan⁻¹" else "tan",
                type = KeypadButtonType.SCIENTIFIC,
                onClick = { onFunction(if (isInverse) "atan" else "tan") },
                modifier = Modifier.weight(1f),
                height = 50.dp,
                testTag = "btn_sc_tan"
            )
        }

        // Row 2
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalculatorButton(
                text = "ln",
                type = KeypadButtonType.SCIENTIFIC,
                onClick = { onFunction("ln") },
                modifier = Modifier.weight(1f),
                height = 50.dp,
                testTag = "btn_sc_ln"
            )
            CalculatorButton(
                text = "log",
                type = KeypadButtonType.SCIENTIFIC,
                onClick = { onFunction("log") },
                modifier = Modifier.weight(1f),
                height = 50.dp,
                testTag = "btn_sc_log"
            )
            CalculatorButton(
                text = "1/x",
                type = KeypadButtonType.SCIENTIFIC,
                onClick = { onFunction("1/x") },
                modifier = Modifier.weight(1f),
                height = 50.dp,
                testTag = "btn_sc_inv"
            )
            CalculatorButton(
                text = "eˣ",
                type = KeypadButtonType.SCIENTIFIC,
                onClick = { onFunction("eˣ") },
                modifier = Modifier.weight(1f),
                height = 50.dp,
                testTag = "btn_sc_exp"
            )
            CalculatorButton(
                text = "x²",
                type = KeypadButtonType.SCIENTIFIC,
                onClick = { onFunction("x²") },
                modifier = Modifier.weight(1f),
                height = 50.dp,
                testTag = "btn_sc_square"
            )
        }

        // Row 3
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalculatorButton(
                text = "xʸ",
                type = KeypadButtonType.SCIENTIFIC,
                onClick = { onFunction("xʸ") },
                modifier = Modifier.weight(1f),
                height = 50.dp,
                testTag = "btn_sc_power"
            )
            CalculatorButton(
                text = "√x",
                type = KeypadButtonType.SCIENTIFIC,
                onClick = { onFunction("√x") },
                modifier = Modifier.weight(1f),
                height = 50.dp,
                testTag = "btn_sc_sqrt"
            )
            CalculatorButton(
                text = "∛x",
                type = KeypadButtonType.SCIENTIFIC,
                onClick = { onFunction("∛x") },
                modifier = Modifier.weight(1f),
                height = 50.dp,
                testTag = "btn_sc_cbrt"
            )
            CalculatorButton(
                text = "x!",
                type = KeypadButtonType.SCIENTIFIC,
                onClick = { onFunction("x!") },
                modifier = Modifier.weight(1f),
                height = 50.dp,
                testTag = "btn_sc_fact"
            )
            CalculatorButton(
                text = "π",
                type = KeypadButtonType.SCIENTIFIC,
                onClick = { onConstant("pi") },
                modifier = Modifier.weight(1f),
                height = 50.dp,
                testTag = "btn_sc_pi"
            )
        }

        // Row 4
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalculatorButton(
                text = "e",
                type = KeypadButtonType.SCIENTIFIC,
                onClick = { onConstant("e") },
                modifier = Modifier.weight(1f),
                height = 50.dp,
                testTag = "btn_sc_e"
            )
            CalculatorButton(
                text = "φ",
                type = KeypadButtonType.SCIENTIFIC,
                onClick = { onConstant("phi") },
                modifier = Modifier.weight(1f),
                height = 50.dp,
                testTag = "btn_sc_phi"
            )
            CalculatorButton(
                text = "|x|",
                type = KeypadButtonType.SCIENTIFIC,
                onClick = { onFunction("|x|") },
                modifier = Modifier.weight(1f),
                height = 50.dp,
                testTag = "btn_sc_abs"
            )
            CalculatorButton(
                text = "10ˣ",
                type = KeypadButtonType.SCIENTIFIC,
                onClick = { onFunction("10ˣ") },
                modifier = Modifier.weight(1f),
                height = 50.dp,
                testTag = "btn_sc_ten_pow"
            )
            CalculatorButton(
                text = "( )",
                type = KeypadButtonType.SCIENTIFIC,
                onClick = { onFunction("()") },
                modifier = Modifier.weight(1f),
                height = 50.dp,
                testTag = "btn_sc_parens"
            )
        }
    }
}
